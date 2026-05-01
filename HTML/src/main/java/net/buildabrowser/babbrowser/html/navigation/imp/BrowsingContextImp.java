package net.buildabrowser.babbrowser.html.navigation.imp;

import net.buildabrowser.babbrowser.html.events.EventLoop;
import net.buildabrowser.babbrowser.html.events.WindowEventLoop;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.navigation.BrowsingContext;
import net.buildabrowser.babbrowser.html.scripting.Realm;
import net.buildabrowser.babbrowser.html.scripting.RealmExecutionContext;
import net.buildabrowser.babbrowser.html.scripting.SimilarOriginWindowAgent;
import net.buildabrowser.babbrowser.html.scripting.Window;

public class BrowsingContextImp implements BrowsingContext {

  private Window activeWindow;
  private Realm realm;

  public BrowsingContextImp() {
    // TODO: A ton of spec steps
    SimilarOriginWindowAgent agent = obtainSimilarOriginWindowAgent();
    
    // I moved this above some of the other steps because I need a document to
    // create a window
    HTMLDocument document = HTMLDocument.create(this);
    // TODO: Proper way to obtain a realm.
    Window window = Window.create(() -> agent.eventLoop(), document);
    Realm realm = Realm.create(window);
    RealmExecutionContext realmExecutionContext = RealmExecutionContext.create(realm);
    Window.setupWindowEnvironmentSettingsObject(realmExecutionContext);
    // TODO: Proper way to make the document active
    this.activeWindow = window;

    // TODO: What is the proper way to pass off the realm to other code?
    this.realm = realm;
  }

  @Override
  public Window activeWindow() {
    return this.activeWindow;
  }

  @Override
  public HTMLDocument activeDocument() {
    return activeWindow.associatedDocument();
  }

  @Override
  public Realm realm() {
    return this.realm;
  }

  private SimilarOriginWindowAgent obtainSimilarOriginWindowAgent() {
    // TODO: Proper way to obtain
    WindowEventLoop eventLoop = EventLoop.createWindowEventLoop();
    return () -> eventLoop;
  }
  
}
