package net.buildabrowser.babbrowser.browser.render.imp;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import javax.swing.JPanel;

import net.buildabrowser.babbrowser.browser.network.ProtocolRegistry;
import net.buildabrowser.babbrowser.browser.render.Renderer;
import net.buildabrowser.babbrowser.browser.render.box.Box;
import net.buildabrowser.babbrowser.browser.render.box.Box.InvalidationLevel;
import net.buildabrowser.babbrowser.browser.render.box.BoxContent;
import net.buildabrowser.babbrowser.browser.render.box.BoxGenerator;
import net.buildabrowser.babbrowser.browser.render.box.DocumentBox;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.box.imp.DocumentBoxImp;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.browser.render.paint.FontMetrics;
import net.buildabrowser.babbrowser.browser.render.paint.java2d.J2DFontMetrics;
import net.buildabrowser.babbrowser.browser.render.paint.java2d.J2DPaintCanvas;
import net.buildabrowser.babbrowser.css.engine.matcher.CSSMatcher;
import net.buildabrowser.babbrowser.cssbase.cssom.CSSStyleSheet;
import net.buildabrowser.babbrowser.cssbase.cssom.StyleSheetList;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokenizer.CSSTokenizerInput;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.mutable.DocumentChangeListener;
import net.buildabrowser.babbrowser.dom.utils.CommonUtils;
import net.buildabrowser.babbrowser.htmlparser.HTMLParser;

public class RendererImp implements Renderer {
  
  private final ProtocolRegistry protocolRegistry;
  private final URL url;

  private DocumentBox documentBox;

  public RendererImp(ProtocolRegistry protocolRegistry, URL url) {
    this.protocolRegistry = protocolRegistry;
    this.url = url;
  }

  public Component render() throws IOException {
    StyleSheetList uaStyleSheets = loadUAStyleSheets();
    CSSMatcher cssMatcher = CSSMatcher.create(new RenderCSSMatcherContext());
    DocumentChangeListener changeListener = new RenderDocumentChangeListener(cssMatcher.documentChangeListener());
    try (InputStream inputStream = protocolRegistry.request(url)) {
      long time = System.currentTimeMillis();
      Document document = HTMLParser.create().parse(new InputStreamReader(inputStream, StandardCharsets.UTF_8), changeListener);
      long elapsed = System.currentTimeMillis() - time;
      System.out.println("Num millis elapsed: " + elapsed);
      // System.out.println(document);
      cssMatcher.applyStylesheets(document, uaStyleSheets);
      
      JPanel jpanel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
          if (documentBox == null) return;
          FontMetrics fontMetrics = new J2DFontMetrics(g.getFontMetrics());
          LayoutContext layoutContext = new LayoutContext(url, fontMetrics);
          BoxContent content = documentBox.htmlBox().content();
          content.prelayout(layoutContext);
          content.layout(layoutContext,
            LayoutConstraint.of(this.getWidth()),
            LayoutConstraint.of(this.getHeight()));

          g.setColor(new Color(0xFFFFFF, false));
          g.fillRect(0, 0, getWidth(), getHeight());
          content.paint(new J2DPaintCanvas((Graphics2D) g));
        }
      };

      BoxGenerator boxGenerator = BoxGenerator.create();
      this.documentBox = new DocumentBoxImp() {
        @Override
        public void invalidate(InvalidationLevel invalidationLevel) {
          jpanel.revalidate();
          jpanel.repaint();
        }
      };
      Box child = boxGenerator.box(documentBox, document.childNodes().item(0)).get(0);
      documentBox.setChild((ElementBox) child);

      documentBox.invalidate(InvalidationLevel.LAYOUT);

      return jpanel;
    }
  }

  @Override
  public Optional<String> getTitle() {
    // TODO: Implement
    return Optional.empty();
  }

  private StyleSheetList loadUAStyleSheets() throws IOException {
    try (Reader reader = new InputStreamReader(ClassLoader.getSystemClassLoader().getResourceAsStream("ua/ua.css"))) {
      CSSTokenizerInput tokenizerInput = CSSTokenizerInput.fromReader(reader);
      CSSTokenStream tokenizerStream = CSSTokenStream.create(tokenizerInput);
      
      CSSStyleSheet styleSheet = CommonUtils.rethrow(() -> CSSParser.create().parseAStyleSheet(tokenizerStream));
      return StyleSheetList.create(List.of(styleSheet));
    }
  }

}
