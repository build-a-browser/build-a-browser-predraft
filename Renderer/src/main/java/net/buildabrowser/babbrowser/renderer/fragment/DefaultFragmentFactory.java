package net.buildabrowser.babbrowser.renderer.fragment;

import java.util.List;

import net.buildabrowser.babbrowser.painter.core.LoadedImage;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.input.InputTypeContent;
import net.buildabrowser.babbrowser.renderer.content.input.hidden.HiddenTypeContent;
import net.buildabrowser.babbrowser.renderer.content.input.text.TextTypeContent;
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
import net.buildabrowser.babbrowser.renderer.fragment.input.BaseInputFragment;
import net.buildabrowser.babbrowser.renderer.fragment.input.DefaultButtonInputFragment;
import net.buildabrowser.babbrowser.renderer.fragment.input.DefaultCheckBoxInputFragment;
import net.buildabrowser.babbrowser.renderer.fragment.input.DefaultHiddenInputFragment;
import net.buildabrowser.babbrowser.renderer.fragment.input.DefaultRadioBoxInputFragment;
import net.buildabrowser.babbrowser.renderer.fragment.input.DefaultTextInputFragment;
import net.buildabrowser.babbrowser.renderer.fragment.scroll.DefaultScrollBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.scroll.ScrollBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.table.DefaultTableBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.table.TableBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.textarea.DefaultTextAreaBoxFragment;

public class DefaultFragmentFactory implements FragmentFactory {

  @Override
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
  
  @Override
  public FlowBlockBoxFragment createFlowBlockBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    float firstBaseline, float lastBaseline,
    ElementBox box, LayoutFragment fragments
  ) {
    return new DefaultFlowBlockBoxFragment(
      width, height, inkWidth, inkHeight,
      firstBaseline, lastBaseline,
      box, fragments);
  }

  @Override
  public FlowInlineBoxFragment createFlowInlineBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    float firstBaseline, float lastBaseline,
    ElementBox box, LayoutFragment fragments
  ) {
    return new DefaultFlowInlineBoxFragment(
      width, height, inkWidth, inkHeight,
      firstBaseline, lastBaseline,
      box, fragments);
  }

  @Override
  public FlexBoxFragment createFlexBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    float firstBaseline, float lastBaseline,
    ElementBox box, UnmanagedBoxFragment<?> fragments
  ) {
    return new DefaultFlexBoxFragment(
      width, height, inkWidth, inkHeight,
      firstBaseline, lastBaseline,
      box, fragments);
  }

  @Override
  public TableBoxFragment createTableBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    float firstBaseline, float lastBaseline,
    ElementBox box,
    Table table,
    TableBorderAssignment borderAssignment,
    List<PosRefBoxFragment> outOfTableFragments
  ) {
    return new DefaultTableBoxFragment(
      width, height, inkWidth, inkHeight,
      firstBaseline, lastBaseline,
      box, table, borderAssignment,
      outOfTableFragments);
  }

  @Override
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

  @Override
  public BaseInputFragment<?> createInputBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    float firstBaseline, float lastBaseline,
    ElementBox box,
    InputTypeContent content
  ) {
    // TODO: Other input types
    return switch (content) {
      case HiddenTypeContent _1 -> new DefaultHiddenInputFragment(
        width, height, inkWidth, inkHeight, box);
      case TextTypeContent _1 -> new DefaultTextInputFragment(
        width, height, inkWidth, inkHeight,
        firstBaseline, lastBaseline,
        box);
      default -> throw new UnsupportedOperationException(
        "Unrecognized content: " + content);
    };
  }

  @Override
  public UnmanagedBoxFragment<?> createButtonBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    float firstBaseline, float lastBaseline,
    ElementBox rootBox,
    UnmanagedBoxFragment<?> innerFragment
  ) {
    return new DefaultButtonInputFragment(
      width, height, inkWidth, inkHeight,
      firstBaseline, lastBaseline,
      rootBox, innerFragment);
  }

  @Override
  public UnmanagedBoxFragment<?> createCheckBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    float firstBaseline, float lastBaseline,
    ElementBox rootBox
  ) {
    return new DefaultCheckBoxInputFragment(
      width, height, inkWidth, inkHeight,
      firstBaseline, lastBaseline,
      rootBox);
  }

  @Override
  public UnmanagedBoxFragment<?> createRadioBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    float firstBaseline, float lastBaseline,
    ElementBox rootBox
  ) {
    return new DefaultRadioBoxInputFragment(
      width, height, inkWidth, inkHeight,
      firstBaseline, lastBaseline,
      rootBox);
  }

  @Override
  public BaseInputFragment<?> createTextAreaBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    float firstBaseline, float lastBaseline,
    ElementBox rootBox
  ) {
    return new DefaultTextAreaBoxFragment(
      width, height, inkWidth, inkHeight,
      firstBaseline, lastBaseline,
      rootBox);
  }

  @Override
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

  @Override
  public UnmanagedBoxFragment<?> createGenericUnmanagedBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    float firstBaseline, float lastBaseline,
    ElementBox box
  ) {
    return new GenericUnmanagedBoxFragment(
      width, height, inkWidth, inkHeight,
      firstBaseline, lastBaseline,
      box);
  }
  
}
