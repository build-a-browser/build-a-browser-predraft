package net.buildabrowser.babbrowser.cssbase.parser;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.cssbase.cssom.CSSRuleList;
import net.buildabrowser.babbrowser.cssbase.cssom.CSSStyleSheet;
import net.buildabrowser.babbrowser.cssbase.cssom.Declaration;
import net.buildabrowser.babbrowser.cssbase.cssom.StyleRule;
import net.buildabrowser.babbrowser.cssbase.selector.ComplexSelector;
import net.buildabrowser.babbrowser.cssbase.selector.TypeSelector;
import net.buildabrowser.babbrowser.cssbase.tokens.ColonToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.LCBracketToken;
import net.buildabrowser.babbrowser.cssbase.tokens.RCBracketToken;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;

public class CSSParserTest {

  private static final CSSTokenStreamSource TEST_SOURCE = new CSSTokenStreamSource(
    CommonUtil.rethrow(() -> new URI("about:blank")));
  
  private final CSSParser parser  = CSSParser.create();
  
  @Test
  @DisplayName("Can parse an empty stylesheet")
  public void canParseAnEmptyStylesheet() throws IOException {
    CSSStyleSheet styleSheet = parseTokens();
    Assertions.assertEquals(CSSStyleSheet.create(CSSRuleList.create(List.of())), styleSheet);
  }

  @Test
  @DisplayName("Can parse a stylesheet with a rule")
  public void canParseAStylesheetWithARule() throws IOException {
    CSSStyleSheet styleSheet = parseTokens(
      IdentToken.create("p"), LCBracketToken.create(), IdentToken.create("color"),
      ColonToken.create(), IdentToken.create("red"), RCBracketToken.create()
    );
    Assertions.assertEquals(CSSStyleSheet.create(CSSRuleList.create(List.of(
      new StyleRule(
        List.of(
          ComplexSelector.create(List.of(TypeSelector.create("p")))),
        List.of(
          Declaration.create(TEST_SOURCE, "color", List.of(IdentToken.create("red")), false))
      )
    ))), styleSheet);
  }
  
  private CSSStyleSheet parseTokens(Token... tokens) throws IOException {
    return parser.parseAStyleSheet(CSSTokenStream.createForTesting(tokens));
  }

}
