package net.buildabrowser.babbrowser.painter.core;

public interface FontMetrics {

  float size();

  int weight();
  
  float stringWidth(String text);

  float height();

  float xHeight();

  float ascent();

  float descent();

}
