package com.filipblazekovic.totpy.exception;

import android.content.Context;
import com.filipblazekovic.totpy.R;

public class PasswordsDoNotMatchException extends Exception {

  public PasswordsDoNotMatchException(Context context) {
    super(
        context
            .getResources()
            .getString(R.string.error_message_passwords_do_not_match)
    );
  }

}
