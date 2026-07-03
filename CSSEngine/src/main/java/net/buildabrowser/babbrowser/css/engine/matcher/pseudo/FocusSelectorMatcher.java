package net.buildabrowser.babbrowser.css.engine.matcher.pseudo;

import java.util.function.Consumer;

import net.buildabrowser.babbrowser.css.engine.matcher.ElementRootSet;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorPart;
import net.buildabrowser.babbrowser.cssbase.selector.SimplePseudoSelector;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.events.Event;

public class FocusSelectorMatcher implements SimplePseudoSelectorMatcher {

  private final ElementSet allElements;
  private final ElementSet matchingElements;
  private final Consumer<SelectorPart> onSelectorChanged;

  public FocusSelectorMatcher(
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
  public void onNodeRemoved(Node node) {
    if (!(node instanceof Element element)) return;
    boolean changed = matchingElements.remove(element);
    if (changed) {
      onSelectorChanged.accept(SimplePseudoSelector.FOCUS);
    }
  }

  @Override
  public void onElementEvent(Element element, Event event) {
    switch (event.type()) {
      case "focus" -> handleFocusEvent(element);
      case "blur" -> handleBlurEvent(element);
      default -> {}
    }
  }

  private void handleFocusEvent(Element element) {
    boolean changed = false;
    for (Element oldElement: matchingElements) {
      if (!oldElement.equals(element)) {
        changed |= matchingElements.remove(oldElement);
      }
    }

    changed |= matchingElements.add(element);

    if (changed) {
      onSelectorChanged.accept(SimplePseudoSelector.FOCUS);
    }
  }

  private void handleBlurEvent(Element element) {
    boolean changed = matchingElements.remove(element);
    if (changed) {
      onSelectorChanged.accept(SimplePseudoSelector.FOCUS);
    }
  }

  @Override
  public ElementSet match(SimplePseudoSelector selector) {
    if (!(selector.equals(SimplePseudoSelector.FOCUS))) {
      return allElements.root().createTemporaryChild();
    }
    return matchingElements;
  }
  
}
