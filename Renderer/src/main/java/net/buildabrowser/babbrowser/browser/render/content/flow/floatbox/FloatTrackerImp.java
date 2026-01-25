package net.buildabrowser.babbrowser.browser.render.content.flow.floatbox;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.buildabrowser.babbrowser.browser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;

// TODO: Needs to account for margins
public class FloatTrackerImp implements FloatTracker {

  private static final Comparator<BoxFragment> fragmentComparator = (r1, r2) -> {
    int result = Float.compare(r1.marginY(), r2.marginY());
    if (result == 0) {
      result = Float.compare(r1.marginX(), r2.marginX());
    }

    return result;
  };

  private final Set<BoxFragment> leftFloats = new TreeSet<BoxFragment>(fragmentComparator);
  private final Set<BoxFragment> rightFloats = new TreeSet<BoxFragment>(fragmentComparator);
  private final List<BoxFragment> allFloats = new LinkedList<>();

  private int lineStartOffset = 0;
  private int blockStartOffset = 0;
  private int blockEnd = 0;

  @Override
  public boolean addLineStartFloat(BoxFragment box, LayoutConstraint lineConstraint, int reservedWidth) {
    // TODO: Find a proper way to handle pre-layout constraints
    if (lineConstraint.isPreLayoutConstraint()) return true;

    int[] freeInfo = new int[2];
    int freePos = findFreePos(lineConstraint, box.marginWidth() + reservedWidth, freeInfo);
    if (reservedWidth != 0 && freePos != blockStartOffset) return false;

    // Since the box is placed by border pos, we need to convert our margin pos to border pos
    int[] margin = box.box().dimensions().getComputedMargin();
    box.setPos(Math.max(freeInfo[0] + margin[2], lineStartOffset), freePos + margin[0]);

    leftFloats.add(box);
    allFloats.add(box);

    this.blockEnd = Math.max(this.blockEnd, freePos + box.marginHeight());

    return true;
  }

  @Override
  public boolean addLineEndFloat(BoxFragment box, LayoutConstraint lineConstraint, int reservedWidth) {
    if (lineConstraint.isPreLayoutConstraint()) return true;

    int[] freeInfo = new int[2];
    int freePos = findFreePos(lineConstraint, box.marginWidth() + reservedWidth, freeInfo);
    if (reservedWidth != 0 && freePos != blockStartOffset) return false;

    int maxEdgePos = lineStartOffset + lineConstraint.value();
    int maxTouchingPos = freeInfo[1];
    int boxStartPos = Math.min(maxEdgePos, maxTouchingPos) - box.marginWidth();

    int[] margin = box.box().dimensions().getComputedMargin();
    box.setPos(boxStartPos + margin[2], freePos + margin[0]);

    rightFloats.add(box);
    allFloats.add(box);

    this.blockEnd = Math.max(this.blockEnd, freePos + box.marginHeight());

    return true;
  }

  @Override
  public int clearedLineStartPosition() {
    return Math.max(getFreePosition(blockStartOffset, leftFloats) - blockStartOffset, 0);
  }

  @Override
  public int clearedLineEndPosition() {
    return Math.max(getFreePosition(blockStartOffset, rightFloats) - blockStartOffset, 0);
  }

  @Override
  public int lineStartPos() {
    int highestOffset = 0;
    for (BoxFragment box : leftFloats) {
      if (blockStartOffset >= box.marginY() && blockStartOffset < box.marginY() + box.marginHeight()) {
        highestOffset = Math.max(highestOffset, box.marginX() + box.marginWidth());
      }
    }

    return Math.max(0, highestOffset - lineStartOffset);
  }

  @Override
  public int lineEndPos(LayoutConstraint lineConstraint) {
    if (lineConstraint.isPreLayoutConstraint()) {
      throw new UnsupportedOperationException("Can not determine line-end during pre-layout!");
    }

    int highestOffset = Integer.MAX_VALUE;
    for (BoxFragment box : rightFloats) {
      if (blockStartOffset >= box.marginY() && blockStartOffset < box.marginY() + box.marginHeight()) {
        highestOffset = Math.min(highestOffset, box.marginX());
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
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public List<LayoutFragment> allFloats() {
    return (List<LayoutFragment>) (List) this.allFloats;
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

    Iterator<BoxFragment> leftFragIt;
    Iterator<BoxFragment> rightFragIt;
    do {
      // TODO: This is very unoptimal, potentially squared, but imagine the case in which a tall float comes before
      // a short float and the [blockStart, blockEnd] of the shorter float is completely contained within the
      // [blockStart, blockEnd) of the taller float. If we only advance the iterator, we can forget that the tall float is
      // still active when going to the next line. Therefore, we restart the iterator and go back to the new initial search point.
      // It'd help if the iterator could go in reverse... unfortunately there is no previous method
      leftFragIt = leftFloats.iterator();
      rightFragIt = rightFloats.iterator();
      nextSearchBlockPos[0] = Integer.MAX_VALUE;
      int leftOffset = lastValidInlinePos(leftFragIt, currentSearchBlockPos, lineStartOffset, nextSearchBlockPos);
      int rightOffset = lastValidInlinePos(rightFragIt, currentSearchBlockPos, lineStartOffset + lineConstraint.value(), nextSearchBlockPos);
      if (
        rightOffset - leftOffset >= minWidth
        || (leftOffset <= 0 && rightOffset >= lineConstraint.value())
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

  private int lastValidInlinePos(Iterator<BoxFragment> fragIt, int blockPos, int initInlinePos, int[] outNextBlockPos) {
    boolean isLeftSide = initInlinePos == lineStartOffset;
    BoxFragment currentFragment = fragIt.hasNext() ? fragIt.next() : null;
    int inlinePos = initInlinePos;
    while (currentFragment != null && currentFragment.marginY() <= blockPos) {
      int fragmentEnd = currentFragment.marginY() + currentFragment.marginHeight();
      if (fragmentEnd <= blockPos) {
        currentFragment = fragIt.hasNext() ? fragIt.next() : null;
        continue;
      }

      outNextBlockPos[0] = Math.min(outNextBlockPos[0], Math.max(fragmentEnd, blockPos + 1));

      inlinePos = isLeftSide ?
        currentFragment.marginX() + currentFragment.marginWidth() :
        Math.min(inlinePos, currentFragment.marginX());
      
      currentFragment = fragIt.hasNext() ? fragIt.next() : null;
    }

    return inlinePos;
  }

  private int getFreePosition(int blockStart, Set<BoxFragment> floats) {
    int nextUncheckedY = -1;
    for (BoxFragment box : floats) {
      if (nextUncheckedY >= blockStart && box.marginY() > nextUncheckedY) {
        return nextUncheckedY;
      } else {
        nextUncheckedY = Math.max(nextUncheckedY, box.marginY() + box.marginHeight());
      }
    }

    return Math.max(nextUncheckedY, blockStart);
  }

}
