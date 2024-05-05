package com.filipblazekovic.totpy.exception;

import android.content.Context;
import com.filipblazekovic.totpy.R;

public class InvalidTokenUriFormatException extends Exception {

  public InvalidTokenUriFormatException(Context context) {
    super(
        context
            .getResources()
            .getString(R.string.error_message_invalid_token_uri_format)
    );
  }

}
