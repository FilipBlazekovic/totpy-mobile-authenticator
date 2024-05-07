package com.filipblazekovic.totpy.model.inout;

import com.filipblazekovic.totpy.model.shared.AsymmetricKeyAlgorithm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExportLockingPublicKey {

  String publicKeyPem;

  AsymmetricKeyAlgorithm keyAlgorithm;

}
