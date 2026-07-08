package net.buildabrowser.babbrowser.cssbase.selector;

import net.buildabrowser.babbrowser.cssbase.microsyntax.ANPlusB;

public record NthChildPseudoSelector(
  NthChildPseudoSelectorType type,
  ANPlusB index,
  ComplexSelector selector
) implements SelectorPart {
  
  public static enum NthChildPseudoSelectorType {
    NTH, NTH_LAST, ONLY_CHILD
  }

}
