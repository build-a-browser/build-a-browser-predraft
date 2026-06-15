package net.buildabrowser.babbrowser.renderer.box.imp;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue.InnerDisplayValue;
import net.buildabrowser.babbrowser.cssbase.property.position.PositionValue;
import net.buildabrowser.babbrowser.cssbase.util.PropertiesUtil;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.renderer.box.Box;
import net.buildabrowser.babbrowser.renderer.box.BoxContent;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.composite.CompositeLayerUtil;
import net.buildabrowser.babbrowser.renderer.content.ReEntrantContent;
import net.buildabrowser.babbrowser.renderer.content.flexbox.FlexBoxContent;
import net.buildabrowser.babbrowser.renderer.content.flow.FlowRootContent;
import net.buildabrowser.babbrowser.renderer.content.image.ImageContent;
import net.buildabrowser.babbrowser.renderer.content.scroll.ScrollBox;
import net.buildabrowser.babbrowser.renderer.content.table.TableContent;
import net.buildabrowser.babbrowser.renderer.context.ElementContext;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public class ElementBoxImp extends AbstractElementBoxImp {

  private final ElementContext context;
  
  private BoxContent content;
  // TODO: Avoid this extra field
  private InnerDisplayValue prevDisplayValue;

  public ElementBoxImp(ElementContext context, Box parentBox, BoxLevel boxLevel) {
    super(parentBox, boxLevel);
    this.context = context;
    update();
  }

  @Override
  public PropertyContainer properties() {
    return context.properties();
  }

  @Override
  public BoxContent content() {
    return this.content;
  }

  @Override
  public HTMLElement element() {
    return context.element();
  }

  @Override
  public ElementContext context() {
    return this.context;
  }

  @Override
  public UnmanagedBoxFragment<?> layout(
    LayoutConstraint widthConstraint, LayoutConstraint heightConstraint
  ) {
    BoxContent realContent = content();
    this.content = ReEntrantContent.instance();
    UnmanagedBoxFragment<?> fragment = layoutWithContent(widthConstraint, heightConstraint, realContent);
    this.content = realContent;
    return fragment;
  }
  
  // As of writing, children are cleared before the update, so don't worry about that yet
  @Override
  public void update() {
    InnerDisplayValue innerDisplay = PropertiesUtil.innerDisplayValue(properties());
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
    if (element().name().equals("img")) {
      return new ImageContent(this);
    }
  
    return switch (innerDisplay) {
      case TABLE -> new TableContent(this);
      case FLEX -> new FlexBoxContent(this);
      default -> new FlowRootContent(this);
    };
  }

  private boolean canShareContent() {
    CSSValue positioning = properties().get(CSSProperty.POSITION);
    return
      positioning.equals(PositionValue.STATIC)
      && !CompositeLayerUtil.hasScrollContent(this)
      && !wouldBeReplaced()
      && !(parentBox() instanceof ScrollBox);
  }

  private boolean wouldBeReplaced() {
    return element().name().equals("img");
  }
  
}
