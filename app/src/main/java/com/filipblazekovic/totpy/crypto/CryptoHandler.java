package com.filipblazekovic.totpy.crypto;

import android.content.Context;
import android.security.keystore.KeyInfo;
import android.security.keystore.KeyProperties;
import android.util.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.filipblazekovic.totpy.model.db.DBToken;
import com.filipblazekovic.totpy.model.inout.Export;
import com.filipblazekovic.totpy.model.inout.ExportLocked;
import com.filipblazekovic.totpy.model.inout.ExportLockingPublicKey;
import com.filipblazekovic.totpy.model.internal.DeviceSecurityInfo;
import com.filipblazekovic.totpy.model.internal.KeyPairGenerationResponse;
import com.filipblazekovic.totpy.model.internal.KeyPairGenerationStatus;
import com.filipblazekovic.totpy.model.internal.Protection;
import com.filipblazekovic.totpy.model.internal.Token;
import com.filipblazekovic.totpy.model.shared.AsymmetricKeyAlgorithm;
import com.filipblazekovic.totpy.utils.Common;
import com.scottyab.rootbeer.RootBeer;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.crypto.spec.SecretKeySpec;
import lombok.SneakyThrows;
import lombok.val;

public final class CryptoHandler {

  static final String SECURE_HARDWARE_KEYSTORE_PROVIDER = "AndroidKeyStore";

  private CryptoHandler() {
  }

  public static boolean shouldRecalculateOTP(int period) {
    return TOTP.shouldRecalculate(period);
  }

  public static int getRemainingOTPSeconds(int period) {
    return TOTP.getRemainingSeconds(period);
  }

  public static String calculateOTP(Token token) {
    try {
      return TOTP.calculateOtp(
          token.getAlgorithm(),
          token.getPeriod(),
          token.getDigits(),
          token.getSecret()
      );
    } catch (Exception e) {
      return "******";
    }
  }

