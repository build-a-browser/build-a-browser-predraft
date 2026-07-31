package net.buildabrowser.babbrowser.renderer.loader.loaders;

import static net.buildabrowser.babbrowser.htmlparser.insertion.util.ParseElementUtil.createHTMLElementForName;

import net.buildabrowser.babbrowser.common.datastruct.SlotFamilyFamily;
import net.buildabrowser.babbrowser.fetch.FetchResponse;
import net.buildabrowser.babbrowser.html.events.EventLoop;
import net.buildabrowser.babbrowser.html.events.TaskSource;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.html.navigation.DocumentRenderer;
import net.buildabrowser.babbrowser.html.navigation.NavigationParams;
import net.buildabrowser.babbrowser.html.navigation.UANavigableOptions;
import net.buildabrowser.babbrowser.html.scripting.Window;
import net.buildabrowser.babbrowser.renderer.RenderingEngine;
import net.buildabrowser.babbrowser.renderer.imp.html.HTMLGraphicalDocumentRendererImp;
import net.buildabrowser.babbrowser.renderer.loader.DocumentLoader;

public class ImageDocumentLoader implements DocumentLoader {

  @Override
  public HTMLDocument load(
    UANavigableOptions uaNavigableOptions,
    RenderingEngine renderingEngine,
    NavigationParams navigationParams,
    SlotFamilyFamily slotFamilyFamily
  ) {
    FetchResponse response = navigationParams.response();
    // TODO: Proper way to obtain a document and its browsing context
    HTMLDocument document = HTMLDocument.create(
      uaNavigableOptions,
      navigationParams.navigable().activeBrowsingContext(),
      navigationParams.navigable());
    document.setURL(response.url());

    formImageDocument(response, document);

    DocumentRenderer renderer = new HTMLGraphicalDocumentRendererImp(
      document, navigationParams.navigable(), renderingEngine, slotFamilyFamily);
    document.attachRenderer(renderer);

    return document;
  }

  private void formImageDocument(
    FetchResponse response,
    HTMLDocument document
  ) {
    Window window = document.browsingContext().activeWindow();
    EventLoop.queueGlobalTask(TaskSource.DOM, window, () -> {
      HTMLElement htmlNode = createHTMLElementForName("html", document);
      document.appendChild(htmlNode);
      HTMLElement bodyNode = createHTMLElementForName("body", htmlNode);
      bodyNode.addAttribute("style", "margin: 0;");
      htmlNode.appendChild(bodyNode);
      HTMLElement imgNode = createHTMLElementForName("img", htmlNode);
      // TODO: This will result in a duplicate request
      imgNode.addAttribute("src", response.url().toString());
      imgNode.addAttribute("alt", "Image from source: " + response.url().toString());
      imgNode.addAttribute("style", "max-width: 100vw; max-height: 100vh;");
      bodyNode.appendChild(imgNode);
    });
  }
  
}
