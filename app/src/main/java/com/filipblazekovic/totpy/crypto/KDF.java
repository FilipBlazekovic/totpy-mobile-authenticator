package com.filipblazekovic.totpy.crypto;

import com.google.crypto.tink.subtle.Hkdf;
import java.security.PublicKey;
import java.security.SecureRandom;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.SneakyThrows;
import lombok.val;

public final class KDF {

  private static final String KEY_ALGORITHM = "AES";
  private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256";
  private static final String HKDF_ALGORITHM = "HmacSHA512";

  private static final int SALT_LENGTH = 32;
  private static final int DESIRED_KEY_LENGTH = 32;
  private static final int ITERATION_COUNT = 100000;

  private KDF() {
  }

  static byte[] generateSalt() {
    val salt = new byte[SALT_LENGTH];
    val rng = new SecureRandom();
    rng.nextBytes(salt);
    return salt;
  }

  @SneakyThrows
  static SecretKey pbkdf2(char[] password, byte[] salt) {
    return new SecretKeySpec(
        SecretKeyFactory
            .getInstance(PBKDF2_ALGORITHM)
            .generateSecret(
                new PBEKeySpec(
                    password,
                    salt,
                    ITERATION_COUNT,
                    DESIRED_KEY_LENGTH * 8
                )
            )
            .getEncoded(),
        KEY_ALGORITHM
    );
  }

  @SneakyThrows
  static SecretKey hkdf(byte[] sharedSecret) {
    return new SecretKeySpec(
        Hkdf.computeHkdf(
            HKDF_ALGORITHM,
            sharedSecret,
            new byte[0],
            new byte[0],
            DESIRED_KEY_LENGTH
        ),
        KEY_ALGORITHM
    );
  }

  // Use ephemoral public key here (on both sides of ECDH),
  // the one who's private key was used to encrypt the data
  @SneakyThrows
  static SecretKey hkdf(PublicKey ephemoralPublicKey, byte[] sharedSecret) {
    return new SecretKeySpec(
        Hkdf.computeEciesHkdfSymmetricKey(
            ephemoralPublicKey.getEncoded(),
            sharedSecret,
            HKDF_ALGORITHM,
            new byte[0],
            new byte[0],
            DESIRED_KEY_LENGTH
        ),
        KEY_ALGORITHM
    );
  }

}

