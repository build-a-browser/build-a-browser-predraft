package net.buildabrowser.babbrowser.renderer.fragment;

import java.util.List;

import net.buildabrowser.babbrowser.painter.core.LoadedImage;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.input.InputTypeContent;
import net.buildabrowser.babbrowser.renderer.content.scroll.ScrollBox;
import net.buildabrowser.babbrowser.renderer.content.table.Table;
import net.buildabrowser.babbrowser.renderer.content.table.imp.border.TableBorderAssignment;
import net.buildabrowser.babbrowser.renderer.fragment.flexbox.FlexBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.flow.FlowBlockBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.flow.FlowInlineBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.flow.FlowRootBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.image.ImageBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.input.BaseInputFragment;
import net.buildabrowser.babbrowser.renderer.fragment.scroll.ScrollBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.table.TableBoxFragment;

public interface FragmentFactory {

  FlowRootBoxFragment createFlowRootBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    ElementBox box, ManagedBoxFragment<?> rootFragment,
    List<BoxFragment<?>> allFloats
  );

  FlowBlockBoxFragment createFlowBlockBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    ElementBox box, LayoutFragment fragments
  );

  FlowInlineBoxFragment createFlowInlineBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    ElementBox box, LayoutFragment fragments
  );

  FlexBoxFragment createFlexBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    ElementBox box, UnmanagedBoxFragment<?> fragments
  );

  TableBoxFragment createTableBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    ElementBox box,
    Table table,
    TableBorderAssignment borderAssignment,
    List<PosRefBoxFragment> outOfTableFragments
  );

  ImageBoxFragment createImageBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    ElementBox box,
    LoadedImage image, String altText
  );

  BaseInputFragment<?> createInputBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    ElementBox box,
    InputTypeContent content
  );

  UnmanagedBoxFragment<?> createButtonBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    ElementBox rootBox,
    UnmanagedBoxFragment<?> innerFragment
  );

  ScrollBoxFragment createScrollBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    boolean addedHorizontalScrollbars,
    boolean addedVerticalScrollbars,
    ScrollBox box, UnmanagedBoxFragment<?> innerFragment
  );

  UnmanagedBoxFragment<?> createGenericUnmanagedBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    ElementBox box
  );

  default UnmanagedBoxFragment<?> createGenericUnmanagedBox(
    float width, float height,
    ElementBox box
  ) {
    return createGenericUnmanagedBoxFragment(width, height, width, height, box);
  }

  static DefaultFragmentFactory createDefault() {
    return new DefaultFragmentFactory();
  }
  
}
