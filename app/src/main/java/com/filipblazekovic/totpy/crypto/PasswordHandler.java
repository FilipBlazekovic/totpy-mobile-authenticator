package com.filipblazekovic.totpy.crypto;

import android.content.Context;
import com.filipblazekovic.totpy.exception.PasswordsDoNotMatchException;
import com.filipblazekovic.totpy.exception.WeakPasswordException;
import com.filipblazekovic.totpy.exception.WeakPhraseException;
import java.util.Arrays;

public final class PasswordHandler {

  private static final String SPECIAL_CHARACTERS = "!@#$%^&*()_+=-[]{};:'\".,><?/|~\\";

  private PasswordHandler() {
  }

  public static void validate(Context context, String remoteWipePhrase) throws WeakPhraseException {
    if (remoteWipePhrase.length() < 8) {
      throw new WeakPhraseException(context);
    }

    boolean hasLowercase = false;
    boolean hasUppercase = false;
    boolean hasNumber = false;
    boolean hasSpecialCharacter = false;

    for (char c : remoteWipePhrase.toCharArray()) {

      if (Character.isLowerCase(c)) {
        hasLowercase = true;
        continue;
      }

      if (Character.isUpperCase(c)) {
        hasUppercase = true;
        continue;
      }

      if (Character.isDigit(c)) {
        hasNumber = true;
        continue;
      }

      if (SPECIAL_CHARACTERS.contains(String.valueOf(c))) {
        hasSpecialCharacter = true;
      }
    }

    if (hasLowercase && hasUppercase && hasNumber && hasSpecialCharacter) {
      return;
    }

    throw new WeakPhraseException(context);
  }

  public static void validate(Context context, char[] password, char[] passwordConfirmation) throws PasswordsDoNotMatchException, WeakPasswordException {
    if (!Arrays.equals(password, passwordConfirmation)) {
      throw new PasswordsDoNotMatchException(context);
    }

    if (password.length < 8) {
      throw new WeakPasswordException(context);
    }

    boolean hasLowercase = false;
    boolean hasUppercase = false;
    boolean hasNumber = false;
    boolean hasSpecialCharacter = false;

    for (char c : password) {

      if (Character.isLowerCase(c)) {
        hasLowercase = true;
        continue;
      }

      if (Character.isUpperCase(c)) {
        hasUppercase = true;
        continue;
      }

      if (Character.isDigit(c)) {
        hasNumber = true;
        continue;
      }

      if (SPECIAL_CHARACTERS.contains(String.valueOf(c))) {
        hasSpecialCharacter = true;
      }
    }

    if (hasLowercase && hasUppercase && hasNumber && hasSpecialCharacter) {
      return;
    }

    throw new WeakPasswordException(context);
  }

}
