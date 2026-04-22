package net.buildabrowser.babbrowser.render.imp;

import java.awt.Component;
import java.awt.Graphics;
import java.io.IOException;
import java.util.Optional;

import javax.swing.JPanel;

import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.navigation.DocumentRenderer;
import net.buildabrowser.babbrowser.html.navigation.DocumentRenderer.DocumentRendererEventListener;
import net.buildabrowser.babbrowser.html.navigation.Navigable;
import net.buildabrowser.babbrowser.render.Renderer;
import net.buildabrowser.babbrowser.render.paint.Painter;

public class RendererImp implements Renderer {
  
  private final Navigable navigable;
  private final DocumentRendererEventListener eventListener;

  private DocumentRenderer activeDocumentRenderer;
  private JPanel jpanel;

  public RendererImp(
    Navigable navigable, Painter painter,
    DocumentRendererEventListener eventListener
  ) {
    this.navigable = navigable;
    navigable.uaNavigableOptions().addRepaintListener(() -> {
      if (jpanel == null) return;
      jpanel.revalidate();
      jpanel.repaint();
    });

    this.eventListener = eventListener;
  }

  public Component render() throws IOException {
    if (this.jpanel != null) {
      return this.jpanel;
    }

    this.jpanel = new JPanel() {
      @Override
      public void doLayout() {
        DocumentRenderer documentRenderer = navigable.activeDocument().renderer();
        activeDocumentRenderer = documentRenderer;
        if (documentRenderer == null) return;

        documentRenderer.setEventListener(eventListener);
        documentRenderer.resize(getWidth(), getHeight());
        super.doLayout();
      }

      @Override
      protected void paintComponent(Graphics g) {
        if (activeDocumentRenderer == null) return;
        activeDocumentRenderer.withImage(image -> g.drawImage(image, 0, 0, null));
      }
    };

    RendererMouseInputAdapter inputHandler = new RendererMouseInputAdapter(() -> activeDocumentRenderer);
    jpanel.addMouseListener(inputHandler);
    jpanel.addMouseMotionListener(inputHandler);
    jpanel.addMouseWheelListener(inputHandler);

    return this.jpanel;
  }

  @Override
  public Optional<String> getTitle() {
    if (navigable.activeDocument() instanceof HTMLDocument document) {
      String title = document.title();
      if (!title.isEmpty()) {
        return Optional.of(title);
      }
    }

    return Optional.empty();
  }

  @Override
  public void close() {}

}
