package com.filipblazekovic.totpy.model.internal;

import com.filipblazekovic.totpy.model.db.DBToken;
import com.filipblazekovic.totpy.model.inout.ExportToken;
import com.filipblazekovic.totpy.model.shared.Category;
import com.filipblazekovic.totpy.model.shared.HashAlgorithm;
import com.filipblazekovic.totpy.model.shared.Type;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;
import org.apache.commons.codec.binary.Base32;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Token {

  private Integer id;

  private Issuer issuer;

  private String account;

  private Category category;

  private Type type;

  private HashAlgorithm algorithm;

  private int digits;

  private int period;

  private byte[] secret;

  private String otp;

  private int remainingSeconds;

  private boolean selected;

  private boolean visible;

  public boolean equals(Object obj) {
    if (!(obj instanceof Token)) {
      return false;
    }
    final Token that = (Token) obj;
    return this.getId().equals(that.getId())
        && this.getAccount().equals(that.getAccount())
        && Arrays.equals(this.secret, that.secret)
        && this.getOtp().equals(that.getOtp())
        && this.getRemainingSeconds() == that.getRemainingSeconds()
        && this.isSelected() == that.isSelected()
        && this.isVisible() == that.isVisible();
  }

  public void clear() {
    Arrays.fill(secret, (byte) '\0');
    this.otp = "******";
    this.remainingSeconds = period;
  }

  public Token merge(Token updatedToken) {
    return new Token(
        this.id,
        updatedToken.getIssuer(),
        updatedToken.getAccount(),
        updatedToken.getCategory(),
        type,
        updatedToken.getAlgorithm(),
        updatedToken.getDigits(),
        updatedToken.getPeriod(),
        updatedToken.getSecret(),
        "******",
        updatedToken.getPeriod(),
        false,
        true
    );
  }

  public static Token getDefault() {
    val token = new Token();
    token.setCategory(Category.DEFAULT);
    token.setType(Type.TOTP);
    token.setAlgorithm(HashAlgorithm.SHA1);
    token.setDigits(6);
    token.setPeriod(30);
    token.setIssuer(Issuer.from(null));
    return token;
  }

  public static Token from(DBToken token, byte[] secret) {
    return new Token(
        token.getId(),
        new Issuer(token.getIssuerIcon(), token.getIssuerName()),
        token.getAccount(),
        token.getCategory(),
        token.getType(),
        token.getAlgorithm(),
        token.getDigits(),
        token.getPeriod(),
        secret,
        "******",
        token.getPeriod(),
        false,
        true
    );
  }

  public static Token from(Token token, String otp, int remainingSeconds) {
    return new Token(
        token.getId(),
        token.getIssuer(),
        token.getAccount(),
        token.getCategory(),
        token.getType(),
        token.getAlgorithm(),
        token.getDigits(),
        token.getPeriod(),
        token.getSecret(),
        otp,
        remainingSeconds,
        token.isSelected(),
        token.isVisible()
    );
  }

  public static Token from(
      Issuer issuer,
      String account,
      Category category,
      HashAlgorithm algorithm,
      int digits,
      int period,
      byte[] secret
  ) {
    return new Token(
        null,
        issuer,
        account,
        category,
        Type.TOTP,
        algorithm,
        digits,
        period,
        secret,
        "******",
        period,
        false,
        true
    );
  }

  public static Token from(ExportToken token) {
    return new Token(
        null,
        token.getIssuer(),
        token.getAccount(),
        token.getCategory(),
        token.getType(),
        token.getAlgorithm(),
        token.getDigits(),
        token.getPeriod(),
        new Base32().decode(token.getSecret()),
        "******",
        token.getPeriod(),
        false,
        true
    );
  }

  public static List<Token> from(List<ExportToken> tokens) {
    return tokens.stream().map(Token::from).collect(Collectors.toList());
  }

}
