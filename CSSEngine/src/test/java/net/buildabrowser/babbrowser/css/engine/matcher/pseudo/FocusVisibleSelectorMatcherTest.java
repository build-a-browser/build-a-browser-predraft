package net.buildabrowser.babbrowser.css.engine.matcher.pseudo;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.css.engine.matcher.CSSMatcher.CSSMatcherContext;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementRootSet;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.cssbase.selector.SimplePseudoSelector;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.events.FocusEvent;

public class FocusVisibleSelectorMatcherTest {

  private ElementRootSet allElements;
  private FocusVisibleSelectorMatcher matcher;

  @BeforeEach
  public void beforeEach() {
    this.allElements = ElementSet.createRoot();
    this.matcher = new FocusVisibleSelectorMatcher(
      allElements,
      _1 -> {},
      new CSSMatcherContext() {
        
        @Override
        public boolean isFocusVisible(Element element) {
          return !element.name().startsWith("hide-");
        }

      });
  }

  @Test
  @DisplayName("Can match visible-focused element")
  @SuppressWarnings("deprecation")
  public void canMatchVisibleFocusedElement() {
    Element element = Element.create("a", Document.create(matcher));
    allElements.add(element);
    SimplePseudoSelector selector = SimplePseudoSelector.FOCUS_VISIBLE;
    matcher.onNodeAdded(element);
    matcher.addSelectorReference(selector);
    matcher.onElementEvent(
      element, FocusEvent.create("focus"), false);
    Assertions.assertEquals(Set.of(element), matcher.match(selector).asSet());
  }

  @Test
  @DisplayName("Can not match not-visible-focused element")
  @SuppressWarnings("deprecation")
  public void canNotMatchNotVisibleFocusedElement() {
    Element element = Element.create("hide-a", Document.create(matcher));
    allElements.add(element);
    SimplePseudoSelector selector = SimplePseudoSelector.FOCUS_VISIBLE;
    matcher.onNodeAdded(element);
    matcher.addSelectorReference(selector);
    matcher.onElementEvent(
      element, FocusEvent.create("focus"), false);
    Assertions.assertEquals(Set.of(), matcher.match(selector).asSet());
  }

  @Test
  @DisplayName("Can not match unfocused element")
  @SuppressWarnings("deprecation")
  public void cannotMatchUnfocusedElement() {
    Element element = Element.create("a", Document.create(matcher));
    allElements.add(element);
    SimplePseudoSelector selector = SimplePseudoSelector.FOCUS_VISIBLE;
    matcher.onNodeAdded(element);
    matcher.addSelectorReference(selector);
    matcher.onElementEvent(
      element, FocusEvent.create("focus"), false);
    matcher.onElementEvent(
      element, FocusEvent.create("blur"), false);
    Assertions.assertEquals(Set.of(), matcher.match(selector).asSet());
  }

  @Test
  @DisplayName("Parent elements are not affected by focus")
  @SuppressWarnings("deprecation")
  public void parentElementsAreNotAffectedByFocus() {
    Element element1 = Element.create("a", Document.create(matcher));
    Element element2 = Element.create("b", element1);
    allElements.add(element1);
    allElements.add(element2);
    SimplePseudoSelector selector = SimplePseudoSelector.FOCUS_VISIBLE;
    matcher.onNodeAdded(element1);
    matcher.onNodeAdded(element2);
    matcher.addSelectorReference(selector);
    matcher.onElementEvent(
      element2, FocusEvent.create("focus"), false);
    Assertions.assertEquals(Set.of(element2), matcher.match(selector).asSet());
  }
  
}
