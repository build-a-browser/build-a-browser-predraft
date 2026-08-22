package net.buildabrowser.babbrowser.renderer.imp.html;

import net.buildabrowser.babbrowser.common.datastruct.SlotFamily;
import net.buildabrowser.babbrowser.debugger.core.DebugObject;
import net.buildabrowser.babbrowser.debugger.core.DebugSideDimensions;
import net.buildabrowser.babbrowser.debugger.core.DebugSnapshot;
import net.buildabrowser.babbrowser.debugger.core.DebugSnapshotBuilder;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.renderer.context.ElementContext;
import net.buildabrowser.babbrowser.renderer.context.RenderContext;

public class HTMLNodeDebugObject implements DebugObject {

  private final Node node;
  private final SlotFamily<HTMLElement, RenderContext> renderContexts;

  public HTMLNodeDebugObject(
    Node node,
    SlotFamily<HTMLElement, RenderContext> renderContexts
  ) {
    this.node = node;
    this.renderContexts = renderContexts;
  }

  @Override
  public DebugSnapshot snapshotDebugInfo() {
    DebugSnapshotBuilder snapshotBuilder = DebugSnapshot.builder();
    if (
      node instanceof HTMLElement element
      && renderContexts.get(element) instanceof RenderContext renderContext
    ) {
      captureDebugInfo(snapshotBuilder, renderContext, null);
    }

    return snapshotBuilder.build();
  }

  public static void captureDebugInfo(
    DebugSnapshotBuilder snapshotBuilder,
    RenderContext renderContext,
    ElementBox relatedBox
  ) {
    snapshotBuilder.setComputedStyles(() -> renderContext.properties());

    if (renderContext instanceof ElementContext elementContext) {
      snapshotBuilder.setStyleRules(() -> elementContext.matchedRules());
      
      if (relatedBox == null) {
        relatedBox = elementContext.box();
      }
    }

    if (relatedBox != null) {
      ElementBoxDimensions dimensions = relatedBox.dimensions();
      snapshotBuilder.setMargin(captureDimensions(dimensions.getComputedMargin()));
      snapshotBuilder.setBorder(captureDimensions(dimensions.getComputedBorder()));
      snapshotBuilder.setPadding(captureDimensions(dimensions.getComputedPadding()));
    }
  }

  private static DebugSideDimensions captureDimensions(float[] dimArr) {
    return new DebugSideDimensions(dimArr[0], dimArr[1], dimArr[2], dimArr[3]);
  }

}
