package net.buildabrowser.babbrowser.cssbase.cssom.mutable.imp;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.cssbase.cssom.CSSStyleSheet;
import net.buildabrowser.babbrowser.cssbase.cssom.mutable.MutableStyleSheetList;

public class MutableStyleSheetListImp implements MutableStyleSheetList {

  private final List<CSSStyleSheet> styleSheets;
  private final Consumer<CSSStyleSheet> styleSheetListener;

  public MutableStyleSheetListImp(Consumer<CSSStyleSheet> stylesheetListener) {
    this(new LinkedList<>(), stylesheetListener);
  }

  public MutableStyleSheetListImp(List<CSSStyleSheet> rules, Consumer<CSSStyleSheet> styleSheetListener) {
    this.styleSheets = rules;
    this.styleSheetListener = styleSheetListener;
    for (CSSStyleSheet rule: rules) {
      styleSheetListener.accept(rule);
    }
  }

  @Override
  public CSSStyleSheet item(long index) {
    assert index <= Integer.MAX_VALUE && index >= 0;
    return styleSheets.get((int) index);
  }

  @Override
  public long length() {
    return styleSheets.size();
  }

  @Override
  public void addStylesheet(CSSStyleSheet styleSheet) {
    styleSheets.add(styleSheet);
    styleSheetListener.accept(styleSheet);
  }
  
}
