package com.filipblazekovic.totpy.crypto;

import com.filipblazekovic.totpy.model.shared.AsymmetricKeyAlgorithm;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import lombok.SneakyThrows;
import lombok.val;

public final class PEM {

  private PEM() {
  }

  static String encode(PublicKey publicKey) {
    val base64Encoded = Base64
        .getEncoder()
        .encodeToString(publicKey.getEncoded())
        .replaceAll("(.{64})", "$1\n");
    return
        "-----BEGIN PUBLIC KEY-----\n"
            + ((base64Encoded.endsWith("\n")) ? base64Encoded : (base64Encoded + "\n"))
            + "-----END PUBLIC KEY-----";
  }

  @SneakyThrows
  static PublicKey decode(AsymmetricKeyAlgorithm algorithm, String publicKeyPem) {
    return KeyFactory
        .getInstance(algorithm.name())
        .generatePublic(
            new X509EncodedKeySpec(
                Base64.getDecoder().decode(
                    publicKeyPem
                        .replace("-----BEGIN PUBLIC KEY-----", "")
                        .replace("-----END PUBLIC KEY-----", "")
                        .replaceAll("\\n", "")
                )
            )
        );
  }
}
