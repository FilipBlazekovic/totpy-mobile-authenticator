package com.filipblazekovic.totpy.crypto;

import static com.filipblazekovic.totpy.crypto.CryptoHandler.SECURE_HARDWARE_KEYSTORE_PROVIDER;
import static com.filipblazekovic.totpy.crypto.Keystore.MASTER_KEYS_ALIAS;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;
import com.filipblazekovic.totpy.model.internal.KeyPairGenerationResponse;
import com.filipblazekovic.totpy.utils.Common;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.MGF1ParameterSpec;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import lombok.SneakyThrows;
import lombok.Synchronized;
import lombok.val;

public final class RSA {

  private static final String ENCRYPTION_ALGORITHM = "RSA/NONE/OAEPwithSHA-256andMGF1Padding";

  private RSA() {
  }

  static KeyPairGenerationResponse generateKeyPair(int keySize) {
    try {

      Keystore.deleteKeyStoreEntry(MASTER_KEYS_ALIAS);

      val keyPairGenerator = KeyPairGenerator.getInstance(
          KeyProperties.KEY_ALGORITHM_RSA,
          SECURE_HARDWARE_KEYSTORE_PROVIDER
      );

      val builder = generateBuilder(keySize);
      Keystore.setNoAuthorizationParams(builder);
      return Keystore.generateKeyPairForParams(keyPairGenerator, builder);

    } catch (Exception e) {
      Log.e(Common.TAG, "Exception thrown while generating RSA key pair", e);
      Keystore.deleteKeyStoreEntry(MASTER_KEYS_ALIAS);
      return KeyPairGenerationResponse.error();
    }
  }

  private static KeyGenParameterSpec.Builder generateBuilder(int keySize) {
    return new KeyGenParameterSpec
        .Builder(MASTER_KEYS_ALIAS, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
        .setKeySize(keySize)
        .setDigests(KeyProperties.DIGEST_SHA256)
        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP);
  }

  @SneakyThrows
  @Synchronized
  static String encrypt(PublicKey publicKey, byte[] plaintext) {
    val cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
//  OAEPParameterSpec is required for RSA encryption with OAEP padding
//  (when used for keys in AndroidKeyStore) due to the bug described at:
//  https://issuetracker.google.com/issues/37075898
    cipher.init(
        Cipher.ENCRYPT_MODE,
        publicKey,
        new OAEPParameterSpec(
            "SHA-256",
            "MGF1",
            MGF1ParameterSpec.SHA1,
            PSource.PSpecified.DEFAULT
        )
    );
    return Base64.getEncoder().encodeToString(
        cipher.doFinal(plaintext)
    );
  }

  @SneakyThrows
  @Synchronized
  static byte[] decrypt(PrivateKey privateKey, String ciphertext) {
    val cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
    cipher.init(Cipher.DECRYPT_MODE, privateKey);
    return cipher.doFinal(
        Base64.getDecoder().decode(ciphertext)
    );
  }
}
