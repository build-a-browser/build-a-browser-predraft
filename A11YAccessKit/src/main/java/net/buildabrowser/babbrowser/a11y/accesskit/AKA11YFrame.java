package net.buildabrowser.babbrowser.a11y.accesskit;

import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.concurrent.atomic.AtomicReference;

import net.buildabrowser.ak4j.AK4J;
import net.buildabrowser.ak4j.AK4JHandle;
import net.buildabrowser.ak4j.AKCallbacks;
import net.buildabrowser.ak4j.AKRole;
import net.buildabrowser.babbrowser.a11y.core.A11YFrame;
import net.buildabrowser.babbrowser.a11y.core.A11YOps;
import net.buildabrowser.babbrowser.a11y.core.AriaCallbacks;
import net.buildabrowser.babbrowser.a11y.core.html.HTMLAriaTraversal;
import net.buildabrowser.babbrowser.dom.Node;

public class AKA11YFrame implements A11YFrame, AKCallbacks {

  private final AtomicReference<MemorySegment> queuedUpdate = new AtomicReference<>();

  private final AK4JHandle ak4jHandle;
  private final A11YOps ops;

  private volatile boolean activated;

  public AKA11YFrame(A11YOps ops) throws IOException {
    this.ak4jHandle = AK4J.init(this);
    this.ops = ops;
  }

  public MemorySegment onActivation(AK4JHandle ak4jHandle) {
    // Unfortunately this is not running on the event loop thread, and sync'ing it
    // freezes the OS A11Y, so there's no choice but to return a dummy update
    this.activated = true;

    // TODO: Need to ask renderer to push an initial update

    return createFakeUpdate(ak4jHandle);
  }

  public void onAction(AK4JHandle ak4jHandle) {}

  public void onDeactivation(AK4JHandle ak4jHandle) {
    this.activated = false;
    disposeQueuedUpdate();
  }

  @Override
  public void update(Node node) {
    if (!activated) return;
    
    Arena scope = Arena.ofAuto();
    MemorySegment tree = ak4jHandle.createTree(0, scope);
    // TODO: Determine capacity
    MemorySegment rootNode = ak4jHandle.nodes().create(AKRole.WINDOW, scope);
    MemorySegment update = ak4jHandle.createTreeUpdate(tree, 128, 0, scope);
    AriaCallbacks<MemorySegment> callbacks = new AKAriaCallbacks(
      ak4jHandle, update, scope);
    HTMLAriaTraversal.traverse(rootNode, node, callbacks, ops);
    ak4jHandle.pushTreeUpdateNode(update, 0, rootNode);
    MemorySegment oldUpdate = queuedUpdate.getAndSet(update);
    disposeQueuedUpdate(oldUpdate);

    ak4jHandle.adapter().update(() -> {
      MemorySegment currentUpdate = queuedUpdate.getAndSet(null);
      if (currentUpdate == null) return createFakeUpdate(ak4jHandle);

      // AccessKit will dispose for us
      return currentUpdate;
    });
  }

  @Override
  public void close() throws IOException {
    disposeQueuedUpdate();
    // TODO: Even though queuedUpdate typically does not have a value,
    // this seems to somehow be leeking memory
    try {
      ak4jHandle.close();
    } catch (Exception e) {
      if (e instanceof IOException ioException) {
        throw ioException;
      }
      throw new IOException(e);
    }
  }

  private MemorySegment createFakeUpdate(AK4JHandle ak4jHandle) {
    Arena scope = Arena.ofAuto();
    MemorySegment tree = ak4jHandle.createTree(0, scope);
    MemorySegment update = ak4jHandle.createTreeUpdate(tree, 1, 0, scope);
    
    MemorySegment rootNode = ak4jHandle.nodes().create(AKRole.WINDOW, scope);
    ak4jHandle.pushTreeUpdateNode(update, 0, rootNode);
    return update;
  }

  private final void disposeQueuedUpdate() {
    // TODO
  }

  private final void disposeQueuedUpdate(MemorySegment update) {
    // TODO
  }
  
}
