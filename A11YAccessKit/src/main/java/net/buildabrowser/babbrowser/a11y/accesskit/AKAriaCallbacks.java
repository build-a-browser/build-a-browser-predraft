package net.buildabrowser.babbrowser.a11y.accesskit;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import net.buildabrowser.ak4j.AK4JHandle;
import net.buildabrowser.ak4j.AKRole;
import net.buildabrowser.babbrowser.a11y.core.AriaCallbacks;
import net.buildabrowser.babbrowser.a11y.core.aom.AriaEvent;
import net.buildabrowser.babbrowser.a11y.core.aom.AriaProperty;
import net.buildabrowser.babbrowser.a11y.core.aom.AriaRole;

public class AKAriaCallbacks implements AriaCallbacks<MemorySegment> {

  private final AK4JHandle ak4jHandle;
  private final MemorySegment treeUpdate;
  private final Arena arena;

  public AKAriaCallbacks(
    AK4JHandle ak4jHandle,
    MemorySegment treeUpdate,
    Arena arena
  ) {
    this.ak4jHandle = ak4jHandle;
    this.treeUpdate = treeUpdate;
    this.arena = arena;
  }

  @Override
  public MemorySegment visitNode(MemorySegment parent, long nodeId, AriaRole role) {
    AKRole mappedRole = AKRoleMapper.map(role);
    ak4jHandle.nodes().pushChild(parent, nodeId);
    return ak4jHandle.nodes().create(mappedRole, arena);
  }

  @Override
  public void exitNode(MemorySegment node, long nodeId) {
    ak4jHandle.pushTreeUpdateNode(treeUpdate, nodeId, node);
  }

  @Override
  public void visitAttribute(MemorySegment node, AriaProperty property, String value) {
    // TODO
  }

  @Override
  public void visitText(MemorySegment node, String value) {
    ak4jHandle.nodes().setValue(node, value, arena);
  }

  @Override
  public void onNodeEvent(long nodeId, AriaEvent event) {}

  @Override
  public void onFocusChanged(long focusId) {
    
  }
  
}
