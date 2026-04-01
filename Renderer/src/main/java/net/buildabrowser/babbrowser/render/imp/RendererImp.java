package net.buildabrowser.babbrowser.render.imp;

import java.awt.Component;
import java.awt.Graphics;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;

import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.cssbase.cssom.CSSStyleSheet;
import net.buildabrowser.babbrowser.cssbase.cssom.StyleSheetList;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokenizer.CSSTokenizerInput;
import net.buildabrowser.babbrowser.fetch.FetchEngine;
import net.buildabrowser.babbrowser.render.Renderer;
import net.buildabrowser.babbrowser.render.event.EventForwardingTarget;
import net.buildabrowser.babbrowser.render.event.EventHandler.MouseEvent;
import net.buildabrowser.babbrowser.render.event.EventHandler.MouseEvent.MouseEventType;
import net.buildabrowser.babbrowser.render.paint.Painter;

public class RendererImp implements Renderer {

  private static StyleSheetList uaStyleSheets;
  
  private final DocumentRendererImp documentRenderer;

  private JPanel jpanel;

  public RendererImp(FetchEngine fetchEngine, URI url, Painter painter) {
    this.documentRenderer = new DocumentRendererImp(
      url, painter, fetchEngine,
      uaStyleSheets, () -> {
        jpanel.revalidate();
        jpanel.repaint();
      });
  }

  public Component render() throws IOException {
    if (this.jpanel != null) {
      return this.jpanel;
    }
    
    documentRenderer.start();

    this.jpanel = new JPanel() {
      @Override
      public void doLayout() {
        documentRenderer.resize(getWidth(), getHeight());
        super.doLayout();
      }

      @Override
      protected void paintComponent(Graphics g) {
        documentRenderer.withImage(image -> g.drawImage(image, 0, 0, null));
      }
    };

    jpanel.addMouseListener(new MouseInputAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent e) {
        // TODO: Translate button
        MouseEvent mouseEvent = new MouseEvent(e.getX(), e.getY(), e.getButton(), MouseEventType.CLICK);
        if (documentRenderer instanceof EventForwardingTarget target) {
          target.forwardEvent(mouseEvent);
        }
      }
    });

    return this.jpanel;
  }

  @Override
  public Optional<String> getTitle() {
    // TODO: Implement
    return Optional.empty();
  }

  @Override
  public void close() {
    documentRenderer.shutdown();
  }

  private static StyleSheetList loadUAStyleSheets() throws IOException {
    try (Reader reader = new InputStreamReader(ClassLoader.getSystemClassLoader().getResourceAsStream("ua/ua.css"))) {
      CSSTokenizerInput tokenizerInput = CSSTokenizerInput.fromReader(reader);
      CSSTokenStream tokenizerStream = CSSTokenStream.create(tokenizerInput);
      
      CSSStyleSheet styleSheet = CommonUtil.rethrow(() -> CSSParser.create().parseAStyleSheet(tokenizerStream));
      return StyleSheetList.create(List.of(styleSheet));
    }
  }

  static {
    try {
      uaStyleSheets = loadUAStyleSheets();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
