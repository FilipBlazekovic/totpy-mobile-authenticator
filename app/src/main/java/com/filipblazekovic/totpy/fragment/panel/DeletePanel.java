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
import java.util.List;

public class DeletePanel extends Fragment {

  public DeletePanel() {
    super(R.layout.panel_delete);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    final CheckBox selectAllCheckbox = view.findViewById(R.id.panel_delete_select_all_checkbox);
    final ImageButton cancelButton = view.findViewById(R.id.panel_delete_cancel_button);
    final ImageButton approveButton = view.findViewById(R.id.panel_delete_approve_button);

    final TokensActivity activity = (TokensActivity) getActivity();

    selectAllCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> activity.toggleSelectCheckbox(isChecked));

    cancelButton.setOnClickListener(v -> {
      activity.toggleSelectCheckboxVisibility(false);
      activity.showScanQRCodeFragment();
    });

    approveButton.setOnClickListener(v -> {
      final List<Integer> selectedTokens = activity.getSelectedTokens();

      activity.toggleSelectCheckboxVisibility(false);

      if (!selectedTokens.isEmpty()) {
        DataHandler.deleteTokens(activity, selectedTokens);
        DataHandler.loadTokens(activity);
      }

      activity.showScanQRCodeFragment();
    });
  }

}
