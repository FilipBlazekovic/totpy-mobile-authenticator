package com.filipblazekovic.totpy.fragment.dialog;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.filipblazekovic.totpy.R;
import com.filipblazekovic.totpy.model.internal.Token;
import com.filipblazekovic.totpy.utils.Common;
import com.filipblazekovic.totpy.utils.OTPAuth;
import com.filipblazekovic.totpy.utils.QRCode;
import lombok.val;

public class TokenQRCodeDialog extends DialogFragment {

  private Token token;

  public static TokenQRCodeDialog newInstance(Token token) {
    val dialog = new TokenQRCodeDialog();
    try {
      val args = new Bundle();
      args.putString("token", new ObjectMapper().writeValueAsString(token));
      dialog.setArguments(args);
    } catch (Exception ignored) {}
    return dialog;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(STYLE_NO_TITLE, R.style.TotpyDialogTheme);
    try {
        token = new ObjectMapper().readValue(
            getArguments().getString("token", null),
            Token.class
        );
    } catch (Exception ignored) {
    }
    setShowsDialog(true);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.dialog_token_qr_code, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    try {
      final ImageView imageViewQrCode = view.findViewById(R.id.dialog_show_token_qr_code_image);
      imageViewQrCode.setImageBitmap(
          QRCode.generate(
              OTPAuth.generateTotpUri(token)
          )
      );
    } catch (Exception e) {
      Log.e(Common.TAG, "Could not generate QR code image", e);
    }
  }

}
