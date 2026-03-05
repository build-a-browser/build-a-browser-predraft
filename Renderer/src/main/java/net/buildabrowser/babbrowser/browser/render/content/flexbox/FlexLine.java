package net.buildabrowser.babbrowser.browser.render.content.flexbox;

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

  public float sumHypotheticalMainSizes() {
    float hypotheticalSum = 0;
    for (FlexItem item: items) {
      hypotheticalSum += item.hypotheticalMainSize();
    }
    return hypotheticalSum;
  }

  public void setCrossSize(float crossSize) {
    this.crossSize = crossSize;
  }

  public float crossSize() {
    return this.crossSize;
  }

}
