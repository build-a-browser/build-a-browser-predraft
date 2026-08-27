package net.buildabrowser.babbrowser.renderer.uistate.imp;

import java.util.List;

import net.buildabrowser.babbrowser.debugger.core.DebuggerDocumentChangeListener;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.events.Event;
import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;
import net.buildabrowser.babbrowser.dom.listener.ForkedDocumentChangeListener;
import net.buildabrowser.babbrowser.renderer.event.RendererDocumentChangeListener;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment;

public class FrameDebuggableDocumentChangeListener extends ForkedDocumentChangeListener implements RendererDocumentChangeListener {

  public FrameDebuggableDocumentChangeListener(
    DocumentChangeListener extraListener,
    List<DocumentChangeListener> nextListeners
  ) {
    super(extraListener, nextListeners);
  }

  @Override
  public void onBoxFragmentAdded(
    BoxFragment<?> fragment
  ) {
    if (nextListener() instanceof RendererDocumentChangeListener nextListener) {
      nextListener.onBoxFragmentAdded(fragment);
    }
  }
  
  @Override
  public boolean onFragmentEvent(
    Element element, Event event,
    BoxFragment<?> refFragment,
    LayoutFragment target,
    boolean allowDefault
  ) {
    return allowDefault;
  }

  @Override
  public boolean onFragmentEventEarly(
    Element element, Event event,
    BoxFragment<?> refFragment,
    LayoutFragment target,
    boolean allowDefault
  ) {
    for (DocumentChangeListener listener: nextListeners()) {
      // TODO: Also pass the fragment
      allowDefault = ((DebuggerDocumentChangeListener) listener).onFragmentEvent(
        element, refFragment.box(), null, event, allowDefault);
    }

    if (nextListener() instanceof RendererDocumentChangeListener nextListener) {
      return nextListener.onFragmentEventEarly(
        element, event, refFragment, target, allowDefault);
    }

    return allowDefault;
  }

}
