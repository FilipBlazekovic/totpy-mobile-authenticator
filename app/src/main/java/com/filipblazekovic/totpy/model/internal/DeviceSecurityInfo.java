package com.filipblazekovic.totpy.model.internal;

import com.filipblazekovic.totpy.model.shared.AsymmetricKeyAlgorithm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceSecurityInfo {

  private Protection keyProtection;

  private AsymmetricKeyAlgorithm keyAlgorithm;

  private boolean deviceRooted;

}
