package net.buildabrowser.babbrowser.renderer.content.flow;

import net.buildabrowser.babbrowser.renderer.content.flow.floatbox.FloatTracker;

public class FlowContext {

  private final FlowBlockLayout blockLayout;
  private final FlowInlineLayout inlineLayout;
  private final FloatTracker floatTracker;

  public FlowContext() {
    this.blockLayout = new FlowBlockLayout(this);
    this.inlineLayout = new FlowInlineLayout(this);
    this.floatTracker = FloatTracker.createForFlow(() -> blockLayout.activeContext());
  }

  FlowBlockLayout blockLayout() {
    return this.blockLayout;
  }

  FlowInlineLayout inlineLayout() {
    return this.inlineLayout;
  }

  FloatTracker floatTracker() {
    return this.floatTracker;
  }
  
}
