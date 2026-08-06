package net.buildabrowser.babbrowser.renderer.imp.html;

import static net.buildabrowser.babbrowser.html.util.HTMLDomUtil.isHtmlElement;

import java.net.URI;

import net.buildabrowser.babbrowser.common.datastruct.SlotFamily;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.util.HTMLSerializerUtil;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.html.selection.Selection;
import net.buildabrowser.babbrowser.html.selection.SelectionUtil;
import net.buildabrowser.babbrowser.painter.core.LoadedImage;
import net.buildabrowser.babbrowser.renderer.clipboard.ClipboardProvider;
import net.buildabrowser.babbrowser.renderer.content.image.ImageContent;
import net.buildabrowser.babbrowser.renderer.context.RenderContext;
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
  private final SlotFamily<HTMLElement, RenderContext> renderContexts;
  private final ClipboardProvider<T> clipboardProvider;

  public HTMLSelectionEventForwardingTarget(
    HTMLDocument document,
    SelectionContext selectionContext,
    ClipboardProvider<T> clipboardProvider,
    SlotFamily<HTMLElement, RenderContext> renderContexts,
    EventForwardingTarget nextTarget
  ) {
    super(nextTarget);
    this.document = document;
    this.selectionContext = selectionContext;
    this.renderContexts = renderContexts;
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
    Selection selection = document.getSelection();
    if (
      selection.anchorNode() != null
      && selection.anchorNode() == selection.focusNode()
      && isHtmlElement(selection.anchorNode(), "img")
    ) {
      copyImageSelection((HTMLElement) selection.anchorNode());
    } else {
      copyHTMLSelection();
    }
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

  private void copyImageSelection(HTMLElement imageNode) {
    RenderContext context = renderContexts.get(imageNode);
    if (context == null) return;
    if (
      context.box() == null
      || !(context.box().content() instanceof ImageContent imageContent)
    ) return;
    
    String alt = imageContent.alt();
    URI imageURI = imageContent.imageSource();
    LoadedImage image = imageContent.loadedImage();
    if (image == null) return;

    T clip = clipboardProvider.createImageClip(imageURI, image::streamData, alt);
    clipboardProvider.setActiveClip(clip);
  }

}