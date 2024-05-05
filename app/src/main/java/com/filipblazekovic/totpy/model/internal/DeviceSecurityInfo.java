package com.filipblazekovic.totpy.model.internal;

import com.filipblazekovic.totpy.model.shared.AsymmetricKeyAlgorithm;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
public class DeviceSecurityInfo {

  Protection keyProtection;

  AsymmetricKeyAlgorithm keyAlgorithm;

  boolean deviceRooted;

}
