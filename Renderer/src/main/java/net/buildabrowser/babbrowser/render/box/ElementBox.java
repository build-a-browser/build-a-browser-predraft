package net.buildabrowser.babbrowser.render.box;

import java.util.Comparator;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.mutable.MutableElement;
import net.buildabrowser.babbrowser.render.box.imp.AnonymousElementBoxImp;
import net.buildabrowser.babbrowser.render.box.imp.ElementBoxImp;
import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.layout.CachedLayoutResult;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.render.layout.StackingContext;

public interface ElementBox extends Box {

  ActiveStyles activeStyles();
  
  BoxContent content();

  Element element();

  Box parentBox();

  ElementBoxDimensions dimensions();

  ElementBoxIterator childBoxes();

  void addChild(Box box);

  void clearChildren();

  // TODO: I don't really like this method, but it is needed for order-modified fixup
  void sortChildren(Comparator<? super Box> comparator);

  BoxLevel boxLevel();

  UnmanagedBoxFragment layout(LayoutConstraint widthConstraint, LayoutConstraint heightConstraint);

  CachedLayoutResult cachedLayout(LayoutConstraint widthConstraint, LayoutConstraint heightConstraint);

  void updateFragment(BoxFragment boxFragment);

  BoxFragment lastCachedFragment();

  LayoutContext layoutContext();

  void setLayoutContext(LayoutContext layoutContext);

  StackingContext stackingContext();

  void setStackingContext(StackingContext stackingContext);

  default boolean isReplaced() {
    return content().isReplaced();
  }
 
  public static ElementBox create(MutableElement element, Box parentBox, BoxLevel boxLevel) {
    return new ElementBoxImp(element, parentBox, boxLevel);
  }

  public static ElementBox createAnonymous(ElementBox parentBox, BoxLevel boxLevel) {
    ActiveStyles styles = ActiveStyles.create(parentBox.activeStyles());
    return new AnonymousElementBoxImp(styles, parentBox, boxLevel);
  }

  public static ElementBox createAnonymous(ActiveStyles styles, ElementBox parentBox, BoxLevel boxLevel) {
    return new AnonymousElementBoxImp(styles, parentBox, boxLevel);
  }

  enum BoxLevel {
    BLOCK_LEVEL, INLINE_LEVEL
  }

}
