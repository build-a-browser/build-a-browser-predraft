package net.buildabrowser.babbrowser.render.imp;

import java.awt.Component;
import java.io.IOException;
import java.util.Optional;

import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.navigation.DocumentRenderer;
import net.buildabrowser.babbrowser.html.navigation.DocumentRenderer.DocumentRendererEventListener;
import net.buildabrowser.babbrowser.html.navigation.Navigable;
import net.buildabrowser.babbrowser.render.Renderer;
import net.buildabrowser.babbrowser.render.paint.backend.CanvasCallbacks;
import net.buildabrowser.babbrowser.render.paint.backend.PaintCanvas;
import net.buildabrowser.babbrowser.render.paint.backend.Painter;

public class RendererImp implements Renderer {
  
  private final Navigable navigable;
  private final Painter painter;
  private final DocumentRendererEventListener eventListener;

  private DocumentRenderer activeDocumentRenderer;
  private Component panel;

  public RendererImp(
    Navigable navigable, Painter painter,
    DocumentRendererEventListener eventListener
  ) {
    this.navigable = navigable;
    this.painter = painter;
    navigable.uaNavigableOptions().addRepaintListener(() -> {
      if (panel == null) return;
      panel.revalidate();
      panel.repaint();
    });

    this.eventListener = eventListener;
  }

  public Component render() throws IOException {
    if (this.panel != null) {
      return this.panel;
    }

    this.panel = painter.createComponent(new CanvasCallbacks() {

      @Override
      public void layout() {
        DocumentRenderer documentRenderer = navigable.activeDocument().renderer();
        activeDocumentRenderer = documentRenderer;
        if (documentRenderer == null) return;

        documentRenderer.setEventListener(eventListener);
        documentRenderer.resize(panel.getWidth(), panel.getHeight());
      }

      @Override
      public void paint(PaintCanvas canvas) {
        if (activeDocumentRenderer == null) return;
        activeDocumentRenderer.draw(canvas);
      }
      
    });

    RendererMouseInputAdapter inputHandler = new RendererMouseInputAdapter(() -> activeDocumentRenderer);
    panel.addMouseListener(inputHandler);
    panel.addMouseMotionListener(inputHandler);
    panel.addMouseWheelListener(inputHandler);

    return this.panel;
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
