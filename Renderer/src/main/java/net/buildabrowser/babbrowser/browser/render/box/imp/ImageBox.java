package net.buildabrowser.babbrowser.browser.render.box.imp;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JPanel;

import net.buildabrowser.babbrowser.browser.network.URLUtil;
import net.buildabrowser.babbrowser.browser.network.exception.BadURLException;
import net.buildabrowser.babbrowser.browser.render.box.Box;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.dom.Element;

public class ImageBox implements Box {

  private final Element element;
  private final JComponent imageComponent = createImageComponent();

  private URL loadingImageURL;
  private BufferedImage image;

  public ImageBox(Element element) {
    this.element = element;
  }

  @Override
  public synchronized JComponent render(ActiveStyles parentStyles) {
    URL imageSource = getImageSource();
    if (loadingImageURL == null || !loadingImageURL.equals(imageSource)) {
      image = null;
      loadingImageURL = imageSource;
      new Thread(() -> loadBufferedImage(loadingImageURL)).start();
    }

    return imageComponent;
  }

  private JPanel createImageComponent() {
    return new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
          g.drawImage(image, 0, 0, this);
          return;
        }

        String alt = getImageAlt();
        
        FontMetrics fm = g.getFontMetrics();
        int stringAscent = fm.getAscent();

        g.drawString(alt, 0, stringAscent);
      }

      @Override
      public Dimension getPreferredSize() {
        if (image != null) {
          return new Dimension(image.getWidth(), image.getHeight());
        }

        String alt = getImageAlt();
        FontMetrics fm = this.getFontMetrics(this.getFont());
        
        int width = fm.stringWidth(alt);
        int height = fm.getHeight();
        
        return new Dimension(width, height);
      }

      @Override
      public Dimension getMinimumSize() {
        return getPreferredSize();
      }

      @Override
      public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, (int) getPreferredSize().getHeight());
      }
  	};
  }

  private synchronized void loadBufferedImage(URL loadingImageURL) {
    try {
      this.image = ImageIO.read(loadingImageURL);
      imageComponent.revalidate();
      imageComponent.repaint();
    } catch (IOException e) {
      e.printStackTrace();
      this.image = null;
    }
  }

  private URL getImageSource() {
    String src = element.attributes().get("src");
    if (src == null || src.isEmpty()) {
      return null;
    }

    try {
      return URLUtil.createURL(src);
    } catch (BadURLException e) {
      return null;
    }
  }

  private String getImageAlt() {
    String alt = element.attributes().get("alt");
    if (alt == null) {
      return "Image";
    }
    return alt;
  }
  
}
