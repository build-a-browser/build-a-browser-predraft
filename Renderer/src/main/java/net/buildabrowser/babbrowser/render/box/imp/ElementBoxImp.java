package net.buildabrowser.babbrowser.render.box.imp;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.css.engine.styles.util.ActiveStylesUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue.InnerDisplayValue;
import net.buildabrowser.babbrowser.cssbase.property.position.PositionValue;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.render.box.Box;
import net.buildabrowser.babbrowser.render.box.BoxContent;
import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.composite.CompositeLayerUtil;
import net.buildabrowser.babbrowser.render.content.ReEntrantContent;
import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.content.flexbox.FlexBoxContent;
import net.buildabrowser.babbrowser.render.content.flow.FlowRootContent;
import net.buildabrowser.babbrowser.render.content.image.ImageContent;
import net.buildabrowser.babbrowser.render.content.scroll.ScrollBox;
import net.buildabrowser.babbrowser.render.content.table.TableContent;
import net.buildabrowser.babbrowser.render.context.ElementContext;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;

public class ElementBoxImp extends AbstractElementBoxImp {

  private final HTMLElement element;
  
  private BoxContent content;
  // TODO: Avoid this extra field
  private InnerDisplayValue prevDisplayValue;

  public ElementBoxImp(HTMLElement element, Box parentBox, BoxLevel boxLevel) {
    super(parentBox, boxLevel);
    this.element = element;
    update();
  }

  @Override
  public ActiveStyles activeStyles() {
    return ((ElementContext) element.getContext()).activeStyles();
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
  public UnmanagedBoxFragment layout(
    LayoutConstraint widthConstraint, LayoutConstraint heightConstraint
  ) {
    BoxContent realContent = content();
    this.content = ReEntrantContent.instance();
    UnmanagedBoxFragment fragment = layoutWithContent(widthConstraint, heightConstraint, realContent);
    this.content = realContent;
    return fragment;
  }
  
  // As of writing, children are cleared before the update, so don't worry about that yet
  @Override
  public void update() {
    InnerDisplayValue innerDisplay = ActiveStylesUtil.innerDisplayValue(activeStyles());
    if (
      this.content == null
      || !innerDisplay.equals(prevDisplayValue)
      || content.rootBox() != this
    ) {
      this.prevDisplayValue = innerDisplay;

      if (
        parentBox() instanceof ElementBox parentBox
        && canShareContent()
        && parentBox.sharesContent(this)
      ) {
        this.content = parentBox.content();
      } else {
        this.content = createContent(innerDisplay);
      }
    }
  }

  @Override
  public void updateDetails(Box parentBox, BoxLevel boxLevel) {
    this.content = null;
    this.prevDisplayValue = null;
    setNext(null);
    super.updateDetails(parentBox, boxLevel);
  }

  private BoxContent createContent(InnerDisplayValue innerDisplay) {
    if (element.name().equals("img")) {
      return new ImageContent(this);
    }
  
    return switch (innerDisplay) {
      case TABLE -> new TableContent(this);
      case FLEX -> new FlexBoxContent(this);
      default -> new FlowRootContent(this);
    };
  }

  private boolean canShareContent() {
    CSSValue positioning = activeStyles().getProperty(CSSProperty.POSITION);
    return
      positioning.equals(PositionValue.STATIC)
      && !CompositeLayerUtil.hasScrollContent(this)
      && !wouldBeReplaced()
      && !(parentBox() instanceof ScrollBox);
  }

  private boolean wouldBeReplaced() {
    return element.name().equals("img");
  }
  
}
