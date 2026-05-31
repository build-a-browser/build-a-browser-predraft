package net.buildabrowser.babbrowser.html.navigation;

import java.net.URI;
import java.util.concurrent.ExecutorService;

import net.buildabrowser.babbrowser.cssbase.cssom.StyleSheetList;
import net.buildabrowser.babbrowser.fetch.FetchEngine;
import net.buildabrowser.babbrowser.html.html.RenderableDocument;
import net.buildabrowser.babbrowser.html.navigation.DocumentRenderer.DocumentRendererEventListener;

public interface UANavigableOptions {

  FetchEngine fetchEngine();

  ExecutorService createThreadGroup();

  RenderableDocument loadDocument(NavigationParams navigationParams);

  StyleSheetList uaStyleSheets();

  DocumentRendererEventListener eventListener();

  void requestRepaint();

  void addRepaintListener(Runnable listener);

  void onNavigate(URI url);

}
