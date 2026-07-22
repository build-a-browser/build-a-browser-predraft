package net.buildabrowser.babbrowser.browser.chrome;

import java.awt.Component;

import net.buildabrowser.babbrowser.browser.uistate.Window;
import net.buildabrowser.babbrowser.browser.uistate.WindowSet;
import net.buildabrowser.babbrowser.browser.uistate.event.WindowSetMutationEventListener;
import net.buildabrowser.babbrowser.debugger.core.Debugger;
import net.buildabrowser.babbrowser.painter.core.ComponentPainter;

public class WindowSetGUI implements WindowSetMutationEventListener {

  private final WindowSet windowSet;
  private final ComponentPainter<Component> painter;
  private final Debugger debugger;

  private WindowSetGUI(
    WindowSet windowSet,
    ComponentPainter<Component> painter,
    Debugger debugger
  ) {
    this.windowSet = windowSet;
    this.painter = painter;
    this.debugger = debugger;
    enableListeners();
  }

  private void enableListeners() {
    windowSet.addWindowSetMutationEventListener(this, true);
  }

  @Override
  public void onWindowAdded(WindowSet windowSet, Window window) {
    WindowGUI windowGUI = WindowGUI.create(window, painter, debugger);
    windowGUI.showWindow();
  }

  public static WindowSetGUI create(
    WindowSet windowSet,
    ComponentPainter<Component> painter,
    Debugger debugger
  ) {
    return new WindowSetGUI(windowSet, painter, debugger);
  }

}
