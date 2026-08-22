package net.buildabrowser.babbrowser.debugger.swing;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.debugger.core.DebugBox;
import net.buildabrowser.babbrowser.debugger.core.DebugContext;
import net.buildabrowser.babbrowser.debugger.core.Debugger;
import net.buildabrowser.babbrowser.debugger.core.FrameDebugger;
import net.buildabrowser.babbrowser.debugger.swing.ops.NodeBoxOps;
import net.buildabrowser.babbrowser.debugger.swing.ops.NodeTreeOps;
import net.buildabrowser.babbrowser.dom.Node;

public class SwingFrameDebugger implements FrameDebugger {

  private final NodeTreeOps nodeTreeOps = new NodeTreeOps();
  private final NodeBoxOps nodeBoxOps = new NodeBoxOps(nodeTreeOps);
  private final LazyDiffTree<Node> nodeTree = LazyDiffTree.create(nodeTreeOps);
  private final LazyDiffTree<DebugBox> boxTree = LazyDiffTree.create(nodeBoxOps);
  private final List<Consumer<DebugContext>> queuedTasks = new ArrayList<>();

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
    boxTree.update(context.rootBox());

    for (Consumer<DebugContext> task: queuedTasks) {
      task.accept(context);
    }
    queuedTasks.clear();
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

  public LazyDiffTree<DebugBox> boxTree() {
    return this.boxTree;
  }

  public void later(Consumer<DebugContext> task) {
    queuedTasks.add(task);
  }
  
}
