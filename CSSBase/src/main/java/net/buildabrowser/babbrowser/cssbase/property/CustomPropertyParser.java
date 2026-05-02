package net.buildabrowser.babbrowser.cssbase.property;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.buildabrowser.babbrowser.common.datastruct.SinglyLinkedList;
import net.buildabrowser.babbrowser.cssbase.cssom.Declaration;
import net.buildabrowser.babbrowser.cssbase.intermediate.FunctionValue;
import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStreamSource;
import net.buildabrowser.babbrowser.cssbase.parser.imp.ListCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSVarValue;
import net.buildabrowser.babbrowser.cssbase.tokens.CommaToken;
import net.buildabrowser.babbrowser.cssbase.tokens.DelimToken;
import net.buildabrowser.babbrowser.cssbase.tokens.EOFToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.LCBracketToken;
import net.buildabrowser.babbrowser.cssbase.tokens.LParenToken;
import net.buildabrowser.babbrowser.cssbase.tokens.LSBracketToken;
import net.buildabrowser.babbrowser.cssbase.tokens.RCBracketToken;
import net.buildabrowser.babbrowser.cssbase.tokens.RParenToken;
import net.buildabrowser.babbrowser.cssbase.tokens.RSBracketToken;
import net.buildabrowser.babbrowser.cssbase.tokens.SemicolonToken;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;

// TODO: Do another pass through this to check if my error-handling is accurate
public final class CustomPropertyParser {

  private static final CSSFailure EXPECTED_CUSTOM_PROPERTY = new CSSFailure("Expected custom property name!");
  
  private CustomPropertyParser() {}

  public static boolean isValidCustomPropertyValue(
    CSSTokenStream stream, boolean isTopLevel
  ) throws IOException {
    List<Token> matchTokens = new LinkedList<>();
    while (!(stream.peek() instanceof EOFToken)) {
      Token token = stream.read();
      // TODO: BadStringToken, BadURLToken once they exist
      if (isTopLevel && (
        token instanceof SemicolonToken
        || (token instanceof DelimToken delimToken && delimToken.ch() == '!')
      )) return false;
      
      if (
        token instanceof LParenToken
        || token instanceof LSBracketToken
        || token instanceof LCBracketToken
      ) matchTokens.add(token);

      boolean areMatchesEmpty = matchTokens.isEmpty();
      if (
        (token instanceof RParenToken
          && (areMatchesEmpty || !(matchTokens.removeLast() instanceof LParenToken)))
        || (token instanceof RSBracketToken
          && (areMatchesEmpty || !(matchTokens.removeLast() instanceof LSBracketToken)))
        || (token instanceof RCBracketToken
          && (areMatchesEmpty || !(matchTokens.removeLast() instanceof LCBracketToken)))
      ) return false;

      if (token instanceof FunctionValue funcValue) {
        CSSTokenStream innerStream = ListCSSTokenStream.createWithSkippedWhitespace(
          stream.source(), funcValue.value());
        if (
          !isValidCustomPropertyValue(innerStream, false)
          || (
            funcValue.name().equals("var")
            && !isValidInnerVarReference(innerStream)
          )
        ) return false;
      }
    }

    // TODO: The spec doesn't seem to say unmatched left variants cause a failure? Double-check this.
    return true;
  }

  // In addition to boolean values, can return null if there is an invalid var reference.
  public static Boolean hasVarReferences(
    CSSTokenStream stream
  ) throws IOException {
    Boolean hasVarReferences = false;
    while (!(stream.peek() instanceof EOFToken)) {
      Token token = stream.read();
      if (token instanceof FunctionValue funcValue) {
        CSSTokenStream innerStream = ListCSSTokenStream.createWithSkippedWhitespace(
          stream.source(), funcValue.value());

        if (funcValue.name().equals("var")) {
          if (!isValidInnerVarReference(innerStream)) return null;
          hasVarReferences = true;
        }
        Boolean hasInnerVarReferences = hasVarReferences(innerStream);
        if (hasInnerVarReferences == null) return null;
        hasVarReferences |= hasInnerVarReferences;
      }
    }

    return hasVarReferences;
  }

  public static CSSValue resolveVarValues(
    CSSTokenStreamSource source, Declaration value, PropertyContainer refContainer
  ) throws IOException {
    CSSTokenStream innerStream = ListCSSTokenStream.createWithSkippedWhitespace(
      source, value.value());
    return resolveVarValues(innerStream, refContainer, null);
  }

  private static boolean isValidInnerVarReference(
    CSSTokenStream stream
  ) throws IOException {
    return !parseInnerVarReference(stream).isFailure();
  }

