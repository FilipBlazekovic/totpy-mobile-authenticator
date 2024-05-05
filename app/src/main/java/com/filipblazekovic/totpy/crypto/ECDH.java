package com.filipblazekovic.totpy.crypto;

import static com.filipblazekovic.totpy.crypto.CryptoHandler.SECURE_HARDWARE_KEYSTORE_PROVIDER;

import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.KeyAgreement;
import lombok.SneakyThrows;
import lombok.Synchronized;
import lombok.val;

public final class ECDH {

  private ECDH() {
  }

  @SneakyThrows
  @Synchronized
  public static byte[] derive(PrivateKey privateKey, PublicKey ephemoralPeerPublicKey) {
    val keyAgreement = KeyAgreement.getInstance("ECDH", SECURE_HARDWARE_KEYSTORE_PROVIDER);
    keyAgreement.init(privateKey);
    keyAgreement.doPhase(ephemoralPeerPublicKey, true);
    return keyAgreement.generateSecret();
  }

}
