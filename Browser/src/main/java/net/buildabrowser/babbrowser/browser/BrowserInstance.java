package net.buildabrowser.babbrowser.browser;

import net.buildabrowser.babbrowser.browser.imp.BrowserInstanceImp;
import net.buildabrowser.babbrowser.renderer.RenderingEngine;

public interface BrowserInstance {
  
  RenderingEngine getRenderingEngine();

  static BrowserInstance create(RenderingEngine renderingEngine) {
    return new BrowserInstanceImp(renderingEngine);
  }

}
