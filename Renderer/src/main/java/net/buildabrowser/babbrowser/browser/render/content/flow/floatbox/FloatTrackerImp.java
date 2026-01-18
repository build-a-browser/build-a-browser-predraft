package net.buildabrowser.babbrowser.browser.render.content.flow.floatbox;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.FlowFragment;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;

public class FloatTrackerImp implements FloatTracker {

  private static final Comparator<FlowFragment> fragmentComparator = (r1, r2) -> {
    int result = Float.compare(r1.posY(), r2.posY());
    if (result == 0) {
      result = Float.compare(r1.posX(), r2.posX());
    }

    return result;
  };

  private final Set<FlowFragment> leftFloats = new TreeSet<FlowFragment>(fragmentComparator);
  private final Set<FlowFragment> rightFloats = new TreeSet<FlowFragment>(fragmentComparator);
  private final List<FlowFragment> allFloats = new LinkedList<>();

  private int lineStartOffset = 0;
  private int blockStartOffset = 0;
  private int blockEnd = 0;

  @Override
  public boolean addLineStartFloat(FlowFragment box, LayoutConstraint lineConstraint, int reservedWidth) {
    // TODO: Find a proper way to handle pre-layout constraints
    if (lineConstraint.isPreLayoutConstraint()) return true;

    int[] freeInfo = new int[2];
    int freePos = findFreePos(lineConstraint, box.width() + reservedWidth, freeInfo);
    if (reservedWidth != 0 && freePos != blockStartOffset) return false;

    box.setPos(Math.max(freeInfo[0], lineStartOffset), freePos);
    leftFloats.add(box);
    allFloats.add(box);

    this.blockEnd = Math.max(this.blockEnd, freePos + box.height());

    return true;
  }

  @Override
  public boolean addLineEndFloat(FlowFragment box, LayoutConstraint lineConstraint, int reservedWidth) {
    if (lineConstraint.isPreLayoutConstraint()) return true;

    int[] freeInfo = new int[2];
    int freePos = findFreePos(lineConstraint, box.width() + reservedWidth, freeInfo);
    if (reservedWidth != 0 && freePos != blockStartOffset) return false;

    int maxEdgePos = lineStartOffset + lineConstraint.value();
    int maxTouchingPos = freeInfo[1];
    int boxStartPos = Math.min(maxEdgePos, maxTouchingPos) - box.width();
    box.setPos(boxStartPos, freePos);

    rightFloats.add(box);
    allFloats.add(box);

    this.blockEnd = Math.max(this.blockEnd, freePos + box.height());

    return true;
  }

  @Override
  public int clearedLineStartPosition() {
    return Math.max(getFreePosition(blockStartOffset, leftFloats) - lineStartOffset, 0);
  }

  @Override
  public int clearedLineEndPosition() {
    return getFreePosition(blockStartOffset, rightFloats);
  }

  @Override
  public int lineStartPos() {
    int highestOffset = 0;
    for (FlowFragment box : leftFloats) {
      if (blockStartOffset >= box.posY() && blockStartOffset < box.posY() + box.height()) {
        highestOffset = Math.max(highestOffset, box.posX() + box.width());
      }
    }

    return highestOffset - lineStartOffset;
  }

  @Override
  public int lineEndPos(LayoutConstraint lineConstraint) {
    if (lineConstraint.isPreLayoutConstraint()) {
      throw new UnsupportedOperationException("Can not determine line-end during pre-layout!");
    }

    int highestOffset = Integer.MAX_VALUE;
    for (FlowFragment box : rightFloats) {
      if (blockStartOffset >= box.posY() && blockStartOffset < box.posY() + box.height()) {
        highestOffset = Math.min(highestOffset, box.posX());
      }
    }

    return Math.max(0, Math.min(lineStartOffset + lineConstraint.value(), highestOffset - lineStartOffset));
  }

  @Override
  public void reset() {
    this.lineStartOffset = 0;
    this.blockStartOffset = 0;
    this.blockEnd = 0;
    leftFloats.clear();
    rightFloats.clear();
    allFloats.clear();
  }

