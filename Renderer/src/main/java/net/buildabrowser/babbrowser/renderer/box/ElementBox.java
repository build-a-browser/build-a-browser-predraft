package net.buildabrowser.babbrowser.renderer.box;

import java.util.Comparator;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.cssbase.property.EmptyPropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.renderer.box.imp.AnonymousElementBoxImp;
import net.buildabrowser.babbrowser.renderer.box.imp.ElementBoxImp;
import net.buildabrowser.babbrowser.renderer.context.RenderContext;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutContext;
import net.buildabrowser.babbrowser.renderer.layout.stacking.StackingContext;

public non-sealed interface ElementBox extends Box {

  PropertyContainer properties();
  
  BoxContent content();

  HTMLElement element();

  RenderContext context();

  Box parentBox();

  ElementBoxDimensions dimensions();

  void alterDimensions(boolean skipIfNone, Consumer<MutableElementBoxDimensions> alterFunc);

  ElementBoxIterator childBoxes();

  void addChild(Box box);

  void clearChildren();

  // TODO: I don't really like this method, but it is needed for order-modified fixup
  void sortChildren(Comparator<? super Box> comparator);

  void startOverwrite();

  void includeChild(Box box);

  void endOverwrite();

  BoxLevel boxLevel();

  boolean updateDetails(Box parentBox, BoxLevel boxLevel);

  UnmanagedBoxFragment<?> layout(LayoutConstraint widthConstraint, LayoutConstraint heightConstraint);

  // TODO: Audit usage of this method
  void updatePositioningFragment(BoxFragment<?> boxFragment);

  BoxFragment<?> positioningFragment();

  LayoutContext layoutContext();

  void setLayoutContext(LayoutContext layoutContext);

  StackingContext stackingContext();

  void setStackingContext(StackingContext stackingContext);

  void update();

  boolean sharesContent(ElementBox elementBox);

  default boolean isReplaced() {
    return content().isReplaced(this);
  }

  default boolean hasCustomContent() {
    return isReplaced() || content().hasCustomContent(this);
  }
 
  public static ElementBox create(
    RenderContext context,
    Box parentBox,
    BoxLevel boxLevel
  ) {
    return new ElementBoxImp(context, parentBox, boxLevel);
  }

  public static ElementBox createAnonymous(ElementBox parentBox, BoxLevel boxLevel) {
    PropertyContainer propertyContainer = new EmptyPropertyContainer(parentBox.properties());
    return new AnonymousElementBoxImp(propertyContainer, parentBox, boxLevel);
  }

  public static ElementBox createAnonymous(
    PropertyContainer properties,
    Box parentBox,
    BoxLevel boxLevel
  ) {
    return new AnonymousElementBoxImp(properties, parentBox, boxLevel);
  }

  enum BoxLevel {
    BLOCK_LEVEL, INLINE_LEVEL
  }

}
