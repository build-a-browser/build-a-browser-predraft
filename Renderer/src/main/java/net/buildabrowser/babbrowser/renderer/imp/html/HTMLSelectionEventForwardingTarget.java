package net.buildabrowser.babbrowser.renderer.imp.html;

import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.util.HTMLSerializerUtil;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.selection.Selection;
import net.buildabrowser.babbrowser.html.selection.SelectionUtil;
import net.buildabrowser.babbrowser.renderer.clipboard.ClipboardProvider;
import net.buildabrowser.babbrowser.renderer.context.SelectionContext;
import net.buildabrowser.babbrowser.renderer.event.AbstractEventForwardingTarget;
import net.buildabrowser.babbrowser.renderer.event.EventForwardingTarget;
import net.buildabrowser.babbrowser.renderer.event.EventHandlerResponse;
import net.buildabrowser.babbrowser.renderer.event.EventHandlerResponse.SyncEventHandlerResponse;
import net.buildabrowser.babbrowser.renderer.event.events.RendererKeyboardEvent;
import net.buildabrowser.babbrowser.renderer.event.events.RendererKeyboardEvent.KeyboardEventType;

public class HTMLSelectionEventForwardingTarget<T> extends AbstractEventForwardingTarget {

  private final HTMLDocument document;
  private final SelectionContext selectionContext;
  private final ClipboardProvider<T> clipboardProvider;

  public HTMLSelectionEventForwardingTarget(
    HTMLDocument document,
    SelectionContext selectionContext,
    ClipboardProvider<T> clipboardProvider,
    EventForwardingTarget nextTarget
  ) {
    super(nextTarget);
    this.document = document;
    this.selectionContext = selectionContext;
    this.clipboardProvider = clipboardProvider;
  }

  @Override
  public EventHandlerResponse forwardEvent(
    RendererKeyboardEvent event,
    SyncEventHandlerResponse prevResponse
  ) {
    if (prevResponse.equals(SyncEventHandlerResponse.HANDLED)) {
      return super.forwardEvent(event, prevResponse);
    }

    if (
      event.type().equals(KeyboardEventType.KEY_DOWN)
      && event.ctrlKey()
      && event.code().equals(RendererKeyboardEvent.KEY_C)
    ) {
      copySelection();
      return super.forwardEvent(event, SyncEventHandlerResponse.HANDLED);
    }

    return super.forwardEvent(event, prevResponse);
  }

  private void copySelection() {
    copyHTMLSelection();
  }

  private void copyHTMLSelection() {
    Selection selection = document.getSelection();
    HTMLSelectionBuilder selectionBuilder = new HTMLSelectionBuilder(
      selection, selectionContext);
    SelectionUtil.determineSelectedNodes(
      selection, selectionBuilder);

    Node selectionRoot = selectionBuilder.rootNode();
    String textContent = HTMLSerializerUtil.serializeNodeAsText(selectionRoot);
    T clip = clipboardProvider.createHtmlClip(selectionRoot, textContent);
    clipboardProvider.setActiveClip(clip);
  }

}