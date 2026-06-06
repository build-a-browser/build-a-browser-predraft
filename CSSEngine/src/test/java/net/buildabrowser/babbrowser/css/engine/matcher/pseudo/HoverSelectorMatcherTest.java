package net.buildabrowser.babbrowser.css.engine.matcher.pseudo;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.css.engine.matcher.ElementRootSet;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.cssbase.selector.SimplePseudoSelector;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.events.PointerEvent;

public class HoverSelectorMatcherTest {
  
  private ElementRootSet allElements;
  private HoverSelectorMatcher matcher;

  @BeforeEach
  public void beforeEach() {
    this.allElements = ElementSet.createRoot();
    this.matcher = new HoverSelectorMatcher(allElements, _1 -> {});
  }

  @Test
  @DisplayName("Can match hovered element")
  @SuppressWarnings("deprecation")
  public void canMatchHoveredElement() {
    Element element = Element.create("a", Document.create(matcher));
    allElements.add(element);
    SimplePseudoSelector selector = SimplePseudoSelector.HOVER;
    matcher.onNodeAdded(element);
    matcher.addSelectorReference(selector);
    matcher.onElementEvent(element, (PointerEvent) () -> "mousemove");
    Assertions.assertEquals(Set.of(element), matcher.match(selector).asSet());
  }

  @Test
  @DisplayName("Can not match not hovered element")
  @SuppressWarnings("deprecation")
  public void cannotMatchNotHoveredElement() {
    Element element = Element.create("a", Document.create(matcher));
    allElements.add(element);
    SimplePseudoSelector selector = SimplePseudoSelector.HOVER;
    matcher.onNodeAdded(element);
    matcher.addSelectorReference(selector);
    Assertions.assertEquals(Set.of(), matcher.match(selector).asSet());
  }

  @Test
  @DisplayName("Can switch hovered element")
  @SuppressWarnings("deprecation")
  public void canSwitchHoveredElement() {
    Element element1 = Element.create("a", Document.create(matcher));
    Element element2 = Element.create("b", Document.create(matcher));
    allElements.add(element1);
    allElements.add(element2);
    SimplePseudoSelector selector = SimplePseudoSelector.HOVER;
    matcher.onNodeAdded(element1);
    matcher.onNodeAdded(element2);
    matcher.addSelectorReference(selector);
    matcher.onElementEvent(element1, (PointerEvent) () -> "mousemove");
    matcher.onElementEvent(element2, (PointerEvent) () -> "mousemove");
    Assertions.assertEquals(Set.of(element2), matcher.match(selector).asSet());
  }

  @Test
  @DisplayName("Parent elements are affected by hover")
  @SuppressWarnings("deprecation")
  public void parentElementsAreAffectedByHover() {
    Element element1 = Element.create("a", Document.create(matcher));
    Element element2 = Element.create("b", element1);
    allElements.add(element1);
    allElements.add(element2);
    SimplePseudoSelector selector = SimplePseudoSelector.HOVER;
    matcher.onNodeAdded(element1);
    matcher.onNodeAdded(element2);
    matcher.addSelectorReference(selector);
    matcher.onElementEvent(element2, (PointerEvent) () -> "mousemove");
    Assertions.assertEquals(Set.of(element1, element2), matcher.match(selector).asSet());
  }

}
