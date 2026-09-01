package net.buildabrowser.babbrowser.painter.core;

public interface ProgressiveImageCallbacks {
  
  void onImageUpdate();

  void onImageDone();

  void onImageFailure(Exception e);

}
