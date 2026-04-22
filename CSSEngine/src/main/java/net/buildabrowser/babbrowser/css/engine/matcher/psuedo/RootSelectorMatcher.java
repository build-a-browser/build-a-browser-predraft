package net.buildabrowser.babbrowser.css.engine.matcher.psuedo;

import java.util.function.Consumer;

import net.buildabrowser.babbrowser.css.engine.matcher.ElementRootSet;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.css.engine.matcher.simple.SimpleSelectorMatcher;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorPart;
import net.buildabrowser.babbrowser.cssbase.selector.SimplePsuedoSelector;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;

public class RootSelectorMatcher implements SimpleSelectorMatcher<SimplePsuedoSelector> {

  private final ElementSet allElements;
  private final ElementSet matchingElements;
  private final Consumer<SelectorPart> onSelectorChanged;

  public RootSelectorMatcher(
    ElementRootSet allElements,
    Consumer<SelectorPart> onSelectorChanged
  ) {
    this.allElements = allElements;
    this.matchingElements = allElements.createChild();
    this.onSelectorChanged = onSelectorChanged;
  }

  @Override
  public void addSelectorReference(SimplePsuedoSelector ref) {}

  @Override
  public void removeSelectorReference(SimplePsuedoSelector ref) {}

  @Override
  public void onNodeAdded(Node node) {
    if (!(node instanceof Element element)) return;
    if (matches(element)) {
      boolean changed = matchingElements.add(element);
      if (changed) {
        onSelectorChanged.accept(SimplePsuedoSelector.ROOT);
      }
    }
  }

  @Override
  public void onNodeRemoved(Node node) {
    if (!(node instanceof Element element)) return;
    boolean changed = matchingElements.remove(element);
    if (changed) {
      onSelectorChanged.accept(SimplePsuedoSelector.ROOT);
    }
  }

  @Override
  public ElementSet match(SimplePsuedoSelector selector) {
    if (!(selector.equals(SimplePsuedoSelector.ROOT))) {
      return allElements.root().createTemporaryChild();
    }
    return matchingElements;
  }

  private boolean matches(Element element) {
    // TODO: Instead do instanceof HTMLElement, once that's a thing
    return element.name().equals("html");
  }
  
}
