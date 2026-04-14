package net.buildabrowser.babbrowser.cssbase.cssom;

import java.io.Reader;
import java.util.List;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.cssbase.cssom.imp.StyleSheetListImp;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokenizer.CSSTokenizerInput;

public interface StyleSheetList extends Iterable<CSSStyleSheet> {

  CSSStyleSheet item(long index);

  long length();

  // Extensions

  void addStylesheet(CSSStyleSheet styleSheet);

  void removeStylesheet(CSSStyleSheet styleSheet);

  static StyleSheetList create(List<CSSStyleSheet> styleSheets) {
    return new StyleSheetListImp(styleSheets, _ -> {});
  }

  static StyleSheetList create(Consumer<CSSStyleSheet> styleSheetListener) {
    return new StyleSheetListImp(styleSheetListener);
  }

  static StyleSheetList createFromReader(Reader reader) {
    CSSTokenizerInput tokenizerInput = CSSTokenizerInput.fromReader(reader);
    CSSTokenStream tokenizerStream = CSSTokenStream.create(tokenizerInput);
    
    CSSStyleSheet styleSheet = CommonUtil.rethrow(() -> CSSParser.create().parseAStyleSheet(tokenizerStream));
    return StyleSheetList.create(List.of(styleSheet));
  }

}
