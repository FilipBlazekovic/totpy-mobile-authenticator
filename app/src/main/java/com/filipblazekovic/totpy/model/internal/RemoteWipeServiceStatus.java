package com.filipblazekovic.totpy.model.internal;

public enum RemoteWipeServiceStatus {

  RUNNING,
  STOPPED;

  public static RemoteWipeServiceStatus from(String value) {
    return value == null ? STOPPED : RemoteWipeServiceStatus.valueOf(value);
  }

}
