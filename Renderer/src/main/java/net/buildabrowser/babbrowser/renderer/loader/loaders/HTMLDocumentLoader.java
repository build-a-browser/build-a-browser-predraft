package net.buildabrowser.babbrowser.renderer.loader.loaders;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicLong;

import net.buildabrowser.babbrowser.common.datastruct.SlotFamilyFamily;
import net.buildabrowser.babbrowser.fetch.FetchResponse;
import net.buildabrowser.babbrowser.html.events.EventLoop;
import net.buildabrowser.babbrowser.html.events.TaskSource;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.navigation.DocumentRenderer;
import net.buildabrowser.babbrowser.html.navigation.NavigationParams;
import net.buildabrowser.babbrowser.html.navigation.UANavigableOptions;
import net.buildabrowser.babbrowser.html.scripting.Window;
import net.buildabrowser.babbrowser.htmlparser.HTMLParser;
import net.buildabrowser.babbrowser.renderer.RenderingEngine;
import net.buildabrowser.babbrowser.renderer.imp.html.HTMLGraphicalDocumentRendererImp;
import net.buildabrowser.babbrowser.renderer.loader.DocumentLoader;
import net.buildabrowser.babbrowser.renderer.logging.PerfLogging;
import net.buildabrowser.babbrowser.stream.ReadRequest;
import net.buildabrowser.babbrowser.stream.ReadableStream.ReadableStreamGetReaderOptions;
import net.buildabrowser.babbrowser.stream.imp.ReadableStreamDefaultReaderImp;

public class HTMLDocumentLoader implements DocumentLoader {

  @Override
  public HTMLDocument load(
    UANavigableOptions uaNavigableOptions,
    RenderingEngine renderingEngine,
    NavigationParams navigationParams,
    SlotFamilyFamily slotFamilyFamily
  ) throws IOException {
    FetchResponse response = navigationParams.response();
    // TODO: Proper way to obtain a document and its browsing context
    HTMLDocument document = HTMLDocument.create(
      uaNavigableOptions,
      navigationParams.navigable().activeBrowsingContext(),
      navigationParams.navigable());
    document.setURL(response.url());
    // TODO: Populate document

    parseHTMLDocument(response, document);

    DocumentRenderer renderer = new HTMLGraphicalDocumentRendererImp(
      document, navigationParams.navigable(), renderingEngine, slotFamilyFamily);
    document.attachRenderer(renderer);

    return document;
  }

  private void parseHTMLDocument(FetchResponse response, HTMLDocument document) {
    Window window = document.browsingContext().activeWindow();
    long downloadStartTime = System.currentTimeMillis();
    HTMLParser htmlParser = HTMLParser.create(document, StandardCharsets.UTF_8);
    AtomicLong parseTime = new AtomicLong(0);

    ReadableStreamGetReaderOptions options = new ReadableStreamGetReaderOptions();
    ReadableStreamDefaultReaderImp reader = (ReadableStreamDefaultReaderImp) response.body().stream().getReader(options);
    // TODO: Use the normal reader's exposed methods instead, once implemented
    reader.read(new ReadRequest() {

      @Override
      public void chunk(ByteBuffer chunk) {
        EventLoop.queueGlobalTask(TaskSource.DOM, window,
          () -> {
            long parseChunkStartTime = System.currentTimeMillis();
            htmlParser.parse(chunk);
            parseTime.addAndGet(System.currentTimeMillis() - parseChunkStartTime);
          });
        reader.read(this);
      }

      @Override
      public void close() {
        PerfLogging.logDownloadTime(downloadStartTime, document.url());
        EventLoop.queueGlobalTask(TaskSource.DOM, window, () -> {
          long parseChunkStartTime = System.currentTimeMillis();
          htmlParser.done();
          long logTime = parseTime.addAndGet(System.currentTimeMillis() - parseChunkStartTime);;
          PerfLogging.logParseTime(logTime, document.url());
        });
      }

      @Override
      public void error(Object e) {
        ((Throwable) e).printStackTrace();
      }
      
    });
  }
  
}
