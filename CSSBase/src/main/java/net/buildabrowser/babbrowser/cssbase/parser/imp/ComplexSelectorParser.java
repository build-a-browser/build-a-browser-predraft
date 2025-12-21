package net.buildabrowser.babbrowser.cssbase.parser.imp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.selector.AttributeSelector;
import net.buildabrowser.babbrowser.cssbase.selector.AttributeSelector.AttributeType;
import net.buildabrowser.babbrowser.cssbase.selector.ChildCombinator;
import net.buildabrowser.babbrowser.cssbase.selector.Combinator;
import net.buildabrowser.babbrowser.cssbase.selector.ComplexSelector;
import net.buildabrowser.babbrowser.cssbase.selector.DescendantCombinator;
import net.buildabrowser.babbrowser.cssbase.selector.IdSelector;
import net.buildabrowser.babbrowser.cssbase.selector.NextSiblingCombinator;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorPart;
import net.buildabrowser.babbrowser.cssbase.selector.SubsequentSiblingCombinator;
import net.buildabrowser.babbrowser.cssbase.selector.TypeSelector;
import net.buildabrowser.babbrowser.cssbase.selector.UniversalSelector;
import net.buildabrowser.babbrowser.cssbase.tokens.CommaToken;
import net.buildabrowser.babbrowser.cssbase.tokens.DelimToken;
import net.buildabrowser.babbrowser.cssbase.tokens.EOFToken;
import net.buildabrowser.babbrowser.cssbase.tokens.HashToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;
import net.buildabrowser.babbrowser.cssbase.tokens.WhitespaceToken;

public final class ComplexSelectorParser {
  
  private ComplexSelectorParser() {}

  public static List<ComplexSelector> parseComplexSelectors(List<Token> prelude) throws IOException {
    List<ComplexSelector> selectors = new ArrayList<>(1);
    CSSTokenStream tokenStream = CSSTokenStream.create(prelude);
    while (!(tokenStream.peek() instanceof EOFToken)) {
      ComplexSelector selector = parseComplexSelector(tokenStream);
      if (selector != null) {
        selectors.add(selector);
      }
      if (tokenStream.peek() instanceof CommaToken) {
        tokenStream.read();
      }
    }

    return selectors;
  }

  private static ComplexSelector parseComplexSelector(CSSTokenStream tokenStream) throws IOException {
    boolean didEncounterWhitespace = false;
    boolean didEncounterCombinator = false;
    boolean isInvalid = false;
    List<SelectorPart> parts = new ArrayList<>(1);
    while (!(
      tokenStream.peek() instanceof EOFToken
      || tokenStream.peek() instanceof CommaToken
    )) {
      Token currentToken = tokenStream.read();
      boolean isWhitespace = currentToken instanceof WhitespaceToken;
      boolean isCombinatorDelim = isCombinatorDelimToken(currentToken);
      isInvalid |= isCombinatorDelim && didEncounterCombinator;
      didEncounterWhitespace |= isWhitespace;
      didEncounterCombinator |= isCombinatorDelim;
      if (!isCombinatorDelim && !isWhitespace) {
        if (didEncounterWhitespace && !didEncounterCombinator) {
          parts.add(DescendantCombinator.create());
        }
        didEncounterWhitespace = false;
        didEncounterCombinator = false;
      }
      isInvalid |= parseSelectorPart(currentToken, tokenStream, parts);
    }

    isInvalid |= parts.getFirst() instanceof Combinator;
    isInvalid |= parts.getLast() instanceof Combinator;
    if (isInvalid) return null;

    return new ComplexSelector(parts);
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
      case WhitespaceToken _ -> {}
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
      default -> {}
    }

    return isInvalid;
  }

  private static boolean isCombinatorDelimToken(Token token) {
    return token instanceof DelimToken delimToken && (
      delimToken.ch() == '>'
      || delimToken.ch() == '+'
      || delimToken.ch() == '~');
  }

}
