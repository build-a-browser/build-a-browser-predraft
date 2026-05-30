package net.buildabrowser.babbrowser.render.uistate.imp;

import java.net.URI;

import net.buildabrowser.babbrowser.html.events.WindowEventLoop;
import net.buildabrowser.babbrowser.html.navigation.DocumentRenderer.DocumentRendererEventListener;
import net.buildabrowser.babbrowser.html.navigation.Navigable;
import net.buildabrowser.babbrowser.html.navigation.NavigateParameters;
import net.buildabrowser.babbrowser.html.navigation.UserNavigationInvolvement;
import net.buildabrowser.babbrowser.html.scripting.Window;
import net.buildabrowser.babbrowser.render.GraphicalDocumentRenderer;
import net.buildabrowser.babbrowser.render.RenderingEngine;
import net.buildabrowser.babbrowser.render.RenderingEngine.NavigableRendererPair;
import net.buildabrowser.babbrowser.render.uistate.Frame;
import net.buildabrowser.babbrowser.render.uistate.event.BrowserEventDispatcher;
import net.buildabrowser.babbrowser.render.uistate.event.FrameEventListener;

public class FrameImp implements Frame {

  private final BrowserEventDispatcher<FrameEventListener> eventDispatcher = BrowserEventDispatcher.create();

  private final Navigable navigable;
  private final GraphicalDocumentRenderer renderer;

  public FrameImp(RenderingEngine renderingEngine) {
    NavigableRendererPair navigableRendererPair = renderingEngine.createNavigable(
      new DocumentRendererEventListener() {

        @Override
        public void onNavigate(URI url) {
          eventDispatcher.fire(l -> l.onURLChange(url));
          eventDispatcher.fire(listener -> listener.onTitleChange(getTitle()));
        }

        @Override
        public void onTitleChanged(String title) {
          eventDispatcher.fire(listener -> listener.onTitleChange(getTitle()));
        }
        
      });
      
    this.navigable = navigableRendererPair.navigable();
    this.renderer = navigableRendererPair.renderer();
  }

  @Override
  public GraphicalDocumentRenderer getRenderer() {
    return this.renderer;
  }

  @Override
  public String getTitle() {
    return renderer
      .title()
      .orElse(navigable.activeDocument().url().toString());
  }

  @Override
  public URI getURL() {
    return navigable.activeDocument().url();
  }

  @Override
  public void navigate(URI url) {
    NavigateParameters parameters = new NavigateParameters();
    parameters.userInvolvement = UserNavigationInvolvement.BROWSER_UI;
    parameters.sourceDocument = navigable.activeDocument();
    // TODO: I think the above should be null, but fetch currently crashes if no client is present.
    navigable.navigate(url, parameters);
  }

  @Override
  public void close() {
    renderer.close();

    Window window = navigable.activeDocument().browsingContext().activeWindow();
    WindowEventLoop eventLoop = window.agent().eventLoop();
    eventLoop.shutdown();
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
      listener.onTitleChange(navigable.activeDocument().title());
      listener.onURLChange(navigable.activeDocument().url());
    }
  }

}
