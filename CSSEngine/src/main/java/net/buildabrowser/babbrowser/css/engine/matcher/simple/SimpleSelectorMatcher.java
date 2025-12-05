package net.buildabrowser.babbrowser.css.engine.matcher.simple;

import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorPart;
import net.buildabrowser.babbrowser.dom.mutable.DocumentChangeListener;

public interface SimpleSelectorMatcher<T extends SelectorPart> extends DocumentChangeListener {
  
  void addSelectorReference(T ref);

  void removeSelectorReference(T ref);

  ElementSet match(T value);

}
