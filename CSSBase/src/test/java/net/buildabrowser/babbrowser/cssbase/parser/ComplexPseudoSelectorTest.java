package net.buildabrowser.babbrowser.cssbase.parser;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.intermediate.FunctionValue;
import net.buildabrowser.babbrowser.cssbase.microsyntax.ANPlusB;
import net.buildabrowser.babbrowser.cssbase.parser.imp.ComplexSelectorParser;
import net.buildabrowser.babbrowser.cssbase.selector.ChildCombinator;
import net.buildabrowser.babbrowser.cssbase.selector.ComplexSelector;
import net.buildabrowser.babbrowser.cssbase.selector.DescendantCombinator;
import net.buildabrowser.babbrowser.cssbase.selector.IdSelector;
import net.buildabrowser.babbrowser.cssbase.selector.LogicalPseudoSelector;
import net.buildabrowser.babbrowser.cssbase.selector.LogicalPseudoSelector.LogicalPseudoSelectorType;
import net.buildabrowser.babbrowser.cssbase.selector.NthChildPseudoSelector;
import net.buildabrowser.babbrowser.cssbase.selector.NthChildPseudoSelector.NthChildPseudoSelectorType;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorPart;
import net.buildabrowser.babbrowser.cssbase.selector.SimplePseudoElement;
import net.buildabrowser.babbrowser.cssbase.selector.SimplePseudoSelector;
import net.buildabrowser.babbrowser.cssbase.selector.TypeSelector;
import net.buildabrowser.babbrowser.cssbase.selector.UniversalSelector;
import net.buildabrowser.babbrowser.cssbase.tokens.ColonToken;
import net.buildabrowser.babbrowser.cssbase.tokens.CommaToken;
import net.buildabrowser.babbrowser.cssbase.tokens.DelimToken;
import net.buildabrowser.babbrowser.cssbase.tokens.HashToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.NumberToken;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;
import net.buildabrowser.babbrowser.cssbase.tokens.WhitespaceToken;

public class ComplexPseudoSelectorTest {

  @Test
  @DisplayName("Can parse simple pseudo selector")
  public void canParseSimplePsuedoSelector() throws IOException {
    List<ComplexSelector> actualSelectors = parseTokens(
      ColonToken.create(), IdentToken.create("root"));
    List<ComplexSelector> expectedSelectors = oneSelector(
      SimplePseudoSelector.ROOT);
    Assertions.assertEquals(expectedSelectors, actualSelectors);
  }

  @Test
  @DisplayName("Can parse simple pseudo element")
  public void canParseSimplePsuedoElement() throws IOException {
    List<ComplexSelector> actualSelectors = parseTokens(
      ColonToken.create(), ColonToken.create(), IdentToken.create("before"));
    List<ComplexSelector> expectedSelectors = oneSelector(
      SimplePseudoElement.BEFORE);
    Assertions.assertEquals(expectedSelectors, actualSelectors);
  }

  @Test
  @DisplayName("Can parse simple pseudo element in legacy format")
  public void canParseSimplePsuedoElementInLegacyFormat() throws IOException {
    List<ComplexSelector> actualSelectors = parseTokens(
      ColonToken.create(), IdentToken.create("after"));
    List<ComplexSelector> expectedSelectors = oneSelector(
      SimplePseudoElement.AFTER);
    Assertions.assertEquals(expectedSelectors, actualSelectors);
  }
  
  @Test
  @DisplayName("Can parse normal logical selector")
  public void canParseNormalLogicalSelector() throws IOException {
    List<ComplexSelector> actualSelectors = parseTokens(
      ColonToken.create(),
      new FunctionValue("is", List.of(
        IdentToken.create("span"),
        WhitespaceToken.create(),
        HashToken.create("id_a", HashToken.Type.ID),
        CommaToken.create(),
        IdentToken.create("div"))));
    List<ComplexSelector> expectedSelectors = oneSelector(
      new LogicalPseudoSelector(
        LogicalPseudoSelectorType.IS,
        List.of(
          ComplexSelector.create(List.of(
            TypeSelector.create("span"),
            DescendantCombinator.create(),
            IdSelector.create("id_a"))),
          ComplexSelector.create(List.of(
            TypeSelector.create("div")))
        )));
    System.out.println(expectedSelectors.get(0) + " " + actualSelectors.get(0));
    Assertions.assertEquals(expectedSelectors, actualSelectors);
  }
  
  @Test
  @DisplayName("Can parse relative logical selector")
  public void canParseRelativeLogicalSelector() throws IOException {
    List<ComplexSelector> actualSelectors = parseTokens(
      ColonToken.create(),
      new FunctionValue("has", List.of(
        DelimToken.create('>'),
        HashToken.create("id_a", HashToken.Type.ID),
        CommaToken.create(),
        IdentToken.create("div"))));
    List<ComplexSelector> expectedSelectors = oneSelector(
      new LogicalPseudoSelector(
        LogicalPseudoSelectorType.HAS,
        List.of(
          ComplexSelector.create(List.of(
            ChildCombinator.create(),
            IdSelector.create("id_a"))),
          ComplexSelector.create(List.of(
            DescendantCombinator.create(),
            TypeSelector.create("div")))
        )));
    System.out.println(expectedSelectors.get(0) + " " + actualSelectors.get(0));
    Assertions.assertEquals(expectedSelectors, actualSelectors);
  }

  @Test
  @DisplayName("Can parse nth-child pseudo-selector")
  public void canParseNthChildPseudoSelector() throws IOException {
    List<ComplexSelector> actualSelectors = parseTokens(
      ColonToken.create(),
      new FunctionValue("nth-child", List.of(
        IdentToken.create("even"))));
    List<ComplexSelector> expectedSelectors = oneSelector(
      new NthChildPseudoSelector(
        NthChildPseudoSelectorType.NTH,
        ANPlusB.create(2, 0),
        UniversalSelector.AS_COMPLEX_SELECTOR));
    Assertions.assertEquals(expectedSelectors, actualSelectors);
  }

  @Test
  @DisplayName("Can parse nth-child pseudo-selector with subselector")
  public void canParseNthChildPseudoSelectorWithSubselector() throws IOException {
    List<ComplexSelector> actualSelectors = parseTokens(
      ColonToken.create(),
      new FunctionValue("nth-child", List.of(
        NumberToken.create(4),
        IdentToken.create("of"),
        IdentToken.create("span"))));
    List<ComplexSelector> expectedSelectors = oneSelector(
      new NthChildPseudoSelector(
        NthChildPseudoSelectorType.NTH,
        ANPlusB.create(0, 4),
        ComplexSelector.create(List.of(
          TypeSelector.create("span")))));
    Assertions.assertEquals(expectedSelectors, actualSelectors);
  }
  
  private List<ComplexSelector> parseTokens(Token... tokens) throws IOException {
    return ComplexSelectorParser.parseComplexSelectors(
      CSSTokenStream.createForTesting(tokens), false);
  }

  private List<ComplexSelector> oneSelector(SelectorPart... parts) {
    return List.of(
      ComplexSelector.create(List.of(parts)));
  }
  
}
