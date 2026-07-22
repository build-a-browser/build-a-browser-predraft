package net.buildabrowser.babbrowser.renderer.event;

import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.events.Event;
import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment;

public interface RendererDocumentChangeListener extends DocumentChangeListener {
  
  default void onBoxFragmentAdded(
    BoxFragment<?> fragment
  ) {}

  default boolean onFragmentEvent(
    Element element, Event event,
    BoxFragment<?> refFragment,
    LayoutFragment target,
    boolean allowDefault
  ) {
    return allowDefault;
  }

}
