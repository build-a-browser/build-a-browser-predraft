package net.buildabrowser.babbrowser.renderer.event;

import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.events.Event;
import net.buildabrowser.babbrowser.dom.listener.AbstractDocumentChangeListener;
import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment;

public class AbstractRendererDocumentChangeListener
  extends AbstractDocumentChangeListener implements RendererDocumentChangeListener {

  public AbstractRendererDocumentChangeListener(DocumentChangeListener nextListener) {
    super(nextListener);
  }

  @Override
  public boolean onFragmentEvent(
    Element element, Event event,
    BoxFragment<?> refFragment,
    LayoutFragment target,
    boolean allowDefault
  ) {
    if (nextListener() instanceof RendererDocumentChangeListener nextListener) {
      return nextListener.onFragmentEvent(
        element, event, refFragment, target, allowDefault);
    }

    return allowDefault;
  }
  
}
