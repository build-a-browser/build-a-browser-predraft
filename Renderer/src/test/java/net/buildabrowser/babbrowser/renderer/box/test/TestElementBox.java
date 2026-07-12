package net.buildabrowser.babbrowser.renderer.box.test;

import java.util.List;
import java.util.function.Function;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.renderer.box.Box;
import net.buildabrowser.babbrowser.renderer.box.BoxContent;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.imp.AbstractElementBoxImp;
import net.buildabrowser.babbrowser.renderer.context.ElementContext;

public class TestElementBox extends AbstractElementBoxImp {

  private final BoxContent content;
  private final PropertyContainer properties;
  private final HTMLElement element;

  public TestElementBox(
    Function<ElementBox, BoxContent> contentFunc,
    BoxLevel boxLevel,
    ActiveStyles activeStyles,
    List<Box> childBoxes,
    HTMLElement element
  ) {
    super(null, boxLevel);
    for (Box childBox: childBoxes) {
      addChild(childBox);
    }
    this.properties = ActiveStyles.unparentedStyles(activeStyles);
    this.content = contentFunc.apply(this);
    this.element = element;
  }

  public TestElementBox(
    Function<ElementBox, BoxContent> contentFunc,
    BoxLevel boxLevel,
    ActiveStyles activeStyles,
    List<Box> childBoxes
  ) {
    this(contentFunc, boxLevel, activeStyles, childBoxes, null);
  }

  @Override
  public PropertyContainer properties() {
    return this.properties;
  }

  @Override
  public BoxContent content() {
    return this.content;
  }

  @Override
  public HTMLElement element() {
    return this.element;
  }

  @Override
  public ElementContext context() {
    return null;
  }

  @Override
  public void update() {
    // No-op
  }
  
}
