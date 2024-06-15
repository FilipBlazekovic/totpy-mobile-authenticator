package com.filipblazekovic.totpy.fragment.dialog;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.filipblazekovic.totpy.R;
import com.filipblazekovic.totpy.activity.TokensActivity;
import com.filipblazekovic.totpy.model.inout.ExportLockingPublicKey;
import com.filipblazekovic.totpy.utils.Common;
import com.filipblazekovic.totpy.utils.DataHandler;
import com.filipblazekovic.totpy.utils.QRCode;
import java.nio.charset.StandardCharsets;
import lombok.val;

public class PublicKeyDialog extends DialogFragment {

  private String publicKey;

  public static PublicKeyDialog newInstance(ExportLockingPublicKey exportLockingPublicKey) {
    val dialog = new PublicKeyDialog();
    try {
      val args = new Bundle();
      args.putString("publicKey", new ObjectMapper().writeValueAsString(exportLockingPublicKey));
      dialog.setArguments(args);
    } catch (Exception ignored) {
    }
    return dialog;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(STYLE_NO_TITLE, R.style.TotpyDialogTheme);
    val arguments = getArguments();
    if (arguments != null) {
      publicKey = arguments.getString("publicKey", null);
    }
    setShowsDialog(true);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.dialog_public_key, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    final TokensActivity context = (TokensActivity) getActivity();

    try {
      final ImageView imageViewQrCode = view.findViewById(R.id.dialog_show_public_key_qr_code_image);
      imageViewQrCode.setImageBitmap(
          QRCode.generate(publicKey)
      );
    } catch (Exception e) {
      Log.e(Common.TAG, "Could not generate QR code image", e);
    }

    final ImageButton copyToClipboardButton = view.findViewById(R.id.dialog_show_public_key_copy_to_clipboard_button);
    copyToClipboardButton.setOnClickListener(v -> {
      final ClipboardManager clipboard = getSystemService(context, ClipboardManager.class);
      if (clipboard != null) {
        clipboard.setPrimaryClip(
            ClipData.newPlainText("publicKey", publicKey)
        );
      }
    });

    final ImageButton shareButton = view.findViewById(R.id.dialog_show_public_key_share_button);
    shareButton.setOnClickListener(v -> DataHandler.shareFile(context, publicKey.getBytes(StandardCharsets.UTF_8), "public_key.json"));
  }

}
