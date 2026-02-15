package net.buildabrowser.babbrowser.cssbase.parser;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.parser.imp.ComplexSelectorParser;
import net.buildabrowser.babbrowser.cssbase.selector.AttributeSelector;
import net.buildabrowser.babbrowser.cssbase.selector.ChildCombinator;
import net.buildabrowser.babbrowser.cssbase.selector.AttributeSelector.AttributeType;
import net.buildabrowser.babbrowser.cssbase.selector.ComplexSelector;
import net.buildabrowser.babbrowser.cssbase.selector.DescendantCombinator;
import net.buildabrowser.babbrowser.cssbase.selector.IdSelector;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorPart;
import net.buildabrowser.babbrowser.cssbase.selector.TypeSelector;
import net.buildabrowser.babbrowser.cssbase.selector.UniversalSelector;
import net.buildabrowser.babbrowser.cssbase.tokens.CommaToken;
import net.buildabrowser.babbrowser.cssbase.tokens.DelimToken;
import net.buildabrowser.babbrowser.cssbase.tokens.HashToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;
import net.buildabrowser.babbrowser.cssbase.tokens.WhitespaceToken;
import net.buildabrowser.babbrowser.cssbase.tokens.HashToken.Type;

public class ComplexSelectorParserTest {
  
  @Test
  @DisplayName("Can parse type selector")
  public void canParseTypeSelector() throws IOException {
    List<ComplexSelector> actualSelectors = parseTokens(
      IdentToken.create("p"));
    List<ComplexSelector> expectedSelectors = oneSelector(
      TypeSelector.create("p"));
    Assertions.assertEquals(expectedSelectors, actualSelectors);
  }

  @Test
  @DisplayName("Can parse class selector")
  public void canParseClassSelector() throws IOException {
    List<ComplexSelector> actualSelectors = parseTokens(
      DelimToken.create('.'), IdentToken.create("red"));
    List<ComplexSelector> expectedSelectors = oneSelector(
      AttributeSelector.create("class", "red", AttributeType.ONE_OF));
    Assertions.assertEquals(expectedSelectors, actualSelectors);
  }

  @Test
  @DisplayName("Can parse ID selector")
  public void canParseIDSelector() throws IOException {
    List<ComplexSelector> actualSelectors = parseTokens(
      HashToken.create("fuffuu", Type.ID));
    List<ComplexSelector> expectedSelectors = oneSelector(
      IdSelector.create("fuffuu"));
    Assertions.assertEquals(expectedSelectors, actualSelectors);
  }

  // TODO: Also test qualified name variants
  @Test
  @DisplayName("Can parse the universal selector")
  public void canParseTheUniversalSelector() throws IOException {
    List<ComplexSelector> actualSelectors = parseTokens(
      DelimToken.create('*'));
    List<ComplexSelector> expectedSelectors = oneSelector(
      UniversalSelector.create());
    Assertions.assertEquals(expectedSelectors, actualSelectors);
  }

  @Test
  @DisplayName("Can parse multipart selector")
  public void canParseMultipartSelector() throws IOException {
    List<ComplexSelector> actualSelectors = parseTokens(
      IdentToken.create("span"),
      HashToken.create("nav-container", Type.ID),
      DelimToken.create('.'), IdentToken.create("top"));
    List<ComplexSelector> expectedSelectors = oneSelector(
      TypeSelector.create("span"),
      IdSelector.create("nav-container"),
      AttributeSelector.create("class", "top", AttributeType.ONE_OF));
    Assertions.assertEquals(expectedSelectors, actualSelectors);
  }

  @Test
  @DisplayName("Can parse selector with symbol combinator")
  public void canParseSelectorWithSymbolCombinator() throws IOException {
    List<ComplexSelector> actualSelectors = parseTokens(
      IdentToken.create("div"),
      DelimToken.create('>'),
      IdentToken.create("span"));
    List<ComplexSelector> expectedSelectors = oneSelector(
      TypeSelector.create("div"),
      ChildCombinator.create(),
      TypeSelector.create("span"));
    Assertions.assertEquals(expectedSelectors, actualSelectors);
  }

  @Test
  @DisplayName("Can parse selector with descendant combinator")
  public void canParseSelectorWithDescendantCombinator() throws IOException {
    List<ComplexSelector> actualSelectors = parseTokens(
      IdentToken.create("div"),
      WhitespaceToken.create(),
      IdentToken.create("span"));
    List<ComplexSelector> expectedSelectors = oneSelector(
      TypeSelector.create("div"),
      DescendantCombinator.create(),
      TypeSelector.create("span"));
    Assertions.assertEquals(expectedSelectors, actualSelectors);
  }

  @Test
  @DisplayName("Can parse selector with whitespace-wrapped symbol combinator")
  public void canParseSelectorWithWhitespaceWrappedCombinator() throws IOException {
    List<ComplexSelector> actualSelectors = parseTokens(
      IdentToken.create("div"),
      WhitespaceToken.create(),
      DelimToken.create('>'),
      WhitespaceToken.create(),
      IdentToken.create("span"));
    List<ComplexSelector> expectedSelectors = oneSelector(
      TypeSelector.create("div"),
      ChildCombinator.create(),
      TypeSelector.create("span"));
    Assertions.assertEquals(expectedSelectors, actualSelectors);
  }

  @Test
  @DisplayName("Can parse multiple selectors")
  public void canParseMultipleSelectors() throws IOException {
    List<ComplexSelector> actualSelectors = parseTokens(
      IdentToken.create("div"),
      CommaToken.create(),
      IdentToken.create("input"));
    List<ComplexSelector> expectedSelectors = List.of(
      ComplexSelector.create(List.of(TypeSelector.create("div"))),
      ComplexSelector.create(List.of(TypeSelector.create("input"))));
    Assertions.assertEquals(expectedSelectors, actualSelectors);
  }

  @Test
  @DisplayName("Can ignore invalid selectors")
  public void canIgnoreInvalidSelectors() throws IOException {
    List<ComplexSelector> actualSelectors = parseTokens(
      IdentToken.create("div"),
      DelimToken.create('.'),
      CommaToken.create(),
      IdentToken.create("div"),
      DelimToken.create('~'),
      CommaToken.create(),
      DelimToken.create('~'),
      IdentToken.create("div"),
      CommaToken.create(),
      IdentToken.create("div"),
      DelimToken.create('~'),
      DelimToken.create('~'),
      IdentToken.create("div"),
      CommaToken.create(),
      IdentToken.create("input"));
    List<ComplexSelector> expectedSelectors = oneSelector(
      TypeSelector.create("input"));
    Assertions.assertEquals(expectedSelectors, actualSelectors);
  }
  
  private List<ComplexSelector> parseTokens(Token... tokens) throws IOException {
    return ComplexSelectorParser.parseComplexSelectors(List.of(tokens));
  }

  private List<ComplexSelector> oneSelector(SelectorPart... parts) {
    return List.of(
      ComplexSelector.create(List.of(parts))
    );
  }

}
