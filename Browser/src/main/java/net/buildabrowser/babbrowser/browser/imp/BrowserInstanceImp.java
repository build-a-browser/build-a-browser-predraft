package net.buildabrowser.babbrowser.browser.imp;

import net.buildabrowser.babbrowser.browser.BrowserInstance;
import net.buildabrowser.babbrowser.browser.render.RenderingEngine;

public class BrowserInstanceImp implements BrowserInstance {

  private final RenderingEngine renderingEngine;

  public BrowserInstanceImp(RenderingEngine renderingEngine) {
    this.renderingEngine = renderingEngine;
  }

  @Override
  public RenderingEngine getRenderingEngine() {
    return this.renderingEngine;
  }
  
}
