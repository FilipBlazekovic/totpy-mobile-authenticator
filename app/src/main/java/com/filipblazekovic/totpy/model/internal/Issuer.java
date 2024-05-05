package com.filipblazekovic.totpy.model.internal;

import com.filipblazekovic.totpy.model.shared.IssuerIcon;
import lombok.Value;

@Value
public class Issuer {

  IssuerIcon icon;

  String name;

  public static Issuer from(String issuer) {
    return new Issuer(
        IssuerIcon.from(issuer),
        issuer == null || issuer.trim().isEmpty() ? null : issuer
    );
  }

}
