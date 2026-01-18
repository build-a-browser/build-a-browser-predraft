package net.buildabrowser.babbrowser.browser.render.uistate.imp;

import java.net.URL;

import net.buildabrowser.babbrowser.browser.network.URLUtil;
import net.buildabrowser.babbrowser.browser.network.exception.BadURLException;
import net.buildabrowser.babbrowser.browser.render.Renderer;
import net.buildabrowser.babbrowser.browser.render.RenderingEngine;
import net.buildabrowser.babbrowser.browser.render.uistate.Frame;
import net.buildabrowser.babbrowser.browser.render.uistate.event.BrowserEventDispatcher;
import net.buildabrowser.babbrowser.browser.render.uistate.event.FrameEventListener;

public class FrameImp implements Frame {

  private final RenderingEngine renderingEngine;
  private final BrowserEventDispatcher<FrameEventListener> eventDispatcher = BrowserEventDispatcher.create();
  
  private URL url;
  private Renderer currentRenderer;

  public FrameImp(RenderingEngine renderingEngine) {
    this.renderingEngine = renderingEngine;
    this.currentRenderer = renderingEngine.createBlankRenderer();
    try {
      navigate(URLUtil.createURL("about:blank"));
    } catch (BadURLException e) {
      // TODO: Handle this better
      e.printStackTrace();
    }
  }

  @Override
  public Renderer getCurrentRenderer() {
    return this.currentRenderer;
  }

  @Override
  public String getName() {
    return currentRenderer
      .getTitle()
      .orElse("Untitled Document");
  }

  @Override
  public URL getURL() {
    return this.url;
  }

  @Override
  public void navigate(URL url) {
    this.url = url;
    eventDispatcher.fire(listener -> listener.onURLChange(url));
    renderingEngine.openRenderer(url, this, renderer -> {
      this.currentRenderer = renderer;
      eventDispatcher.fire(listener -> listener.onRendererChange(renderer));
    });
  }
  
  @Override
  public boolean redirect(URL url) {
    this.url = url;
    eventDispatcher.fire(listener -> listener.onURLChange(url));
    
    return true;
  }

  @Override
  public void close() {
    // TODO: Implement
  }

  @Override
  public void reload() {
    // TODO: Implement
  }

  @Override
  public void back() {
    // TODO: Implement
  }

  @Override
  public void forward() {
    // TODO: Implement
  }

  @Override
  public void addEventListener(FrameEventListener listener, boolean sync) {
    eventDispatcher.addListener(listener);
    if (sync) {
      listener.onURLChange(url);
      listener.onRendererChange(currentRenderer);
    }
  }

}
