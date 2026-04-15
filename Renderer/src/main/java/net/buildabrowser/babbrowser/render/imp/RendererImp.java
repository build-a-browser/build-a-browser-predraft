package net.buildabrowser.babbrowser.render.imp;

import java.awt.Component;
import java.awt.Graphics;
import java.io.IOException;
import java.util.Optional;

import javax.swing.JPanel;

import net.buildabrowser.babbrowser.html.navigation.DocumentRenderer;
import net.buildabrowser.babbrowser.html.navigation.Navigable;
import net.buildabrowser.babbrowser.render.Renderer;
import net.buildabrowser.babbrowser.render.paint.Painter;

public class RendererImp implements Renderer {
  
  private final Navigable navigable;

  private DocumentRenderer activeDocumentRenderer;
  private JPanel jpanel;

  public RendererImp(
    Navigable navigable, Painter painter
  ) {
    this.navigable = navigable;
    navigable.uaNavigableOptions().addRepaintListener(() -> {
      if (jpanel == null) return;
      jpanel.revalidate();
      jpanel.repaint();
    });
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

    return this.jpanel;
  }

  @Override
  public Optional<String> getTitle() {
    // TODO: Implement
    return Optional.empty();
  }

  @Override
  public void close() {}

}
