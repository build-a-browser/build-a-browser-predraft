package net.buildabrowser.babbrowser.renderer.paint.backend;

public interface FontMetrics {

  float size();

  int weight();
  
  float stringWidth(String text);

  float height();

  float xHeight();

  float ascent();

}
