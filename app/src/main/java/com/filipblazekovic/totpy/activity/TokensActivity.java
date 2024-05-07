package com.filipblazekovic.totpy.activity;

import android.Manifest.permission;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.GetContent;
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.filipblazekovic.totpy.R;
import com.filipblazekovic.totpy.adapter.TokensAdapter;
import com.filipblazekovic.totpy.fragment.dialog.AboutDialog;
import com.filipblazekovic.totpy.fragment.panel.DeletePanel;
import com.filipblazekovic.totpy.fragment.panel.ExportPanel;
import com.filipblazekovic.totpy.fragment.panel.ScanQRCodePanel;
import com.filipblazekovic.totpy.fragment.panel.SearchPanel;
import com.filipblazekovic.totpy.model.inout.ExportLocked;
import com.filipblazekovic.totpy.model.internal.Token;
import com.filipblazekovic.totpy.model.internal.FragmentType;
import com.filipblazekovic.totpy.utils.Common;
import com.filipblazekovic.totpy.utils.ConfigStore;
import com.filipblazekovic.totpy.utils.DataHandler;
import com.filipblazekovic.totpy.utils.OTPAuth;
import com.filipblazekovic.totpy.task.OTPRecalculationTask;
import com.filipblazekovic.totpy.task.RemoteWipeTask;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.val;

public class TokensActivity extends AppCompatActivity {

  private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

  private ScheduledFuture<?> otpRecalculationTaskController = null;

  private TokensAdapter adapter;

  private RecyclerView recyclerView;

  private SearchPanel searchPanel;

  private ScanQRCodePanel scanQRCodePanel;

  private DeletePanel deletePanel;

  private ExportPanel exportPanel;

  private FragmentType currentFragment = FragmentType.SCAN_QR_CODE;

  private MenuItem tokenCategoryMenuItem;

  private boolean tokenCategoryVisible = false;

