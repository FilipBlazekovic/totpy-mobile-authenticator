package com.filipblazekovic.totpy.service;

import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import androidx.annotation.Nullable;
import com.filipblazekovic.totpy.R;
import com.filipblazekovic.totpy.activity.LoginActivity;
import com.filipblazekovic.totpy.model.internal.RemoteWipeServiceAction;
import com.filipblazekovic.totpy.model.internal.RemoteWipeServiceStatus;
import com.filipblazekovic.totpy.utils.Common;
import com.filipblazekovic.totpy.utils.ConfigStore;
import com.filipblazekovic.totpy.task.RemoteWipeTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import lombok.val;

public class RemoteWipeService extends Service {

  private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
  private ScheduledFuture<?> executorController = null;

  private static final String NOTIFICATION_CHANNEL_ID = "RemoteWipeService";
  private static final String NOTIFICATION_CHANNEL_NAME = "Totpy - Remote wipe service";
  private static final int NOTIFICATION_ID = 1000;

  @Override
  public void onCreate() {
    super.onCreate();
    Log.d(Common.TAG, "RemoteWipeService onCreate() called!");

    val notification = createNotification();
    if (VERSION.SDK_INT >= VERSION_CODES.UPSIDE_DOWN_CAKE) {
      startForeground(NOTIFICATION_ID, notification, FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
    } else {
      startForeground(NOTIFICATION_ID, notification);
    }
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    // The service does not provide binding
    return null;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    super.onStartCommand(intent, flags, startId);
    Log.d(Common.TAG, "RemoteWipeService onStartCommand() called!");

    val action = intent == null
        ? null
        : intent.getAction();

    if (action != null && action.equals(RemoteWipeServiceAction.STOP.name())) {
      stopService();
    } else {
      startService();
    }

    return START_STICKY;
  }

  private void startService() {
    Log.d(Common.TAG, "RemoteWipeService startService() called!");
    ConfigStore.setRemoteWipeServiceStatus(this, RemoteWipeServiceStatus.RUNNING);
    if (executorController != null) {
      executorController.cancel(true);
    }
    executorController = executor.scheduleWithFixedDelay(
        new RemoteWipeTask(this),
        0,
        180,
        TimeUnit.SECONDS
    );
  }

  private void stopService() {
    Log.d(Common.TAG, "RemoteWipeService stopService() called!");
    ConfigStore.setRemoteWipeServiceStatus(this, RemoteWipeServiceStatus.STOPPED);
    stopForeground(STOP_FOREGROUND_REMOVE);
    stopSelf();
  }

  @Override
  public void onTaskRemoved(Intent rootIntent) {
    super.onTaskRemoved(rootIntent);
    Log.d(Common.TAG, "RemoteWipeService onTaskRemoved() called!");

    final AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
    alarmManager.set(
        AlarmManager.ELAPSED_REALTIME,
        SystemClock.elapsedRealtime() + 1000,
        PendingIntent.getService(
            this,
            1,
            new Intent(getApplicationContext(), RemoteWipeService.class)
                .setAction(RemoteWipeServiceAction.START.name()),
            PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        )
    );
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.d(Common.TAG, "RemoteWipeService onDestroy() called!");
    executor.shutdownNow();
  }

  private Notification createNotification() {
    final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    val channel = new NotificationChannel(
        NOTIFICATION_CHANNEL_ID,
        NOTIFICATION_CHANNEL_NAME,
        NotificationManager.IMPORTANCE_HIGH
    );

    channel.setDescription("Foreground service that listens for SMS messages containing a predefined phrase for remote wipe of OTP tokens");
    notificationManager.createNotificationChannel(channel);

    return new Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.core_icon_totpy_logo)
        .setContentTitle(getText(R.string.remote_wipe_service_notification_title))
        .setContentText(getText(R.string.remote_wipe_service_notification_message))
        .setContentIntent(
            PendingIntent.getActivity(
                this,
                0,
                new Intent(this, LoginActivity.class),
                PendingIntent.FLAG_IMMUTABLE
            )
        )
        .build();
  }

}
