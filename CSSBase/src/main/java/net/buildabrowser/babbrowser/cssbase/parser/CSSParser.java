package net.buildabrowser.babbrowser.cssbase.parser;

import java.io.IOException;
import java.util.List;

import net.buildabrowser.babbrowser.cssbase.cssom.CSSRuleList;
import net.buildabrowser.babbrowser.cssbase.cssom.CSSRuleOrDeclarations;
import net.buildabrowser.babbrowser.cssbase.cssom.CSSStyleSheet;
import net.buildabrowser.babbrowser.cssbase.parser.imp.CSSParserImp;

public interface CSSParser {
  
  CSSStyleSheet parseAStyleSheet(CSSTokenStream tokenStream) throws IOException;

  CSSRuleList parseARuleList(CSSTokenStream tokenStream) throws IOException;

  List<CSSRuleOrDeclarations> parseABlocksContents(
    CSSTokenStream tokenStream
  ) throws IOException;

  // Unfortunately, INSTANCE cannot be private due to interface rules
  static final CSSParser INSTANCE = new CSSParserImp();
  static CSSParser create() {
    return INSTANCE;
  }

}
