package com.filipblazekovic.totpy.fragment.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.filipblazekovic.totpy.R;
import com.filipblazekovic.totpy.model.internal.DeviceSecurityInfo;
import com.filipblazekovic.totpy.model.shared.AsymmetricKeyAlgorithm;
import com.filipblazekovic.totpy.utils.ConfigStore;
import java.time.format.DateTimeFormatter;
import lombok.val;

public class SecurityDetailsDialog extends DialogFragment {

  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy mm:hh:ss");

  private final Context context;

  private final DeviceSecurityInfo deviceSecurityInfo;

  private SecurityDetailsDialog(Context context, DeviceSecurityInfo deviceSecurityInfo) {
    this.context = context;
    this.deviceSecurityInfo = deviceSecurityInfo;
  }

  public static SecurityDetailsDialog newInstance(Context context, DeviceSecurityInfo deviceSecurityInfo) {
    return new SecurityDetailsDialog(context, deviceSecurityInfo);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(STYLE_NO_TITLE, R.style.TotpyDialogTheme);
    setShowsDialog(true);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.dialog_security_details, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    final TextView deviceRootedView = view.findViewById(R.id.device_rooted_view);
    final TextView usedKeystoreView = view.findViewById(R.id.used_keystore_view);
    final TextView tokenEncryptionView = view.findViewById(R.id.token_encryption_view);
    final TextView lastExportView = view.findViewById(R.id.last_export_view);
    final TextView remoteWipeOnView = view.findViewById(R.id.remote_wipe_on_view);

    val config = ConfigStore.get(context);

    deviceRootedView.setText(
        deviceSecurityInfo.isDeviceRooted()
            ? context.getResources().getString(R.string.security_details_dialog_device_rooted_positive)
            : context.getResources().getString(R.string.security_details_dialog_device_rooted_negative)
    );

    switch (deviceSecurityInfo.getKeyProtection()) {
      case STRONGBOX:
        usedKeystoreView.setText(
            context.getResources().getString(R.string.security_details_dialog_hardware_keystore_used, "StrongBox")
        );
        break;
      case TEE:
        usedKeystoreView.setText(
            context.getResources().getString(R.string.security_details_dialog_hardware_keystore_used, "TEE")
        );
        break;
      default:
        usedKeystoreView.setText(
            context.getResources().getString(R.string.security_details_dialog_software_keystore_used)
        );
    }

    tokenEncryptionView.setText(
        deviceSecurityInfo.getKeyAlgorithm() == AsymmetricKeyAlgorithm.EC
            ? context.getResources().getString(R.string.security_details_dialog_token_encryption_algorithm_ecies)
            : context.getResources().getString(R.string.security_details_dialog_token_encryption_algorithm_rsa)
    );

    lastExportView.setText(
        (config.getLastExportDateTime() == null)
            ? ""
            : config.getLastExportDateTime().format(DATE_TIME_FORMATTER)
    );

    remoteWipeOnView.setText(
        config.isSmsRemoteWipeOn()
            ? context.getResources().getString(R.string.security_details_dialog_device_remote_wipe_positive)
            : context.getResources().getString(R.string.security_details_dialog_device_remote_wipe_negative)
    );
  }

}
