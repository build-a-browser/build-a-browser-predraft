package net.buildabrowser.babbrowser.cssbase.parser.imp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.cssbase.cssom.CSSRuleList;
import net.buildabrowser.babbrowser.cssbase.cssom.CSSRuleOrDeclarations;
import net.buildabrowser.babbrowser.cssbase.cssom.CSSStyleSheet;
import net.buildabrowser.babbrowser.cssbase.cssom.rule.AtRule;
import net.buildabrowser.babbrowser.cssbase.cssom.rule.CSSRule;
import net.buildabrowser.babbrowser.cssbase.cssom.rule.LayerListRule;
import net.buildabrowser.babbrowser.cssbase.cssom.rule.LayerRule;
import net.buildabrowser.babbrowser.cssbase.cssom.rule.MediaRule;
import net.buildabrowser.babbrowser.cssbase.cssom.rule.StyleRule;
import net.buildabrowser.babbrowser.cssbase.intermediate.QualifiedRule;
import net.buildabrowser.babbrowser.cssbase.media.ast.MediaNode;
import net.buildabrowser.babbrowser.cssbase.media.parser.CSSMediaQueryParser;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser;
import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStreamSource;
import net.buildabrowser.babbrowser.cssbase.selector.ComplexSelector;
import net.buildabrowser.babbrowser.cssbase.tokens.AtKeywordToken;
import net.buildabrowser.babbrowser.cssbase.tokens.CommaToken;
import net.buildabrowser.babbrowser.cssbase.tokens.DelimToken;
import net.buildabrowser.babbrowser.cssbase.tokens.EOFToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

// Includes some changes from https://drafts.csswg.org/css-syntax/
// not present in https://www.w3.org/TR/css-syntax-3/

public class CSSParserImp implements CSSParser {

  private final CSSIntermediateParserImp intermediateParser = new CSSIntermediateParserImp();

  @Override
  public CSSStyleSheet parseAStyleSheet(
    CSSTokenStream stream
  ) throws IOException {
    // TODO: Location
    CSSRuleList ruleList = consumeAStylesheetsContents(stream, true);
    return CSSStyleSheet.create(ruleList);
  }

  @Override
  public CSSRuleList parseARuleList(
    CSSTokenStream stream
  ) throws IOException {
    return consumeAStylesheetsContents(stream, false);
  }

  @Override
  public List<CSSRuleOrDeclarations> parseABlocksContents(
    CSSTokenStream tokenStream
  ) throws IOException {
    return intermediateParser.consumeABlocksContents(tokenStream);
  }

  private CSSRuleList consumeAStylesheetsContents(
    CSSTokenStream stream, boolean topLevel
  ) throws IOException {
    List<CSSRule> rawRules = intermediateParser.consumeAStylesheetsContents(stream, topLevel);
    List<CSSRule> mappedRules = remapRules(stream.source(), rawRules, null);
    
    return CSSRuleList.create(mappedRules);
  }

  private List<CSSRule> remapRules(
    CSSTokenStreamSource source,
    List<CSSRule> rawRules,
    List<ComplexSelector> parentSelectors
  ) throws IOException {
    List<CSSRule> mappedRules = new ArrayList<>(rawRules.size());
    for (CSSRule rawRule: rawRules) {
      CSSRule remappedRule = remapRule(source, rawRule, parentSelectors);
      if (remappedRule == null) continue;
      mappedRules.add(remappedRule);
    }
    return mappedRules;
  }

  private CSSRule remapRule(
    CSSTokenStreamSource source,
    CSSRule rule,
    List<ComplexSelector> parentSelectors
  ) throws IOException {
    switch (rule) {
      case QualifiedRule qualifiedRule:
        return createStyleRule(source, qualifiedRule, parentSelectors);
      case AtRule atRule:
        if (atRule.name().equals(AtKeywordToken.create("media"))) {
          return createMediaRule(source, atRule, parentSelectors);
        } else if (
          atRule.name().equals(AtKeywordToken.create("layer"))
        ) {
          return createLayerRule(source, atRule, parentSelectors);
        }
        // TODO: Add support for more rules
        return null;
      case StyleRule styleRule:
        return styleRule;
      default:
        throw new UnsupportedOperationException("Unrecognized rule type!");
    }
  }

