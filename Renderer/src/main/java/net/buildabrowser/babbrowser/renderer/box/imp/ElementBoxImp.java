package net.buildabrowser.babbrowser.renderer.box.imp;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue.InnerDisplayValue;
import net.buildabrowser.babbrowser.cssbase.util.PropertiesUtil;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.html.html.HTMLObjectElement;
import net.buildabrowser.babbrowser.html.html.HTMLTextAreaElement;
import net.buildabrowser.babbrowser.renderer.box.Box;
import net.buildabrowser.babbrowser.renderer.box.BoxContent;
import net.buildabrowser.babbrowser.renderer.content.image.ImageContent;
import net.buildabrowser.babbrowser.renderer.content.input.InputContent;
import net.buildabrowser.babbrowser.renderer.content.textarea.TextAreaContent;
import net.buildabrowser.babbrowser.renderer.context.RenderContext;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.imp.html.HTMLObjectLoader;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public class ElementBoxImp extends AbstractElementBoxImp {

  private final RenderContext context;
  
  private BoxContent content;
  // TODO: Avoid this extra field
  private InnerDisplayValue prevDisplayValue;

  public ElementBoxImp(RenderContext context, Box parentBox, BoxLevel boxLevel) {
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
  public RenderContext context() {
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
      || isAlwaysUpdate()
    ) {
      this.prevDisplayValue = innerDisplay;
      // No longer does content sharing since the main types are singletons now
      this.content = createContent(innerDisplay);
      context().invalidate(InvalidationLevel.LAYOUT);
    }
  }

  @Override
  public boolean updateDetails(Box parentBox, BoxLevel boxLevel) {
    if (super.updateDetails(parentBox, boxLevel)) {
      this.content = null;
      this.prevDisplayValue = null;
      return true;
    }
    
    return false;
  }

  private BoxContent createContent(InnerDisplayValue innerDisplay) {
    if (element() == null) {
      return createSpecifiedContent(innerDisplay);
    }

    // TODO: Instead switch on element interface
    BoxContent elementContent = switch (element().name()) {
      case "img" -> new ImageContent();
      case "input" -> new InputContent();
      case "textarea" -> new TextAreaContent(
        (HTMLTextAreaElement) element(), this);
      case "object" -> HTMLObjectLoader.createContent(
        (HTMLObjectElement) element());
      default -> null;
    };

    if (elementContent != null) {
      return elementContent;
    }
  
    return createSpecifiedContent(innerDisplay);
  }

  private boolean isAlwaysUpdate() {
    // TODO: This is definitely a bit hacky
    HTMLElement element = element();
    if (element instanceof HTMLObjectElement) return true;
    return false;
  }
  
}
