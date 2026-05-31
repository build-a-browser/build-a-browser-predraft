package net.buildabrowser.babbrowser.css.engine.matcher.psuedo;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.css.engine.matcher.ElementRootSet;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.cssbase.selector.SimplePsuedoSelector;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Element;

public class RootSelectorMatcherTest {
  
  private ElementRootSet allElements;
  private RootSelectorMatcher matcher;

  @BeforeEach
  public void beforeEach() {
    this.allElements = ElementSet.createRoot();
    this.matcher = new RootSelectorMatcher(allElements, _1 -> {});
  }

  @Test
  @DisplayName("Can match valid types")
  @SuppressWarnings("deprecation")
  public void canMatchValidTypes() {
    Element element = Element.create("html", Document.create(matcher));
    allElements.add(element);
    SimplePsuedoSelector selector = SimplePsuedoSelector.ROOT;
    matcher.onNodeAdded(element);
    matcher.addSelectorReference(selector);
    Assertions.assertEquals(Set.of(element), matcher.match(selector).asSet());
  }

  @Test
  @DisplayName("Can not match invalid types")
  @SuppressWarnings("deprecation")
  public void cannotMatchInvalidValidTypes() {
    Element element = Element.create("b", Document.create(matcher));
    allElements.add(element);
    SimplePsuedoSelector selector = SimplePsuedoSelector.ROOT;
    matcher.onNodeAdded(element);
    matcher.addSelectorReference(selector);
    Assertions.assertEquals(Set.of(), matcher.match(selector).asSet());
  }

}
