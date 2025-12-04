package net.buildabrowser.babbrowser.cssbase.parser.imp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.cssbase.cssom.CSSRule;
import net.buildabrowser.babbrowser.cssbase.cssom.CSSRuleList;
import net.buildabrowser.babbrowser.cssbase.cssom.CSSStyleSheet;
import net.buildabrowser.babbrowser.cssbase.cssom.Declaration;
import net.buildabrowser.babbrowser.cssbase.cssom.StyleRule;
import net.buildabrowser.babbrowser.cssbase.intermediate.QualifiedRule;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser;
import net.buildabrowser.babbrowser.cssbase.selector.AttributeSelector;
import net.buildabrowser.babbrowser.cssbase.selector.ComplexSelector;
import net.buildabrowser.babbrowser.cssbase.selector.IdSelector;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorPart;
import net.buildabrowser.babbrowser.cssbase.selector.TypeSelector;
import net.buildabrowser.babbrowser.cssbase.selector.AttributeSelector.AttributeType;
import net.buildabrowser.babbrowser.cssbase.tokens.CommaToken;
import net.buildabrowser.babbrowser.cssbase.tokens.DelimToken;
import net.buildabrowser.babbrowser.cssbase.tokens.EOFToken;
import net.buildabrowser.babbrowser.cssbase.tokens.HashToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;
import net.buildabrowser.babbrowser.cssbase.tokens.WhitespaceToken;

public class CSSParserImp implements CSSParser {

  private final CSSIntermediateParserImp intermediateParser = new CSSIntermediateParserImp();

  @Override
  public CSSStyleSheet parseAStyleSheet(CSSTokenStream stream) throws IOException {
    // TODO: Location
    CSSRuleList ruleList = consumeAListOfRules(stream, true);
    return CSSStyleSheet.create(ruleList);
  }

  @Override
  public CSSRuleList parseARuleList(CSSTokenStream stream) throws IOException {
    return consumeAListOfRules(stream, false);
  }

  @Override
  public List<Declaration> parseAStyleBlocksContents(CSSTokenStream tokenStream) throws IOException {
    return intermediateParser.consumeAStyleBlocksContents(tokenStream);
  }

  private CSSRuleList consumeAListOfRules(CSSTokenStream stream, boolean topLevel) throws IOException {
    List<CSSRule> rawRules = intermediateParser.consumeAListOfRules(stream, topLevel);
    List<CSSRule> mappedRules = new ArrayList<>(rawRules.size());
    for (CSSRule rawRule: rawRules) {
      mappedRules.add(remapRule(rawRule));
    }
    
    return CSSRuleList.create(mappedRules);
  }

  private CSSRule remapRule(CSSRule rule) throws IOException {
    switch (rule) {
      case QualifiedRule qualifiedRule:
        return createStyleRule(qualifiedRule);
      default:
        throw new UnsupportedOperationException("Unrecognized rule type!");
    }
  }

  private CSSRule createStyleRule(QualifiedRule qualifiedRule) throws IOException {
    List<Declaration> declarations = intermediateParser.consumeAStyleBlocksContents(
      ListCSSTokenStream.create(qualifiedRule.simpleBlock().value())
    );

    List<ComplexSelector> selectors = parseComplexSelectors(qualifiedRule.prelude());

    return StyleRule.create(selectors, declarations);
  }

  private List<ComplexSelector> parseComplexSelectors(List<Token> prelude) throws IOException {
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

  private ComplexSelector parseComplexSelector(CSSTokenStream tokenStream) throws IOException {
    @SuppressWarnings("unused")
    boolean didEncounterWhitespace = false;
    boolean isInvalid = false;
    List<SelectorPart> parts = new ArrayList<>(1);
    while (!(
      tokenStream.peek() instanceof EOFToken
      || tokenStream.peek() instanceof CommaToken
    )) {
      Token currentToken = tokenStream.read();
      // TODO: Support whitespace
      switch (currentToken) {
        case IdentToken identToken -> parts.add(TypeSelector.create(identToken.value()));
        case DelimToken delimToken -> {
          if (delimToken.ch() == '.' && tokenStream.peek() instanceof IdentToken identToken) {
            tokenStream.read();
            parts.add(AttributeSelector.create("class", identToken.value(), AttributeType.ONE_OF));
          } else {
            isInvalid = true;
          }
        }
        case HashToken hashToken -> {
          if (hashToken.type().equals(HashToken.Type.ID)) {
            parts.add(IdSelector.create(hashToken.value()));
          } else {
            isInvalid = true;
          }
        }
        case WhitespaceToken _ -> didEncounterWhitespace = true;
        default -> isInvalid = true;
      }
    }

    if (isInvalid) return null;

    return new ComplexSelector(parts);
  }
  
}