  @Override
  public List<FlowFragment> allFloats() {
    return this.allFloats;
  }

  @Override
  public void adjustPos(int x, int y) {
    this.lineStartOffset += x;
    this.blockStartOffset += y;
  }

  @Override
  public int mark() {
    return this.blockStartOffset;
  }

  @Override
  public void restoreMark(int mark) {
    this.blockStartOffset = mark;
  }

  @Override
  public int contentHeight() {
    return this.blockEnd;
  }

  private int findFreePos(LayoutConstraint lineConstraint, int minWidth, int[] outParams) {
    if (lineConstraint.isPreLayoutConstraint()) {
      throw new UnsupportedOperationException("Can not determine line-end during pre-layout!");
    }

    int currentSearchBlockPos = blockStartOffset;
    int[] nextSearchBlockPos = new int[] { 0 };

    Iterator<FlowFragment> leftFragIt;
    Iterator<FlowFragment> rightFragIt;
    do {
      // TODO: This is very unoptimal, potentially squared, but imagine the case in which a tall float comes before
      // a short float and the [blockStart, blockEnd] of the shorter float is completely contained within the
      // [blockStart, blockEnd) of the taller float. If we only advance the iterator, we can forget that the tall float is
      // still active when going to the next line. Therefore, we restart the iterator and go back to the new initial search point.
      // It'd help if the iterator could go in reverse... unfortunately there is no previous method
      leftFragIt = leftFloats.iterator();
      rightFragIt = rightFloats.iterator();
      nextSearchBlockPos[0] = Integer.MAX_VALUE;
      int leftOffset = lastValidInlinePos(leftFragIt, currentSearchBlockPos, 0, nextSearchBlockPos);
      int rightOffset = lastValidInlinePos(rightFragIt, currentSearchBlockPos, lineConstraint.value(), nextSearchBlockPos);
      if (
        rightOffset - leftOffset > minWidth
        || (leftOffset == 0 && rightOffset == lineConstraint.value())
      ) {
        outParams[0] = leftOffset;
        outParams[1] = rightOffset;
        return currentSearchBlockPos;
      }
      currentSearchBlockPos = nextSearchBlockPos[0] == Integer.MAX_VALUE ?
        currentSearchBlockPos + 1 :
        nextSearchBlockPos[0];
    } while (nextSearchBlockPos[0] != Integer.MAX_VALUE);

    outParams[0] = 0;
    outParams[1] = lineConstraint.value();
    return currentSearchBlockPos;
  }

  private int lastValidInlinePos(Iterator<FlowFragment> fragIt, int blockPos, int initInlinePos, int[] outNextBlockPos) {
    boolean isLeftSide = initInlinePos == 0;
    FlowFragment currentFragment = fragIt.hasNext() ? fragIt.next() : null;
    int inlinePos = initInlinePos;
    while (currentFragment != null && currentFragment.posY() <= blockPos) {
      int fragmentEnd = currentFragment.posY() + currentFragment.height();
      if (fragmentEnd <= blockPos) {
        currentFragment = fragIt.hasNext() ? fragIt.next() : null;
        continue;
      }

      outNextBlockPos[0] = Math.min(outNextBlockPos[0], Math.max(fragmentEnd, blockPos + 1));

      inlinePos = isLeftSide ?
        currentFragment.posX() + currentFragment.width() :
        Math.min(inlinePos, currentFragment.posX());
      
      currentFragment = fragIt.hasNext() ? fragIt.next() : null;
    }

    return inlinePos;
  }

  private int getFreePosition(int blockStart, Set<FlowFragment> floats) {
    int nextUncheckedY = -1;
    for (FlowFragment box : floats) {
      if (nextUncheckedY >= blockStart && box.posY() > nextUncheckedY) {
        return nextUncheckedY;
      } else {
        nextUncheckedY = Math.max(nextUncheckedY, box.posY() + box.height());
      }
    }

    return Math.max(nextUncheckedY, blockStart);
  }

}
