package net.buildabrowser.babbrowser.renderer.box.imp;

import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue.InnerDisplayValue;
import net.buildabrowser.babbrowser.cssbase.util.PropertiesUtil;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.html.html.HTMLTextAreaElement;
import net.buildabrowser.babbrowser.renderer.box.Box;
import net.buildabrowser.babbrowser.renderer.box.BoxContent;
import net.buildabrowser.babbrowser.renderer.content.image.ImageContent;
import net.buildabrowser.babbrowser.renderer.content.input.InputContent;
import net.buildabrowser.babbrowser.renderer.content.textarea.TextAreaContent;
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
    // TODO: There was previously a re-entrancy check here, but it interfered with clamp-width evaluating
    // min/max sizes on self
    return layoutWithContent(widthConstraint, heightConstraint, realContent);
  }
  
  // As of writing, children are cleared before the update, so don't worry about that yet
  @Override
  public void update() {
    InnerDisplayValue innerDisplay = PropertiesUtil.innerDisplayValue(properties());
    if (
      this.content == null
      || !innerDisplay.equals(prevDisplayValue)
    ) {
      this.prevDisplayValue = innerDisplay;
      // No longer does content sharing since the main types are singletons now
      this.content = createContent(innerDisplay);
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
    BoxContent elementContent = switch (element().name()) {
      case "img" -> new ImageContent();
      case "input" -> new InputContent();
      case "textarea" -> new TextAreaContent(
        (HTMLTextAreaElement) element(), this);
      default -> null;
    };

    if (elementContent != null) {
      return elementContent;
    }
  
    return createSpecifiedContent(innerDisplay);
  }
  
}
