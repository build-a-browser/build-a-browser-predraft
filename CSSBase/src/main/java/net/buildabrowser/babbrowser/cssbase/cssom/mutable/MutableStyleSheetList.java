package net.buildabrowser.babbrowser.cssbase.cssom.mutable;

import java.util.function.Consumer;

import net.buildabrowser.babbrowser.cssbase.cssom.CSSStyleSheet;
import net.buildabrowser.babbrowser.cssbase.cssom.StyleSheetList;
import net.buildabrowser.babbrowser.cssbase.cssom.mutable.imp.MutableStyleSheetListImp;

public interface MutableStyleSheetList extends StyleSheetList {
  
  void addStylesheet(CSSStyleSheet styleSheet);

  static MutableStyleSheetList create(Consumer<CSSStyleSheet> styleSheetListener) {
    return new MutableStyleSheetListImp(styleSheetListener);
  }

}
