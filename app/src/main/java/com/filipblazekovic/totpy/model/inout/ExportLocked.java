package com.filipblazekovic.totpy.model.inout;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Base64;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(Include.NON_EMPTY)
public class ExportLocked {

  private ExportLockingMethod exportLockingMethod;

  private String ephemeralPublicKeyPem;

  private String salt;

  private String keyLocked;

  private String exportLockedBase64;

  public static ExportLocked passwordLockedExport(byte[] salt, String exportLockedBase64) {
    return new ExportLocked(
        ExportLockingMethod.PASSWORD,
        null,
        Base64.getEncoder().encodeToString(salt),
        null,
        exportLockedBase64
    );
  }

  public static ExportLocked rsaKeyLockedExport(String keyLocked, String exportLockedBase64) {
    return new ExportLocked(
        ExportLockingMethod.PUBLIC_KEY,
        null,
        null,
        keyLocked,
        exportLockedBase64
    );
  }

  public static ExportLocked ecKeyLockedExport(String ephemoralPublicKeyPem, String exportLockedBase64) {
    return new ExportLocked(
        ExportLockingMethod.PUBLIC_KEY,
        ephemoralPublicKeyPem,
        null,
        null,
        exportLockedBase64
    );
  }

}
