package net.buildabrowser.babbrowser.css.parser;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.css.cssom.CSSRuleList;
import net.buildabrowser.babbrowser.css.cssom.CSSStyleSheet;
import net.buildabrowser.babbrowser.css.cssom.Declaration;
import net.buildabrowser.babbrowser.css.cssom.StyleRule;
import net.buildabrowser.babbrowser.css.parser.helper.TestCSSTokenStream;
import net.buildabrowser.babbrowser.css.selector.ComplexSelector;
import net.buildabrowser.babbrowser.css.selector.TypeSelector;
import net.buildabrowser.babbrowser.css.tokens.ColonToken;
import net.buildabrowser.babbrowser.css.tokens.IdentToken;
import net.buildabrowser.babbrowser.css.tokens.LCBracketToken;
import net.buildabrowser.babbrowser.css.tokens.RCBracketToken;
import net.buildabrowser.babbrowser.css.tokens.Token;

public class CSSParserTest {

  private CSSParser parser;

  @BeforeEach
  public void beforeEach() {
    this.parser = CSSParser.create();
  }
  
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
      StyleRule.create(
        List.of(
          ComplexSelector.create(List.of(TypeSelector.create("p")))
        ),
        List.of(
          Declaration.create("color", List.of(IdentToken.create("red")), false)
        )
      )
    ))), styleSheet);
  }
  
  private CSSStyleSheet parseTokens(Token... tokens) throws IOException {
    return parser.parseAStyleSheet(TestCSSTokenStream.create(tokens));
  }

}
