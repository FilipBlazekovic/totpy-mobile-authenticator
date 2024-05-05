package com.filipblazekovic.totpy.model.internal;

import com.filipblazekovic.totpy.model.shared.AsymmetricKeyAlgorithm;
import lombok.Value;

@Value
public class KeyPairGenerationResponse {

  KeyPairGenerationStatus status;

  Protection protection;

  AsymmetricKeyAlgorithm algorithm;

  public static KeyPairGenerationResponse error() {
    return new KeyPairGenerationResponse(
        KeyPairGenerationStatus.FAILURE,
        null,
        null
    );
  }

  public static KeyPairGenerationResponse success(Protection protection, AsymmetricKeyAlgorithm algorithm) {
    return new KeyPairGenerationResponse(
        KeyPairGenerationStatus.SUCCESS,
        protection,
        algorithm
    );
  }

}