  private static CSSValue resolveVarValues(
    CSSTokenStream stream,
    PropertyContainer refContainer, SinglyLinkedList<String> resolveDepth
  ) throws IOException {
    List<Token> convertedTokens = new ArrayList<>(4);
    while (!(stream.peek() instanceof EOFToken)) {
      Token token = stream.read();
      if (
        token instanceof FunctionValue functionValue
        && functionValue.name().equals("var")
      ) {
        CSSTokenStream innerStream = ListCSSTokenStream.createWithSkippedWhitespace(
          stream.source(), functionValue.value());
        CSSValue expansion = evalVarFuncValue(innerStream, refContainer, resolveDepth);
        if (expansion == null) return null;
        if (expansion.isFailure()) return expansion;
        List<Token> expansionTokens = ((CSSVarValue) expansion).propertyTokens();
        convertedTokens.addAll(expansionTokens);
      } else if (token instanceof FunctionValue functionValue) {
        CSSTokenStream innerStream = ListCSSTokenStream.createWithSkippedWhitespace(
          stream.source(), functionValue.value());
        CSSValue expansion = resolveVarValues(innerStream, refContainer, resolveDepth);
        if (expansion == null) return null;
        if (expansion.isFailure()) return expansion;
        List<Token> expansionTokens = ((CSSVarValue) expansion).propertyTokens();
        convertedTokens.add(new FunctionValue(functionValue.name(), expansionTokens));
      } else {
        convertedTokens.add(token);
      }
    }

    return new CSSVarValue(convertedTokens);
  }

  private static CSSValue evalVarFuncValue(
    CSSTokenStream stream,
    PropertyContainer refContainer, SinglyLinkedList<String> resolveDepth
  ) throws IOException {
    CSSValue innerVal = parseInnerVarReference(stream);
    if (innerVal.isFailure()) return innerVal;
    String varName = ((VarOrFallback) innerVal).varName();
    CSSValue resolvedValue = resolveVarValue(
      stream.source(), varName, refContainer, resolveDepth);
    if (resolvedValue != null) return resolvedValue;
    CSSValue fallback = ((VarOrFallback) innerVal).fallbackValue();
    if (fallback == null) return CSSFailure.UNSET_CUSTOM_PROPERTY;
    if (fallback.isFailure()) return fallback;

    CSSTokenStream innerStream = ListCSSTokenStream.createWithSkippedWhitespace(
      stream.source(), ((CSSVarValue) fallback).propertyTokens());
    return resolveVarValues(innerStream, refContainer, resolveDepth);
  }

  private static CSSValue resolveVarValue(
    CSSTokenStreamSource source,
    String varName, PropertyContainer refContainer, SinglyLinkedList<String> resolveDepth
  ) throws IOException {
    if (alreadyContainsVar(resolveDepth, varName)) {
      return new InvalidVarResolution(varName);
    }

    CSSValue retValue = refContainer.getCustomProperty(varName);
    if (retValue == null) {
      return null;
    } else if (retValue.equals(CSSFailure.UNSET_CUSTOM_PROPERTY)) {
      if (refContainer.parent() == null) return null;
      return resolveVarValue(source, varName, refContainer.parent(), null);
    } else if (retValue instanceof CSSVarValue varValue) {
      SinglyLinkedList<String> newResolvedDepth = new SinglyLinkedList<>(varName);
      newResolvedDepth.setNext(resolveDepth);
      CSSTokenStream stream = ListCSSTokenStream.createWithSkippedWhitespace(source, varValue.propertyTokens());
      CSSValue resolvedValue = resolveVarValues(stream, refContainer, newResolvedDepth);

      if (
        resolvedValue instanceof InvalidVarResolution ivr
        && ivr.varName().equals(varName)
      ) return null;
      // TODO: Also cap number of tokens in resolution, as per the spec
      return resolvedValue;
    } else if (retValue.isFailure()) {
      return retValue;
    } else {
      throw new IllegalStateException("Expected to obtain a var value or unset value!");
    }
  }

  private static CSSValue parseInnerVarReference(CSSTokenStream stream) throws IOException {
    if (!(
      stream.read() instanceof IdentToken identToken
      && identToken.value().startsWith("--")
      && !identToken.value().equals("--")
    )) return EXPECTED_CUSTOM_PROPERTY;

    Token nextToken = stream.read();
    if (nextToken instanceof EOFToken) {
      return new VarOrFallback(identToken.value(), null);
    }
    if (!(
      nextToken instanceof CommaToken
    )) return CSSFailure.EXPECTED_EOF;

    List<Token> fallbackTokens = new ArrayList<>(4);
    while (!(stream.peek() instanceof EOFToken)) {
      fallbackTokens.add(stream.read());
    }

    // TODO: Does the fallback need validated?
    return new VarOrFallback(identToken.value(), new CSSVarValue(fallbackTokens));
  }

  private static boolean alreadyContainsVar(SinglyLinkedList<String> resolveDepth, String varName) {
    while (resolveDepth != null) {
      if (resolveDepth.item().equals(varName)) {
        return true;
      }
      resolveDepth = resolveDepth.next();
    }

    return false;
  }

  private static record InvalidVarResolution(String varName) implements CSSValue {
    @Override
    public boolean isFailure() {
      return true;
    }
  }

  private static record VarOrFallback(String varName, CSSVarValue fallbackValue) implements CSSValue {}

}
