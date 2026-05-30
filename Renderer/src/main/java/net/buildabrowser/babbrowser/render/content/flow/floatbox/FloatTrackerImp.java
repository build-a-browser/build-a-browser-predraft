package net.buildabrowser.babbrowser.render.content.flow.floatbox;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.render.content.flow.BlockFormattingContext;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;

public class FloatTrackerImp implements FloatTracker {

  private static final Comparator<BoxFragment> fragmentComparator = (r1, r2) -> {
    int result = Float.compare(r1.posY(Measurement.MARGIN), r2.posY(Measurement.MARGIN));
    if (result == 0) {
      result = Float.compare(r1.posX(Measurement.MARGIN), r2.posX(Measurement.MARGIN));
    }

    return result;
  };

  private final Supplier<BlockFormattingContext> activeFormattingContext;

  public FloatTrackerImp(Supplier<BlockFormattingContext> activeFormattingContext) {
    this.activeFormattingContext = activeFormattingContext;
  }

  // TreeSet has a ton of overhead, sort on access instead
  private List<BoxFragment> leftFloats;
  private List<BoxFragment> rightFloats;
  private List<BoxFragment> allFloats;

  private boolean sidesSorted = true;
  private float lineEnd = 0;
  private float blockEnd = 0;

  // TODO: More accurately lay out floats during pre-render. Right now it's a bit hacked together.

  @Override
  public boolean addLineStartFloat(BoxFragment box, LayoutConstraint lineConstraint, float reservedWidth) {
    if (lineConstraint.isPreLayoutConstraint()) {
      lineConstraint = LayoutConstraint.of(Float.MAX_VALUE);
    }
    ensureListsInit();

    float[] freeInfo = new float[2];
    float freePos = findFreePos(lineConstraint, box.width(Measurement.MARGIN) + reservedWidth, freeInfo);
    if (reservedWidth != 0 && freePos != posY()) return false;

    // Since the box is placed by border pos, we need to convert our margin pos to border pos
    float[] margin = box.box().dimensions().getComputedMargin();
    float boxX = Math.max(freeInfo[0] + margin[2], posX());
    box.setPos(boxX, freePos + margin[0]);

    leftFloats.add(box);
    allFloats.add(box);
    sidesSorted = false;

    this.lineEnd = Math.max(this.lineEnd, boxX + box.width(Measurement.BORDER));
    this.blockEnd = Math.max(this.blockEnd, freePos + box.height(Measurement.MARGIN));

    return true;
  }

  @Override
  public boolean addLineEndFloat(BoxFragment box, LayoutConstraint lineConstraint, float reservedWidth) {
    if (lineConstraint.isPreLayoutConstraint()) {
      return addLineStartFloat(box, lineConstraint, reservedWidth);
    }
    ensureListsInit();

    float[] freeInfo = new float[2];
    float freePos = findFreePos(lineConstraint, box.width(Measurement.MARGIN) + reservedWidth, freeInfo);
    if (reservedWidth != 0 && freePos != posY()) return false;

    float maxEdgePos = posX() + lineConstraint.value();
    float maxTouchingPos = freeInfo[1];
    float boxStartPos = Math.min(maxEdgePos, maxTouchingPos) - box.width(Measurement.MARGIN);

    float[] margin = box.box().dimensions().getComputedMargin();
    box.setPos(boxStartPos + margin[2], freePos + margin[0]);

    rightFloats.add(box);
    allFloats.add(box);
    sidesSorted = true;

    this.lineEnd = Math.max(this.lineEnd, maxEdgePos);
    this.blockEnd = Math.max(this.blockEnd, freePos + box.height(Measurement.MARGIN));

    return true;
  }

  @Override
  public float clearedLineStartPosition() {
    return Math.max(getFreePosition(posY(), leftFloats) - posY(), 0);
  }

  @Override
  public float clearedLineEndPosition() {
    return Math.max(getFreePosition(posY(), rightFloats) - posY(), 0);
  }

  @Override
  public float lineStartPos() {
    if (leftFloats == null) return 0;

    float highestOffset = 0;
    for (BoxFragment box : leftFloats) {
      if (posY() >= box.posY(Measurement.MARGIN) && posY() < box.posY(Measurement.MARGIN) + box.height(Measurement.MARGIN)) {
        highestOffset = Math.max(highestOffset, box.posX(Measurement.MARGIN) + box.width(Measurement.MARGIN));
      }
    }

    return Math.max(0, highestOffset - posX());
  }

