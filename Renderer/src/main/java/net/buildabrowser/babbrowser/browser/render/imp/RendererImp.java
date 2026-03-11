package net.buildabrowser.babbrowser.browser.render.imp;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Optional;

import javax.swing.JPanel;

import net.buildabrowser.babbrowser.browser.network.ProtocolRegistry;
import net.buildabrowser.babbrowser.browser.render.Renderer;
import net.buildabrowser.babbrowser.browser.render.box.Box;
import net.buildabrowser.babbrowser.browser.render.box.Box.InvalidationLevel;
import net.buildabrowser.babbrowser.browser.render.box.BoxGenerator;
import net.buildabrowser.babbrowser.browser.render.box.DocumentBox;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.box.imp.DocumentBoxImp;
import net.buildabrowser.babbrowser.browser.render.composite.CompositeLayer;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.position.PositionLayout;
import net.buildabrowser.babbrowser.browser.render.layout.GlobalLayoutContext;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.browser.render.layout.StackingContext;
import net.buildabrowser.babbrowser.browser.render.layout.StackingContextGenerator;
import net.buildabrowser.babbrowser.browser.render.paint.FontMetrics;
import net.buildabrowser.babbrowser.browser.render.paint.Painter;
import net.buildabrowser.babbrowser.browser.render.paint.java2d.J2DFontMetrics;
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
  private final URI url;
  private final Painter painter;

  private DocumentBox documentBox;

  public RendererImp(ProtocolRegistry protocolRegistry, URI url, Painter painter) {
    this.protocolRegistry = protocolRegistry;
    this.url = url;
    this.painter = painter;
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
      cssMatcher.applyStylesheets(document, uaStyleSheets);
      
      JPanel jpanel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
          if (documentBox == null) return;
          FontMetrics fontMetrics = new J2DFontMetrics(g.getFontMetrics());
          ElementBox rootBox = documentBox.htmlBox();

          Object cacheKey = new Object();
          GlobalLayoutContext globalLayoutContext = new GlobalLayoutContext(
            url, painter.resourceLoader(), fontMetrics, cacheKey);

          LayoutContext layoutContext = new LayoutContext(globalLayoutContext, fontMetrics);
          ArrayDeque<ElementBox> deferredLayout = new ArrayDeque<>();

          UnmanagedBoxFragment fragment = rootBox.layout(layoutContext,
            LayoutConstraint.of(this.getWidth()),
            LayoutConstraint.of(this.getHeight()));
          fragment.setPos(0, 0);
          StackingContextGenerator.generateStackingContextsRoot(rootBox, deferredLayout, globalLayoutContext);
          rootBox.stackingContext().addFragment(0, 0, fragment);
          rootBox.content().positionLayers(0, 0, layoutContext.global());
          
          while (!deferredLayout.isEmpty()) {
            ElementBox itemBox = deferredLayout.pop();
            layoutAbsolute(globalLayoutContext, layoutContext, deferredLayout, itemBox);
          }
          
          CompositeLayer rootLayer = rootBox.stackingContext().createLayer();

          g.setColor(new Color(0xFFFFFF, false));
          g.fillRect(0, 0, getWidth(), getHeight());
          
          painter.withCanvas(g, this.getWidth(), this.getHeight(), canvas ->
            rootLayer.paint(canvas));
          
          System.gc();
        }

        private void layoutAbsolute(
          GlobalLayoutContext globalLayoutContext,
          LayoutContext layoutContext,
          ArrayDeque<ElementBox> deferredLayout,
          ElementBox itemBox
        ) {
          // TODO: Need to use proper layout context for item
          StackingContext parentContext = itemBox.stackingContext().parentContext();
          float[] insets = itemBox.stackingContext().computeInsets(layoutContext);
          float refWidth = parentContext.computeWidth();
          float refHeight = parentContext.computeHeight();
          UnmanagedBoxFragment itemFragment = PositionLayout.actuallyLayoutAbsolute(
            layoutContext, itemBox, refWidth, refHeight, insets);
          float[] position = PositionLayout.positionAbsolute(
            insets, itemFragment, refWidth, refHeight);
          
          StackingContextGenerator.generateStackingContextsDeferred(itemBox, deferredLayout, globalLayoutContext);
          itemBox.stackingContext().addFragment(
            parentContext.posX() + position[0],
            parentContext.posY() + position[1],
            itemFragment);
          itemBox.content().positionLayers(0, 0, layoutContext.global());
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
