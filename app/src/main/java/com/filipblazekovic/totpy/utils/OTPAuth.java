package com.filipblazekovic.totpy.utils;

import android.content.Context;
import com.filipblazekovic.totpy.exception.InvalidTokenUriFormatException;
import com.filipblazekovic.totpy.model.internal.Issuer;
import com.filipblazekovic.totpy.model.internal.Token;
import com.filipblazekovic.totpy.model.shared.HashAlgorithm;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.codec.binary.Base32;

public final class OTPAuth {

  private static final String CHARSET_UTF_8 = "UTF-8";

  private OTPAuth() {
  }

  public static Token parseTotpUri(Context context, String uri) throws UnsupportedEncodingException, InvalidTokenUriFormatException {
    val token = Token.getDefault();

    if (!uri.startsWith("otpauth://totp/")) {
      throw new InvalidTokenUriFormatException(context);
    }

    String[] temp = uri.substring(15).split("\\?");
    if (temp.length != 2) {
      throw new InvalidTokenUriFormatException(context);
    }

    val label = temp[0];
    val remainingData = temp[1];

    if (label.contains(":")) {
      temp = label.split(":");
      token.setIssuer(Issuer.from(URLDecoder.decode(temp[0], CHARSET_UTF_8)));
      token.setAccount(URLDecoder.decode(temp[1], CHARSET_UTF_8));
    } else {
      token.setAccount(URLDecoder.decode(label, CHARSET_UTF_8));
    }

    val params = remainingData.split("&");
    for (String param : params) {
      temp = param.split("=");
      if (temp.length != 2) {
        throw new InvalidTokenUriFormatException(context);
      }

      val name = temp[0].toLowerCase();
      val value = URLDecoder.decode(temp[1], CHARSET_UTF_8);

      switch (name) {
        case "secret":
          token.setSecret(new Base32().decode(value));
          break;
        case "issuer":
          token.setIssuer(Issuer.from(value));
          break;
        case "algorithm":
          token.setAlgorithm(HashAlgorithm.from(value));
          break;
        case "digits":
          token.setDigits(Integer.parseInt(value));
          break;
        case "period":
          token.setPeriod(Integer.parseInt(value));
          break;
        default:
          throw new InvalidTokenUriFormatException(context);
      }
    }

    return token;
  }

  @SneakyThrows
  public static String generateTotpUri(Token token) {
    val issuer = (token.getIssuer().getName() == null)
        ? ""
        : token.getIssuer().getName().trim();

    val encodedIssuer = issuer.isEmpty()
        ? ""
        : URLEncoder
            .encode(issuer, CHARSET_UTF_8)
            .replace("+", "%20");

    val encodedAccount = URLEncoder
        .encode(token.getAccount(), CHARSET_UTF_8)
        .replace("+", "%20");

    val builder = new StringBuilder("otpauth://totp/");

    if (!issuer.isEmpty()) {
      builder.append(encodedIssuer);
      builder.append(":");
    }

    builder.append(encodedAccount);
    builder.append("?");
    builder.append("secret=").append(new Base32().encodeAsString(token.getSecret()));

    if (!issuer.isEmpty()) {
      builder.append("&issuer=").append(encodedIssuer);
    }
    builder.append("&algorithm=").append(token.getAlgorithm().name());
    builder.append("&digits=").append(token.getDigits());
    builder.append("&period=").append(token.getPeriod());
    return builder.toString();
  }

}