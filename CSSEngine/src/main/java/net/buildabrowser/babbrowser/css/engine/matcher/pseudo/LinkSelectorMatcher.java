package net.buildabrowser.babbrowser.css.engine.matcher.pseudo;

import java.util.function.Consumer;

import net.buildabrowser.babbrowser.css.engine.matcher.ElementRootSet;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorPart;
import net.buildabrowser.babbrowser.cssbase.selector.SimplePseudoSelector;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;

public class LinkSelectorMatcher implements SimplePseudoSelectorMatcher {

  private final ElementSet allElements;
  private final ElementSet matchingElements;
  private final Consumer<SelectorPart> onSelectorChanged;

  public LinkSelectorMatcher(
    ElementRootSet allElements,
    Consumer<SelectorPart> onSelectorChanged
  ) {
    this.allElements = allElements;
    this.matchingElements = allElements.createChild();
    this.onSelectorChanged = onSelectorChanged;
  }

  @Override
  public void addSelectorReference(SimplePseudoSelector ref) {}

  @Override
  public void removeSelectorReference(SimplePseudoSelector ref) {}

  @Override
  public void onNodeAdded(Node node) {
    if (!(node instanceof Element element)) return;
    if (matches(element)) {
      boolean changed = matchingElements.add(element);
      if (changed) {
        onSelectorChanged.accept(SimplePseudoSelector.LINK);
      }
    }
  }

  @Override
  public void onNodeRemoved(Node node) {
    if (!(node instanceof Element element)) return;
    boolean changed = matchingElements.remove(element);
    if (changed) {
      onSelectorChanged.accept(SimplePseudoSelector.LINK);
    }
  }

  @Override
  public ElementSet match(SimplePseudoSelector selector) {
    if (!(selector.equals(SimplePseudoSelector.LINK))) {
      return allElements.root().createTemporaryChild();
    }
    return matchingElements;
  }

  private boolean matches(Element element) {
    // TODO: Instead do instanceof AnchorElement, once that's a thing
    boolean isMatchingElementType =
      element.name().equals("a")
      || element.name().equals("area");
    boolean hasHref = element.hasAttribute("href");
    return isMatchingElementType && hasHref;
  }
  
}
