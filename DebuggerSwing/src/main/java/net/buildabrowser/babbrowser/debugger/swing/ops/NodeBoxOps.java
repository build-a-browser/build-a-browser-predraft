package net.buildabrowser.babbrowser.debugger.swing.ops;

import java.util.List;

import net.buildabrowser.babbrowser.debugger.core.DebugBox;
import net.buildabrowser.babbrowser.debugger.core.DebugBox.DebugBoxType;
import net.buildabrowser.babbrowser.debugger.swing.LazyDiffTree.TreeOps;

public class NodeBoxOps implements TreeOps<DebugBox> {

  private final NodeTreeOps treeOps;

  public NodeBoxOps(NodeTreeOps treeOps) {
    this.treeOps = treeOps;
  }

  @Override
  public String name(DebugBox debugBox) {
    String boxType = switch (debugBox.debugBoxType()) {
      case ELEMENT -> "ElementBox";
      case TEXT -> "TextBox";
      case DOCUMENT -> "DocumentBox";
      default -> "UnknownBox";
    };

    if (debugBox.relatedNode() == null) {
      return boxType;
    } else {
      return String.format(
        "%s (%s)", boxType, treeOps.name(debugBox.relatedNode()));
    }
  }

  @Override
  public List<DebugBox> children(DebugBox debugBox) {
    return debugBox.childDebugBoxes();
  }

  @Override
  public boolean isNodeIgnored(DebugBox debugBox) {
    if (debugBox.relatedNode() == null) return false;
    return treeOps.isNodeIgnored(debugBox.relatedNode());
  }

  @Override
  public boolean isNodeLeaf(DebugBox debugBox) {
    return debugBox.debugBoxType().equals(DebugBoxType.TEXT);
  }
  
}
