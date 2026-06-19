package net.buildabrowser.babbrowser.css.engine.matcher.pseudo;

import java.util.function.Consumer;

import net.buildabrowser.babbrowser.css.engine.matcher.CSSMatcher.CSSMatcherContext;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementRootSet;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorPart;
import net.buildabrowser.babbrowser.cssbase.selector.SimplePseudoSelector;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.events.Event;

public class FocusVisibleSelectorMatcher implements PseudoSelectorMatcher {

  private final ElementSet allElements;
  private final ElementSet matchingElements;
  private final Consumer<SelectorPart> onSelectorChanged;
  private final CSSMatcherContext matcherContext;

  public FocusVisibleSelectorMatcher(
    ElementRootSet allElements,
    Consumer<SelectorPart> onSelectorChanged,
    CSSMatcherContext matcherContext
  ) {
    this.allElements = allElements;
    this.matchingElements = allElements.createChild();
    this.onSelectorChanged = onSelectorChanged;
    this.matcherContext = matcherContext;
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
      onSelectorChanged.accept(SimplePseudoSelector.FOCUS_VISIBLE);
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
    if (!matcherContext.isFocusVisible(element)) {
      handleBlurEvent(element);
      return;
    }

    boolean changed = false;
    for (Element oldElement: matchingElements) {
      if (!oldElement.equals(element)) {
        changed |= matchingElements.remove(oldElement);
      }
    }

    changed |= matchingElements.add(element);

    if (changed) {
      onSelectorChanged.accept(SimplePseudoSelector.FOCUS_VISIBLE);
    }
  }

  private void handleBlurEvent(Element element) {
    boolean changed = matchingElements.remove(element);
    if (changed) {
      onSelectorChanged.accept(SimplePseudoSelector.FOCUS_VISIBLE);
    }
  }

  @Override
  public ElementSet match(SimplePseudoSelector selector) {
    if (!(selector.equals(SimplePseudoSelector.FOCUS_VISIBLE))) {
      return allElements.root().createTemporaryChild();
    }
    return matchingElements;
  }
  
}
