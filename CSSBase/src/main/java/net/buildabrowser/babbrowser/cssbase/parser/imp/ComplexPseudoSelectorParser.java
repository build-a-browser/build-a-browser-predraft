package net.buildabrowser.babbrowser.cssbase.parser.imp;

import static net.buildabrowser.babbrowser.cssbase.parser.imp.ComplexSelectorParser.ignoreWhitespace;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import net.buildabrowser.babbrowser.cssbase.intermediate.FunctionValue;
import net.buildabrowser.babbrowser.cssbase.microsyntax.ANPlusB;
import net.buildabrowser.babbrowser.cssbase.microsyntax.ANPlusBParser;
import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStreamSource;
import net.buildabrowser.babbrowser.cssbase.parser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.selector.ComplexSelector;
import net.buildabrowser.babbrowser.cssbase.selector.LogicalPseudoSelector;
import net.buildabrowser.babbrowser.cssbase.selector.LogicalPseudoSelector.LogicalPseudoSelectorType;
import net.buildabrowser.babbrowser.cssbase.selector.NthChildPseudoSelector;
import net.buildabrowser.babbrowser.cssbase.selector.NthChildPseudoSelector.NthChildPseudoSelectorType;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorPart;
import net.buildabrowser.babbrowser.cssbase.selector.SimplePseudoElement;
import net.buildabrowser.babbrowser.cssbase.selector.SimplePseudoSelector;
import net.buildabrowser.babbrowser.cssbase.selector.UniversalSelector;
import net.buildabrowser.babbrowser.cssbase.tokens.ColonToken;
import net.buildabrowser.babbrowser.cssbase.tokens.EOFToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;

public final class ComplexPseudoSelectorParser {

  private static final Set<String> LEGACY_PSEUDO_ELEMENTS = Set.of(
    "first-line", "first-letter", "before", "after");
  
  private static final Set<String> SIMPLE_CHILD_SELECTORS = Set.of(
    "first-child", "last-child", "only-child",
    "first-of-type", "last-of-type", "only-of-type");

  private ComplexPseudoSelectorParser() {}

  public static boolean parsePseudoSelector(
    CSSTokenStream tokenStream, List<SelectorPart> parts
  ) throws IOException {
    Token nextToken = tokenStream.peek();
    if (nextToken instanceof ColonToken) {
      tokenStream.read();
      return parsePseudoElement(tokenStream, parts);
    } else if (nextToken instanceof FunctionValue functionValue) {
      tokenStream.read();
      return parseComplexPseudoSelector(functionValue, parts, tokenStream.source());
    }
    if (!(nextToken instanceof IdentToken identToken)) return true;
    String selectorName = identToken.value();
    SimplePseudoSelector matchingSimplePseudoSelector = SimplePseudoSelector.lookupType(selectorName);
    if (matchingSimplePseudoSelector == null) {
      if (SIMPLE_CHILD_SELECTORS.contains(selectorName)) {
        tokenStream.read();
        return parseSimpleChildSelector(selectorName, parts);
      }
      if (LEGACY_PSEUDO_ELEMENTS.contains(selectorName)) {
        return parsePseudoElement(tokenStream, parts);
      }
      return true;
    }
    tokenStream.read();
    parts.add(matchingSimplePseudoSelector);
    return false;
  }

  private static boolean parsePseudoElement(
    CSSTokenStream tokenStream, List<SelectorPart> parts
  ) throws IOException {
    Token nextToken = tokenStream.read();
    if (!(nextToken instanceof IdentToken identToken)) return true;
    String className = identToken.value();
    SimplePseudoElement matchingSimplePseudoClass = SimplePseudoElement.lookupType(className);
    if (matchingSimplePseudoClass == null) return true;
    parts.add(matchingSimplePseudoClass);
    return false;
  }

  private static boolean parseComplexPseudoSelector(
    FunctionValue functionValue, List<SelectorPart> parts,
    CSSTokenStreamSource source
  ) throws IOException {
    return switch (functionValue.name()) {
      case "is", "where", "not", "has" -> parseLogicalPseudoSelector(functionValue, parts, source);
      case "nth-child", "nth-last-child" -> parseComplexChildSelector(functionValue, parts, source);
      default -> true;
    };
  }

  private static boolean parseLogicalPseudoSelector(
    FunctionValue functionValue, List<SelectorPart> parts,
    CSSTokenStreamSource source
  ) throws IOException {
    LogicalPseudoSelectorType type = switch (functionValue.name()) {
      case "is" -> LogicalPseudoSelectorType.IS;
      case "where" -> LogicalPseudoSelectorType.WHERE;
      case "not" -> LogicalPseudoSelectorType.NOT;
      case "has" -> LogicalPseudoSelectorType.HAS;
      default -> null;
    };
    if (type == null) return true;

    CSSTokenStream tokenStream = CSSTokenStream.create(source, functionValue.value());
    List<ComplexSelector> subSelectors = ComplexSelectorParser.parseComplexSelectors(
      tokenStream,
      type.equals(LogicalPseudoSelectorType.HAS));
    parts.add(new LogicalPseudoSelector(type, subSelectors));
    return false;
  }

  private static boolean parseSimpleChildSelector(
    String selectorName, List<SelectorPart> parts
  ) {
    NthChildPseudoSelectorType type = switch (selectorName) {
      case "first-child", "first-of-type" -> NthChildPseudoSelectorType.NTH;
      case "last-child", "last-of-type" -> NthChildPseudoSelectorType.NTH_LAST;
      case "only-child", "only-of-type" -> NthChildPseudoSelectorType.ONLY_CHILD;
      default -> null;
    };
    if (type == null) return true;

    NthChildPseudoSelector selector = new NthChildPseudoSelector(
      type,
      ANPlusB.create(0, 1),
      UniversalSelector.AS_COMPLEX_SELECTOR);
    parts.add(selector);
    return false;
  }

    private static boolean parseComplexChildSelector(
    FunctionValue functionValue, List<SelectorPart> parts,
    CSSTokenStreamSource source
  ) throws IOException {
    NthChildPseudoSelectorType type = switch (functionValue.name()) {
      case "nth-child" -> NthChildPseudoSelectorType.NTH;
      case "nth-last-child" -> NthChildPseudoSelectorType.NTH_LAST;
      default -> null;
    };
    if (type == null) return true;

    SeekableCSSTokenStream tokenStream = CSSTokenStream.create(
      source, functionValue.value());
    ANPlusB anPlusB = ANPlusBParser.parse(tokenStream);
    if (anPlusB == null) return true;


    ignoreWhitespace(tokenStream);
    ComplexSelector subSelector = UniversalSelector.AS_COMPLEX_SELECTOR;
    if (
      tokenStream.peek() instanceof IdentToken identToken
      && identToken.value().equals("of")
    ) {
      tokenStream.read();
      ignoreWhitespace(tokenStream);
      subSelector = ComplexSelectorParser.parseComplexSelector(
        tokenStream, false);
      if (subSelector == null) return true;
    }
    ignoreWhitespace(tokenStream);

    if (!(
      tokenStream.peek() instanceof EOFToken
    )) return true;

    parts.add(new NthChildPseudoSelector(
      type, anPlusB, subSelector));
    return false;
  }
  
}
