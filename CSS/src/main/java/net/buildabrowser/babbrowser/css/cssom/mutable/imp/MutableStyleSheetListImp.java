package net.buildabrowser.babbrowser.css.cssom.mutable.imp;

import java.util.LinkedList;
import java.util.List;

import net.buildabrowser.babbrowser.css.cssom.CSSStyleSheet;
import net.buildabrowser.babbrowser.css.cssom.StyleSheetList;
import net.buildabrowser.babbrowser.css.cssom.mutable.MutableStyleSheetList;

public class MutableStyleSheetListImp implements MutableStyleSheetList {

  private final List<CSSStyleSheet> styleSheets;

  public MutableStyleSheetListImp() {
    this(new LinkedList<>());
  }

  public MutableStyleSheetListImp(List<CSSStyleSheet> rules) {
    this.styleSheets = rules;
  }

  @Override
  public CSSStyleSheet item(long index) {
    assert index <= Integer.MAX_VALUE && index > 0;
    return styleSheets.get((int) index);
  }

  @Override
  public long length() {
    return styleSheets.size();
  }

  @Override
  public void addStylesheet(CSSStyleSheet styleSheet) {
    styleSheets.add(styleSheet);
  }

  @Override
  public StyleSheetList immutable() {
    return StyleSheetList.create(styleSheets);
  }
  
}
