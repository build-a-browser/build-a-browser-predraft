package net.buildabrowser.babbrowser.cssbase.cssom.imp;

import java.util.List;

import net.buildabrowser.babbrowser.cssbase.cssom.CSSStyleSheet;
import net.buildabrowser.babbrowser.cssbase.cssom.StyleSheetList;

public record StyleSheetListImp(List<CSSStyleSheet> styleSheets) implements StyleSheetList {

  @Override
  public CSSStyleSheet item(long index) {
    assert index <= Integer.MAX_VALUE && index >= 0;
    return styleSheets.get((int) index);
  }

  @Override
  public long length() {
    return styleSheets.size();
  }
  
}
