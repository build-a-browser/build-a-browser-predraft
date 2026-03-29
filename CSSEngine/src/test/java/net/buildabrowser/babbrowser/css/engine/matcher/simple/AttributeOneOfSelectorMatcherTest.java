package net.buildabrowser.babbrowser.css.engine.matcher.simple;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.css.engine.matcher.ElementRootSet;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.cssbase.selector.AttributeSelector;
import net.buildabrowser.babbrowser.cssbase.selector.AttributeSelector.AttributeType;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Element;

public class AttributeOneOfSelectorMatcherTest {
  
  private ElementRootSet allElements;
  private AttributeOneOfSelectorMatcher matcher;

  @BeforeEach
  public void beforeEach() {
    this.allElements = ElementSet.createRoot();
    this.matcher = new AttributeOneOfSelectorMatcher(allElements);
  }

  @Test
  @DisplayName("Can match valid attributes")
  @SuppressWarnings("deprecation")
  public void canMatchValidAttributes() {
    Element element = Element.create("a", Document.create(matcher));
    allElements.add(element);
    AttributeSelector selector = AttributeSelector.create(
      "class", "john", AttributeType.ONE_OF);
    matcher.onNodeAdded(element);
    matcher.addSelectorReference(selector);
    element.addAttribute("class", "adam john avery");
    Assertions.assertEquals(Set.of(element), matcher.match(selector).asSet());
  }

  @Test
  @DisplayName("Can not match invalid attributes")
  @SuppressWarnings("deprecation")
  public void cannotMatchInvalidValidAttributes() {
    Element element = Element.create("b", Document.create(matcher));
    allElements.add(element);
    AttributeSelector selector = AttributeSelector.create(
      "class", "john", AttributeType.ONE_OF);
    matcher.onNodeAdded(element);
    matcher.addSelectorReference(selector);
    Assertions.assertEquals(Set.of(), matcher.match(selector).asSet());
  }

}
