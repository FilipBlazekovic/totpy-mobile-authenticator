package com.filipblazekovic.totpy.adapter;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.filipblazekovic.totpy.R;
import com.filipblazekovic.totpy.activity.AddEditTokenActivity;
import com.filipblazekovic.totpy.fragment.dialog.TokenQRCodeDialog;
import com.filipblazekovic.totpy.model.internal.Token;
import com.filipblazekovic.totpy.utils.Common;
import com.filipblazekovic.totpy.utils.ConfigStore;
import com.filipblazekovic.totpy.utils.IconStore;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import java.util.List;
import lombok.Getter;
import lombok.val;

public class TokensAdapter extends RecyclerView.Adapter<TokensAdapter.ViewHolder> {

  private final Context context;

  @Getter
  private final List<Token> tokens;

  private boolean selectCheckBoxVisible = false;

  public TokensAdapter(Context context, List<Token> tokens) {
    this.context = context;
    this.tokens = tokens;
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    public ConstraintLayout tokenCategoryLayout;
    public TextView tokenCategoryView;
    public ImageView issuerImageView;
    public TextView otpView;
    public CircularProgressIndicator remainingTimeIndicator;
    public TextView accountView;
    public TextView issuerNameView;
    public ImageButton copyToClipboardButton;
    public ImageButton showQRCodeButton;
    public ImageButton editButton;
    public CheckBox selectCheckBox;

    public ViewHolder(View itemView) {
      super(itemView);

      tokenCategoryLayout = itemView.findViewById(R.id.token_category_layout);
      tokenCategoryView = itemView.findViewById(R.id.token_category_value);
      issuerImageView = itemView.findViewById(R.id.token_issuer_image);
      otpView = itemView.findViewById(R.id.token_otp);
      remainingTimeIndicator = itemView.findViewById(R.id.token_remaining_time);
      accountView = itemView.findViewById(R.id.token_account);
      issuerNameView = itemView.findViewById(R.id.token_issuer_name);
      copyToClipboardButton = itemView.findViewById(R.id.token_copy_to_clipboard_button);
      showQRCodeButton = itemView.findViewById(R.id.token_show_qr_code_button);
      editButton = itemView.findViewById(R.id.token_edit_button);
      selectCheckBox = itemView.findViewById(R.id.token_selected_checkbox);
    }
  }

