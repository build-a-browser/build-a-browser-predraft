package net.buildabrowser.babbrowser.css.parser.imp;

import java.io.IOException;

import net.buildabrowser.babbrowser.css.cssom.CSSRuleList;
import net.buildabrowser.babbrowser.css.cssom.CSSStyleSheet;
import net.buildabrowser.babbrowser.css.parser.CSSParser;

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

  private CSSRuleList consumeAListOfRules(CSSTokenStream stream, boolean topLevel) throws IOException {
    return CSSRuleList.create(intermediateParser.consumeAListOfRules(stream, topLevel));
  }
  
}
