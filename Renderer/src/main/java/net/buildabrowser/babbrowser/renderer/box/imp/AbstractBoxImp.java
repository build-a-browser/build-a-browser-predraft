package net.buildabrowser.babbrowser.renderer.box.imp;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.debugger.core.DebugObject.DebugObjectSelection;
import net.buildabrowser.babbrowser.renderer.box.Box;

public abstract class AbstractBoxImp implements IntrusiveList<Box> {

  private static final short SELECTION_SELECTED_FLAG = 0x1;
  private static final short SELECTION_TARGETED_FLAG = 0x2;

  private Box nextBox;
  private short flags;

  public Box next() {
    return this.nextBox;
  }

  public void setNext(Box nextNode) {
    this.nextBox = nextNode;
    assert IntrusiveList._ensureNoLoops(this);
  }
 
  // TODO: Need to edit PaintUtil to draw the selectio
  public void markSelection(DebugObjectSelection selection) {
    flags &= 0xFC;
    if (selection.equals(DebugObjectSelection.SELECTED)) {
      flags |= SELECTION_SELECTED_FLAG;
    } else if (selection.equals(DebugObjectSelection.TARGETED)) {
      flags |= SELECTION_TARGETED_FLAG;
    } 
  }

  public DebugObjectSelection debugSelection() {
    if ((flags & SELECTION_SELECTED_FLAG) != 0) {
      return DebugObjectSelection.SELECTED;
    } else if ((flags & SELECTION_TARGETED_FLAG) != 0) {
      return DebugObjectSelection.TARGETED;
    } else {
      return DebugObjectSelection.NONE;
    }
  }
  
}
