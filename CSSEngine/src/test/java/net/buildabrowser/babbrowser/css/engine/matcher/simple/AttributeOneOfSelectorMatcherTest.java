package net.buildabrowser.babbrowser.css.engine.matcher.simple;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.cssbase.selector.AttributeSelector;
import net.buildabrowser.babbrowser.cssbase.selector.AttributeSelector.AttributeType;
import net.buildabrowser.babbrowser.dom.mutable.MutableDocument;
import net.buildabrowser.babbrowser.dom.mutable.MutableElement;

public class AttributeOneOfSelectorMatcherTest {
  
  private ElementSet elementSet;
  private AttributeOneOfSelectorMatcher matcher;

  @BeforeEach
  public void beforeEach() {
    this.elementSet = ElementSet.create();
    this.matcher = new AttributeOneOfSelectorMatcher(elementSet);
  }

  @Test
  @DisplayName("Can match valid attributes")
  @SuppressWarnings("deprecation")
  public void canMatchValidAttributes() {
    MutableElement element = MutableElement.create("a", MutableDocument.create(matcher));
    elementSet.add(element);
    AttributeSelector selector = AttributeSelector.create(
      "class", "john", AttributeType.ONE_OF);
    matcher.onNodeAdded(element);
    matcher.addSelectorReference(selector);
    element.addAttribute("class", "adam john avery");
    Assertions.assertEquals(Set.of(element), matcher.match(selector).raw());
  }

  @Test
  @DisplayName("Can not match invalid attributes")
  @SuppressWarnings("deprecation")
  public void cannotMatchInvalidValidAttributes() {
    MutableElement element = MutableElement.create("b", MutableDocument.create(matcher));
    elementSet.add(element);
    AttributeSelector selector = AttributeSelector.create(
      "class", "john", AttributeType.ONE_OF);
    matcher.onNodeAdded(element);
    matcher.addSelectorReference(selector);
    Assertions.assertEquals(Set.of(), matcher.match(selector).raw());
  }

}
