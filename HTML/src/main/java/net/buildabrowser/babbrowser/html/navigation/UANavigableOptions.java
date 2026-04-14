package net.buildabrowser.babbrowser.html.navigation;

import net.buildabrowser.babbrowser.cssbase.cssom.StyleSheetList;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.fetch.FetchEngine;

public interface UANavigableOptions {

  FetchEngine fetchEngine();

  Document loadDocument(NavigationParams navigationParams);

  StyleSheetList uaStyleSheets();

  void requestRepaint();

  void addRepaintListener(Runnable listener);

}
