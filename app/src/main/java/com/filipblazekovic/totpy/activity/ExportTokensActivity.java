package com.filipblazekovic.totpy.activity;

import android.Manifest.permission;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.GetContent;
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import com.filipblazekovic.totpy.R;
import com.filipblazekovic.totpy.fragment.dialog.PasswordDefineDialog;
import com.filipblazekovic.totpy.model.internal.Token;
import com.filipblazekovic.totpy.utils.Common;
import com.filipblazekovic.totpy.utils.DataHandler;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import java.util.Collections;
import java.util.List;
import lombok.val;

public class ExportTokensActivity extends AppCompatActivity {

  private List<Token> tokens = Collections.emptyList();

  private final ActivityResultLauncher<ScanOptions> getScannedQRCodeContents = registerForActivityResult(
      new ScanContract(),
      result -> {
        val qrCode = result.getContents();
        if (qrCode != null) {
          DataHandler.shareLockedExport(ExportTokensActivity.this, tokens, qrCode);
        }
      });

  private final ActivityResultLauncher<String> getFileContents = registerForActivityResult(
      new GetContent(),
      uri -> {
        if (uri != null) {
          DataHandler.shareLockedExport(ExportTokensActivity.this, tokens, uri);
        }
      });

  final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new RequestPermission(), isGranted -> {
    if (isGranted) {
      getScannedQRCodeContents.launch(
          new ScanOptions().setBeepEnabled(false)
      );
    }
  });

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_export_tokens);

    val extras = getIntent().getExtras();
    if (extras != null) {
      val selectedTokenIds = extras.getIntegerArrayList(Common.SELECTED_TOKEN_IDS);
      if (selectedTokenIds != null && !selectedTokenIds.isEmpty()) {
        DataHandler.loadTokens(this, selectedTokenIds);
      }
    }

    final ImageButton scanPublicKeyQRCodeButton = findViewById(R.id.activity_export_tokens_scan_qr_code_button);
    final ImageButton loadPublicKeyFileButton = findViewById(R.id.activity_export_tokens_load_file_button);
    final ImageButton enterPasswordButton = findViewById(R.id.activity_export_tokens_enter_password_button);

    scanPublicKeyQRCodeButton.setOnClickListener(v -> {
      if (Common.shouldRequestCameraPermission(ExportTokensActivity.this)) {
        requestPermissionLauncher.launch(permission.CAMERA);
        return;
      }
      getScannedQRCodeContents.launch(new ScanOptions().setBeepEnabled(false));
    });

    loadPublicKeyFileButton.setOnClickListener(v -> getFileContents.launch(Common.JSON_FILE_TYPE));

    enterPasswordButton.setOnClickListener(v -> {
      final DialogFragment passwordDefineDialog = PasswordDefineDialog.newInstance();
      passwordDefineDialog.show(getSupportFragmentManager(), Common.DIALOG_LABEL);
    });
  }

  public void loadTokens(List<Token> tokens) {
    this.tokens = tokens;
  }

  public void processPasswordDefineDialogCancelButton() {
    Common.dismissDialog(ExportTokensActivity.this);
  }

  public void processPasswordDefineDialogApproveButton(String password, String passwordConfirmation) {
    Common.dismissDialog(ExportTokensActivity.this);
    DataHandler.shareLockedExport(this, tokens, password.toCharArray(), passwordConfirmation.toCharArray());
  }

}
