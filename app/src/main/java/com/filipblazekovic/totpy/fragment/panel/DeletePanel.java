package com.filipblazekovic.totpy.fragment.panel;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.filipblazekovic.totpy.R;
import com.filipblazekovic.totpy.activity.TokensActivity;
import com.filipblazekovic.totpy.utils.DataHandler;
import lombok.val;

public class DeletePanel extends Fragment {

  private TokensActivity activity;
  private CheckBox selectAllCheckbox;

  public DeletePanel() {
    super(R.layout.panel_delete);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    final ImageButton cancelButton = view.findViewById(R.id.panel_delete_cancel_button);
    final ImageButton approveButton = view.findViewById(R.id.panel_delete_approve_button);

    activity = (TokensActivity) getActivity();

    selectAllCheckbox = view.findViewById(R.id.panel_delete_select_all_checkbox);
    selectAllCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> activity.toggleSelectCheckbox(isChecked));

    cancelButton.setOnClickListener(v -> {
      activity.toggleSelectCheckboxVisibility(false);
      cleanupAndClose();
    });

    approveButton.setOnClickListener(v -> {
      val selectedTokens = activity.getSelectedTokens();
      if (!selectedTokens.isEmpty()) {
        DataHandler.deleteTokens(activity, selectedTokens);
        DataHandler.loadTokens(activity);
      }
      cleanupAndClose();
    });
  }

  private void cleanupAndClose() {
    selectAllCheckbox.setOnCheckedChangeListener(null);
    selectAllCheckbox.setChecked(false);
    selectAllCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> activity.toggleSelectCheckbox(isChecked));
    activity.showScanQRCodeFragment();
  }

}

