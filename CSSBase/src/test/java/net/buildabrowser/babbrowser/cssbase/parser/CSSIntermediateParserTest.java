package net.buildabrowser.babbrowser.cssbase.parser;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.cssbase.cssom.AtRule;
import net.buildabrowser.babbrowser.cssbase.cssom.CSSDeclarationList;
import net.buildabrowser.babbrowser.cssbase.cssom.CSSRule;
import net.buildabrowser.babbrowser.cssbase.cssom.CSSRuleOrDeclarations;
import net.buildabrowser.babbrowser.cssbase.cssom.Declaration;
import net.buildabrowser.babbrowser.cssbase.intermediate.FunctionValue;
import net.buildabrowser.babbrowser.cssbase.intermediate.QualifiedRule;
import net.buildabrowser.babbrowser.cssbase.parser.imp.CSSIntermediateParserImp;
import net.buildabrowser.babbrowser.cssbase.tokens.AtKeywordToken;
import net.buildabrowser.babbrowser.cssbase.tokens.ColonToken;
import net.buildabrowser.babbrowser.cssbase.tokens.CommaToken;
import net.buildabrowser.babbrowser.cssbase.tokens.DelimToken;
import net.buildabrowser.babbrowser.cssbase.tokens.EOFToken;
import net.buildabrowser.babbrowser.cssbase.tokens.FunctionToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.LCBracketToken;
import net.buildabrowser.babbrowser.cssbase.tokens.NumberToken;
import net.buildabrowser.babbrowser.cssbase.tokens.RCBracketToken;
import net.buildabrowser.babbrowser.cssbase.tokens.RParenToken;
import net.buildabrowser.babbrowser.cssbase.tokens.SemicolonToken;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;
import net.buildabrowser.babbrowser.cssbase.tokens.WhitespaceToken;

public class CSSIntermediateParserTest {

  private static final CSSTokenStreamSource TEST_SOURCE = new CSSTokenStreamSource(
    CommonUtil.rethrow(() -> new URI("about:blank")));
  
  private final CSSIntermediateParserImp parser = new CSSIntermediateParserImp();

  @Test
  @DisplayName("Can parse an empty CSS stylesheet")
  public void canParseAnEmptyCSSStyleSheet() throws IOException {
    List<CSSRule> rules = parseTokens(EOFToken.create());
    Assertions.assertEquals(List.of(), rules);
  }

  @Test
  @DisplayName("Can parse a CSS stylesheet with an empty qualified rule")
  public void canParseACSSStyleSheetWithAnEmptyQualifiedRule() throws IOException {
    List<CSSRule> rules = parseTokens(
      IdentToken.create("p"), LCBracketToken.create(), RCBracketToken.create(),
      EOFToken.create()
    );
    Assertions.assertEquals(List.of(new QualifiedRule(
      List.of(new IdentToken("p")),
      List.of(),
      List.of()
    )), rules);
  }

  @Test
  @DisplayName("Can parse a CSS stylesheet with a qualified rule with tokens")
  public void canParseACSSStyleSheetWithAQualifiedRuleWithTokens() throws IOException {
    List<CSSRule> rules = parseTokens(
      IdentToken.create("p"), LCBracketToken.create(), IdentToken.create("color"),
      ColonToken.create(), IdentToken.create("red"), RCBracketToken.create(),
      EOFToken.create()
    );
    Assertions.assertEquals(List.of(new QualifiedRule(
      List.of(new IdentToken("p")),
      List.of(
        declaration("color", List.of(IdentToken.create("red")))),
      List.of()
    )), rules);
  }

  @Test
  @DisplayName("Can parse a CSS stylesheet with a qualified rule with whitespace")
  public void canParseACSSStyleSheetWithAQualifiedRuleWithWhitespace() throws IOException {
    List<CSSRule> rules = parseTokens(
      WhitespaceToken.create(), IdentToken.create("p"), WhitespaceToken.create(),
      LCBracketToken.create(), WhitespaceToken.create(), RCBracketToken.create(),
      WhitespaceToken.create(), EOFToken.create()
    );
    Assertions.assertEquals(List.of(new QualifiedRule(
      List.of(new IdentToken("p"), new WhitespaceToken()),
      List.of(),
      List.of()
    )), rules);
  }

  @Test
  @DisplayName("Can parse a CSS stylesheet with an empty at rule")
  public void canParseACSSStyleSheetWithAnEmptyAtRule() throws IOException {
    List<CSSRule> rules = parseTokens(
      AtKeywordToken.create("media"), IdentToken.create("screen"),
      LCBracketToken.create(), RCBracketToken.create(), EOFToken.create()
    );
    Assertions.assertEquals(List.of(new AtRule(
      AtKeywordToken.create("media"),
      List.of(new IdentToken("screen")),
      List.of()
    )), rules);
  }

  @Test
  @DisplayName("Can parse the contents of an empty style rule")
  public void canParseTheContentsOfAnEmptyStyleRule() throws IOException {
    List<CSSRuleOrDeclarations> contents = parseBlock();
    Assertions.assertEquals(List.of(), contents);
  }

