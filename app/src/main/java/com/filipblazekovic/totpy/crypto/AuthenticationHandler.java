package com.filipblazekovic.totpy.crypto;

import static android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.biometrics.BiometricManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager.Authenticators;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import com.filipblazekovic.totpy.R;
import com.filipblazekovic.totpy.activity.TokensActivity;
import com.filipblazekovic.totpy.model.internal.ScreenLockStatus;
import com.filipblazekovic.totpy.utils.Common;
import lombok.val;

public final class AuthenticationHandler {

  private static BiometricPrompt biometricPrompt;

  private AuthenticationHandler() {
  }

  public static ScreenLockStatus getScreenLockEnabled(Context context) {
    val keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
    if (keyguardManager.isDeviceSecure()) {
      return ScreenLockStatus.ENABLED;
    }

    val biometricManager = (BiometricManager) context.getSystemService(Context.BIOMETRIC_SERVICE);
    if (VERSION.SDK_INT >= VERSION_CODES.R) {
      switch (biometricManager.canAuthenticate(BIOMETRIC_STRONG | DEVICE_CREDENTIAL)) {
        case BiometricManager.BIOMETRIC_SUCCESS:
          return ScreenLockStatus.ENABLED;
        case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
          return ScreenLockStatus.DISABLED;
        case BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED:
          return ScreenLockStatus.SECURITY_UPDATE_REQUIRED;
        default:
          return ScreenLockStatus.DEVICE_NOT_SUPPORTED;
      }
    }

    switch (biometricManager.canAuthenticate()) {
      case BiometricManager.BIOMETRIC_SUCCESS:
        return ScreenLockStatus.ENABLED;
      case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
        return ScreenLockStatus.DISABLED;
      default:
        return ScreenLockStatus.DEVICE_NOT_SUPPORTED;
    }
  }

  public static void login(Context context) {
    try {

      val executor = ContextCompat.getMainExecutor(context);
      val promptInfo = new BiometricPrompt.PromptInfo.Builder()
          .setTitle(context.getResources().getString(R.string.login_prompt_title))
          .setSubtitle(context.getResources().getString(R.string.login_prompt_subtitle))
          .setDescription(context.getResources().getString(R.string.login_prompt_message))
          .setAllowedAuthenticators(Authenticators.BIOMETRIC_STRONG | Authenticators.DEVICE_CREDENTIAL)
          .build();

      val authCallback = new BiometricPrompt.AuthenticationCallback() {

        @Override
        public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
          super.onAuthenticationError(errorCode, errString);
          Log.d(Common.TAG, "Authentication error: " + errString);
          biometricPrompt.cancelAuthentication();
        }

        @Override
        public void onAuthenticationFailed() {
          Log.d(Common.TAG, "Authentication failed");
          super.onAuthenticationFailed();
        }

        @Override
        public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
          super.onAuthenticationSucceeded(result);
          Log.d(Common.TAG, "Authentication succeeded");
          context.startActivity(
              new Intent(context, TokensActivity.class)
          );
        }
      };

      biometricPrompt = new BiometricPrompt((FragmentActivity) context, executor, authCallback);
      biometricPrompt.authenticate(promptInfo);

    } catch (Exception e) {
      Log.e(Common.TAG, "Exception thrown while constructing authentication dialog", e);
    }
  }

}
