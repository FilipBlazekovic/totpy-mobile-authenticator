package com.filipblazekovic.totpy.task;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Telephony;
import android.util.Log;
import com.filipblazekovic.totpy.utils.Common;
import com.filipblazekovic.totpy.utils.ConfigStore;
import com.filipblazekovic.totpy.utils.DataHandler;
import java.util.Calendar;
import lombok.val;

public class RemoteWipeTask implements Runnable {

  public static String REMOTE_WIPE_BROADCAST_ACTION = "com.filipblazekovic.totpy.RemoteWipeTask";

  private final Context context;

  public RemoteWipeTask(Context context) {
    this.context = context;
  }

  @Override
  public void run() {
    val config = ConfigStore.get(context);
    val lastRemoteWipeTaskRunTimestamp = config.getLastRemoteWipeTaskRunTimestamp();

    val cutoffTime = (lastRemoteWipeTaskRunTimestamp != null)
        ? lastRemoteWipeTaskRunTimestamp
        : Calendar.getInstance().getTimeInMillis();

    searchForRemoteWipeKeyword(
        cutoffTime,
        config.getRemoteWipeKeyphrase()
    );
  }

  private void searchForRemoteWipeKeyword(long cutoffTimestamp, String keyphrase) {
    final String selection = "date >= ?";
    final String[] selectionArgument = {String.valueOf(cutoffTimestamp)};

    try (
        val cursor = context.getContentResolver().query(
            Uri.parse("content://sms/inbox"),
            null,
            selection,
            selectionArgument,
            null
        )
    ) {

      if (cursor != null && cursor.moveToFirst()) {
        do {
          val smsBody = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY));
          if (smsBody != null && smsBody.contains(keyphrase)) {
            ConfigStore.updateLastRemoteWipeDateTime(context);
            DataHandler.deleteTokens(context);
            context.sendBroadcast(
                new Intent(REMOTE_WIPE_BROADCAST_ACTION)
            );
            break;
          }
        } while (cursor.moveToNext());
      }

      ConfigStore.updateLastRemoteWipeTaskRunTimestamp(
          context,
          Calendar.getInstance().getTimeInMillis()
      );

    } catch (Exception e) {
      Log.e(Common.TAG, "Exception thrown while searching for remote wipe keyword in SMS messages", e);
    }
  }

}
