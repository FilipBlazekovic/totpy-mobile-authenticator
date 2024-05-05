package com.filipblazekovic.totpy.fragment.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.filipblazekovic.totpy.R;
import com.filipblazekovic.totpy.activity.TokensActivity;
import com.filipblazekovic.totpy.model.inout.ExportLocked;
import com.google.android.material.textfield.TextInputLayout;

public class PasswordInputDialog extends DialogFragment {

  private final ExportLocked exportLocked;

  private PasswordInputDialog(ExportLocked exportLocked) {
    this.exportLocked = exportLocked;
  }

  public static PasswordInputDialog newInstance(ExportLocked exportLocked) {
    return new PasswordInputDialog(exportLocked);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(STYLE_NO_TITLE, R.style.TotpyDialogTheme);
    setShowsDialog(true);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.dialog_password_input, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    final TextInputLayout enterPasswordView = view.findViewById(R.id.dialog_password_input_enter_password);
    final Button cancelButton = view.findViewById(R.id.dialog_password_input_cancel_button);
    final Button approveButton = view.findViewById(R.id.dialog_password_input_approve_button);

    final TokensActivity activity = (TokensActivity) getActivity();

    cancelButton.setOnClickListener(v -> activity.processPasswordInputDialogCancelButton());
    approveButton.setOnClickListener(v -> activity.processPasswordInputDialogApproveButton(
        exportLocked,
        enterPasswordView.getEditText().getText().toString().toCharArray())
    );
  }

}
