package net.buildabrowser.babbrowser.render.content.table;

import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;

public interface TableCell {
    
  int cellX();
  
  int cellY();

  int width();

  int height();

  ElementBox cellBox();

  TableComputedBorders borders();

  // TODO: Having to have a second copy is not great
  TableComputedBorders preservedBorders();

  void setRelatedFragment(UnmanagedBoxFragment fragment);

  UnmanagedBoxFragment getRelatedFragment();

  float minContentContribution(int colNum);

  float maxContentContribution(int colNum);

  float outerMinContentWidth();

  float outerMaxContentWidth();

  // TODO: Associated header cells

}