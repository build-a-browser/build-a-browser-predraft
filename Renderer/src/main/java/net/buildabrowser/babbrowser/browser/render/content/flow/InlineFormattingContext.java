package net.buildabrowser.babbrowser.browser.render.content.flow;

import java.util.LinkedList;
import java.util.List;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.FlowFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.LineBoxFragment;

public class InlineFormattingContext {
 
  private final List<LineBox> lineBoxes = new LinkedList<>();
  private LineBox activeLineBox = new LineBox();

  public InlineFormattingContext() {
    lineBoxes.add(activeLineBox);
  }

  public void addFragment(FlowFragment flowFragment) {
    activeLineBox.addFragment(flowFragment);
  }

  public void pushElement(ElementBox elementBox) {
    activeLineBox.pushElement(elementBox);
  }

  public ElementBox popElement() {
    return activeLineBox.popElement();
  }

  public InlineFormattingContext split() {
    return null;
  };

  public List<LineBoxFragment> fragments() {
    return lineBoxes.stream().map(box -> box.toFragment()).toList();
  }

}
