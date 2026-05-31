package net.buildabrowser.babbrowser.cssbase.parser.imp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.cssbase.cssom.AtRule;
import net.buildabrowser.babbrowser.cssbase.cssom.CSSRule;
import net.buildabrowser.babbrowser.cssbase.cssom.CSSRuleList;
import net.buildabrowser.babbrowser.cssbase.cssom.CSSStyleSheet;
import net.buildabrowser.babbrowser.cssbase.cssom.Declaration;
import net.buildabrowser.babbrowser.cssbase.cssom.StyleRule;
import net.buildabrowser.babbrowser.cssbase.intermediate.QualifiedRule;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser;
import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStreamSource;
import net.buildabrowser.babbrowser.cssbase.selector.ComplexSelector;

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
      CSSRule remappedRule = remapRule(stream.source(), rawRule);
      if (remappedRule == null) continue;
      mappedRules.add(remappedRule);
    }
    
    return CSSRuleList.create(mappedRules);
  }

  private CSSRule remapRule(
    CSSTokenStreamSource source, CSSRule rule
  ) throws IOException {
    switch (rule) {
      case QualifiedRule qualifiedRule:
        return createStyleRule(source, qualifiedRule);
      case AtRule _1:
        // TODO
        return null;
      default:
        throw new UnsupportedOperationException("Unrecognized rule type!");
    }
  }

  private CSSRule createStyleRule(
    CSSTokenStreamSource source, QualifiedRule qualifiedRule
  ) throws IOException {
    List<Declaration> declarations = intermediateParser.consumeAStyleBlocksContents(
      ListCSSTokenStream.create(source, qualifiedRule.simpleBlock().value()));

    List<ComplexSelector> selectors = ComplexSelectorParser.parseComplexSelectors(
      ListCSSTokenStream.create(source, qualifiedRule.prelude()));

    return new StyleRule(selectors, declarations);
  }
  
}
