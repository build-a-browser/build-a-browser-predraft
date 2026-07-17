package net.buildabrowser.babbrowser.renderer.content.flow;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.text.LineHeightValue;
import net.buildabrowser.babbrowser.painter.core.FontMetrics;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.common.SizingUtil;
import net.buildabrowser.babbrowser.renderer.content.common.position.PositionUtil;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.FragmentFactory;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.TextFragment;
import net.buildabrowser.babbrowser.renderer.fragment.flow.FlowInlineBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public class LineSegment {

  private final ElementBox box;
  private LayoutFragment fragments;
  private LayoutFragment nextFragment;
  private float width = 0;
  private float inkWidth = 0;
  private float largestTextHeight = 0;
  private boolean isEmpty = true;

  public LineSegment(ElementBox box) {
    this.box = box;
    this.isEmpty = !FlowUtil.boxHasDecor(box);
  }

  public ElementBox box() {
    return this.box;
  }

  public LayoutFragment fragments() {
    return this.fragments;
  }

  public float width() {
    return this.width;
  }

  public float inkWidth() {
    return this.inkWidth;
  }

  public float height() {
    if (isEmpty) return 0;

    float lineHeight = computeLineHeight();
    float contentHeight = computeContentHeight();

    return Math.max(lineHeight, contentHeight);
  }

  public float inkHeight() {
    if (isEmpty) return 0;

    float height = computeLineHeight();

    LayoutFragment curNode = fragments;
    while (curNode != null) {
      height = Math.max(height, curNode.inkHeight(Measurement.MARGIN));
      curNode = curNode.next();
    }

    return height;
  }

  public void addFragment(LayoutFragment managedBoxFragment, boolean isEmpty) {
    LayoutFragment newFragment = IntrusiveList.add(nextFragment, managedBoxFragment);
    if (fragments == null) {
      fragments = newFragment;
    }

    nextFragment = nextFragment == null ? newFragment : nextFragment.next();

    if (!PositionUtil.affectsLayout(managedBoxFragment)) return;
    this.isEmpty &= isEmpty;
    inkWidth = Math.max(
      width + managedBoxFragment.inkWidth(Measurement.MARGIN),
      inkWidth);
    width += managedBoxFragment.width(Measurement.MARGIN);
    if (managedBoxFragment instanceof TextFragment textFragment) {
      largestTextHeight = Math.max(largestTextHeight, textFragment.height(Measurement.CONTENT));
    }
  }

  public FlowInlineBoxFragment toFragment() {
    FragmentFactory fragmentFactory = box().layoutContext().global().fragmentFactory();
    FontMetrics metrics = box.layoutContext().font().metrics();
    float fontHeight = normalHeight(metrics);
    float height = height();    
    float contentHeight = Math.max(fontHeight, computeContentHeight());
    float firstBaseline = FlowLinePositioner.computeFirstBaseline(fragments(), height, contentHeight);
    float lastBaseline = FlowLinePositioner.computeLastBaseline(fragments(), height, contentHeight);
    return fragmentFactory.createFlowInlineBoxFragment(
      width(), height,
      inkWidth(), inkHeight(),
      firstBaseline, lastBaseline,
      box(), fragments());
  }

  public boolean isEmpty() {
    return this.isEmpty;
  }

  private float computeLineHeight() {
    CSSValue lineHeight = box.properties().get(CSSProperty.LINE_HEIGHT);
    FontMetrics metrics = box.layoutContext().font().metrics();
    if (lineHeight.equals(LineHeightValue.NORMAL)) {
      return normalHeight(metrics);
    } else if (lineHeight instanceof LineHeightValue.NumberHeight numberHeight) {
      return numberHeight.multiplier().floatValue() * metrics.size();
    } else {
      LayoutConstraint lineHeightConstraint = SizingUtil.evaluateBaseSize(
        box.layoutContext(),
        LayoutConstraint.of(metrics.size()),
        lineHeight);

      return lineHeightConstraint.isBounded() ?
        lineHeightConstraint.value() :
        metrics.size(); // TODO: What is the proper fallback?
    }
  }

  private float normalHeight(FontMetrics metrics) {
    if (largestTextHeight != 0) {
      return largestTextHeight;
    }

    return metrics.height(); // Presumably, this is just the precomputed ascent plus descent
  }

  // TODO: Support other alignments
  private float computeContentHeight() {
    float desiredHeight = 0;
    float maxBaseline = 0;

    LayoutFragment fragment = fragments;
    while (fragment != null) {
      if (!(fragment instanceof TextFragment)) {
        float itemBaseline = FlowLinePositioner.itemBaselineLast(fragment);
        float itemHeight = fragment.height(Measurement.BORDER) - fragment.lastBaseline(Measurement.BORDER);
        if (fragment instanceof BoxFragment boxFragment) {
          float[] margin = boxFragment.box().dimensions().getComputedMargin();
          itemHeight = itemHeight + Math.max(0, margin[1]) + Math.max(0, margin[0]);
        }
        maxBaseline = Math.max(maxBaseline, itemBaseline);
        desiredHeight = Math.max(desiredHeight, itemHeight);
      }
      fragment = fragment.next();
    }

    return desiredHeight + maxBaseline;
  }

}