package net.buildabrowser.babbrowser.renderer.content.table.imp.border;

import java.util.Set;

import net.buildabrowser.babbrowser.renderer.content.table.TableComputedBorders;
import net.buildabrowser.babbrowser.renderer.content.table.imp.border.TableCollapsedBorderAssigner.SlotComputedBorder;

public record TableBorderAssignment(
  TableComputedBorders[][] slotGrid,
  Set<SlotComputedBorder> borderOrder
) {
  
}
