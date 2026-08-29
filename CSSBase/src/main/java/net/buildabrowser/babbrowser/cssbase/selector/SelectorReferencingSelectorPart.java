package net.buildabrowser.babbrowser.cssbase.selector;

import java.util.List;

public interface SelectorReferencingSelectorPart extends SelectorPart {
  
  List<ComplexSelector> complexSelectors();

  SelectorReferencingSelectorPart rewrite(List<ComplexSelector> newChildren);

}
