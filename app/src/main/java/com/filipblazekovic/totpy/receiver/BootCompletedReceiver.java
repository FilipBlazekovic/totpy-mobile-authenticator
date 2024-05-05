package com.filipblazekovic.totpy.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import com.filipblazekovic.totpy.service.RemoteWipeService;
import com.filipblazekovic.totpy.utils.ConfigStore;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.val;

public class BootCompletedReceiver extends BroadcastReceiver {

  private final ExecutorService executor = Executors.newSingleThreadExecutor();
  private final Handler handler = new Handler(Looper.getMainLooper());

  @Override
  public void onReceive(Context context, Intent intent) {
    if (!Objects.equals(intent.getAction(), Intent.ACTION_BOOT_COMPLETED)) {
      return;
    }
    executor.execute(() -> {
      val config = ConfigStore.get(context);
      if (config.isSmsRemoteWipeOn()) {
        handler.post(() -> context.startForegroundService(
            new Intent(context, RemoteWipeService.class)
        ));
      }
    });
  }

}
