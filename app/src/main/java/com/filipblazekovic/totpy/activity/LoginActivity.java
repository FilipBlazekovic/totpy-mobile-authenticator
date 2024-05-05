package com.filipblazekovic.totpy.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.filipblazekovic.totpy.R;
import com.filipblazekovic.totpy.crypto.AuthenticationHandler;
import com.filipblazekovic.totpy.crypto.CryptoHandler;
import com.filipblazekovic.totpy.model.internal.KeyPairGenerationStatus;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

  private final ExecutorService executor = Executors.newSingleThreadExecutor();
  private final Handler handler = new Handler(Looper.getMainLooper());

  private ImageView logoImage;
  private LinearLayout progressPanel;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    logoImage = findViewById(R.id.logo_view);
    progressPanel = findViewById(R.id.progess_panel);
  }

  @Override
  protected void onResume() {
    super.onResume();

    executor.execute(() -> {
      switch (AuthenticationHandler.getScreenLockEnabled(getApplicationContext())) {
        case ENABLED:
          if (CryptoHandler.authenticatorKeysExist()) {
            handler.post(() -> AuthenticationHandler.login(LoginActivity.this));
            return;
          }
          initialize();
          return;
        case DISABLED:
          handler.post(() -> Toast
              .makeText(
                  LoginActivity.this,
                  getResources().getString(R.string.activity_login_error_message_enable_screen_lock),
                  Toast.LENGTH_LONG
              )
              .show());
          return;
        case SECURITY_UPDATE_REQUIRED:
          handler.post(() -> Toast
              .makeText(
                  LoginActivity.this,
                  getResources().getString(R.string.activity_login_error_message_security_update_required),
                  Toast.LENGTH_LONG
              )
              .show());
          return;
        default:
          handler.post(() -> Toast
              .makeText(
                  LoginActivity.this,
                  getResources().getString(R.string.activity_login_error_message_device_not_supported),
                  Toast.LENGTH_LONG
              )
              .show());
      }
    });
  }

  private void initialize() {
    handler.post(() -> {
      logoImage.setVisibility(View.GONE);
      progressPanel.setVisibility(View.VISIBLE);
    });
    try {
      if (CryptoHandler.generateAuthenticatorKeys().getStatus() == KeyPairGenerationStatus.SUCCESS) {
        handler.post(() -> {
          logoImage.setVisibility(View.VISIBLE);
          progressPanel.setVisibility(View.GONE);
          AuthenticationHandler.login(LoginActivity.this);
        });
        return;
      }
      handler.post(LoginActivity.this::handleError);
    } catch (Exception e) {
      handler.post(LoginActivity.this::handleError);
    }
  }

  private void handleError() {
    logoImage.setVisibility(View.VISIBLE);
    progressPanel.setVisibility(View.GONE);
    Toast
        .makeText(
            LoginActivity.this,
            getResources().getString(R.string.activity_login_error_message_device_not_supported),
            Toast.LENGTH_LONG
        )
        .show();
  }

}