  private final BroadcastReceiver remoteWipeSignalReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      startActivity(new Intent(TokensActivity.this, LoginActivity.class));
    }
  };

  private final ActivityResultLauncher<ScanOptions> getScannedQRCodeContents = registerForActivityResult(
      new ScanContract(),
      result -> {
        val qrCode = result.getContents();
        if (qrCode == null) {
          return;
        }
        try {
          DataHandler.insertToken(TokensActivity.this, OTPAuth.parseTotpUri(TokensActivity.this, qrCode));
          DataHandler.loadTokens(TokensActivity.this);
        } catch (Exception e) {
          Toast
              .makeText(
                  TokensActivity.this,
                  e.getMessage() == null
                      ? getResources().getString(R.string.error_message_internal_error)
                      : e.getMessage(),
                  Toast.LENGTH_LONG
              )
              .show();
        }
      });

  private final ActivityResultLauncher<String> getImportFileContents = registerForActivityResult(
      new GetContent(),
      uri -> {
        if (uri == null) {
          return;
        }
        DataHandler.loadLockedExport(TokensActivity.this, uri);
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
    setContentView(R.layout.activity_tokens);
    setSupportActionBar(findViewById(R.id.toolbar));

    val intent = getIntent();
    restoreState(savedInstanceState);
    if (intent != null) {
      restoreState(intent.getExtras());
    }

    searchPanel = new SearchPanel();
    scanQRCodePanel = new ScanQRCodePanel();
    deletePanel = new DeletePanel();
    exportPanel = new ExportPanel();

    adapter = new TokensAdapter(this, new ArrayList<>());
    recyclerView = findViewById(R.id.tokens_view);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setAdapter(adapter);
    recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    recyclerView.setItemAnimator(null);

    switch (currentFragment) {
      case DELETE:
        showDeleteAuthenticatorsFragment();
        break;
      case EXPORT:
        showExportAuthenticatorsFragment();
        break;
      default:
        showScanQRCodeFragment();
    }

    ContextCompat.registerReceiver(
        this,
        remoteWipeSignalReceiver,
        new IntentFilter(RemoteWipeTask.REMOTE_WIPE_BROADCAST_ACTION),
        ContextCompat.RECEIVER_NOT_EXPORTED
    );

    DataHandler.loadConfig(this);
    DataHandler.loadTokens(this);
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (otpRecalculationTaskController != null) {
      otpRecalculationTaskController.cancel(true);
    }
    otpRecalculationTaskController = executor.scheduleWithFixedDelay(
        new OTPRecalculationTask(adapter),
        0,
        1,
        TimeUnit.SECONDS
    );
  }

  @Override
  protected void onPause() {
    super.onPause();
    otpRecalculationTaskController.cancel(true);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    executor.shutdownNow();
    adapter.getTokens().forEach(Token::clear);
    unregisterReceiver(remoteWipeSignalReceiver);
  }

  @Override
  protected void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString("currentFragment", currentFragment.name());
  }

  private void restoreState(Bundle bundle) {
    if (bundle == null) {
      return;
    }
    val temp = bundle.getString("currentFragment");
    if (temp != null) {
      currentFragment = FragmentType.valueOf(temp);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main_menu, menu);
    tokenCategoryMenuItem = menu.findItem(R.id.menu_button_show_token_category);
    tokenCategoryMenuItem.setChecked(tokenCategoryVisible);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.menu_button_search) {
      toggleSearchFragmentVisibility();
      return true;
    }
    if (item.getItemId() == R.id.menu_button_add_token) {
      startActivity(new Intent(this, AddEditTokenActivity.class));
      return true;
    }
    if (item.getItemId() == R.id.menu_button_import) {
      getImportFileContents.launch(Common.JSON_FILE_TYPE);
      return true;
    }
    if (item.getItemId() == R.id.menu_button_export) {
      toggleSelectCheckboxVisibility(true);
      showExportAuthenticatorsFragment();
      return true;
    }
    if (item.getItemId() == R.id.menu_button_delete) {
      toggleSelectCheckboxVisibility(true);
      showDeleteAuthenticatorsFragment();
      return true;
    }
    if (item.getItemId() == R.id.menu_button_show_public_key) {
      DataHandler.loadAndShowExportLockingPublicKey(this);
      return true;
    }
    if (item.getItemId() == R.id.menu_button_setup_remote_wipe) {
      startActivity(new Intent(this, SetupRemoteWipeActivity.class));
      return true;
    }
    if (item.getItemId() == R.id.menu_button_show_token_category) {
      if (item.isChecked()) {
        item.setChecked(false);
        ConfigStore.setTokenCategoryVisible(this, false);
      } else {
        item.setChecked(true);
        ConfigStore.setTokenCategoryVisible(this, true);
      }
      toggleTokenCategoryVisibility();
      return true;
    }
    if (item.getItemId() == R.id.menu_button_security_info) {
      DataHandler.loadAndShowSecurityInfo(this);
      return true;
    }
    if (item.getItemId() == R.id.menu_button_about) {
      AboutDialog
          .newInstance()
          .show(getSupportFragmentManager(), Common.DIALOG_LABEL);
      return true;
    }
    if (item.getItemId() == R.id.menu_button_logout) {
      adapter.getTokens().forEach(Token::clear);
      startActivity(new Intent(this, LoginActivity.class));
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  public void showScanQRCodeFragment() {
    currentFragment = FragmentType.SCAN_QR_CODE;

    val ft = getSupportFragmentManager().beginTransaction();
    ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
    if (scanQRCodePanel.isAdded()) {
      ft.show(scanQRCodePanel);
    } else {
      ft.add(R.id.actions_panel, scanQRCodePanel, "ScanQRCodePanel");
    }

    if (deletePanel.isAdded()) {
      ft.hide(deletePanel);
    }
    if (exportPanel.isAdded()) {
      ft.hide(exportPanel);
    }
    ft.commit();
  }

  private void showDeleteAuthenticatorsFragment() {
    currentFragment = FragmentType.DELETE;
    val ft = getSupportFragmentManager().beginTransaction();
    ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
    if (deletePanel.isAdded()) {
      ft.show(deletePanel);
    } else {
      ft.add(R.id.actions_panel, deletePanel, "DeletePanel");
    }

    if (scanQRCodePanel.isAdded()) {
      ft.hide(scanQRCodePanel);
    }
    if (exportPanel.isAdded()) {
      ft.hide(exportPanel);
    }
    ft.commit();
  }

  private void showExportAuthenticatorsFragment() {
    currentFragment = FragmentType.EXPORT;
    val ft = getSupportFragmentManager().beginTransaction();
    ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
    if (exportPanel.isAdded()) {
      ft.show(exportPanel);
    } else {
      ft.add(R.id.actions_panel, exportPanel, "ExportPanel");
    }

    if (scanQRCodePanel.isAdded()) {
      ft.hide(scanQRCodePanel);
    }
    if (deletePanel.isAdded()) {
      ft.hide(deletePanel);
    }
    ft.commit();
  }

  private void toggleSearchFragmentVisibility() {
    val ft = getSupportFragmentManager().beginTransaction();
    ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
    if (!searchPanel.isAdded()) {
      ft.add(R.id.search_panel, searchPanel, "SearchPanel");
    } else {
      if (searchPanel.isHidden()) {
        ft.show(searchPanel);
      } else {
        ft.hide(searchPanel);
      }
    }
    ft.commit();
  }

  public void loadTokens(List<Token> tokens) {
    if (otpRecalculationTaskController != null) {
      otpRecalculationTaskController.cancel(true);
    }
    adapter.replaceTokens(tokens);
    adapter.toggleSelectCheckboxVisibility(currentFragment != FragmentType.SCAN_QR_CODE);
    adapter.notifyDataSetChanged();
    otpRecalculationTaskController = executor.scheduleWithFixedDelay(
        new OTPRecalculationTask(adapter),
        0,
        1,
        TimeUnit.SECONDS
    );
  }

  public void search(String searchPhrase) {
    if (otpRecalculationTaskController != null) {
      otpRecalculationTaskController.cancel(true);
    }
    adapter.search(searchPhrase);
    otpRecalculationTaskController = executor.scheduleWithFixedDelay(
        new OTPRecalculationTask(adapter),
        0,
        1,
        TimeUnit.SECONDS
    );
  }

  public void toggleSelectCheckbox(boolean selected) {
    if (otpRecalculationTaskController != null) {
      otpRecalculationTaskController.cancel(true);
    }
    adapter.toggleSelectCheckbox(selected);
    otpRecalculationTaskController = executor.scheduleWithFixedDelay(
        new OTPRecalculationTask(adapter),
        0,
        1,
        TimeUnit.SECONDS
    );
  }

  public void toggleSelectCheckboxVisibility(boolean visible) {
    if (otpRecalculationTaskController != null) {
      otpRecalculationTaskController.cancel(true);
    }
    adapter.toggleSelectCheckboxVisibility(visible);
    otpRecalculationTaskController = executor.scheduleWithFixedDelay(
        new OTPRecalculationTask(adapter),
        0,
        1,
        TimeUnit.SECONDS
    );
  }

  private void toggleTokenCategoryVisibility() {
    if (otpRecalculationTaskController != null) {
      otpRecalculationTaskController.cancel(true);
    }
    otpRecalculationTaskController = executor.scheduleWithFixedDelay(
        new OTPRecalculationTask(adapter),
        0,
        1,
        TimeUnit.SECONDS
    );
  }

  public void setTokenCategoryVisible(boolean visible) {
    tokenCategoryVisible = visible;
    if (tokenCategoryMenuItem != null) {
      tokenCategoryMenuItem.setChecked(visible);
    }
  }

  public List<Integer> getSelectedTokens() {
    return adapter
        .getTokens()
        .stream()
        .filter(Token::isSelected)
        .map(Token::getId)
        .collect(Collectors.toList());
  }

  public void scanTokenQRCode() {
    if (Common.shouldRequestCameraPermission(TokensActivity.this)) {
      requestPermissionLauncher.launch(permission.CAMERA);
      return;
    }
    getScannedQRCodeContents.launch(
        new ScanOptions().setBeepEnabled(false)
    );
  }

  public void processPasswordInputDialogCancelButton() {
    Common.dismissDialog(TokensActivity.this);
  }

  public void processPasswordInputDialogApproveButton(ExportLocked exportLocked, char[] password) {
    Common.dismissDialog(TokensActivity.this);
    DataHandler.loadLockedExport(this, exportLocked, password);
  }

}
