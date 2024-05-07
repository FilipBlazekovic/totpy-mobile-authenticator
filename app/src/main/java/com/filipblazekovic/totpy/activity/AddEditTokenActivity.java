package com.filipblazekovic.totpy.activity;

import android.Manifest.permission;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission;
import androidx.appcompat.app.AppCompatActivity;
import com.filipblazekovic.totpy.R;
import com.filipblazekovic.totpy.exception.MandatoryParameter;
import com.filipblazekovic.totpy.exception.MissingMandatoryParameterException;
import com.filipblazekovic.totpy.fragment.dialog.EnterOtpAuthUriDialog;
import com.filipblazekovic.totpy.model.internal.Issuer;
import com.filipblazekovic.totpy.model.internal.Token;
import com.filipblazekovic.totpy.model.shared.Category;
import com.filipblazekovic.totpy.model.shared.HashAlgorithm;
import com.filipblazekovic.totpy.utils.Common;
import com.filipblazekovic.totpy.utils.DataHandler;
import com.filipblazekovic.totpy.utils.IconStore;
import com.filipblazekovic.totpy.utils.OTPAuth;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import java.util.Arrays;
import java.util.List;
import lombok.val;
import org.apache.commons.codec.binary.Base32;

public class AddEditTokenActivity extends AppCompatActivity {

  private List<Category> categories;
  private List<String> algorithms;
  private List<String> periods;
  private List<String> digits;

  private ImageView issuerImageView;
  private EditText issuerNameView;
  private EditText accountView;
  private Spinner categorySpinner;
  private Spinner algorithmSpinner;
  private Spinner digitsSpinner;
  private Spinner periodSpinner;
  private EditText secretView;

  private Token token = null;

  private final ActivityResultLauncher<ScanOptions> qrCodeScanner = registerForActivityResult(
      new ScanContract(),
      result -> {
        val qrCode = result.getContents();
        if (qrCode == null) {
          return;
        }
        try {
          populateGUI(OTPAuth.parseTotpUri(AddEditTokenActivity.this, qrCode));
        } catch (Exception e) {
          Toast
              .makeText(
                  AddEditTokenActivity.this,
                  e.getMessage() == null
                      ? getResources().getString(R.string.error_message_internal_error)
                      : e.getMessage(),
                  Toast.LENGTH_LONG
              )
              .show();
        }
      });

