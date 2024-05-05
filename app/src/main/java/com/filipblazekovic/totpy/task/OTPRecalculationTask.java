package com.filipblazekovic.totpy.task;

import android.os.Handler;
import android.os.Looper;
import androidx.recyclerview.widget.DiffUtil;
import com.filipblazekovic.totpy.adapter.TokensAdapter;
import com.filipblazekovic.totpy.adapter.TokensDiffUtil;
import com.filipblazekovic.totpy.crypto.CryptoHandler;
import com.filipblazekovic.totpy.model.internal.Token;
import java.util.stream.Collectors;
import lombok.val;

public class OTPRecalculationTask implements Runnable {

  private final Handler handler = new Handler(Looper.getMainLooper());
  private final TokensAdapter adapter;
  private boolean firstRun = true;

  public OTPRecalculationTask(TokensAdapter adapter) {
    this.adapter = adapter;
  }

  @Override
  public void run() {

    val modifiedTokens = adapter
        .getTokens()
        .stream()
        .map(t -> Token.from(
            t,
            (firstRun || CryptoHandler.shouldRecalculateOTP(t.getPeriod())) ? CryptoHandler.calculateOTP(t) : t.getOtp(),
            CryptoHandler.getRemainingOTPSeconds(t.getPeriod())
        ))
        .collect(Collectors.toList());

    val diffResult = DiffUtil.calculateDiff(
        new TokensDiffUtil(firstRun, adapter.getTokens(), modifiedTokens)
    );

    firstRun = false;

    handler.post(() -> adapter.updateChanges(modifiedTokens, diffResult));
  }

}
