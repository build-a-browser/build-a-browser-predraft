package net.buildabrowser.babbrowser.renderer.imp.html;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;
import net.buildabrowser.babbrowser.html.events.EventLoop;
import net.buildabrowser.babbrowser.html.events.TaskSource;
import net.buildabrowser.babbrowser.html.html.RenderableDocument;
import net.buildabrowser.babbrowser.html.scripting.GlobalObject;
import net.buildabrowser.babbrowser.renderer.event.AbstractRendererDocumentChangeListener;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.util.FragmentUtil;

public class HTMLFragmentNavigationDocumentChangeListener
  extends AbstractRendererDocumentChangeListener {

  // TODO: Need to purge invalid entries
  // TODO: This probably is not very performant
  private final Map<String, WeakReference<BoxFragment<?>>> foundFragments = new HashMap<>();

  private boolean taskQueued = false;
  private String targetFragment;

  public HTMLFragmentNavigationDocumentChangeListener(
    DocumentChangeListener nextListener
  ) {
    super(nextListener);
  }

  @Override
  public void onBoxFragmentAdded(
    BoxFragment<?> fragment
  ) {
    Element fragmentElement = fragment.box().element();

    String id = fragmentElement.getAttribute("id");
    if (
      fragmentElement != null
      && id != null
    ) {
      foundFragments.put(id, new WeakReference<>(fragment));
    }


    if (
      fragmentElement != null
      && targetFragment != null
      && targetFragment.equals(id)
    ) {
      // TODO: This is hacky. Pre-render fragments aren't valid,
      // so hope the last fragment is the actual fragment
      queueTask(fragment, targetFragment, true);
    }

    super.onBoxFragmentAdded(fragment);
  }

  @Override
  public void onURLChanged(URI prevURL, URI newURL) {
    this.targetFragment = newURL.getFragment();
    WeakReference<BoxFragment<?>> foundFragmentRef = foundFragments.get(targetFragment);
    BoxFragment<?> foundFragment = foundFragmentRef == null ? null : foundFragmentRef.get();
    if (foundFragment != null) {
      queueTask(foundFragment, targetFragment, false);
    }

    super.onURLChanged(prevURL, newURL);
  }

  private void queueTask(BoxFragment<?> fragment, String queuedFragment, boolean waitForStable) {
    if (this.taskQueued) return;
    Element element = fragment.box().element();
    if (element == null) return;
    if (!(
      element.nodeDocument() instanceof RenderableDocument renderableDocument
    )) return;
    GlobalObject globalObject = renderableDocument.browsingContext().activeWindow();

    // TODO: This is not great, but it gives a moment for the document to become stable
    // Re-evaluate the need for this once render-blocking is supported
    if (waitForStable) {
      new Timer().schedule(new TimerTask() {
        @Override
        public void run() {
          queueTaskToEventLoop(queuedFragment, globalObject);
        }
      }, 120);
    } else {
      queueTaskToEventLoop(queuedFragment, globalObject);
    }
    this.taskQueued = true;
  }

  private void queueTaskToEventLoop(String queuedFragment, GlobalObject globalObject) {
    EventLoop.queueGlobalTask(
      TaskSource.NAVIGATION, globalObject,
      () -> {
        this.taskQueued = false;
        WeakReference<BoxFragment<?>> foundFragmentRef = foundFragments.get(queuedFragment);
        BoxFragment<?> foundFragment = foundFragmentRef == null ? null : foundFragmentRef.get();
        if (foundFragment == null) return;
        FragmentUtil.scrollIntoViewOnCurrentThread(foundFragment);
        if (queuedFragment.equals(targetFragment)) {
          this.targetFragment = null;
        }
    });
  }
  
}