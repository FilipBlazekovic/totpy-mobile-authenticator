package com.filipblazekovic.totpy.crypto;

import static com.filipblazekovic.totpy.crypto.CryptoHandler.SECURE_HARDWARE_KEYSTORE_PROVIDER;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyInfo;
import android.security.keystore.KeyProperties;
import android.security.keystore.StrongBoxUnavailableException;
import android.util.Log;
import com.filipblazekovic.totpy.model.internal.KeyPairGenerationResponse;
import com.filipblazekovic.totpy.model.internal.Protection;
import com.filipblazekovic.totpy.model.shared.AsymmetricKeyAlgorithm;
import com.filipblazekovic.totpy.utils.Common;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import lombok.SneakyThrows;
import lombok.Synchronized;
import lombok.val;

public final class Keystore {

  static final String MASTER_KEYS_ALIAS = "TOTPY";
  static final String ECIES_KEYS_ALIAS = "TOTPY_ECIES";

  private Keystore() {
  }

  @Synchronized
  static boolean keyPairExists(String alias) {
    try {
      val keyStore = KeyStore.getInstance(SECURE_HARDWARE_KEYSTORE_PROVIDER);
      keyStore.load(null);
      return keyStore.containsAlias(alias);
    } catch (Exception e) {
      Log.e(Common.TAG, "Exception thrown while checking key store entry", e);
      return false;
    }
  }

  @SneakyThrows
  @Synchronized
  static PrivateKey getPrivateKey() {
    val keyStore = KeyStore.getInstance(SECURE_HARDWARE_KEYSTORE_PROVIDER);
    keyStore.load(null);
    return (PrivateKey) keyStore.getKey(MASTER_KEYS_ALIAS, null);
  }

  @SneakyThrows
  @Synchronized
  static PublicKey getPublicKey() {
    val keyStore = KeyStore.getInstance(SECURE_HARDWARE_KEYSTORE_PROVIDER);
    keyStore.load(null);
    return keyStore.getCertificate(MASTER_KEYS_ALIAS).getPublicKey();
  }

  @SneakyThrows
  @Synchronized
  static KeyPair getNonEphemeralEciesKeys() {
    val keyStore = KeyStore.getInstance(SECURE_HARDWARE_KEYSTORE_PROVIDER);
    keyStore.load(null);
    return new KeyPair(
        keyStore.getCertificate(ECIES_KEYS_ALIAS).getPublicKey(),
        (PrivateKey) keyStore.getKey(ECIES_KEYS_ALIAS, null)
    );
  }

  @SneakyThrows
  @Synchronized
  static PublicKey getNonEphemeralEciesPublicKey() {
    val keyStore = KeyStore.getInstance(SECURE_HARDWARE_KEYSTORE_PROVIDER);
    keyStore.load(null);
    return keyStore.getCertificate(ECIES_KEYS_ALIAS).getPublicKey();
  }

  static void setNoAuthorizationParams(KeyGenParameterSpec.Builder builder) {
    builder
        // UnlockedDeviceRequired needs to be false for keys
        // that don't require confirmation or authentication,
        // otherwise Keymaster returns an error on some devices
        .setUnlockedDeviceRequired(false)
        .setUserConfirmationRequired(false)
        .setUserAuthenticationRequired(false)
        .setInvalidatedByBiometricEnrollment(false);

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
      builder.setIsStrongBoxBacked(true);
    }
  }

  @SneakyThrows
  @Synchronized
  static KeyPairGenerationResponse generateKeyPairForParams(KeyPairGenerator keyPairGenerator, KeyGenParameterSpec.Builder builder) {
    KeyPair keyPair;
    try {
      keyPairGenerator.initialize(builder.build());
      keyPair = keyPairGenerator.generateKeyPair();

    } catch (StrongBoxUnavailableException e) {
      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
        Log.w(Common.TAG, "Strongbox unavailable => falling back to TEE");
        keyPairGenerator.initialize(builder.setIsStrongBoxBacked(false).build());
        keyPair = keyPairGenerator.generateKeyPair();
      } else {
        throw e;
      }
    }

    val privateKey = keyPair.getPrivate();
    val factory = KeyFactory.getInstance(privateKey.getAlgorithm(), SECURE_HARDWARE_KEYSTORE_PROVIDER);
    val keyInfo = factory.getKeySpec(privateKey, KeyInfo.class);
    val algorithm = privateKey.getAlgorithm().equals("EC")
        ? AsymmetricKeyAlgorithm.EC
        : AsymmetricKeyAlgorithm.RSA;

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
      val securityLevel = keyInfo.getSecurityLevel();

      if (securityLevel == KeyProperties.SECURITY_LEVEL_STRONGBOX) {
        Log.i(Common.TAG, "Generated StrongBox protected key pair");
        return KeyPairGenerationResponse.success(Protection.STRONGBOX, algorithm);
      }

      if (securityLevel == KeyProperties.SECURITY_LEVEL_TRUSTED_ENVIRONMENT) {
        Log.i(Common.TAG, "Generated TEE protected key pair");
        return KeyPairGenerationResponse.success(Protection.TEE, algorithm);
      }

      if (securityLevel == KeyProperties.SECURITY_LEVEL_UNKNOWN_SECURE) {
        Log.i(Common.TAG, "Generated TEE or better protected key pair");
        return KeyPairGenerationResponse.success(Protection.TEE, algorithm);
      }

      Log.i(Common.TAG, "Generated software protected key pair");
      return KeyPairGenerationResponse.success(Protection.SOFTWARE, algorithm);
    }

    // Fallback for older devices that don't support getSecurityLevel() enum values
    if (keyInfo.isInsideSecureHardware()) {
      Log.i(Common.TAG, "Generated TEE or better protected key pair");
      return KeyPairGenerationResponse.success(Protection.TEE, algorithm);
    }

    Log.i(Common.TAG, "Generated software protected key pair");
    return KeyPairGenerationResponse.success(Protection.SOFTWARE, algorithm);
  }

  @Synchronized
  static void deleteKeyStoreEntry(String alias) {
    try {
      val keyStore = KeyStore.getInstance(SECURE_HARDWARE_KEYSTORE_PROVIDER);
      keyStore.load(null);
      keyStore.deleteEntry(alias);
    } catch (Exception e) {
      Log.e(Common.TAG, "Exception thrown while deleting key store entry", e);
    }
  }

}
