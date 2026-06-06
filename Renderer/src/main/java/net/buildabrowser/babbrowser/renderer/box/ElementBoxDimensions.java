package net.buildabrowser.babbrowser.renderer.box;

import net.buildabrowser.babbrowser.renderer.box.imp.ElementBoxDimensionsImp;

public interface ElementBoxDimensions {

  float[] getComputedBorder();

  float[] getComputedPadding();

  float[] getComputedMargin();

  float staticX();

  float staticY();
  
  float intrinsicWidth();

  float intrinsicHeight();

  float intrinsicRatio();

  float decorWidth();

  float decorHeight();

  static MutableElementBoxDimensions create() {
    return new ElementBoxDimensionsImp();
  }
  
}
