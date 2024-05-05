package com.filipblazekovic.totpy.model.inout;

import com.filipblazekovic.totpy.model.internal.Issuer;
import com.filipblazekovic.totpy.model.internal.Token;
import com.filipblazekovic.totpy.model.shared.Category;
import com.filipblazekovic.totpy.model.shared.HashAlgorithm;
import com.filipblazekovic.totpy.model.shared.Type;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.codec.binary.Base32;

@Data
@AllArgsConstructor
public class ExportToken {

  private Issuer issuer;

  private String account;

  private Category category;

  private Type type;

  private HashAlgorithm algorithm;

  private int digits;

  private int period;

  private String secret;

  public static ExportToken from(Token token) {
    return new ExportToken(
        token.getIssuer(),
        token.getAccount(),
        token.getCategory(),
        token.getType(),
        token.getAlgorithm(),
        token.getDigits(),
        token.getPeriod(),
        new Base32().encodeAsString(token.getSecret())
    );
  }

  public static List<ExportToken> from(List<Token> tokens) {
    return tokens.stream().map(ExportToken::from).collect(Collectors.toList());
  }

}
