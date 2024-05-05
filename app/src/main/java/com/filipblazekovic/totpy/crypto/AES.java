package com.filipblazekovic.totpy.crypto;

import com.google.common.primitives.Bytes;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import lombok.SneakyThrows;
import lombok.val;

public final class AES {

  private static final String ENCRYPTION_ALGORITHM = "AES/GCM/NoPadding";

  private static final int AES_KEY_SIZE = 32;
  private static final int GCM_TAG_LENGTH = 16;
  private static final int GCM_IV_LENGTH = 12;

  private AES() {
  }

  private static byte[] generateIV() {
    val iv = new byte[GCM_IV_LENGTH];
    val rng = new SecureRandom();
    rng.nextBytes(iv);
    return iv;
  }

  static byte[] generateKey() {
    val key = new byte[AES_KEY_SIZE];
    val rng = new SecureRandom();
    rng.nextBytes(key);
    return key;
  }

  @SneakyThrows
  static String encrypt(SecretKey secretKey, byte[] plaintext) {
    val iv = generateIV();
    val cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
    cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv));
    return Base64.getEncoder().encodeToString(
        Bytes.concat(iv, cipher.doFinal(plaintext))
    );
  }

  @SneakyThrows
  static byte[] decrypt(SecretKey secretKey, String ciphertext) {
    // GCM CIPHERTEXT FORMAT [NONCE (12 bytes) | CIHERTEXT | TAG(16 bytes)]
    val ciphertextDecoded = Base64.getDecoder().decode(ciphertext);
    val iv = Arrays.copyOfRange(ciphertextDecoded, 0, GCM_IV_LENGTH);
    val ciphertextAndTag = Arrays.copyOfRange(ciphertextDecoded, GCM_IV_LENGTH, ciphertextDecoded.length);
    val cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
    cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv));
    return cipher.doFinal(ciphertextAndTag);
  }

}
