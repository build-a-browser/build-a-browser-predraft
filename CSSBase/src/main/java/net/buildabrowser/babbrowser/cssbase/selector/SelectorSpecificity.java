package net.buildabrowser.babbrowser.cssbase.selector;

public record SelectorSpecificity(boolean isAttr, int numIdSelectors, int numClassSelectors, int numTypeSelectors) {
  
  public SelectorSpecificity(int numIdSelectors, int numClassSelectors, int numTypeSelectors) {
    this(false, numIdSelectors, numClassSelectors, numTypeSelectors);
  }

  public static int compare(SelectorSpecificity a, SelectorSpecificity b) {
    return
      a.isAttr && !b.isAttr ? 1 :
      b.isAttr && !a.isAttr ? -1 :
      a.numIdSelectors > b.numIdSelectors ? 1 :
      a.numIdSelectors < b.numIdSelectors ? -1 :
      a.numClassSelectors > b.numClassSelectors ? 1 :
      a.numClassSelectors < b.numClassSelectors ? -1 :
      a.numTypeSelectors > b.numTypeSelectors ? 1 :
      a.numTypeSelectors < b.numTypeSelectors ? -1 :
      0;
  }

}
