package net.buildabrowser.babbrowser.css.engine.matcher.simple;

import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.cssbase.selector.SimpleSelector;
import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;

public interface SimpleSelectorMatcher<T extends SimpleSelector> extends DocumentChangeListener {
  
  void addSelectorReference(T ref);

  void removeSelectorReference(T ref);

  ElementSet match(T value);

}
