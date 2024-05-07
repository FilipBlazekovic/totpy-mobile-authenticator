package com.filipblazekovic.totpy.exception;

import android.content.Context;
import com.filipblazekovic.totpy.R;

public class WeakPhraseException extends Exception {

  public WeakPhraseException(Context context) {
    super(
        context
            .getResources()
            .getString(R.string.error_message_weak_phrase)
    );
  }

}