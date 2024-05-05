package com.filipblazekovic.totpy.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.filipblazekovic.totpy.model.internal.Config;
import com.filipblazekovic.totpy.model.internal.RemoteWipeServiceStatus;
import java.time.LocalDateTime;
import lombok.val;

public class ConfigStore {

  private static final String SHARED_PREFERENCES_NAME = "totpy_config";

  private static Config config = null;

  private ConfigStore() {
  }

  public static Config get(Context context) {
    if (config == null) {

      val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

      config = new Config();
      config.setTokenCategoryVisible(
          sharedPreferences.getBoolean("tokenCategoryVisible", false)
      );
      config.setSmsRemoteWipeOn(
          sharedPreferences.getBoolean("smsRemoteWipeOn", false)
      );
      config.setRemoteWipeKeyphrase(
          sharedPreferences.getString("remoteWipeKeyphrase", null)
      );
      val lastExportDateTime = sharedPreferences.getString("lastExportDateTime", null);
      config.setLastExportDateTime(
          lastExportDateTime == null ? null : LocalDateTime.parse(lastExportDateTime)
      );
      val lastRemoteWipeDateTime = sharedPreferences.getString("lastRemoteWipeDateTime", null);
      config.setLastRemoteWipeDateTime(
          lastRemoteWipeDateTime == null ? null : LocalDateTime.parse(lastRemoteWipeDateTime)
      );
      val lastRemoteWipeTaskRunTimestamp = sharedPreferences.getLong("lastRemoteWipeTaskRunTimestamp", 0);
      config.setLastRemoteWipeTaskRunTimestamp(
          lastRemoteWipeTaskRunTimestamp == 0 ? null : lastRemoteWipeTaskRunTimestamp
      );
      config.setRemoteWipeServiceStatus(
          RemoteWipeServiceStatus.from(
              sharedPreferences.getString("remoteWipeServiceStatus",  null)
          )
      );
    }
    return config;
  }

  public static void setTokenCategoryVisible(Context context, boolean visible) {
    get(context);
    config.setTokenCategoryVisible(visible);
    save(context);
  }

  public static void setRemoteWipeServiceStatus(Context context, RemoteWipeServiceStatus status) {
    get(context);
    config.setRemoteWipeServiceStatus(status);
    save(context);
  }

  public static void setSmsRemoteWipeOff(Context context) {
    get(context);
    config.setSmsRemoteWipeOn(false);
    config.setRemoteWipeKeyphrase(null);
    save(context);
  }

  public static void setSmsRemoteWipeOn(Context context, String keyphrase) {
    get(context);
    config.setSmsRemoteWipeOn(true);
    config.setRemoteWipeKeyphrase(keyphrase);
    save(context);
  }

  public static void updateLastExportDateTime(Context context) {
    get(context);
    config.setLastExportDateTime(LocalDateTime.now());
    save(context);
  }

  public static void updateLastRemoteWipeDateTime(Context context) {
    get(context);
    config.setLastRemoteWipeDateTime(LocalDateTime.now());
    save(context);
  }

  public static void updateLastRemoteWipeTaskRunTimestamp(Context context, long timestamp) {
    get(context);
    config.setLastRemoteWipeTaskRunTimestamp(timestamp);
    save(context);
  }

  public static void save(Context context) {
    final SharedPreferences.Editor editor = context
        .getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        .edit();

    editor.putBoolean("tokenCategoryVisible", config.isTokenCategoryVisible());
    editor.putBoolean("smsRemoteWipeOn", config.isSmsRemoteWipeOn());

    if (config.getRemoteWipeKeyphrase() != null) {
      editor.putString("remoteWipeKeyphrase", config.getRemoteWipeKeyphrase());
    }
    if (config.getLastExportDateTime() != null) {
      editor.putString("lastExportDateTime", config.getLastExportDateTime().toString());
    }
    if (config.getLastExportDateTime() != null) {
      editor.putString("lastRemoteWipeDateTime", config.getLastRemoteWipeDateTime().toString());
    }
    if (config.getLastRemoteWipeTaskRunTimestamp() != null) {
      editor.putLong("lastRemoteWipeTaskRunTimestamp", config.getLastRemoteWipeTaskRunTimestamp());
    }

    editor.putString("remoteWipeServiceStatus", config.getRemoteWipeServiceStatus().name());
    editor.apply();
  }

}
