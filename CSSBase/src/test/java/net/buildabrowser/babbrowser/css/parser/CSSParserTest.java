package net.buildabrowser.babbrowser.css.parser;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.cssom.CSSRuleList;
import net.buildabrowser.babbrowser.cssbase.cssom.CSSStyleSheet;
import net.buildabrowser.babbrowser.cssbase.cssom.Declaration;
import net.buildabrowser.babbrowser.cssbase.cssom.StyleRule;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.selector.AttributeSelector;
import net.buildabrowser.babbrowser.cssbase.selector.AttributeSelector.AttributeType;
import net.buildabrowser.babbrowser.cssbase.selector.ComplexSelector;
import net.buildabrowser.babbrowser.cssbase.selector.IdSelector;
import net.buildabrowser.babbrowser.cssbase.selector.TypeSelector;
import net.buildabrowser.babbrowser.cssbase.tokens.ColonToken;
import net.buildabrowser.babbrowser.cssbase.tokens.CommaToken;
import net.buildabrowser.babbrowser.cssbase.tokens.DelimToken;
import net.buildabrowser.babbrowser.cssbase.tokens.HashToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.LCBracketToken;
import net.buildabrowser.babbrowser.cssbase.tokens.RCBracketToken;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;
import net.buildabrowser.babbrowser.cssbase.tokens.WhitespaceToken;

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

  @Test
  @DisplayName("Can parse a stylesheet with a complex selector")
  public void canParseAStylesheetWithAComplexSelector() throws IOException {
    CSSStyleSheet styleSheet = parseTokens(
      IdentToken.create("p"),
      DelimToken.create('.'), IdentToken.create("highlighted"),
      HashToken.create("my-item", HashToken.Type.ID),
      LCBracketToken.create(), RCBracketToken.create()
    );
    Assertions.assertEquals(CSSStyleSheet.create(CSSRuleList.create(List.of(
      StyleRule.create(
        List.of(
          ComplexSelector.create(List.of(
            TypeSelector.create("p"),
            AttributeSelector.create("class", "highlighted", AttributeType.ONE_OF),
            IdSelector.create("my-item")
          ))
        ),
        List.of()
      )
    ))), styleSheet);
  }

  @Test
  @DisplayName("Can parse a stylesheet with a complex selector list")
  public void canParseAStylesheetWithAComplexSelectorList() throws IOException {
    CSSStyleSheet styleSheet = parseTokens(
      IdentToken.create("p"), new CommaToken(), new WhitespaceToken(),
      DelimToken.create('.'), IdentToken.create("highlighted"),
      HashToken.create("my-item", HashToken.Type.ID),
      new WhitespaceToken(),
      LCBracketToken.create(), RCBracketToken.create()
    );
    Assertions.assertEquals(CSSStyleSheet.create(CSSRuleList.create(List.of(
      StyleRule.create(
        List.of(
          ComplexSelector.create(List.of(
            TypeSelector.create("p")
          )),
          ComplexSelector.create(List.of(
            AttributeSelector.create("class", "highlighted", AttributeType.ONE_OF),
            IdSelector.create("my-item")
          ))
        ),
        List.of()
      )
    ))), styleSheet);
  }
  
  private CSSStyleSheet parseTokens(Token... tokens) throws IOException {
    return parser.parseAStyleSheet(CSSTokenStream.create(tokens));
  }

}
