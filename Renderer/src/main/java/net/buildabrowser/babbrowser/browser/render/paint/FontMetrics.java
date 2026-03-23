package net.buildabrowser.babbrowser.browser.render.paint;

public interface FontMetrics {

  float size();

  int weight();
  
  float stringWidth(String text);

  float height();

  float xHeight();

  float ascent();

}
