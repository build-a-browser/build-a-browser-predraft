package net.buildabrowser.babbrowser.browser.render.imp;

import java.awt.Component;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import net.buildabrowser.babbrowser.browser.network.ProtocolRegistry;
import net.buildabrowser.babbrowser.browser.render.Renderer;
import net.buildabrowser.babbrowser.browser.render.box.Box;
import net.buildabrowser.babbrowser.browser.render.box.BoxGenerator;
import net.buildabrowser.babbrowser.css.engine.matcher.CSSMatcher;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.mutable.DocumentChangeListener;
import net.buildabrowser.babbrowser.htmlparser.HTMLParser;

public class RendererImp implements Renderer {
  
  private final ProtocolRegistry protocolRegistry;
  private final URL url;

  public RendererImp(ProtocolRegistry protocolRegistry, URL url) {
    this.protocolRegistry = protocolRegistry;
    this.url = url;
  }

  public Component render() throws IOException {
    CSSMatcher cssMatcher = CSSMatcher.create(new RenderCSSMatcherContext());
    DocumentChangeListener changeListener = new RenderDocumentChangeListener(cssMatcher.documentChangeListener());
    try (InputStream inputStream = protocolRegistry.request(url)) {
      long time = System.currentTimeMillis();
      Document document = HTMLParser.create().parse(new InputStreamReader(inputStream, StandardCharsets.UTF_8), changeListener);
      long elapsed = System.currentTimeMillis() - time;
      System.out.println("Num millis elapsed: " + elapsed);
      // System.out.println(document);
      cssMatcher.applyStylesheets(document);
      
      BoxGenerator boxGenerator = BoxGenerator.create();
      Box documentBox = boxGenerator.box(document).get(0);
      return documentBox.render(ActiveStyles.create());
    }
  }

}
