package net.buildabrowser.babbrowser.html.navigation;

import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.navigation.imp.BrowsingContextImp;
import net.buildabrowser.babbrowser.html.scripting.Realm;
import net.buildabrowser.babbrowser.html.scripting.Window;

public interface BrowsingContext {

  Window activeWindow();

  HTMLDocument activeDocument();

  Realm realm();

  public static BrowsingContext create() {
    return new BrowsingContextImp();
  }

}
