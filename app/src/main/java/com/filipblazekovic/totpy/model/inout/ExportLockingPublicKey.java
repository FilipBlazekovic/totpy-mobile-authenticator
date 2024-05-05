package com.filipblazekovic.totpy.model.inout;

import com.filipblazekovic.totpy.model.shared.AsymmetricKeyAlgorithm;
import lombok.Value;

@Value
public class ExportLockingPublicKey {

  String publicKeyPem;

  AsymmetricKeyAlgorithm keyAlgorithm;

}
