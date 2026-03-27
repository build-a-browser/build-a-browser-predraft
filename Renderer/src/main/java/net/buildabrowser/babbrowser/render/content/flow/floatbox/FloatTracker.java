package net.buildabrowser.babbrowser.render.content.flow.floatbox;

import java.util.List;
import java.util.function.Supplier;

import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.render.content.flow.BlockFormattingContext;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;

public interface FloatTracker {
  
  // Boolean return - if we have a non-0 reservedWidth, it could be hard to determine where the next line is
  // so just let the caller handle it for now
  boolean addLineStartFloat(BoxFragment box, LayoutConstraint lineConstraint, float reservedWidth);

  // Line constraint is a hacky way to determine where the box will start
  boolean addLineEndFloat(BoxFragment box, LayoutConstraint lineConstraint, float reservedWidth);

  float clearedLineStartPosition();

  float clearedLineEndPosition();

  // TODO: Optimize repeatedly getting the same value
  float lineStartPos();

  float lineEndPos(LayoutConstraint lineConstraint);

  void reset();

  List<LayoutFragment> allFloats();

  // Exists to min-bound the containing block
  float contentHeight();
  
  static FloatTracker createForFlow(Supplier<BlockFormattingContext> activeFormattingContext) {
    return new FloatTrackerImp(activeFormattingContext);
  }

}