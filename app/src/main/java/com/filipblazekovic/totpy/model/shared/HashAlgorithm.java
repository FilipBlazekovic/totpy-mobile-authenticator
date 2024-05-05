package com.filipblazekovic.totpy.model.shared;

public enum HashAlgorithm {

  SHA1,
  SHA256,
  SHA512;

  public static HashAlgorithm from(String algorithm) {
    try {
      return HashAlgorithm.valueOf(algorithm.toUpperCase());
    } catch (Exception e) {
      return HashAlgorithm.SHA1;
    }
  }

  public String getHMACAlgorithm() {
    switch (this) {
      case SHA1:
        return "HmacSHA1";
      case SHA256:
        return "HmacSHA256";
      default:
        return "HmacSHA512";
    }
  }

}