  @Override
  public float lineEndPos(LayoutConstraint lineConstraint) {
    if (lineConstraint.isPreLayoutConstraint()) {
      throw new UnsupportedOperationException("Can not determine line-end during pre-layout!");
    }

    if (rightFloats == null) return lineConstraint.value();

    float highestOffset = Integer.MAX_VALUE;
    for (BoxFragment box : rightFloats) {
      if (posY() >= box.posY(Measurement.MARGIN) && posY() < box.posY(Measurement.MARGIN) + box.height(Measurement.MARGIN)) {
        highestOffset = Math.min(highestOffset, box.posX(Measurement.MARGIN));
      }
    }

    return Math.max(0, Math.min(lineConstraint.value(), highestOffset - posX()));
  }

  @Override
  public void reset() {
    this.blockEnd = 0;

    if (allFloats == null) return;
    leftFloats.clear();
    rightFloats.clear();
    allFloats.clear();
  }

  @Override
  public List<BoxFragment> allFloats() {
    if (allFloats == null) return List.of(); // Java caches this
    return this.allFloats;
  }

  @Override
  public float contentWidth() {
    return this.lineEnd;
  }

  @Override
  public float contentHeight() {
    return this.blockEnd;
  }

  private float findFreePos(LayoutConstraint lineConstraint, float minWidth, float[] outParams) {
    if (lineConstraint.isPreLayoutConstraint()) {
      throw new UnsupportedOperationException("Can not determine line-end during pre-layout!");
    }

    float currentSearchBlockPos = posY();
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
      float leftOffset = lastValidInlinePos(leftFragIt, currentSearchBlockPos, posX(), nextSearchBlockPos);
      float rightOffset = lastValidInlinePos(rightFragIt, currentSearchBlockPos, posX() + lineConstraint.value(), nextSearchBlockPos);
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
    boolean isLeftSide = initInlinePos == posX();
    BoxFragment currentFragment = fragIt.hasNext() ? fragIt.next() : null;
    float inlinePos = initInlinePos;
    while (currentFragment != null && currentFragment.posY(Measurement.MARGIN) <= blockPos) {
      float fragmentEnd = currentFragment.posY(Measurement.MARGIN) + currentFragment.height(Measurement.MARGIN);
      if (fragmentEnd <= blockPos) {
        currentFragment = fragIt.hasNext() ? fragIt.next() : null;
        continue;
      }

      outNextBlockPos[0] = Math.min(outNextBlockPos[0], Math.max(fragmentEnd, blockPos + 1));

      inlinePos = isLeftSide ?
        currentFragment.posX(Measurement.MARGIN) + currentFragment.width(Measurement.MARGIN) :
        Math.min(inlinePos, currentFragment.posX(Measurement.MARGIN));
      
      currentFragment = fragIt.hasNext() ? fragIt.next() : null;
    }

    return inlinePos;
  }

  private float getFreePosition(float blockStart, List<BoxFragment> floats) {
    if (floats == null) return blockStart;

    if (!sidesSorted) {
      leftFloats.sort(fragmentComparator);
      rightFloats.sort(fragmentComparator);
      this.sidesSorted = true;
    }

    float nextUncheckedY = -1;
    for (BoxFragment box : floats) {
      if (nextUncheckedY >= blockStart && box.posY(Measurement.MARGIN) > nextUncheckedY) {
        return nextUncheckedY;
      } else {
        nextUncheckedY = Math.max(nextUncheckedY, box.posY(Measurement.MARGIN) + box.height(Measurement.MARGIN));
      }
    }

    return Math.max(nextUncheckedY, blockStart);
  }

  private void ensureListsInit() {
    if (this.allFloats != null) return;
    this.leftFloats = new LinkedList<>();
    this.rightFloats = new LinkedList<>();
    this.allFloats = new LinkedList<>();
  }

  private float posX() {
    return activeFormattingContext.get().estimateAbsX();
  }

  private float posY() {
    return activeFormattingContext.get().estimateAbsY();
  }

}
