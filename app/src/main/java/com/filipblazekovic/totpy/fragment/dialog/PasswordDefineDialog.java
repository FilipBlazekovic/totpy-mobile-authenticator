package com.filipblazekovic.totpy.fragment.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.filipblazekovic.totpy.R;
import com.filipblazekovic.totpy.activity.ExportTokensActivity;
import com.google.android.material.textfield.TextInputLayout;

public class PasswordDefineDialog extends DialogFragment {

  private PasswordDefineDialog() {
  }

  public static PasswordDefineDialog newInstance() {
    return new PasswordDefineDialog();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(STYLE_NO_TITLE, R.style.TotpyDialogTheme);
    setShowsDialog(true);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.dialog_password_define, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    final TextInputLayout enterPasswordView = view.findViewById(R.id.dialog_password_define_enter_password);
    final TextInputLayout confirmPasswordView = view.findViewById(R.id.dialog_password_define_confirm_password);

    final Button cancelButton = view.findViewById(R.id.dialog_password_define_cancel_button);
    final Button approveButton = view.findViewById(R.id.dialog_password_define_approve_button);

    cancelButton.setOnClickListener(v -> ((ExportTokensActivity) getActivity()).processPasswordDefineDialogCancelButton());
    approveButton.setOnClickListener(v -> ((ExportTokensActivity) getActivity()).processPasswordDefineDialogApproveButton(
        enterPasswordView.getEditText().getText().toString(),
        confirmPasswordView.getEditText().getText().toString()
    ));
  }

}
