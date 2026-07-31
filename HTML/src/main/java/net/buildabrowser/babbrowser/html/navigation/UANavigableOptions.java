package net.buildabrowser.babbrowser.html.navigation;

import java.net.URI;
import java.util.concurrent.ExecutorService;

import net.buildabrowser.babbrowser.fetch.FetchEngine;
import net.buildabrowser.babbrowser.html.html.RenderableDocument;
import net.buildabrowser.babbrowser.html.navigation.DocumentRenderer.DocumentRendererEventListener;
import net.buildabrowser.babbrowser.html.ua.UAUIFeatures;

public interface UANavigableOptions {

  FetchEngine fetchEngine();

  ExecutorService createThreadGroup();

  RenderableDocument loadDocument(NavigationParams navigationParams);

  DocumentRendererEventListener eventListener();

  UAUIFeatures uiFeatures();

  void requestRepaint();

  void addRepaintListener(Runnable listener);

  void removeRepaintListener(Runnable listener);

  void onNavigate(URI url);

}
