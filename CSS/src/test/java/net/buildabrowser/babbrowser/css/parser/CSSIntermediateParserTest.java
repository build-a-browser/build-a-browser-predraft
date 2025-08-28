package net.buildabrowser.babbrowser.css.parser;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.css.cssom.CSSRule;
import net.buildabrowser.babbrowser.css.cssom.Declaration;
import net.buildabrowser.babbrowser.css.intermediate.QualifiedRule;
import net.buildabrowser.babbrowser.css.intermediate.SimpleBlock;
import net.buildabrowser.babbrowser.css.parser.helper.TestCSSTokenStream;
import net.buildabrowser.babbrowser.css.parser.imp.CSSIntermediateParserImp;
import net.buildabrowser.babbrowser.css.tokens.ColonToken;
import net.buildabrowser.babbrowser.css.tokens.EOFToken;
import net.buildabrowser.babbrowser.css.tokens.IdentToken;
import net.buildabrowser.babbrowser.css.tokens.LCBracketToken;
import net.buildabrowser.babbrowser.css.tokens.RCBracketToken;
import net.buildabrowser.babbrowser.css.tokens.SemicolonToken;
import net.buildabrowser.babbrowser.css.tokens.Token;
import net.buildabrowser.babbrowser.css.tokens.WhitespaceToken;

public class CSSIntermediateParserTest {
  
  private CSSIntermediateParserImp parser;

  @BeforeEach
  public void beforeEach() {
    this.parser = new CSSIntermediateParserImp();
  }

  @Test
  @DisplayName("Can parse an empty CSS stylesheet")
  public void canParseAnEmptyCSSStyleSheet() throws IOException {
    List<CSSRule> rules = parseTokens(EOFToken.create());
    Assertions.assertEquals(List.of(), rules);
  }

  @Test
  @DisplayName("Can parse a CSS stylesheet with empty qualified rule")
  public void canParseACSSStyleSheetWithEmptyQualifiedRule() throws IOException {
    List<CSSRule> rules = parseTokens(
      IdentToken.create("p"), LCBracketToken.create(), RCBracketToken.create(),
      EOFToken.create()
    );
    Assertions.assertEquals(List.of(new QualifiedRule(
      List.of(new IdentToken("p")),
      new SimpleBlock(new LCBracketToken(), List.of())
    )), rules);
  }

  @Test
  @DisplayName("Can parse a CSS stylesheet with qualified rule with tokens")
  public void canParseACSSStyleSheetWithQualifiedRuleWithTokens() throws IOException {
    List<CSSRule> rules = parseTokens(
      IdentToken.create("p"), LCBracketToken.create(), IdentToken.create("color"),
      ColonToken.create(), IdentToken.create("red"), RCBracketToken.create(),
      EOFToken.create()
    );
    Assertions.assertEquals(List.of(new QualifiedRule(
      List.of(new IdentToken("p")),
      new SimpleBlock(new LCBracketToken(), List.of(
        IdentToken.create("color"), ColonToken.create(), IdentToken.create("red")
      ))
    )), rules);
  }

  @Test
  @DisplayName("Can parse a CSS stylesheet with qualified rule with whitespace")
  public void canParseACSSStyleSheetWithQualifiedRuleWithWhitespace() throws IOException {
    List<CSSRule> rules = parseTokens(
      WhitespaceToken.create(), IdentToken.create("p"), WhitespaceToken.create(),
      LCBracketToken.create(), WhitespaceToken.create(), RCBracketToken.create(),
      WhitespaceToken.create(), EOFToken.create()
    );
    Assertions.assertEquals(List.of(new QualifiedRule(
      List.of(new IdentToken("p"), new WhitespaceToken()),
      new SimpleBlock(new LCBracketToken(), List.of(
        WhitespaceToken.create()
      ))
    )), rules);
  }

  @Test
  @DisplayName("Can parse the contents of an empty style rule")
  public void canParseTheContentsOfAnEmptyStyleRule() throws IOException {
    List<Declaration> contents = parser.consumeAStyleBlocksContents(TestCSSTokenStream.create());
    Assertions.assertEquals(List.of(), contents);
  }

  @Test
  @DisplayName("Can parse the contents of a style rule with a declaration")
  public void canParseTheContentsOfAStyleRuleWithADeclaration() throws IOException {
    List<Declaration> contents = parser.consumeAStyleBlocksContents(TestCSSTokenStream.create(
      IdentToken.create("color"), ColonToken.create(), IdentToken.create("red"), SemicolonToken.create()
    ));
    Assertions.assertEquals(List.of(
      Declaration.create("color", List.of(IdentToken.create("red")), false)
    ), contents);
  }

  @Test
  @DisplayName("Can parse the contents of a style rule with a declaration and whitespace")
  public void canParseTheContentsOfAStyleRuleWithADeclarationAndWhitespace() throws IOException {
    List<Declaration> contents = parser.consumeAStyleBlocksContents(TestCSSTokenStream.create(
      WhitespaceToken.create(), IdentToken.create("color"), WhitespaceToken.create(), ColonToken.create(),
      WhitespaceToken.create(), IdentToken.create("red"), WhitespaceToken.create(), SemicolonToken.create(),
      WhitespaceToken.create()
    ));
    Assertions.assertEquals(List.of(
      Declaration.create("color", List.of(IdentToken.create("red")), false)
    ), contents);
  }

  private List<CSSRule> parseTokens(Token... tokens) throws IOException {
    return parser.consumeAListOfRules(TestCSSTokenStream.create(tokens), true);
  }

}