  public static DeviceSecurityInfo getDeviceSecurityInfo(Context context) {
    try {

      val deviceRooted = new RootBeer(context).isRooted();
      val privateKey = Keystore.getPrivateKey();
      val factory = KeyFactory.getInstance(privateKey.getAlgorithm(), SECURE_HARDWARE_KEYSTORE_PROVIDER);
      val keyInfo = factory.getKeySpec(privateKey, KeyInfo.class);
      val algorithm = privateKey.getAlgorithm().equals("EC")
          ? AsymmetricKeyAlgorithm.EC
          : AsymmetricKeyAlgorithm.RSA;

      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
        switch (keyInfo.getSecurityLevel()) {
          case KeyProperties.SECURITY_LEVEL_STRONGBOX:
            return new DeviceSecurityInfo(Protection.STRONGBOX, algorithm, deviceRooted);
          case KeyProperties.SECURITY_LEVEL_TRUSTED_ENVIRONMENT:
          case KeyProperties.SECURITY_LEVEL_UNKNOWN_SECURE:
            return new DeviceSecurityInfo(Protection.TEE, algorithm, deviceRooted);
          default:
            return new DeviceSecurityInfo(Protection.SOFTWARE, algorithm, deviceRooted);
        }
      }

      if (keyInfo.isInsideSecureHardware()) {
        return new DeviceSecurityInfo(Protection.TEE, algorithm, deviceRooted);
      }
      return new DeviceSecurityInfo(Protection.SOFTWARE, algorithm, deviceRooted);

    } catch (Exception e) {
      Log.e(Common.TAG, "Got exception while retrieving device security info", e);
      return null;
    }
  }

  public static KeyPairGenerationResponse generateAuthenticatorKeys() {
    val ecKeysSupported = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S);
    KeyPairGenerationResponse response = KeyPairGenerationResponse.error();

    if (ecKeysSupported) {
      response = EC.generateKeyPairs();
    }
    if (response.getStatus() == KeyPairGenerationStatus.FAILURE || response.getProtection() == Protection.SOFTWARE) {
      response = RSA.generateKeyPair(4096);
    }
    if (response.getStatus() == KeyPairGenerationStatus.FAILURE || response.getProtection() == Protection.SOFTWARE) {
      response = RSA.generateKeyPair(2048);
    }
    if (ecKeysSupported && (response.getStatus() == KeyPairGenerationStatus.FAILURE || response.getProtection() == Protection.SOFTWARE)) {
      response = EC.generateKeyPairs();
    }
    if (response.getStatus() == KeyPairGenerationStatus.FAILURE || (!ecKeysSupported && response.getProtection() == Protection.SOFTWARE)) {
      response = RSA.generateKeyPair(4096);
    }
    if (response.getStatus() == KeyPairGenerationStatus.FAILURE) {
      response = RSA.generateKeyPair(2048);
    }

    return response;
  }

  public static void deleteAuthenticatorKeys() {
    Keystore.deleteKeyStoreEntry(Keystore.MASTER_KEYS_ALIAS);
  }

  public static boolean authenticatorKeysExist() {
    return Keystore.keyPairExists(Keystore.MASTER_KEYS_ALIAS);
  }

  public static ExportLockingPublicKey getExportLockingPublicKey() {
    try {
      val publicKey = Keystore.getPublicKey();
      return new ExportLockingPublicKey(
          PEM.encode(publicKey),
          publicKey.getAlgorithm().equals("EC")
              ? AsymmetricKeyAlgorithm.EC
              : AsymmetricKeyAlgorithm.RSA
      );
    } catch (Exception e) {
      return null;
    }
  }

  public static DBToken lockToken(Token token) {
    try {

      val publicKey = Keystore.getPublicKey();

      // For EC keys lock with ECIES [ECDH+HKDF+AES-GCM]
      if (publicKey.getAlgorithm().equals("EC")) {
        val nonEphemeralEciesKeys = Keystore.getNonEphemeralEciesKeys();
        val secretKey = KDF.hkdf(
            nonEphemeralEciesKeys.getPublic(),
            ECDH.derive(nonEphemeralEciesKeys.getPrivate(), publicKey)
        );
        return DBToken.from(token, AES.encrypt(secretKey, token.getSecret()));
      }

      // For RSA keys lock directly with RSA_OAEP
      return DBToken.from(token, RSA.encrypt(publicKey, token.getSecret()));

    } catch (Exception e) {
      Log.e(Common.TAG, "Exception thrown while locking token", e);
      return null;
    }
  }

  public static List<DBToken> lockTokens(List<Token> tokens) {
    try {

      val publicKey = Keystore.getPublicKey();

      if (publicKey.getAlgorithm().equals("EC")) {
        val nonEphemeralEciesKeys = Keystore.getNonEphemeralEciesKeys();
        val secretKey = KDF.hkdf(
            nonEphemeralEciesKeys.getPublic(),
            ECDH.derive(nonEphemeralEciesKeys.getPrivate(), publicKey)
        );
        return tokens
            .stream()
            .map(t -> {
              try {
                return DBToken.from(t, AES.encrypt(secretKey, t.getSecret()));
              } catch (Exception e) {
                Log.e(Common.TAG, "Exception thrown during token encryption", e);
                throw new RuntimeException(e);
              }
            })
            .collect(Collectors.toList());
      }

      return tokens
          .stream()
          .map(t -> {
            try {
              return DBToken.from(t, RSA.encrypt(publicKey, t.getSecret()));
            } catch (Exception e) {
              Log.e(Common.TAG, "Exception thrown during token encryption", e);
              throw new RuntimeException(e);
            }
          })
          .collect(Collectors.toList());

    } catch (Exception e) {
      Log.e(Common.TAG, "Exception thrown while locking tokens", e);
      return Collections.emptyList();
    }
  }

  public static Token unlockToken(DBToken token) {
    try {

      val privateKey = Keystore.getPrivateKey();

      if (privateKey.getAlgorithm().equals("EC")) {
        val nonEphemeralEciesPublicKey = Keystore.getNonEphemeralEciesPublicKey();
        val secretKey = KDF.hkdf(
            nonEphemeralEciesPublicKey,
            ECDH.derive(privateKey, nonEphemeralEciesPublicKey)
        );

        return Token.from(token, AES.decrypt(secretKey, token.getSecretLocked()));
      }

      return Token.from(token, RSA.decrypt(privateKey, token.getSecretLocked()));

    } catch (Exception e) {
      Log.e(Common.TAG, "Exception thrown while unlocking token", e);
      return null;
    }
  }

  public static List<Token> unlockTokens(List<DBToken> tokens) {
    try {

      val privateKey = Keystore.getPrivateKey();

      if (privateKey.getAlgorithm().equals("EC")) {
        val nonEphemeralEciesPublicKey = Keystore.getNonEphemeralEciesPublicKey();
        val secretKey = KDF.hkdf(
            nonEphemeralEciesPublicKey,
            ECDH.derive(privateKey, nonEphemeralEciesPublicKey)
        );
        return tokens
            .stream()
            .map(t -> {
              try {
                return Token.from(t, AES.decrypt(secretKey, t.getSecretLocked()));
              } catch (Exception e) {
                Log.e(Common.TAG, "Exception thrown during token decryption", e);
                throw new RuntimeException(e);
              }
            })
            .collect(Collectors.toList());
      }

      return tokens
          .stream()
          .map(t -> {
            try {
              return Token.from(t, RSA.decrypt(privateKey, t.getSecretLocked()));
            } catch (Exception e) {
              Log.e(Common.TAG, "Exception thrown during token decryption", e);
              throw new RuntimeException(e);
            }
          })
          .collect(Collectors.toList());

    } catch (Exception e) {
      Log.e(Common.TAG, "Exception thrown while unlocking tokens", e);
      return Collections.emptyList();
    }
  }

  @SneakyThrows
  public static ExportLocked generateLockedExport(Context context, List<Token> tokens, char[] password, char[] passwordConfirmation) {
    PasswordHandler.validate(context, password, passwordConfirmation);

    val salt = KDF.generateSalt();
    val secretKey = KDF.pbkdf2(password, salt);
    Arrays.fill(password, '\0');
    Arrays.fill(passwordConfirmation, '\0');

    return ExportLocked.passwordLockedExport(
        salt,
        AES.encrypt(
            secretKey,
            new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .writeValueAsString(Export.from(tokens))
                .getBytes(StandardCharsets.UTF_8)
        )
    );
  }

  @SneakyThrows
  public static ExportLocked generateLockedExport(List<Token> tokens, ExportLockingPublicKey exportLockingPublicKey) {
    val peerPublicKey = PEM.decode(
        exportLockingPublicKey.getKeyAlgorithm(),
        exportLockingPublicKey.getPublicKeyPem()
    );

    if (peerPublicKey.getAlgorithm().equals("EC")) {
      return generateECIESLockedExport(tokens, peerPublicKey);
    }

    return generateRSALockedExport(tokens, peerPublicKey);
  }

  @SneakyThrows
  private static ExportLocked generateECIESLockedExport(List<Token> tokens, PublicKey peerPublicKey) {
    // Use EC keys from AndroidKeyStore if they are supported,
    // otherwise generate software-based ephemeral keys
    KeyPair keyPair;

    if (Keystore.keyPairExists(Keystore.ECIES_KEYS_ALIAS)) {
      keyPair = Keystore.getNonEphemeralEciesKeys();
    } else {
      keyPair = EC.generateEphemoralKeyPair();
    }

    val secretKey = KDF.hkdf(
        keyPair.getPublic(),
        ECDH.derive(keyPair.getPrivate(), peerPublicKey)
    );

    return ExportLocked.ecKeyLockedExport(
        PEM.encode(keyPair.getPublic()),
        AES.encrypt(
            secretKey,
            new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .writeValueAsString(Export.from(tokens))
                .getBytes(StandardCharsets.UTF_8)

        )
    );
  }

  @SneakyThrows
  private static ExportLocked generateRSALockedExport(List<Token> tokens, PublicKey peerPublicKey) {
    val dataLockingKey = AES.generateKey();
    return ExportLocked.rsaKeyLockedExport(
        RSA.encrypt(peerPublicKey, dataLockingKey),
        AES.encrypt(
            new SecretKeySpec(dataLockingKey, "AES"),
            new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .writeValueAsString(Export.from(tokens))
                .getBytes(StandardCharsets.UTF_8)
        )
    );
  }

  @SneakyThrows
  public static Export unlockExport(ExportLocked exportLocked, char[] password) {
    val secretKey = KDF.pbkdf2(
        password,
        Base64.getDecoder().decode(exportLocked.getSalt())
    );
    Arrays.fill(password, '\0');

    return new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .readValue(
            AES.decrypt(secretKey, exportLocked.getExportLockedBase64()),
            Export.class
        );
  }

  @SneakyThrows
  public static Export unlockExport(ExportLocked exportLocked) {
    val privateKey = Keystore.getPrivateKey();
    if (privateKey.getAlgorithm().equals("EC")) {
      return unlockECIESLockedExport(exportLocked, privateKey);
    }
    return unlockRSALockedExport(exportLocked, privateKey);
  }

  @SneakyThrows
  private static Export unlockECIESLockedExport(ExportLocked exportLocked, PrivateKey privateKey) {
    val ephemeralPeerPublicKey = PEM.decode(
        AsymmetricKeyAlgorithm.EC,
        exportLocked.getEphemeralPublicKeyPem()
    );
    val secretKey = KDF.hkdf(
        ephemeralPeerPublicKey,
        ECDH.derive(privateKey, ephemeralPeerPublicKey)
    );
    return new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .readValue(
            AES.decrypt(secretKey, exportLocked.getExportLockedBase64()),
            Export.class
        );
  }

  @SneakyThrows
  private static Export unlockRSALockedExport(ExportLocked exportLocked, PrivateKey privateKey) {
    val secretKey = new SecretKeySpec(
        RSA.decrypt(privateKey, exportLocked.getKeyLocked()),
        "AES"
    );
    return new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .readValue(
            AES.decrypt(
                secretKey,
                exportLocked.getExportLockedBase64()
            ),
            Export.class
        );
  }

}
