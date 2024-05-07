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
import com.filipblazekovic.totpy.activity.AddEditTokenActivity;
import com.google.android.material.textfield.TextInputLayout;

public class EnterOtpAuthUriDialog extends DialogFragment {

  public static EnterOtpAuthUriDialog newInstance() {
    return new EnterOtpAuthUriDialog();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(STYLE_NO_TITLE, R.style.TotpyDialogTheme);
    setShowsDialog(true);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.dialog_enter_otpauth_uri, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    final TextInputLayout enteredOtpAuthUriField = view.findViewById(R.id.dialog_enter_otpauth_uri_entered_uri);
    final Button cancelButton = view.findViewById(R.id.dialog_enter_otpauth_uri_cancel_button);
    final Button approveButton = view.findViewById(R.id.dialog_enter_otpauth_uri_approve_button);

    cancelButton.setOnClickListener(v -> ((AddEditTokenActivity) getActivity()).processEnterOtpAuthUriDialogCancelButton());
    approveButton.setOnClickListener(v -> ((AddEditTokenActivity) getActivity()).processEnterOtpAuthUriDialogApproveButton(
        enteredOtpAuthUriField.getEditText().getText().toString())
    );
  }

}
