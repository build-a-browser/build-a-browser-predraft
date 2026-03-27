package net.buildabrowser.babbrowser.render.content.flow.floatbox;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.render.content.flow.BlockFormattingContext;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;

public class FloatTrackerImp implements FloatTracker {

  private static final Comparator<BoxFragment> fragmentComparator = (r1, r2) -> {
    int result = Float.compare(r1.marginY(), r2.marginY());
    if (result == 0) {
      result = Float.compare(r1.marginX(), r2.marginX());
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
  private float blockEnd = 0;

  @Override
  public boolean addLineStartFloat(BoxFragment box, LayoutConstraint lineConstraint, float reservedWidth) {
    // TODO: Find a proper way to handle pre-layout constraints
    if (lineConstraint.isPreLayoutConstraint()) return true;
    ensureListsInit();

    float[] freeInfo = new float[2];
    float freePos = findFreePos(lineConstraint, box.marginWidth() + reservedWidth, freeInfo);
    if (reservedWidth != 0 && freePos != posY()) return false;

    // Since the box is placed by border pos, we need to convert our margin pos to border pos
    float[] margin = box.box().dimensions().getComputedMargin();
    box.setPos(Math.max(freeInfo[0] + margin[2], posX()), freePos + margin[0]);

    leftFloats.add(box);
    allFloats.add(box);
    sidesSorted = false;

    this.blockEnd = Math.max(this.blockEnd, freePos + box.marginHeight());

    return true;
  }

  @Override
  public boolean addLineEndFloat(BoxFragment box, LayoutConstraint lineConstraint, float reservedWidth) {
    if (lineConstraint.isPreLayoutConstraint()) return true;
    ensureListsInit();

    float[] freeInfo = new float[2];
    float freePos = findFreePos(lineConstraint, box.marginWidth() + reservedWidth, freeInfo);
    if (reservedWidth != 0 && freePos != posY()) return false;

    float maxEdgePos = posX() + lineConstraint.value();
    float maxTouchingPos = freeInfo[1];
    float boxStartPos = Math.min(maxEdgePos, maxTouchingPos) - box.marginWidth();

    float[] margin = box.box().dimensions().getComputedMargin();
    box.setPos(boxStartPos + margin[2], freePos + margin[0]);

    rightFloats.add(box);
    allFloats.add(box);
    sidesSorted = true;

    this.blockEnd = Math.max(this.blockEnd, freePos + box.marginHeight());

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
      if (posY() >= box.marginY() && posY() < box.marginY() + box.marginHeight()) {
        highestOffset = Math.max(highestOffset, box.marginX() + box.marginWidth());
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
      if (posY() >= box.marginY() && posY() < box.marginY() + box.marginHeight()) {
        highestOffset = Math.min(highestOffset, box.marginX());
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
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public List<LayoutFragment> allFloats() {
    if (allFloats == null) return List.of(); // Java caches this
    return (List<LayoutFragment>) (List) this.allFloats;
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

  private float getFreePosition(float blockStart, List<BoxFragment> floats) {
    if (floats == null) return blockStart;

    if (!sidesSorted) {
      leftFloats.sort(fragmentComparator);
      rightFloats.sort(fragmentComparator);
      this.sidesSorted = true;
    }

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