  @NonNull
  @Override
  public TokensAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new ViewHolder(
        LayoutInflater
            .from(parent.getContext())
            .inflate(R.layout.token, parent, false)
    );
  }

  @Override
  public void onBindViewHolder(TokensAdapter.ViewHolder holder, int position) {
    val token = tokens.get(position);

    val tokenCategoryLayout = holder.tokenCategoryLayout;
    val tokenCategoryView = holder.tokenCategoryView;
    val issuerImageView = holder.issuerImageView;
    val otpView = holder.otpView;
    val remainingTimeIndicator = holder.remainingTimeIndicator;
    val accountView = holder.accountView;
    val issuerNameView = holder.issuerNameView;
    val copyToClipboardButton = holder.copyToClipboardButton;
    val showQRCodeButton = holder.showQRCodeButton;
    val editButton = holder.editButton;
    val selectCheckBox = holder.selectCheckBox;

    // Set values from the current token
    issuerImageView.setImageDrawable(
        IconStore.get(context, token.getIssuer().getIcon())
    );

    remainingTimeIndicator.setMax(token.getPeriod());
    remainingTimeIndicator.setProgress(token.getRemainingSeconds());

    otpView.setText(token.getOtp());
    accountView.setText(token.getAccount());
    issuerNameView.setText(
        token.getIssuer().getName() == null
            ? ""
            : token.getIssuer().getName()
    );

    if (ConfigStore.get(context).isTokenCategoryVisible()) {
      tokenCategoryLayout.setVisibility(View.VISIBLE);
      tokenCategoryLayout.setBackgroundColor(token.getCategory().getColor(context));
      tokenCategoryView.setText(token.getCategory().getDisplayName());
    } else {
      tokenCategoryLayout.setVisibility(View.GONE);
    }

    if (selectCheckBoxVisible) {
      selectCheckBox.setVisibility(View.VISIBLE);
    } else {
      selectCheckBox.setVisibility(View.GONE);
    }

    selectCheckBox.setChecked(token.isSelected());
    selectCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> token.setSelected(isChecked));

    copyToClipboardButton.setOnClickListener(v -> {
      val clipboard = getSystemService(context, ClipboardManager.class);
      if (clipboard != null) {
        clipboard.setPrimaryClip(
            ClipData.newPlainText("otp", token.getOtp())
        );
      }
    });

    showQRCodeButton.setOnClickListener(v -> {
      val fragment = TokenQRCodeDialog.newInstance(token);
      fragment.show(((AppCompatActivity) context).getSupportFragmentManager(), Common.DIALOG_LABEL);
    });

    editButton.setOnClickListener(v -> {
      val intent = new Intent(context, AddEditTokenActivity.class);
      intent.putExtra(Common.SELECTED_TOKEN_ID, token.getId());
      context.startActivity(intent);
    });
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
    if (!payloads.isEmpty()) {

      val token = tokens.get(position);
      val otpView = holder.otpView;
      val remainingTimeIndicator = holder.remainingTimeIndicator;
      val selectCheckBox = holder.selectCheckBox;

      val changes = (Bundle) payloads.get(payloads.size() - 1);
      for (String key : changes.keySet()) {
        switch (key) {
          case "otp":
            otpView.setText(token.getOtp());
            break;
          case "remainingSeconds":
            remainingTimeIndicator.setProgress(token.getRemainingSeconds());
            break;
          case "selected":
            selectCheckBox.setChecked(token.isSelected());
            break;
          case "visible":
            if (token.isVisible()) {
              holder.itemView.setVisibility(View.VISIBLE);
              holder.itemView.setLayoutParams(
                  new RecyclerView.LayoutParams(
                      ViewGroup.LayoutParams.MATCH_PARENT,
                      ViewGroup.LayoutParams.WRAP_CONTENT
                  )
              );
              continue;
            }
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            break;
        }
      }
    }
    super.onBindViewHolder(holder, position, payloads);
  }

  @Override
  public int getItemCount() {
    return tokens.size();
  }

  @Override
  public long getItemId(int position) {
    return (long) tokens.get(position).getId();
  }

  public void replaceTokens(List<Token> newTokens) {
    tokens.clear();
    tokens.addAll(newTokens);
  }

  public void updateChanges(List<Token> modifiedTokens, DiffUtil.DiffResult diffResult) {
    replaceTokens(modifiedTokens);
    diffResult.dispatchUpdatesTo(this);
  }

  public void toggleSelectCheckboxVisibility(boolean visible) {
    this.getTokens().forEach(t -> t.setSelected(false));
    selectCheckBoxVisible = visible;
  }

  public void toggleSelectCheckbox(boolean selected) {
    this.getTokens().forEach(t -> t.setSelected(selected));
  }

  public void search(String searchPhrase) {
    if (searchPhrase == null || searchPhrase.trim().isEmpty()) {
      this.getTokens().forEach(t -> t.setVisible(true));
      return;
    }

    val phrase = searchPhrase.toLowerCase().trim();
    this.getTokens().forEach(t -> {
      if (t.getCategory().getDisplayName().toLowerCase().contains(phrase)) {
        t.setVisible(true);
        return;
      }
      if (t.getAccount().toLowerCase().contains(phrase)) {
        t.setVisible(true);
        return;
      }
      if (t.getIssuer().getName() != null && t.getIssuer().getName().toLowerCase().contains(phrase)) {
        t.setVisible(true);
        return;
      }
      t.setVisible(false);
    });
  }

}

