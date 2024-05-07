package com.filipblazekovic.totpy.model.inout;

import com.filipblazekovic.totpy.model.internal.Token;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Export {

  private OffsetDateTime creationDateTime;

  private List<ExportToken> tokens;

  public static Export from(List<Token> tokens) {
    return new Export(
        OffsetDateTime.now(),
        ExportToken.from(tokens)
    );
  }

}
