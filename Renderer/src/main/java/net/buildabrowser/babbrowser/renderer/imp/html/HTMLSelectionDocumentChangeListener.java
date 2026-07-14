package net.buildabrowser.babbrowser.renderer.imp.html;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.events.Event;
import net.buildabrowser.babbrowser.dom.events.PointerEvent;
import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.selection.Selection;
import net.buildabrowser.babbrowser.painter.core.FontMetrics;
import net.buildabrowser.babbrowser.renderer.context.SelectionContext;
import net.buildabrowser.babbrowser.renderer.event.AbstractRendererDocumentChangeListener;
import net.buildabrowser.babbrowser.renderer.event.util.MouseEventUtil;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.TextFragment;

public class HTMLSelectionDocumentChangeListener extends AbstractRendererDocumentChangeListener {

  private final SelectionContext selectionContext;
  private final Selection selection;

  private Node currentAnchor;
  private int currentAnchorSource = 0;
  private boolean mouseDown; // TODO: Store in the event

  public HTMLSelectionDocumentChangeListener(
    HTMLDocument document,
    SelectionContext selectionContext,
    DocumentChangeListener nextListener
  ) {
    super(nextListener);
    this.selectionContext = selectionContext;
    this.selection = document.getSelection();
  }
  
  @Override
  public boolean onFragmentEvent(
    Element element, Event event,
    BoxFragment<?> refFragment,
    LayoutFragment fragment,
    boolean allowDefault
  ) {
    if (event.type().equals("mousedown")) {
      this.currentAnchor = fragment instanceof TextFragment textFragment ?
        textFragment.sourceNode() :
        element;
      this.mouseDown = true;
      
      if (allowDefault) {
        this.currentAnchorSource = determineSourceTextOffset(event, refFragment, fragment);
        selection.setBaseAndExtent(
          currentAnchor, currentAnchorSource, null, 0);
        // TODO: These should be in an event listener on Selection itself
        selectionContext.updateSelection();
        refFragment.box().context().invalidate(InvalidationLevel.PAINT);
      }
    } else if (
      allowDefault
      && mouseDown
      && event.type().equals("mousemove")
    ) {
      Node currentFocus = fragment instanceof TextFragment textFragment ?
        textFragment.sourceNode() :
        element;
      int currentFocusSource = determineSourceTextOffset(event, refFragment, fragment);
      selection.setBaseAndExtent(
        currentAnchor, currentAnchorSource, currentFocus, currentFocusSource);
      selectionContext.updateSelection();
      refFragment.box().context().invalidate(InvalidationLevel.PAINT);
    } else if (event.type().equals("mouseup")) {
      this.mouseDown = false;
    }

    return super.onElementEvent(element, event, allowDefault);
  }

  @Override
  public void onSelectionChanged() {
    selectionContext.updateSelection();
  }

  private int determineSourceTextOffset(
    Event event, BoxFragment<?> refFragment, LayoutFragment fragment
  ) {
    int currentAnchorSource = 0;
    if (
      fragment instanceof TextFragment textFragment
      && event instanceof PointerEvent pointerEvent
    ) {
      FontMetrics fontMetrics = refFragment.box().layoutContext().font().metrics();
      
      float mouseX = pointerEvent.layerX() - fragment.layerX(Measurement.CONTENT);
      int textIndex = MouseEventUtil.determineTextMouseIndex(
        mouseX, fontMetrics, textFragment.text());
      currentAnchorSource = textFragment.sourceIndex(textIndex);
    }

    return currentAnchorSource;
  }

}
