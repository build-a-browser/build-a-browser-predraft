package net.buildabrowser.babbrowser.renderer.content.flexbox;

import java.util.LinkedList;
import java.util.List;

import net.buildabrowser.babbrowser.renderer.content.generic.GenericItem;
import net.buildabrowser.babbrowser.renderer.content.generic.GenericTrack;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;

public class FlexLine implements GenericTrack {
  
  private final List<FlexItem> items = new LinkedList<>();
  private final boolean isVertical;
  
  private float crossSize;

  public FlexLine(boolean isVertical) {
    this.isVertical = isVertical;
  }

  public List<FlexItem> items() {
    return items;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public List<GenericItem> genericItems() {
    return (List<GenericItem>) (List) items;
  }

  public void addItem(FlexItem item) {
    items.add(item);
  }

  public boolean isEmpty() {
    return items.isEmpty();
  }

  public float sumHypotheticalMainSizes(float mainGap) {
    float hypotheticalSum = 0;
    for (FlexItem item: items) {
      hypotheticalSum += item.hypotheticalMainSize();
      hypotheticalSum += item.mainMargin();
    }
    hypotheticalSum += mainGap * (items.size() - 1);
    return hypotheticalSum;
  }

  public void setCrossSize(float crossSize) {
    this.crossSize = crossSize;
  }

  @Override
  public float crossSize() {
    return this.crossSize;
  }

  @Override
  public void setCrossPos(float startPos) {
    for (GenericItem item: genericItems()) {
      // TODO: Handle auto margin
      float[] margin = item.box().dimensions().getComputedMargin();
      if (isVertical) {
        float newX = item.fragment().posX(Measurement.BORDER) + startPos;
        item.fragment().setPos(newX + margin[2], item.fragment().posY(Measurement.BORDER));
      } else {
        float newY = item.fragment().posY(Measurement.BORDER) + startPos;
        item.fragment().setPos(item.fragment().posX(Measurement.BORDER), newY + margin[0]);
      }
    }
  }

}
