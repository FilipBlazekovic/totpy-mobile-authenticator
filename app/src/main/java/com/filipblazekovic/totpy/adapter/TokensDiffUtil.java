package com.filipblazekovic.totpy.adapter;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import com.filipblazekovic.totpy.model.internal.Token;
import java.util.List;
import java.util.Objects;
import lombok.val;

public class TokensDiffUtil extends DiffUtil.Callback {

  private final List<Token> oldTokens;
  private final List<Token> newTokens;
  private final boolean firstRun;

  public TokensDiffUtil(boolean firstRun, List<Token> oldTokens, List<Token> newTokens) {
    this.oldTokens = oldTokens;
    this.newTokens = newTokens;
    this.firstRun = firstRun;
  }

  @Override
  public int getOldListSize() {
    if (oldTokens != null) {
      return oldTokens.size();
    }
    return 0;
  }

  @Override
  public int getNewListSize() {
    if (newTokens != null) {
      return newTokens.size();
    }
    return 0;
  }

  @Override
  public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
    return Objects.equals(
        oldTokens.get(oldItemPosition).getId(),
        newTokens.get(newItemPosition).getId()
    );
  }

  @Override
  public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
    if (firstRun) {
      return false;
    }
    return newTokens.get(newItemPosition).equals(oldTokens.get(oldItemPosition));
  }

  @Nullable
  @Override
  public Object getChangePayload(int oldItemPosition, int newItemPosition) {
    val newToken = newTokens.get(newItemPosition);
    val oldToken = oldTokens.get(oldItemPosition);

    val bundle = new Bundle();

    if (newToken.getRemainingSeconds() != oldToken.getRemainingSeconds()) {
      bundle.putInt("remainingSeconds", newToken.getRemainingSeconds());
    }
    if (!newToken.getOtp().equals(oldToken.getOtp())) {
      bundle.putString("otp", newToken.getOtp());
    }

    bundle.putBoolean("visible", newToken.isVisible());
    bundle.putBoolean("selected", newToken.isSelected());

    if (bundle.isEmpty()) {
      return null;
    }
    return bundle;
  }

}
