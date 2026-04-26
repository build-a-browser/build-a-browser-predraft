package net.buildabrowser.babbrowser.render.paint.backend;

public interface FontMetrics {

  float size();

  int weight();
  
  float stringWidth(String text);

  float height();

  float xHeight();

  float ascent();

}
