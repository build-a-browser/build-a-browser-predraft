package net.buildabrowser.babbrowser.browser;

import java.awt.Component;
import java.net.URI;

import net.buildabrowser.babbrowser.browser.imp.BrowserInstanceImp;
import net.buildabrowser.babbrowser.browser.uistate.WindowSet;
import net.buildabrowser.babbrowser.cookies.CookieStore;
import net.buildabrowser.babbrowser.cookies.exception.CookieStoreException;
import net.buildabrowser.babbrowser.painter.core.ComponentPainter;
import net.buildabrowser.babbrowser.renderer.RenderingEngine;

public interface BrowserInstance {
  
  RenderingEngine getRenderingEngine();

  WindowSet windowSet();

  static BrowserInstance create(
    URI profilePath,
    ComponentPainter<Component> painter,
    CookieStore cookieStore
  ) throws CookieStoreException {
    cookieStore.initialize();
    return new BrowserInstanceImp(profilePath, painter, cookieStore);
  }

}
