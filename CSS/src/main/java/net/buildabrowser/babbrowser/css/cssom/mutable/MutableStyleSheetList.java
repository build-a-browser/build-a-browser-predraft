package net.buildabrowser.babbrowser.css.cssom.mutable;

import net.buildabrowser.babbrowser.css.cssom.CSSStyleSheet;
import net.buildabrowser.babbrowser.css.cssom.StyleSheetList;
import net.buildabrowser.babbrowser.css.cssom.mutable.imp.MutableStyleSheetListImp;

public interface MutableStyleSheetList extends StyleSheetList {
  
  void addStylesheet(CSSStyleSheet styleSheet);

  static MutableStyleSheetList create() {
    return new MutableStyleSheetListImp();
  }

  StyleSheetList immutable();

}
