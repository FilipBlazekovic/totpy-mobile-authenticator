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
import com.filipblazekovic.totpy.model.inout.ExportLockingPublicKey;
import com.filipblazekovic.totpy.utils.Common;
import com.filipblazekovic.totpy.utils.DataHandler;
import com.filipblazekovic.totpy.utils.QRCode;
import java.nio.charset.StandardCharsets;

public class PublicKeyDialog extends DialogFragment {

  private final Context context;
  private String publicKey;

  private PublicKeyDialog(Context context, ExportLockingPublicKey exportLockingPublicKey) {
    this.context = context;
    try {
      this.publicKey = new ObjectMapper().writeValueAsString(exportLockingPublicKey);
    } catch (Exception ignored) {
      publicKey = "";
    }
  }

  public static PublicKeyDialog newInstance(Context context, ExportLockingPublicKey exportLockingPublicKey) {
    return new PublicKeyDialog(context, exportLockingPublicKey);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(STYLE_NO_TITLE, R.style.TotpyDialogTheme);
    setShowsDialog(true);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.dialog_public_key, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

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
