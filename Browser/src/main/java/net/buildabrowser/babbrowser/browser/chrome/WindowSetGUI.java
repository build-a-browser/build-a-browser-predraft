package net.buildabrowser.babbrowser.browser.chrome;

import java.awt.Component;

import net.buildabrowser.babbrowser.browser.uistate.Window;
import net.buildabrowser.babbrowser.browser.uistate.WindowSet;
import net.buildabrowser.babbrowser.browser.uistate.event.WindowSetMutationEventListener;
import net.buildabrowser.babbrowser.renderer.paint.backend.ComponentPainter;

public class WindowSetGUI implements WindowSetMutationEventListener {

  private final WindowSet windowSet;
  private final ComponentPainter<Component> painter;

  private WindowSetGUI(WindowSet windowSet, ComponentPainter<Component> painter) {
    this.windowSet = windowSet;
    this.painter = painter;
    enableListeners();
  }

  private void enableListeners() {
    windowSet.addWindowSetMutationEventListener(this, true);
  }

  public static WindowSetGUI create(
    WindowSet windowSet, ComponentPainter<Component> painter
  ) {
    return new WindowSetGUI(windowSet, painter);
  }

  @Override
  public void onWindowAdded(WindowSet windowSet, Window window) {
    WindowGUI windowGUI = WindowGUI.create(window, painter);
    windowGUI.showWindow();
  }

}
