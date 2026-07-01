package net.buildabrowser.babbrowser.debugger.swing;

import net.buildabrowser.babbrowser.debugger.core.DebugContext;
import net.buildabrowser.babbrowser.debugger.core.Debugger;
import net.buildabrowser.babbrowser.debugger.core.FrameDebugger;
import net.buildabrowser.babbrowser.debugger.swing.ops.NodeTreeOps;
import net.buildabrowser.babbrowser.dom.Node;

public class SwingFrameDebugger implements FrameDebugger {

  private final LazyDiffTree<Node> nodeTree = LazyDiffTree.create(new NodeTreeOps());

  private final SwingDebugger relatedDebugger;

  public SwingFrameDebugger(
    SwingDebugger relatedDebugger
  ) {
    this.relatedDebugger = relatedDebugger;
  }

  @Override
  public Debugger relatedDebugger() {
    return this.relatedDebugger;
  }

  @Override
  public void update(DebugContext context) {
    nodeTree.update(context.rootNode());
  }

  @Override
  public void reset() {
    nodeTree.close(false);
  }

  @Override
  public void detach() {
    relatedDebugger.detach(this);
  }

  public LazyDiffTree<Node> nodeTree() {
    return this.nodeTree;
  }
  
}
