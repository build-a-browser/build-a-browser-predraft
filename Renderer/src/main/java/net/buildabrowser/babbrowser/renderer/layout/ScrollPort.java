package net.buildabrowser.babbrowser.renderer.layout;

import net.buildabrowser.babbrowser.renderer.layout.stacking.StackingContextPosition;

public record ScrollPort(
  StackingContextPosition position,
  float width, float height
) {
  
}
