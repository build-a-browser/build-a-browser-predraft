package net.buildabrowser.babbrowser.html.events.imp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.html.events.EventLoop;
import net.buildabrowser.babbrowser.html.events.TaskSource;
import net.buildabrowser.babbrowser.html.events.WindowEventLoop;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.navigation.DocumentRenderer;
import net.buildabrowser.babbrowser.html.navigation.Navigable;
import net.buildabrowser.babbrowser.html.scripting.GlobalObject;

public class WindowEventLoopImp extends EventLoopImp implements WindowEventLoop {

  private final Set<Navigable> relatedNavigables = new HashSet<>();
  private final AtomicBoolean isLooping = new AtomicBoolean(false);
  private final AtomicBoolean queuedGlobalTask = new AtomicBoolean(false);
  
  // TODO: The spec defines an algorithm involving realms and a loop
  // but we do not have realms yet
  @Override
  public void addNavigable(Navigable navigable) {
    // Hopefully called in the same thread as the main event loop
    // Avoiding synchronizing
    relatedNavigables.add(navigable);
    if (hasStarted.get() && !isLooping.get() && !isClosing.get()) {
      assert navigable.activeWindow().agent().eventLoop() == this;
      isLooping.set(true);
      runInParallel(() -> updateRenderingLoop(navigable.activeWindow()));
    }
  }

  @Override
  public void start() {
    if (!relatedNavigables.isEmpty()) {
      isLooping.set(true);
      runInParallel(() -> updateRenderingLoop(
        relatedNavigables.iterator().next().activeWindow()));
    }
    super.start();
  }

  private void updateRenderingLoop(GlobalObject globalObject) {
    while (isLooping.get() && !isClosing.get()) {
      long taskStart = System.currentTimeMillis();
      if (!queuedGlobalTask.get()) {
        queuedGlobalTask.set(true);
        EventLoop.queueGlobalTask(
          TaskSource.RENDERING,
          globalObject,
          this::updateRendering);
      }

      long timeElapsed = System.currentTimeMillis() - taskStart;
      try {
        // TODO: Vsync
        Thread.sleep(Math.max(0, 16 - timeElapsed));
      } catch (InterruptedException e) {}
    }
  }

  private List<Document> docs = new ArrayList<>();
  private void updateRendering() {
    if (relatedNavigables.isEmpty()) {
      isLooping.set(false);
      return;
    }

    // Start of spec (other portions of this code are for scheduling)
    // TODO: Timing
    // TODO: Proper document ordering
    docs.clear();
    // I did this additively, the spec subtracts, but this is simpler
    for (Navigable navigable: relatedNavigables) {
      Document doc = navigable.activeDocument();
      // TODO: A number of conditions
      DocumentRenderer renderer = ((HTMLDocument) doc).renderer();
      if (!renderer.shouldRender()) continue;
      docs.add(doc);
    }
    
    // TODO: A ton of other steps
    for (Document doc: docs) {
      DocumentRenderer renderer = ((HTMLDocument) doc).renderer();
      renderer.recalculateStyles();
      renderer.updateLayout();
      // TODO: and resize observers
    }
    
    // TODO: and a few steps in between...
    for (Document doc: docs) {
      DocumentRenderer renderer = ((HTMLDocument) doc).renderer();
      renderer.updateRendering();
    }
    // TODO: and the rest...
    // End of spec

    queuedGlobalTask.set(false);
  }

}
