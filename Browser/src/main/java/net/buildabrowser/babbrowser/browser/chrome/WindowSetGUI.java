package net.buildabrowser.babbrowser.browser.chrome;

import net.buildabrowser.babbrowser.browser.uistate.Window;
import net.buildabrowser.babbrowser.browser.uistate.WindowSet;
import net.buildabrowser.babbrowser.browser.uistate.event.WindowSetMutationEventListener;

public class WindowSetGUI implements WindowSetMutationEventListener {

  private final WindowSet windowSet;

  private WindowSetGUI(WindowSet windowSet) {
    this.windowSet = windowSet;
    enableListeners();
  }

  private void enableListeners() {
    windowSet.addWindowSetMutationEventListener(this, true);
  }

  public static WindowSetGUI create(WindowSet windowSet) {
    return new WindowSetGUI(windowSet);
  }

  @Override
  public void onWindowAdded(WindowSet windowSet, Window window) {
    WindowGUI windowGUI = WindowGUI.create(window);
    windowGUI.showWindow();
  }

}