  final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new RequestPermission(), isGranted -> {
    if (isGranted) {
      qrCodeScanner.launch(
          new ScanOptions().setBeepEnabled(false)
      );
    }
  });

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_edit_token);

    categories = Arrays.asList(Category.values());

    algorithms = Arrays.asList(
        getResources().getStringArray(R.array.activity_add_edit_token_algorithm_field_values)
    );
    periods = Arrays.asList(
        getResources().getStringArray(R.array.activity_add_edit_token_period_field_values)
    );
    digits = Arrays.asList(
        getResources().getStringArray(R.array.activity_add_edit_token_digits_field_values)
    );

    issuerImageView = findViewById(R.id.activity_add_edit_token_issuer_image);
    issuerNameView = findViewById(R.id.activity_add_edit_token_issuer_input);
    accountView = findViewById(R.id.activity_add_edit_token_account_input);
    categorySpinner = findViewById(R.id.activity_add_edit_token_category_input);
    algorithmSpinner = findViewById(R.id.activity_add_edit_token_algorithm_input);
    digitsSpinner = findViewById(R.id.activity_add_edit_token_digits_input);
    periodSpinner = findViewById(R.id.activity_add_edit_token_period_input);
    secretView = findViewById(R.id.activity_add_edit_token_secret_input);

    categorySpinner.setAdapter(
        new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Category.values())
    );

    final ImageButton scanQRCodeButton = findViewById(R.id.activity_add_edit_token_scan_qr_code_button);
    final ImageButton loadOtpAuthUriButton = findViewById(R.id.activity_add_edit_token_load_uri_button);
    final Button cancelButton = findViewById(R.id.activity_add_edit_token_cancel_button);
    final Button saveButton = findViewById(R.id.activity_add_edit_token_save_button);

    val extras = getIntent().getExtras();
    if (extras != null) {
      val selectedTokenId = extras.getInt(Common.SELECTED_TOKEN_ID);
      if (selectedTokenId != 0) {
        DataHandler.loadToken(this, selectedTokenId);
      }
    }

    scanQRCodeButton.setOnClickListener(v -> qrCodeScanner.launch(
        new ScanOptions().setBeepEnabled(false))
    );

    scanQRCodeButton.setOnClickListener(v -> {
      if (Common.shouldRequestCameraPermission(AddEditTokenActivity.this)) {
        requestPermissionLauncher.launch(permission.CAMERA);
        return;
      }
      qrCodeScanner.launch(
          new ScanOptions().setBeepEnabled(false)
      );
    });

    loadOtpAuthUriButton.setOnClickListener(v -> {
      val loadOtpAuthUriDialog = EnterOtpAuthUriDialog.newInstance();
      loadOtpAuthUriDialog.show(getSupportFragmentManager(), Common.DIALOG_LABEL);
    });

    cancelButton.setOnClickListener(v -> startActivity(
        new Intent(AddEditTokenActivity.this, TokensActivity.class))
    );

    saveButton.setOnClickListener(v -> {
      try {
        val collectedToken = collectValues();
        if (token == null) {
          DataHandler.insertToken(AddEditTokenActivity.this, collectedToken);
        } else {
          DataHandler.updateToken(AddEditTokenActivity.this, token.merge(collectedToken));
        }
        startActivity(new Intent(this, TokensActivity.class));
      } catch (Exception e) {
        Toast
            .makeText(
                AddEditTokenActivity.this,
                e.getMessage() == null
                    ? getResources().getString(R.string.error_message_internal_error)
                    : e.getMessage(),
                Toast.LENGTH_LONG
            )
            .show();
      }
    });
  }

  public void loadToken(Token token) {
    this.token = token;
    populateGUI(token);
  }

  private void populateGUI(Token token) {
    if (token == null) {
      categorySpinner.setSelection(categories.indexOf(Category.DEFAULT));
      return;
    }
    issuerImageView.setImageDrawable(
        IconStore.get(this, token.getIssuer().getIcon())
    );
    issuerNameView.setText(
        token.getIssuer().getName() == null
            ? ""
            : token.getIssuer().getName()
    );
    accountView.setText(token.getAccount());
    categorySpinner.setSelection(categories.indexOf(token.getCategory()));
    algorithmSpinner.setSelection(algorithms.indexOf(token.getAlgorithm().name()));
    digitsSpinner.setSelection(digits.indexOf(String.valueOf(token.getDigits())));
    periodSpinner.setSelection(periods.indexOf(String.valueOf(token.getPeriod())));
    secretView.setText(new Base32().encodeAsString(token.getSecret()));
  }

  private Token collectValues() throws MissingMandatoryParameterException {

    val issuer = issuerNameView.getText().toString();
    val account = accountView.getText().toString();
    val secret = secretView.getText().toString();

    if (account.trim().isEmpty()) {
      throw new MissingMandatoryParameterException(this, MandatoryParameter.ACCOUNT);
    }

    if (secret.trim().isEmpty()) {
      throw new MissingMandatoryParameterException(this, MandatoryParameter.SECRET);
    }

    return Token.from(
        Issuer.from(issuer),
        account,
        categories.get(categorySpinner.getSelectedItemPosition()),
        HashAlgorithm.from(
            algorithms.get(algorithmSpinner.getSelectedItemPosition())
        ),
        Integer.parseInt(
            digits.get(digitsSpinner.getSelectedItemPosition())
        ),
        Integer.parseInt(
            periods.get(periodSpinner.getSelectedItemPosition())
        ),
        new Base32().decode(secret)
    );
  }

  public void processEnterOtpAuthUriDialogCancelButton() {
    Common.dismissDialog(AddEditTokenActivity.this);
  }

  public void processEnterOtpAuthUriDialogApproveButton(String enteredOtpAuthUri) {
    Common.dismissDialog(AddEditTokenActivity.this);
    try {
      populateGUI(
          OTPAuth.parseTotpUri(AddEditTokenActivity.this, enteredOtpAuthUri.trim())
      );
    } catch (Exception e) {
      Toast
          .makeText(
              AddEditTokenActivity.this,
              e.getMessage() == null
                  ? getResources().getString(R.string.error_message_internal_error)
                  : e.getMessage(),
              Toast.LENGTH_LONG
          )
          .show();
    }
  }

}
