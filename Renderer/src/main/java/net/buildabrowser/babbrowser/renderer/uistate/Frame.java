package net.buildabrowser.babbrowser.renderer.uistate;

import java.net.URI;

import net.buildabrowser.babbrowser.renderer.GraphicalDocumentRenderer;
import net.buildabrowser.babbrowser.renderer.RenderingEngine;
import net.buildabrowser.babbrowser.renderer.uistate.event.FrameEventListener;
import net.buildabrowser.babbrowser.renderer.uistate.imp.FrameImp;

public interface Frame {

  GraphicalDocumentRenderer getRenderer();

  String getTitle();

  URI getURL();

  void navigate(URI url);

  void reload();

  void back();

  void forward();

  void close();

  void addEventListener(FrameEventListener listener, boolean sync);

  void addRepaintListener(Runnable repaintListener);

  void removeRepaintListener(Runnable repaintListener);

  static Frame create(RenderingEngine renderingEngine) {
    return new FrameImp(renderingEngine);
  }
  
}