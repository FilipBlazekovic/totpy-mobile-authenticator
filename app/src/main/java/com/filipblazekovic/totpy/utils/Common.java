package com.filipblazekovic.totpy.utils;

import android.Manifest.permission;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import lombok.val;

public class Common {

  public static final String TAG = "totpy";

  public static final String DIALOG_LABEL = "totpy_dialog";

  public static final String SELECTED_TOKEN_ID = "selectedTokenId";
  public static final String SELECTED_TOKEN_IDS = "selectedTokenIds";

  public static final String JSON_FILE_TYPE = "application/json";

  private Common() {
  }

  public static boolean shouldRequestCameraPermission(Context context) {
    int status = ContextCompat.checkSelfPermission(context, permission.CAMERA);
    return status != PackageManager.PERMISSION_GRANTED;
  }

  public static boolean shouldRequestReadSmsPermission(Context context) {
    int status = ContextCompat.checkSelfPermission(context, permission.READ_SMS);
    return status != PackageManager.PERMISSION_GRANTED;
  }


  public static void dismissDialog(Context context) {
    val fragment = ((AppCompatActivity)context).getSupportFragmentManager().findFragmentByTag(DIALOG_LABEL);
    if (fragment != null) {
      final DialogFragment df = (DialogFragment) fragment;
      df.dismiss();
    }
  }

}
