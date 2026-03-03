package net.buildabrowser.babbrowser.cssbase.property;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.buildabrowser.babbrowser.common.datastruct.SinglyLinkedList;
import net.buildabrowser.babbrowser.cssbase.cssom.Declaration;
import net.buildabrowser.babbrowser.cssbase.intermediate.FunctionValue;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.CSSTokenStream;
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

  public static boolean isValidCustomPropertyValue(List<Token> tokens, boolean isTopLevel) {
    List<Token> matchTokens = new LinkedList<>();
    for (Token token: tokens) {
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

      if (
        token instanceof FunctionValue funcValue
        && (
          (
            funcValue.name().equals("var")
            && !isValidInnerVarReference(funcValue.value())
          ) || !isValidCustomPropertyValue(funcValue.value(), false)
        )
      ) return false;
    }

    // TODO: The spec doesn't seem to say unmatched left variants cause a failure? Double-check this.
    return true;
  }

  // In addition to boolean values, can return null if there is an invalid var reference.
  public static Boolean hasVarReferences(List<Token> tokens) {
    Boolean hasVarReferences = false;
    for (Token token: tokens) {
      if (token instanceof FunctionValue funcValue) {
        if (funcValue.name().equals("var")) {
          if (!isValidInnerVarReference(funcValue.value())) return null;
          hasVarReferences = true;
        }

        Boolean hasInnerVarReferences = hasVarReferences(funcValue.value());
        if (hasInnerVarReferences == null) return null;
        hasVarReferences |= hasInnerVarReferences;
      }
    }

    return hasVarReferences;
  }

  public static CSSValue resolveVarValues(Declaration value, PropertyContainer refContainer) {
    return resolveVarValues(value.value(), refContainer, null);
  }

  private static boolean isValidInnerVarReference(List<Token> value) {
    return !parseInnerVarReference(value).isFailure();
  }

  private static CSSValue resolveVarValues(List<Token> tokens, PropertyContainer refContainer, SinglyLinkedList<String> resolveDepth) {
    List<Token> convertedTokens = new ArrayList<>(4);
    for (Token token: tokens) {
      if (
        token instanceof FunctionValue functionValue
        && functionValue.name().equals("var")
      ) {
        CSSValue expansion = evalVarFuncValue(functionValue.value(), refContainer, resolveDepth);
        if (expansion == null) return null;
        if (expansion.isFailure()) return expansion;
        List<Token> expansionTokens = ((CSSVarValue) expansion).propertyTokens();
        convertedTokens.addAll(expansionTokens);
      } else if (token instanceof FunctionValue functionValue) {
        CSSValue expansion = resolveVarValues(functionValue.value(), refContainer, resolveDepth);
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

  private static CSSValue evalVarFuncValue(List<Token> value, PropertyContainer refContainer, SinglyLinkedList<String> resolveDepth) {
    CSSValue innerVal = parseInnerVarReference(value);
    if (innerVal.isFailure()) return innerVal;
    String varName = ((VarOrFallback) innerVal).varName();
    CSSValue resolvedValue = resolveVarValue(varName, refContainer, resolveDepth);
    if (resolvedValue != null) return resolvedValue;
    CSSValue fallback = ((VarOrFallback) innerVal).fallbackValue();
    if (fallback == null) return CSSFailure.UNSET_CUSTOM_PROPERTY;
    if (fallback.isFailure()) return fallback;
    return resolveVarValues(((CSSVarValue) fallback).propertyTokens(), refContainer, resolveDepth);
  }

  private static CSSValue resolveVarValue(String varName, PropertyContainer refContainer, SinglyLinkedList<String> resolveDepth) {
    if (alreadyContainsVar(resolveDepth, varName)) {
      return new InvalidVarResolution(varName);
    }

    CSSValue retValue = refContainer.getCustomProperty(varName);
    if (retValue == null) {
      return null;
    } else if (retValue.equals(CSSFailure.UNSET_CUSTOM_PROPERTY)) {
      if (refContainer.parent() == null) return null;
      return resolveVarValue(varName, refContainer.parent(), null);
    } else if (retValue instanceof CSSVarValue varValue) {
      SinglyLinkedList<String> newResolvedDepth = new SinglyLinkedList<>(varName);
      newResolvedDepth.setNext(resolveDepth);
      CSSValue resolvedValue = resolveVarValues(varValue.propertyTokens(), refContainer, newResolvedDepth);

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

  private static CSSValue parseInnerVarReference(List<Token> value) {
    CSSTokenStream stream = ListCSSTokenStream.createWithSkippedWhitespace(value);
    try {
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
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
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
