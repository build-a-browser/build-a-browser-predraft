package net.buildabrowser.babbrowser.css.engine.matcher.pseudo;

import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.cssbase.selector.SimplePseudoSelector;
import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;

public interface PseudoSelectorMatcher extends DocumentChangeListener {
  
  void addSelectorReference(SimplePseudoSelector ref);

  void removeSelectorReference(SimplePseudoSelector ref);

  ElementSet match(SimplePseudoSelector value);

}
