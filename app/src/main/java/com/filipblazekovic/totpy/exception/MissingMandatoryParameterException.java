package com.filipblazekovic.totpy.exception;

import android.content.Context;
import com.filipblazekovic.totpy.R;

public class MissingMandatoryParameterException extends Exception {

  public MissingMandatoryParameterException(Context context, MandatoryParameter parameter) {
    super(
        context
            .getResources()
            .getString(R.string.error_message_mandatory_parameter_empty) + parameter.name()
    );
  }

}
