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
import net.buildabrowser.babbrowser.browser.render.layout.PositionTracker;

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

  private final PositionTracker positionTracker = PositionTracker.create();

  private float blockEnd = 0;

  @Override
  public boolean addLineStartFloat(BoxFragment box, LayoutConstraint lineConstraint, float reservedWidth) {
    // TODO: Find a proper way to handle pre-layout constraints
    if (lineConstraint.isPreLayoutConstraint()) return true;

    float[] freeInfo = new float[2];
    float freePos = findFreePos(lineConstraint, box.marginWidth() + reservedWidth, freeInfo);
    if (reservedWidth != 0 && freePos != positionTracker.posY()) return false;

    // Since the box is placed by border pos, we need to convert our margin pos to border pos
    float[] margin = box.box().dimensions().getComputedMargin();
    box.setPos(Math.max(freeInfo[0] + margin[2], positionTracker.posX()), freePos + margin[0]);

    leftFloats.add(box);
    allFloats.add(box);

    this.blockEnd = Math.max(this.blockEnd, freePos + box.marginHeight());

    return true;
  }

  @Override
  public boolean addLineEndFloat(BoxFragment box, LayoutConstraint lineConstraint, float reservedWidth) {
    if (lineConstraint.isPreLayoutConstraint()) return true;

    float[] freeInfo = new float[2];
    float freePos = findFreePos(lineConstraint, box.marginWidth() + reservedWidth, freeInfo);
    if (reservedWidth != 0 && freePos != positionTracker.posY()) return false;

    float maxEdgePos = positionTracker.posX() + lineConstraint.value();
    float maxTouchingPos = freeInfo[1];
    float boxStartPos = Math.min(maxEdgePos, maxTouchingPos) - box.marginWidth();

    float[] margin = box.box().dimensions().getComputedMargin();
    box.setPos(boxStartPos + margin[2], freePos + margin[0]);

    rightFloats.add(box);
    allFloats.add(box);

    this.blockEnd = Math.max(this.blockEnd, freePos + box.marginHeight());

    return true;
  }

  @Override
  public float clearedLineStartPosition() {
    return Math.max(getFreePosition(positionTracker.posY(), leftFloats) - positionTracker.posY(), 0);
  }

  @Override
  public float clearedLineEndPosition() {
    return Math.max(getFreePosition(positionTracker.posY(), rightFloats) - positionTracker.posY(), 0);
  }

  @Override
  public float lineStartPos() {
    float highestOffset = 0;
    for (BoxFragment box : leftFloats) {
      if (positionTracker.posY() >= box.marginY() && positionTracker.posY() < box.marginY() + box.marginHeight()) {
        highestOffset = Math.max(highestOffset, box.marginX() + box.marginWidth());
      }
    }

    return Math.max(0, highestOffset - positionTracker.posX());
  }

  @Override
  public float lineEndPos(LayoutConstraint lineConstraint) {
    if (lineConstraint.isPreLayoutConstraint()) {
      throw new UnsupportedOperationException("Can not determine line-end during pre-layout!");
    }

    float highestOffset = Integer.MAX_VALUE;
    for (BoxFragment box : rightFloats) {
      if (positionTracker.posY() >= box.marginY() && positionTracker.posY() < box.marginY() + box.marginHeight()) {
        highestOffset = Math.min(highestOffset, box.marginX());
      }
    }

    return Math.max(0, Math.min(positionTracker.posX() + lineConstraint.value(), highestOffset - positionTracker.posX()));
  }

  @Override
  public void reset() {
    this.blockEnd = 0;
    positionTracker.reset();
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
  public PositionTracker positionTracker() {
    return this.positionTracker;
  }

  @Override
  public float contentHeight() {
    return this.blockEnd;
  }

  private float findFreePos(LayoutConstraint lineConstraint, float minWidth, float[] outParams) {
    if (lineConstraint.isPreLayoutConstraint()) {
      throw new UnsupportedOperationException("Can not determine line-end during pre-layout!");
    }

    float currentSearchBlockPos = positionTracker.posY();
    float[] nextSearchBlockPos = new float[] { 0 };

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
      float leftOffset = lastValidInlinePos(leftFragIt, currentSearchBlockPos, positionTracker.posX(), nextSearchBlockPos);
      float rightOffset = lastValidInlinePos(rightFragIt, currentSearchBlockPos, positionTracker.posX() + lineConstraint.value(), nextSearchBlockPos);
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

  private float lastValidInlinePos(Iterator<BoxFragment> fragIt, float blockPos, float initInlinePos, float[] outNextBlockPos) {
    boolean isLeftSide = initInlinePos == positionTracker.posX();
    BoxFragment currentFragment = fragIt.hasNext() ? fragIt.next() : null;
    float inlinePos = initInlinePos;
    while (currentFragment != null && currentFragment.marginY() <= blockPos) {
      float fragmentEnd = currentFragment.marginY() + currentFragment.marginHeight();
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

  private float getFreePosition(float blockStart, Set<BoxFragment> floats) {
    float nextUncheckedY = -1;
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
