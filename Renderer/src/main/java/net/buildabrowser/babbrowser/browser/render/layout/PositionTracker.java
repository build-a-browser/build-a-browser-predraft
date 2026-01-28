package net.buildabrowser.babbrowser.browser.render.layout;

import net.buildabrowser.babbrowser.browser.render.layout.imp.PositionTrackerImp;

// Annoying interface because Floats and Stacking Contexts both
// want the coordinates of one thing relative to another.
// There's probably a better way...
public interface PositionTracker {
  
  void adjustPos(int x, int y);

  long mark();

  void restoreMark(long mark);

  int posX();

  int posY();

  void reset();

  static PositionTracker create() {
    return new PositionTrackerImp();
  }

}