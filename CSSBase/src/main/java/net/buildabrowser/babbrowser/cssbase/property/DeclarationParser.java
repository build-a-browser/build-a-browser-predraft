package net.buildabrowser.babbrowser.cssbase.property;

import static net.buildabrowser.babbrowser.cssbase.property.PropertyParsers.PROPERTY_PARSERS;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.buildabrowser.babbrowser.cssbase.cssom.Declaration;
import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStreamSource;
import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.parser.imp.ListCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSDeferred;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSVarValue;
import net.buildabrowser.babbrowser.cssbase.tokens.EOFToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;

public final class DeclarationParser {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeclarationParser.class);



  public static boolean isKnownDeclarationName(String declName) {
    return PROPERTY_PARSERS.containsKey(declName.toLowerCase());
  }

  public static CSSValue parseDeclaration(
    CSSTokenStreamSource source, Declaration declaration
  ) throws IOException {
    PropertyValueParser parser = PROPERTY_PARSERS.get(declaration.name().toLowerCase());
    if (parser == null) return CSSValue.SpecialCSSValue.INVALID;
    if (parser.relatedProperty() == null) {
      throw new UnsupportedOperationException("Parser does not have a related property!");
    }

    if (
      declaration.value().size() == 1
      && declaration.value().get(0) instanceof IdentToken identToken
    ) {
      if (identToken.value().equals("initial")) {
        return CSSValue.SpecialCSSValue.INITIAL;
      } else if (identToken.value().equals("inherit")) {
        return CSSValue.SpecialCSSValue.INHERIT;
      } else if (identToken.value().equals("unset")) {
        return CSSValue.SpecialCSSValue.UNSET;
      }

      // TODO: Support revert keyword
    }

    CSSTokenStream innerStream = ListCSSTokenStream.createWithSkippedWhitespace(
      source, declaration.value());
    Boolean shouldDefer = CustomPropertyParser.hasVarReferences(innerStream);
    if (shouldDefer == null) return CSSValue.SpecialCSSValue.INVALID;
    // TODO: Include referenced variables
    if (shouldDefer) return new CSSDeferred(declaration, parser, List.of(), source);

    // TODO: Do any cases preserve whitespace?
    CSSTokenStream tokenStream = ListCSSTokenStream.createWithSkippedWhitespace(
      source, declaration.value());
    try {
      CSSValue result = parser.parse(tokenStream);
      if (
        !result.isFailure()
        && tokenStream.peek() instanceof EOFToken
      ) {
        return result;
      }
    } catch (IOException e) {
      LOGGER.error("Could not parse the declaration!", e);
    }

    return CSSValue.SpecialCSSValue.INVALID;
  }
  
  public static PropertyValueParser declarationDetails(String declName) {
    PropertyValueParser parser = PROPERTY_PARSERS.get(declName);
    if (parser == null) return null;
    if (parser.relatedProperty() == null) {
      throw new UnsupportedOperationException("Parser does not have a related property!");
    }

    return parser;
  }

  public static CSSValue parseDeferredDeclaration(
    CSSDeferred deferredValue,
    PropertyContainer refContainer
  ) throws IOException {
    CSSValue resolvedValue = CustomPropertyParser.resolveVarValues(
      deferredValue.source(), deferredValue.value(), refContainer);
    if (resolvedValue == null) return CSSValue.SpecialCSSValue.INVALID;
    if (resolvedValue.isFailure()) return CSSValue.SpecialCSSValue.INVALID;
    List<Token> resolvedTokens = ((CSSVarValue) resolvedValue).propertyTokens();
    CSSTokenStream tokenStream = ListCSSTokenStream.createWithSkippedWhitespace(
      deferredValue.source(), resolvedTokens);
    try {
      CSSValue result = deferredValue.parser().parse(tokenStream);
      if (
        !result.isFailure()
        && tokenStream.peek() instanceof EOFToken
      ) {
        return result;
      }
    } catch (IOException e) {
      LOGGER.error("Could not parse the declaration!", e);
    }

    return CSSValue.SpecialCSSValue.INVALID;
  }

}
