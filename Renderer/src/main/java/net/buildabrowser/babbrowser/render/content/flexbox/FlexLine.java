package net.buildabrowser.babbrowser.render.content.flexbox;

import java.util.LinkedList;
import java.util.List;

public class FlexLine {
  
  private final List<FlexItem> items = new LinkedList<>();
  
  private float crossSize;

  public List<FlexItem> items() {
    return items;
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
      hypotheticalSum += item.outerSize(item.hypotheticalMainSize());
    }
    hypotheticalSum += mainGap * (items.size() - 1);
    return hypotheticalSum;
  }

  public void setCrossSize(float crossSize) {
    this.crossSize = crossSize;
  }

  public float crossSize() {
    return this.crossSize;
  }

}
