package net.buildabrowser.babbrowser.renderer.imp.html;

import java.util.List;

import net.buildabrowser.babbrowser.common.datastruct.SlotFamily;
import net.buildabrowser.babbrowser.cssbase.cssom.Declaration;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule.RuleSource;
import net.buildabrowser.babbrowser.cssbase.cssom.rule.StyleRule;
import net.buildabrowser.babbrowser.cssbase.property.color.ColorValue;
import net.buildabrowser.babbrowser.cssbase.property.color.ColorValue.SRGBAColor;
import net.buildabrowser.babbrowser.cssbase.property.outline.OutlineCompositeValue;
import net.buildabrowser.babbrowser.cssbase.property.shared.LineStyleValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue.LengthType;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorSpecificity;
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

  private static final WeightedStyleRule SELECTION_OUTLINE_RULE =
    createOutlineRule(SRGBAColor.create(0, 0, 255, 255));
  private static final WeightedStyleRule TARGETED_OUTLINE_RULE =
    createOutlineRule(SRGBAColor.create(255, 0, 255, 255));

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

  @Override
  public void markSelection(DebugObjectSelection selection) {
    if (
      node instanceof HTMLElement element
      && renderContexts.get(element) instanceof ElementContext elementContext
    ) {
      elementContext.onCSSRuleUnmatched(SELECTION_OUTLINE_RULE);
      elementContext.onCSSRuleUnmatched(TARGETED_OUTLINE_RULE);
      switch (selection) {
        case SELECTED -> elementContext.onCSSRuleMatched(SELECTION_OUTLINE_RULE);
        case TARGETED -> elementContext.onCSSRuleMatched(TARGETED_OUTLINE_RULE);
        default -> {}
      }
    }
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

  private static WeightedStyleRule createOutlineRule(ColorValue color) {
    // TODO: This could conflict with another important outline property
    OutlineCompositeValue outlineValue = new OutlineCompositeValue(
      LengthValue.create(2, LengthType.PX),
      LineStyleValue.SOLID, color);
    StyleRule styleRule = new StyleRule(List.of(
      // TODO: Get the actual source to pass in
      Declaration.create(null, "outline", outlineValue, true)));
    return WeightedStyleRule.create(
      styleRule, SelectorSpecificity.ZERO_SPECIFICITY, RuleSource.AUTHOR, 0, 0);
  }

}
