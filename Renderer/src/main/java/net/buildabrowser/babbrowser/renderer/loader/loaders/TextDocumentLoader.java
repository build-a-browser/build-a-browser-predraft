package net.buildabrowser.babbrowser.renderer.loader.loaders;

import static net.buildabrowser.babbrowser.htmlparser.insertion.util.ParseElementUtil.createHTMLElementForName;

import net.buildabrowser.babbrowser.common.datastruct.SlotFamilyFamily;
import net.buildabrowser.babbrowser.fetch.FetchResponse;
import net.buildabrowser.babbrowser.html.events.EventLoop;
import net.buildabrowser.babbrowser.html.events.TaskSource;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.html.html.HTMLText;
import net.buildabrowser.babbrowser.html.navigation.DocumentRenderer;
import net.buildabrowser.babbrowser.html.navigation.NavigationParams;
import net.buildabrowser.babbrowser.html.navigation.UANavigableOptions;
import net.buildabrowser.babbrowser.html.scripting.Window;
import net.buildabrowser.babbrowser.renderer.RenderingEngine;
import net.buildabrowser.babbrowser.renderer.imp.html.HTMLGraphicalDocumentRendererImp;
import net.buildabrowser.babbrowser.renderer.loader.DocumentLoader;
import net.buildabrowser.babbrowser.stream.ReadableStreamDefaultReader;

public class TextDocumentLoader implements DocumentLoader {

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

    loadTextDocument(response, document);

    DocumentRenderer renderer = new HTMLGraphicalDocumentRendererImp(
      document, navigationParams.navigable(),
      renderingEngine, renderingEngine.newFrameAPIs(),
      slotFamilyFamily);
    document.attachRenderer(renderer);

    return document;
  }

  private void loadTextDocument(
    FetchResponse response,
    HTMLDocument document
  ) {
    Window window = document.browsingContext().activeWindow();
    ReadableStreamDefaultReader reader = (ReadableStreamDefaultReader) response.body().stream().getReader(null);
    reader.readAllBytes(bytes -> {
      formTextDocument(new String(bytes), document, window);
    }, err -> {
      // TODO: Properly show an error page
      formTextDocument("Renderer: Failed to load page text contents.", document, window);
    });
  }

  private void formTextDocument(String text, HTMLDocument document, Window window) {
    EventLoop.queueGlobalTask(TaskSource.DOM, window, () -> {
      HTMLElement htmlNode = createHTMLElementForName("html", document);
      document.appendChild(htmlNode);
      HTMLElement bodyNode = createHTMLElementForName("body", htmlNode);
      htmlNode.appendChild(bodyNode);
      HTMLText textNode = HTMLText.create(text);
      bodyNode.appendChild(textNode);
    });
  }
  
}
