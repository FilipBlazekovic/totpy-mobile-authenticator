package com.filipblazekovic.totpy.fragment.panel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.filipblazekovic.totpy.R;
import com.filipblazekovic.totpy.activity.ExportTokensActivity;
import com.filipblazekovic.totpy.activity.TokensActivity;
import com.filipblazekovic.totpy.utils.Common;
import java.util.ArrayList;
import lombok.val;

public class ExportPanel extends Fragment {

  private TokensActivity activity;
  private CheckBox selectAllCheckbox;

  public ExportPanel() {
    super(R.layout.panel_export);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    final ImageButton cancelButton = view.findViewById(R.id.panel_export_cancel_button);
    final ImageButton approveButton = view.findViewById(R.id.panel_export_approve_button);

    activity = (TokensActivity) getActivity();

    selectAllCheckbox = view.findViewById(R.id.panel_export_select_all_checkbox);
    selectAllCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> activity.toggleSelectCheckbox(isChecked));

    cancelButton.setOnClickListener(v -> {
      activity.toggleSelectCheckboxVisibility(false);
      cleanupAndClose();
    });

    approveButton.setOnClickListener(v -> {
      val selectedTokens = activity.getSelectedTokens();

      if (!selectedTokens.isEmpty()) {
        val intent = new Intent(getActivity(), ExportTokensActivity.class);
        intent.putIntegerArrayListExtra(
            Common.SELECTED_TOKEN_IDS,
            new ArrayList<>(selectedTokens)
        );
        startActivity(intent);
      }
    });
  }

  private void cleanupAndClose() {
    selectAllCheckbox.setOnCheckedChangeListener(null);
    selectAllCheckbox.setChecked(false);
    selectAllCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> activity.toggleSelectCheckbox(isChecked));
    activity.showScanQRCodeFragment();
  }

}