  @Test
  @DisplayName("Can parse the contents of a style rule with a declaration")
  public void canParseTheContentsOfAStyleRuleWithADeclaration() throws IOException {
    List<CSSRuleOrDeclarations> contents = parseBlock(
      IdentToken.create("color"), ColonToken.create(), IdentToken.create("red"), SemicolonToken.create()
    );
    Assertions.assertEquals(declarationBlock(
      Declaration.create(TEST_SOURCE, "color", List.of(IdentToken.create("red")), false)
    ), contents);
  }

  @Test
  @DisplayName("Can parse the contents of a style rule with a declaration and whitespace")
  public void canParseTheContentsOfAStyleRuleWithADeclarationAndWhitespace() throws IOException {
    List<CSSRuleOrDeclarations> contents = parseBlock(
      WhitespaceToken.create(), IdentToken.create("color"), WhitespaceToken.create(), ColonToken.create(),
      WhitespaceToken.create(), IdentToken.create("red"), WhitespaceToken.create(), SemicolonToken.create(),
      WhitespaceToken.create()
    );
    Assertions.assertEquals(declarationBlock(
      Declaration.create(TEST_SOURCE, "color", List.of(IdentToken.create("red")), false)
    ), contents);
  }
  
  @Test
  @DisplayName("Can parse the contents of a style rule with a declaration that is important")
  public void canParseTheContentsOfAStyleRuleWithADeclarationThatIsImportant() throws IOException {
    List<CSSRuleOrDeclarations> contents = parseBlock(
      IdentToken.create("color"), ColonToken.create(), IdentToken.create("red"),
      DelimToken.create('!'), IdentToken.create("important"), SemicolonToken.create()
    );
    Assertions.assertEquals(declarationBlock(
      Declaration.create(TEST_SOURCE, "color", List.of(IdentToken.create("red")), true)
    ), contents);
  }

  @Test
  @DisplayName("Can parse a CSS stylesheet referencing a function")
  public void canParseAFunction() throws IOException {
    List<CSSRuleOrDeclarations> contents = parseBlock(
      IdentToken.create("color"), ColonToken.create(),
      FunctionToken.create("rgb"),
      NumberToken.create(0), CommaToken.create(),
      NumberToken.create(0), CommaToken.create(),
      NumberToken.create(0), CommaToken.create(),
      RParenToken.create()
    );
    Assertions.assertEquals(declarationBlock(
      Declaration.create(TEST_SOURCE, "color", List.of(new FunctionValue("rgb", List.of(
        NumberToken.create(0), CommaToken.create(),
        NumberToken.create(0), CommaToken.create(),
        NumberToken.create(0), CommaToken.create()
      ))), false)
    ), contents);
  }
  
  @Test
  @DisplayName("Can parse a media query with one declaration in a style rule")
  public void canParseAMediaQueryWithOneDeclarationInAStyleRule() throws IOException {
    List<CSSRule> rules = parseTokens(
      AtKeywordToken.create("media"), IdentToken.create("screen"), LCBracketToken.create(),
      IdentToken.create("p"), LCBracketToken.create(),
      IdentToken.create("color"), ColonToken.create(), IdentToken.create("red"),
      RCBracketToken.create(),
      RCBracketToken.create(),
      EOFToken.create()
    );
    Assertions.assertEquals(List.of(new AtRule(
      AtKeywordToken.create("media"),
      List.of(new IdentToken("screen")),
      List.of(new QualifiedRule(
        List.of(new IdentToken("p")),
        List.of(declaration("color", List.of(IdentToken.create("red")))),
        List.of()
      ))
    )), rules);
  }

  @Test
  @DisplayName("Can parse a style rule in a style rule")
  public void canParseAStyleRuleInAStyleRule() throws IOException {
    List<CSSRule> rules = parseTokens(
      IdentToken.create("p"), LCBracketToken.create(),
      IdentToken.create("span"), LCBracketToken.create(),
      IdentToken.create("color"), ColonToken.create(), IdentToken.create("red"),
      RCBracketToken.create(),
      RCBracketToken.create(),
      EOFToken.create()
    );
    Assertions.assertEquals(List.of(new QualifiedRule(
      List.of(new IdentToken("p")),
      List.of(),
      List.of(new QualifiedRule(
        List.of(new IdentToken("span")),
        List.of(declaration("color", List.of(IdentToken.create("red")))),
        List.of()
      ))
    )), rules);
  }

  private List<CSSRuleOrDeclarations> declarationBlock(Declaration... declarations) {
    return List.of(new CSSDeclarationList(List.of(declarations)));
  }

  private Declaration declaration(String name, List<Token> value) {
    return Declaration.create(TEST_SOURCE, name, value, false);
  }

  private List<CSSRule> parseTokens(Token... tokens) throws IOException {
    return parser.consumeAStylesheetsContents(CSSTokenStream.createForTesting(tokens), true);
  }

  private List<CSSRuleOrDeclarations> parseBlock(Token... tokens) throws IOException {
    return parser.consumeABlocksContents(
      CSSTokenStream.createForTesting(tokens));
  }

}
