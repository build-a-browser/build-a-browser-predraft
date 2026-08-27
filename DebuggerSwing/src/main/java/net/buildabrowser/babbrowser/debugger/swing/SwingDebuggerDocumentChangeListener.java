package net.buildabrowser.babbrowser.debugger.swing;

import net.buildabrowser.babbrowser.debugger.core.DebugBox;
import net.buildabrowser.babbrowser.debugger.core.DebugFragment;
import net.buildabrowser.babbrowser.debugger.core.DebugObject;
import net.buildabrowser.babbrowser.debugger.core.DebugObject.DebugObjectSelection;
import net.buildabrowser.babbrowser.debugger.core.DebuggerDocumentChangeListener;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.events.Event;
import net.buildabrowser.babbrowser.dom.events.PointerEvent;
import net.buildabrowser.babbrowser.dom.listener.AbstractDocumentChangeListener;

public class SwingDebuggerDocumentChangeListener
  extends AbstractDocumentChangeListener implements DebuggerDocumentChangeListener {
  
  private final SwingFrameDebugger frameDebugger;

  public SwingDebuggerDocumentChangeListener(
    SwingFrameDebugger frameDebugger
  ) {
    super(null);
    this.frameDebugger = frameDebugger;
  }

  @Override
  public boolean onElementEventEarly(
    Element element, Event event, boolean allowDefault
  ) {
    SwingNodeSelection nodeSelection = frameDebugger.nodeSelection();
    if (!nodeSelection.clickToSelectEnabled()) return allowDefault;
    if (!(event instanceof PointerEvent mouseEvent)) return allowDefault;
    if (mouseEvent.type().equals("mousemove")) {
      return selectLater(element, DebugObjectSelection.TARGETED, false);
    } else if (mouseEvent.type().equals("click")) {
      nodeSelection.expand(element, null, null);
      return selectLater(element, DebugObjectSelection.SELECTED, true);
    }
    return allowDefault;
  }

  @Override
  public boolean onFragmentEvent(
    Node node, DebugBox box, DebugFragment fragment,
    Event event, boolean allowDefault
  ) {
    SwingNodeSelection nodeSelection = frameDebugger.nodeSelection();
    if (!nodeSelection.clickToSelectEnabled()) return allowDefault;
    if (!(event instanceof PointerEvent mouseEvent)) return allowDefault;
    if (mouseEvent.type().equals("mousemove")) {
      return selectLater(node, DebugObjectSelection.TARGETED, false);
    } else if (mouseEvent.type().equals("click")) {
      nodeSelection.expand(node, box, fragment);
      return selectLater(node, DebugObjectSelection.SELECTED, true);
    }
    return allowDefault;
  }

  private boolean selectLater(
    Node node, DebugObjectSelection selection, boolean cancelClickToSelect
  ) {
    frameDebugger.later(debugContext -> {
      DebugObject debugObject = debugContext.debugObjectForNode(node);
      frameDebugger.nodeSelection().select(debugObject, selection, cancelClickToSelect);
    });

    return false;
  }

}
