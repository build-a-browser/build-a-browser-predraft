package net.buildabrowser.babbrowser.renderer.fragment;

import java.util.List;

import net.buildabrowser.babbrowser.painter.core.LoadedImage;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.scroll.ScrollBox;
import net.buildabrowser.babbrowser.renderer.content.table.Table;
import net.buildabrowser.babbrowser.renderer.content.table.imp.border.TableBorderAssignment;
import net.buildabrowser.babbrowser.renderer.fragment.flexbox.DefaultFlexBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.flexbox.FlexBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.flow.DefaultFlowBlockBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.flow.DefaultFlowInlineBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.flow.DefaultFlowRootBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.flow.FlowBlockBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.flow.FlowInlineBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.flow.FlowRootBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.image.DefaultImageBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.image.ImageBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.scroll.DefaultScrollBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.scroll.ScrollBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.table.DefaultTableBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.table.TableBoxFragment;

public class DefaultFragmentFactory implements FragmentFactory {

  public FlowRootBoxFragment createFlowRootBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    ElementBox box, ManagedBoxFragment<?> rootFragment,
    List<BoxFragment<?>> allFloats
  ) {
    return new DefaultFlowRootBoxFragment(
      width, height, inkWidth, inkHeight,
      box, rootFragment, allFloats);
  }
  
  public FlowBlockBoxFragment createFlowBlockBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    ElementBox box, LayoutFragment fragments
  ) {
    return new DefaultFlowBlockBoxFragment(
      width, height, inkWidth, inkHeight,
      box, fragments);
  }

  public FlowInlineBoxFragment createFlowInlineBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    ElementBox box, LayoutFragment fragments
  ) {
    return new DefaultFlowInlineBoxFragment(
      width, height, inkWidth, inkHeight,
      box, fragments);
  }

  public FlexBoxFragment createFlexBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    ElementBox box, UnmanagedBoxFragment<?> fragments
  ) {
    return new DefaultFlexBoxFragment(
      width, height, inkWidth, inkHeight,
      box, fragments);
  }

  public TableBoxFragment createTableBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    ElementBox box,
    Table table,
    TableBorderAssignment borderAssignment
  ) {
    return new DefaultTableBoxFragment(
      width, height, inkWidth, inkHeight,
      box, table, borderAssignment);
  }

  public ImageBoxFragment createImageBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    ElementBox box,
    LoadedImage image, String altText
  ) {
    return new DefaultImageBoxFragment(
      width, height, inkWidth, inkHeight,
      box, image, altText);
  }

  public ScrollBoxFragment createScrollBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    boolean addedHorizontalScrollbars,
    boolean addedVerticalScrollbars,
    ScrollBox box, UnmanagedBoxFragment<?> innerFragment
  ) {
    return new DefaultScrollBoxFragment(
      width, height, inkWidth, inkHeight,
      addedHorizontalScrollbars, addedVerticalScrollbars,
      box, innerFragment);
  }

  public UnmanagedBoxFragment<?> createGenericUnmanagedBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    ElementBox box
  ) {
    return new GenericUnmanagedBoxFragment(width, height, inkWidth, inkHeight, box);
  }
  
}
