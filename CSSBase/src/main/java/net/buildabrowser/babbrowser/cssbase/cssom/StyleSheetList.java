package net.buildabrowser.babbrowser.cssbase.cssom;

import java.util.List;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.cssbase.cssom.imp.StyleSheetListImp;

public interface StyleSheetList {

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

}
