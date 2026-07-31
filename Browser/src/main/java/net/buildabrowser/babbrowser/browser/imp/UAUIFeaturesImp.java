package net.buildabrowser.babbrowser.browser.imp;

import net.buildabrowser.babbrowser.browser.uistate.Tab;
import net.buildabrowser.babbrowser.browser.uistate.WindowSet;
import net.buildabrowser.babbrowser.html.navigation.Navigable;
import net.buildabrowser.babbrowser.html.ua.UAUIFeatures;

public class UAUIFeaturesImp implements UAUIFeatures {

  private WindowSet windowSet;

  public UAUIFeaturesImp(
    WindowSet windowSet
  ) {
    this.windowSet = windowSet;
  }

  @Override
  public Navigable addTopLevelTraversable(Navigable sourceNavigable) {
    Tab tab = windowSet.openTabAfter(sourceNavigable.uuid());
    return tab.getFrame().navigable();
  }
  
}
