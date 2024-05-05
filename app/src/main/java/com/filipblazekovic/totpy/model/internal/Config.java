package com.filipblazekovic.totpy.model.internal;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Config {

  boolean tokenCategoryVisible;

  boolean smsRemoteWipeOn;

  String remoteWipeKeyphrase;

  LocalDateTime lastExportDateTime;

  LocalDateTime lastRemoteWipeDateTime;

  Long lastRemoteWipeTaskRunTimestamp;

  RemoteWipeServiceStatus remoteWipeServiceStatus;

}