  private CSSRule createStyleRule(
    CSSTokenStreamSource source,
    QualifiedRule qualifiedRule,
    List<ComplexSelector> parentSelectors
  ) throws IOException {
    boolean isContinuation =
      parentSelectors != null
      && qualifiedRule.prelude() == null;
    assert isContinuation || qualifiedRule.prelude() != null;
    // ComplexSelectors must be duplicated because they have a unique slot
    List<ComplexSelector> sourceSelectors =
      isContinuation ? duplicateSelectors(parentSelectors) :
      qualifiedRule.prelude() == null ? List.of() : // TODO: Why does this happen?
      ComplexSelectorParser.parseComplexSelectors(
        ListCSSTokenStream.create(source, qualifiedRule.prelude()),
        parentSelectors != null);
    List<ComplexSelector> desugaredSelectors = parentSelectors != null && !isContinuation ?
      CSSDesugaring.desugarSelectors(parentSelectors, sourceSelectors, isContinuation) :
      CSSDesugaring.desugarSelectors(List.of(), sourceSelectors, isContinuation);

    // Don't rewrite the rules here (other than for continuations) as it affects serialization
    return new StyleRule(
      sourceSelectors,
      desugaredSelectors,
      qualifiedRule.declarations(),
      remapRules(source, qualifiedRule.rules(), desugaredSelectors)
    );
  }

  private CSSRule createMediaRule(
    CSSTokenStreamSource source,
    AtRule atRule,
    List<ComplexSelector> parentSelectors
  ) throws IOException {
    MediaNode query = CSSMediaQueryParser.parseQuery(
      ListCSSTokenStream.createWithSkippedWhitespace(source, atRule.prelude()));
    List<CSSRule> rules = CSSIntermediateParserImp.wrapDeclarations(atRule.rules());
    return new MediaRule(query, remapRules(source, rules, parentSelectors));
  }

  private CSSRule createLayerRule(
    CSSTokenStreamSource source,
    AtRule atRule,
    List<ComplexSelector> parentSelectors
  ) throws IOException {
    CSSTokenStream stream = ListCSSTokenStream.createWithSkippedWhitespace(source, atRule.prelude());
    List<String> nameParts = parseLayerName(stream);
    if (nameParts == null) return null;

    List<List<String>> manyNames = new ArrayList<>(1);
    manyNames.add(nameParts);
    while (stream.peek() instanceof CommaToken) {
      stream.read();
      nameParts = parseLayerName(stream);
      manyNames.add(nameParts);
    }
    
    if (!(stream.peek() instanceof EOFToken)) return null;

    if (atRule.rules() == null) {
      return new LayerListRule(manyNames);
    }

    if (manyNames.size() > 1) return null;
    List<CSSRule> rules = CSSIntermediateParserImp.wrapDeclarations(atRule.rules());
    return new LayerRule(nameParts, remapRules(source, rules, parentSelectors));
  }

  private List<ComplexSelector> duplicateSelectors(List<ComplexSelector> selectors) {
    List<ComplexSelector> duplicates = new ArrayList<>(selectors.size());
    for (ComplexSelector selector: selectors) {
      duplicates.add(new ComplexSelector(selector.parts()));
    }

    return duplicates;
  }

  private List<String> parseLayerName(CSSTokenStream stream) throws IOException {
    if (stream.peek() instanceof EOFToken) return List.of();

    List<String> nameParts = new ArrayList<>();
    if (!(stream.read() instanceof IdentToken identToken)) return null;
    nameParts.add(identToken.value());

    while (
      stream.peek() instanceof DelimToken delimToken
      && delimToken.ch() == '.'
    ) {
      stream.read();

      if (!(stream.read() instanceof IdentToken identToken2)) return null;
      nameParts.add(identToken2.value());
    }
    
    return nameParts;
  }
  
}
