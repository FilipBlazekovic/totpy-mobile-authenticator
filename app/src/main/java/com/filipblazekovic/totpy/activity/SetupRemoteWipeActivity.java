package com.filipblazekovic.totpy.activity;

import android.Manifest.permission;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission;
import androidx.appcompat.app.AppCompatActivity;
import com.filipblazekovic.totpy.R;
import com.filipblazekovic.totpy.crypto.PasswordHandler;
import com.filipblazekovic.totpy.model.internal.RemoteWipeServiceAction;
import com.filipblazekovic.totpy.service.RemoteWipeService;
import com.filipblazekovic.totpy.model.internal.RemoteWipeServiceStatus;
import com.filipblazekovic.totpy.utils.Common;
import com.filipblazekovic.totpy.utils.ConfigStore;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import lombok.val;

public class SetupRemoteWipeActivity extends AppCompatActivity {

  private SwitchMaterial remoteWipeOnSwitch;
  private TextInputLayout remoteWipeKeyphraseView;

  final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new RequestPermission(), isGranted -> {
    if (isGranted) {
      setupRemoteWipe();
    }
  });

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_setup_remote_wipe);

    final Button cancelButton = findViewById(R.id.activity_setup_remote_wipe_cancel_button);
    final Button approveButton = findViewById(R.id.activity_setup_remote_wipe_approve_button);

    remoteWipeOnSwitch = findViewById(R.id.activity_setup_remote_wipe_sms_on_switch);
    remoteWipeKeyphraseView = findViewById(R.id.activity_setup_remote_wipe_keyphrase_field);

    val config = ConfigStore.get(this);

    remoteWipeOnSwitch.setChecked(config.isSmsRemoteWipeOn());

    val keyphrase = config.getRemoteWipeKeyphrase();
    if (keyphrase != null) {
      remoteWipeKeyphraseView.getEditText().setText(keyphrase);
    }

    cancelButton.setOnClickListener(v -> startActivity(
        new Intent(SetupRemoteWipeActivity.this, TokensActivity.class))
    );

    approveButton.setOnClickListener(v -> {
      if (!remoteWipeOnSwitch.isChecked()) {
        cancelRemoteWipe();
        return;
      }
      if (Common.shouldRequestReadSmsPermission(SetupRemoteWipeActivity.this)) {
        requestPermissionLauncher.launch(permission.READ_SMS);
        return;
      }
      setupRemoteWipe();
    });
  }

  private void cancelRemoteWipe() {
    ConfigStore.setSmsRemoteWipeOff(SetupRemoteWipeActivity.this);

    val remoteWipeServiceStatus = ConfigStore
        .get(SetupRemoteWipeActivity.this)
        .getRemoteWipeServiceStatus();

    if (remoteWipeServiceStatus == RemoteWipeServiceStatus.STOPPED) {
      return;
    }

    startForegroundService(
        new Intent(this, RemoteWipeService.class)
            .setAction(RemoteWipeServiceAction.STOP.name())
    );

    startActivity(new Intent(this, TokensActivity.class));
  }

  private void setupRemoteWipe() {
    val remoteWipePhrase = remoteWipeKeyphraseView.getEditText().getText().toString();
    try {
      PasswordHandler.validate(SetupRemoteWipeActivity.this, remoteWipePhrase);
    } catch (Exception e) {
      Toast
          .makeText(
              SetupRemoteWipeActivity.this,
              e.getMessage() == null
                  ? getResources().getString(R.string.error_message_internal_error)
                  : e.getMessage(),
              Toast.LENGTH_LONG
          )
          .show();
      return;
    }

    ConfigStore.setSmsRemoteWipeOn(SetupRemoteWipeActivity.this, remoteWipePhrase);
    startForegroundService(
        new Intent(this, RemoteWipeService.class)
            .setAction(RemoteWipeServiceAction.START.name())
    );
    startActivity(new Intent(this, TokensActivity.class));
  }

}
