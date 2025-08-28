package net.buildabrowser.babbrowser.css.parser.imp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.css.cssom.CSSRule;
import net.buildabrowser.babbrowser.css.cssom.CSSRuleList;
import net.buildabrowser.babbrowser.css.cssom.CSSStyleSheet;
import net.buildabrowser.babbrowser.css.cssom.Declaration;
import net.buildabrowser.babbrowser.css.cssom.StyleRule;
import net.buildabrowser.babbrowser.css.intermediate.QualifiedRule;
import net.buildabrowser.babbrowser.css.parser.CSSParser;
import net.buildabrowser.babbrowser.css.selector.ComplexSelector;
import net.buildabrowser.babbrowser.css.selector.TypeSelector;
import net.buildabrowser.babbrowser.css.tokens.IdentToken;
import net.buildabrowser.babbrowser.css.tokens.Token;

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

  private List<ComplexSelector> parseComplexSelectors(List<Token> prelude) {
    return List.of(parseComplexSelector(prelude));
  }

  private ComplexSelector parseComplexSelector(List<Token> selectorTokens) {
    if (selectorTokens.size()  == 1 && selectorTokens.get(0) instanceof IdentToken identToken) {
      return ComplexSelector.create(List.of(TypeSelector.create(identToken.value())));
    }

    throw new UnsupportedOperationException("Currently only support one type selector!");
  }
  
}
