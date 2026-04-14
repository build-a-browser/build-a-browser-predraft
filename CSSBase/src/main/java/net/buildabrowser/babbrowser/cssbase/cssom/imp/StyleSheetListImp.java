package net.buildabrowser.babbrowser.cssbase.cssom.imp;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.cssbase.cssom.CSSStyleSheet;
import net.buildabrowser.babbrowser.cssbase.cssom.StyleSheetList;

public class StyleSheetListImp implements StyleSheetList {

  private final List<CSSStyleSheet> styleSheets;
  private final Consumer<CSSStyleSheet> styleSheetListener;

  public StyleSheetListImp(Consumer<CSSStyleSheet> stylesheetListener) {
    this(new LinkedList<>(), stylesheetListener);
  }

  public StyleSheetListImp(List<CSSStyleSheet> rules, Consumer<CSSStyleSheet> styleSheetListener) {
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

  @Override
  public void removeStylesheet(CSSStyleSheet styleSheet) {
    styleSheets.remove(styleSheet);
    // TODO: Fire a listener to remove
  }

  @Override
  public Iterator<CSSStyleSheet> iterator() {
    return styleSheets.iterator();
  }
  
}
