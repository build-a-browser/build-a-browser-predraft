package net.buildabrowser.babbrowser.cssbase.parser.imp;

import static net.buildabrowser.babbrowser.common.util.CompatUtil.getFirst;
import static net.buildabrowser.babbrowser.common.util.CompatUtil.getLast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.buildabrowser.babbrowser.cssbase.intermediate.FunctionValue;
import net.buildabrowser.babbrowser.cssbase.intermediate.SimpleBlock;
import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStreamSource;
import net.buildabrowser.babbrowser.cssbase.selector.AttributeSelector;
import net.buildabrowser.babbrowser.cssbase.selector.AttributeSelector.AttributeType;
import net.buildabrowser.babbrowser.cssbase.selector.LogicalPseudoSelector.LogicalPseudoSelectorType;
import net.buildabrowser.babbrowser.cssbase.selector.ChildCombinator;
import net.buildabrowser.babbrowser.cssbase.selector.Combinator;
import net.buildabrowser.babbrowser.cssbase.selector.ComplexSelector;
import net.buildabrowser.babbrowser.cssbase.selector.DescendantCombinator;
import net.buildabrowser.babbrowser.cssbase.selector.IdSelector;
import net.buildabrowser.babbrowser.cssbase.selector.LogicalPseudoSelector;
import net.buildabrowser.babbrowser.cssbase.selector.NextSiblingCombinator;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorPart;
import net.buildabrowser.babbrowser.cssbase.selector.SimplePseudoElement;
import net.buildabrowser.babbrowser.cssbase.selector.SimplePseudoSelector;
import net.buildabrowser.babbrowser.cssbase.selector.SubsequentSiblingCombinator;
import net.buildabrowser.babbrowser.cssbase.selector.TypeSelector;
import net.buildabrowser.babbrowser.cssbase.selector.UniversalSelector;
import net.buildabrowser.babbrowser.cssbase.tokens.ColonToken;
import net.buildabrowser.babbrowser.cssbase.tokens.CommaToken;
import net.buildabrowser.babbrowser.cssbase.tokens.DelimToken;
import net.buildabrowser.babbrowser.cssbase.tokens.EOFToken;
import net.buildabrowser.babbrowser.cssbase.tokens.HashToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.LSBracketToken;
import net.buildabrowser.babbrowser.cssbase.tokens.StringToken;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;
import net.buildabrowser.babbrowser.cssbase.tokens.WhitespaceToken;

public final class ComplexSelectorParser {

  private static final Set<String> LEGACY_PSEUDO_ELEMENTS = Set.of(
    "first-line", "first-letter", "before", "after");
  
  private ComplexSelectorParser() {}

  public static List<ComplexSelector> parseComplexSelectors(
    CSSTokenStream tokenStream, boolean isRelative
  ) throws IOException {
    List<ComplexSelector> selectors = new ArrayList<>(1);
    while (!(tokenStream.peek() instanceof EOFToken)) {
      ComplexSelector selector = parseComplexSelector(tokenStream, isRelative);
      if (
        selector != null
        && verifyComplexSelector(selector, isRelative)
      ) {
        selectors.add(selector);
      }
      if (tokenStream.peek() instanceof CommaToken) {
        tokenStream.read();
      }
    }

    return selectors;
  }
  
  private static ComplexSelector parseComplexSelector(
    CSSTokenStream tokenStream,
    boolean isRelative
  ) throws IOException {
    boolean didEncounterWhitespace = isRelative;
    boolean didEncounterCombinator = false;
    boolean isInvalid = false;
    List<SelectorPart> parts = new ArrayList<>(1);
    while (!(
      tokenStream.peek() instanceof EOFToken
      || tokenStream.peek() instanceof CommaToken
    )) {
      // TODO: Ensure a parse failing on a comma will always preserve that comma for the outer loop
      Token currentToken = tokenStream.read();
      boolean isWhitespace = currentToken instanceof WhitespaceToken;
      boolean isCombinatorDelim = isCombinatorDelimToken(currentToken);
      isInvalid |= isCombinatorDelim && didEncounterCombinator;
      didEncounterWhitespace |= isWhitespace;
      didEncounterCombinator |= isCombinatorDelim;
      if (!isCombinatorDelim && !isWhitespace) {
        if (
          didEncounterWhitespace && !didEncounterCombinator
          && (isRelative || !parts.isEmpty())
        ) {
          parts.add(DescendantCombinator.create());
        }
        didEncounterWhitespace = false;
        didEncounterCombinator = false;
      }
      isInvalid |= parseSelectorPart(currentToken, tokenStream, parts);
    }

    if (isInvalid || parts.isEmpty()) return null;
    return new ComplexSelector(parts);
  }

  private static boolean verifyComplexSelector(
    ComplexSelector complexSelector, boolean isRelative
  ) throws IOException {
    List<SelectorPart> parts = complexSelector.parts();
    return
      getFirst(parts) instanceof Combinator == isRelative
      && !(getLast(parts) instanceof Combinator);
  }

