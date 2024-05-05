package com.filipblazekovic.totpy.fragment.panel;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.filipblazekovic.totpy.R;
import com.filipblazekovic.totpy.activity.TokensActivity;

public class ScanQRCodePanel extends Fragment {

  public ScanQRCodePanel() {
    super(R.layout.panel_scan_qr_code);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    final ImageButton scanQRCodeButton = view.findViewById(R.id.panel_scan_qr_code_button);
    scanQRCodeButton.setOnClickListener(v -> ((TokensActivity) getActivity()).scanTokenQRCode());
  }

}