  private static boolean parseSelectorPart(
    Token currentToken, CSSTokenStream tokenStream, List<SelectorPart> parts
  ) throws IOException {
    boolean isInvalid = false;
    switch (currentToken) {
      case IdentToken identToken -> parts.add(TypeSelector.create(identToken.value()));
      case DelimToken delimToken -> isInvalid |= parseDelimToken(tokenStream, parts, delimToken);
      case HashToken hashToken -> {
        if (hashToken.type().equals(HashToken.Type.ID)) {
          parts.add(IdSelector.create(hashToken.value()));
        } else {
          isInvalid = true;
        }
      }
      case ColonToken _1 -> isInvalid |= parsePseudoSelector(tokenStream, parts);
      case SimpleBlock simpleBlock -> isInvalid |= parseAttributeSelector(
        tokenStream.source(), simpleBlock, parts);
      case WhitespaceToken _1 -> {}
      default -> isInvalid = true;
    }

    return isInvalid;
  }

  private static boolean parseDelimToken(
    CSSTokenStream tokenStream, List<SelectorPart> parts, DelimToken delimToken
  ) throws IOException {
    boolean isInvalid = false;
    switch (delimToken.ch()) {
      case '.' -> {
        // ignoreWhitespace(tokenStream); // TODO: ?
        if (tokenStream.peek() instanceof IdentToken identToken) {
          tokenStream.read();
          parts.add(AttributeSelector.create("class", identToken.value(), AttributeType.ONE_OF));
        } else {
          isInvalid = true;
        }
      }
      case '*' -> parts.add(UniversalSelector.create()); // TODO: Qualified variant
      case '>' -> parts.add(ChildCombinator.create());
      case '+' -> parts.add(NextSiblingCombinator.create());
      case '~' -> parts.add(SubsequentSiblingCombinator.create());
      default -> isInvalid = true;
    }

    return isInvalid;
  }

  private static boolean parseAttributeSelector(
    CSSTokenStreamSource source, SimpleBlock simpleBlock, List<SelectorPart> parts
  ) throws IOException {
    if (!(simpleBlock.type() instanceof LSBracketToken)) return true;

    CSSTokenStream tokenStream = ListCSSTokenStream.create(source, simpleBlock.value());
    ignoreWhitespace(tokenStream);
    String attrName = parseIdentOrString(tokenStream);
    if (attrName == null) return true;

    AttributeType attrType = AttributeType.HAS_ATTR;
    String attrValue = "";

    int delimValue = -1;
    if (tokenStream.peek() instanceof DelimToken delimToken) {
      tokenStream.read();
      delimValue = delimToken.ch();
    }
    if (delimValue != -1) {
      if (
        delimValue != '='
        && !(
          tokenStream.peek() instanceof DelimToken delimToken
          && delimToken.ch() == '='
      )) return true;
      if (delimValue != '=') tokenStream.read();

      attrType = switch (delimValue) {
        case '=' -> AttributeType.EXACTLY;
        case '~' -> AttributeType.ONE_OF;
        case '|' -> AttributeType.PREFIX;
        case '^' -> AttributeType.STARTS_WITH;
        case '$' -> AttributeType.ENDS_WITH;
        case '*' -> AttributeType.CONTAINS;
        default -> null;
      };

      if (attrType == null) return true;

      attrValue = parseIdentOrString(tokenStream);
      if (attrValue == null) return true;
    }

    ignoreWhitespace(tokenStream);
    if (!(tokenStream.read() instanceof EOFToken)) return true;

    parts.add(new AttributeSelector(attrName, attrValue, attrType));
    return false;
  }

  private static boolean parsePseudoSelector(
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
    // TODO: Another : means pseudo-class
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
      default -> true;
    };
  }

  private static boolean parseLogicalPseudoSelector(
    FunctionValue functionValue, List<SelectorPart> parts,
    CSSTokenStreamSource source
  ) throws IOException {
    CSSTokenStream tokenStream = CSSTokenStream.create(source, functionValue.value());
    LogicalPseudoSelectorType type = switch (functionValue.name()) {
      case "is" -> LogicalPseudoSelectorType.IS;
      case "where" -> LogicalPseudoSelectorType.WHERE;
      case "not" -> LogicalPseudoSelectorType.NOT;
      case "has" -> LogicalPseudoSelectorType.HAS;
      default -> null;
    };
    if (type == null) return true;

    List<ComplexSelector> subSelectors = parseComplexSelectors(
      tokenStream,
      type.equals(LogicalPseudoSelectorType.HAS));

    parts.add(new LogicalPseudoSelector(type, subSelectors));
    return false;
  }

  private static String parseIdentOrString(CSSTokenStream tokenStream) throws IOException {
    Token token = tokenStream.peek();
    if (token instanceof IdentToken identToken) {
      tokenStream.read();
      return identToken.value();
    } else if (token instanceof StringToken stringToken) {
      tokenStream.read();
      return stringToken.value();
    } else {
      return null;
    }
  }

  private static void ignoreWhitespace(CSSTokenStream tokenStream) throws IOException {
    while (tokenStream.peek() instanceof WhitespaceToken) {
      tokenStream.read();
    }
  }

  private static boolean isCombinatorDelimToken(Token token) {
    return token instanceof DelimToken delimToken && (
      delimToken.ch() == '>'
      || delimToken.ch() == '+'
      || delimToken.ch() == '~');
  }

}
