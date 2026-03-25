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

public class AttributeSelectorMatcherTest {
  
  private ElementSet elementSet;
  private AttributeSelectorMatcher matcher;

  @BeforeEach
  public void beforeEach() {
    this.elementSet = ElementSet.create();
    this.matcher = new AttributeSelectorMatcher(elementSet);
  }

  @Test
  @DisplayName("Can match valid has-attr attributes")
  @SuppressWarnings("deprecation")
  public void canMatchValidHasAttrAttributes() {
    MutableElement element = MutableElement.create("a", MutableDocument.createForTesting(matcher));
    elementSet.add(element);
    AttributeSelector selector = AttributeSelector.create(
      "text", "", AttributeType.HAS_ATTR);
    matcher.onNodeAdded(element);
    matcher.addSelectorReference(selector);
    element.addAttribute("text", "");
    Assertions.assertEquals(Set.of(element), matcher.match(selector).raw());
  }

  @Test
  @DisplayName("Can not match invalid has-attr attributes")
  @SuppressWarnings("deprecation")
  public void cannotMatchInvalidValidHasAttrAttributes() {
    MutableElement element = MutableElement.create("a", MutableDocument.createForTesting(matcher));
    elementSet.add(element);
    AttributeSelector selector = AttributeSelector.create(
      "text", "", AttributeType.HAS_ATTR);
    matcher.onNodeAdded(element);
    matcher.addSelectorReference(selector);
    Assertions.assertEquals(Set.of(), matcher.match(selector).raw());
  }

  @Test
  @DisplayName("Can match valid exactly attributes")
  @SuppressWarnings("deprecation")
  public void canMatchValidExactlyAttributes() {
    MutableElement element = MutableElement.create("a", MutableDocument.createForTesting(matcher));
    elementSet.add(element);
    AttributeSelector selector = AttributeSelector.create(
      "text", "mytext", AttributeType.EXACTLY);
    matcher.onNodeAdded(element);
    matcher.addSelectorReference(selector);
    element.addAttribute("text", "mytext");
    Assertions.assertEquals(Set.of(element), matcher.match(selector).raw());
  }

  @Test
  @DisplayName("Can not match invalid exactly attributes")
  @SuppressWarnings("deprecation")
  public void cannotMatchInvalidExactlyAttributes() {
    MutableElement element = MutableElement.create("a", MutableDocument.createForTesting(matcher));
    elementSet.add(element);
    AttributeSelector selector = AttributeSelector.create(
      "text", "mytext", AttributeType.EXACTLY);
    matcher.onNodeAdded(element);
    matcher.addSelectorReference(selector);
    element.addAttribute("text", "mytexts");
    Assertions.assertEquals(Set.of(), matcher.match(selector).raw());
  }

  @Test
  @DisplayName("Can match valid prefix attributes")
  @SuppressWarnings("deprecation")
  public void canMatchValidPrefixAttributes() {
    MutableElement element = MutableElement.create("a", MutableDocument.createForTesting(matcher));
    elementSet.add(element);
    AttributeSelector selector = AttributeSelector.create(
      "text", "jordan", AttributeType.PREFIX);
    matcher.onNodeAdded(element);
    matcher.addSelectorReference(selector);
    element.addAttribute("text", "jordan-jon");
    Assertions.assertEquals(Set.of(element), matcher.match(selector).raw());
  }

  @Test
  @DisplayName("Can not match invalid prefix attributes")
  @SuppressWarnings("deprecation")
  public void cannotMatchInvalidPrefixAttributes() {
    MutableElement element = MutableElement.create("a", MutableDocument.createForTesting(matcher));
    elementSet.add(element);
    AttributeSelector selector = AttributeSelector.create(
      "text", "jordan", AttributeType.PREFIX);
    matcher.onNodeAdded(element);
    matcher.addSelectorReference(selector);
    element.addAttribute("text", "jordanjon");
    Assertions.assertEquals(Set.of(), matcher.match(selector).raw());
  }

  @Test
  @DisplayName("Can match valid starts-with attributes")
  @SuppressWarnings("deprecation")
  public void canMatchValidStartsWithAttributes() {
    MutableElement element = MutableElement.create("a", MutableDocument.createForTesting(matcher));
    elementSet.add(element);
    AttributeSelector selector = AttributeSelector.create(
      "text", "jordan", AttributeType.STARTS_WITH);
    matcher.onNodeAdded(element);
    matcher.addSelectorReference(selector);
    element.addAttribute("text", "jordanjon");
    Assertions.assertEquals(Set.of(element), matcher.match(selector).raw());
  }

  @Test
  @DisplayName("Can not match invalid starts-with attributes")
  @SuppressWarnings("deprecation")
  public void cannotMatchInvalidStartsWithAttributes() {
    MutableElement element = MutableElement.create("a", MutableDocument.createForTesting(matcher));
    elementSet.add(element);
    AttributeSelector selector = AttributeSelector.create(
      "text", "jordan", AttributeType.STARTS_WITH);
    matcher.onNodeAdded(element);
    matcher.addSelectorReference(selector);
    element.addAttribute("text", "ordanjon");
    Assertions.assertEquals(Set.of(), matcher.match(selector).raw());
  }

  @Test
  @DisplayName("Can match valid ends-with attributes")
  @SuppressWarnings("deprecation")
  public void canMatchValidEndsWithAttributes() {
    MutableElement element = MutableElement.create("a", MutableDocument.createForTesting(matcher));
    elementSet.add(element);
    AttributeSelector selector = AttributeSelector.create(
      "text", "jon", AttributeType.ENDS_WITH);
    matcher.onNodeAdded(element);
    matcher.addSelectorReference(selector);
    element.addAttribute("text", "jordanjon");
    Assertions.assertEquals(Set.of(element), matcher.match(selector).raw());
  }

  @Test
  @DisplayName("Can not match valid ends-with attributes")
  @SuppressWarnings("deprecation")
  public void cannotMatchInvalidEndsWithAttributes() {
    MutableElement element = MutableElement.create("a", MutableDocument.createForTesting(matcher));
    elementSet.add(element);
    AttributeSelector selector = AttributeSelector.create(
      "text", "jon", AttributeType.ENDS_WITH);
    matcher.onNodeAdded(element);
    matcher.addSelectorReference(selector);
    element.addAttribute("text", "jordanjo");
    Assertions.assertEquals(Set.of(), matcher.match(selector).raw());
  }

  @Test
  @DisplayName("Can match valid contains attributes")
  @SuppressWarnings("deprecation")
  public void canMatchValidContainsAttributes() {
    MutableElement element = MutableElement.create("a", MutableDocument.createForTesting(matcher));
    elementSet.add(element);
    AttributeSelector selector = AttributeSelector.create(
      "text", "onati", AttributeType.CONTAINS);
    matcher.onNodeAdded(element);
    matcher.addSelectorReference(selector);
    element.addAttribute("text", "onceuponatime");
    Assertions.assertEquals(Set.of(element), matcher.match(selector).raw());
  }

  @Test
  @DisplayName("Can not match invalid contains attributes")
  @SuppressWarnings("deprecation")
  public void cannotMatchInvalidContainsAttributes() {
    MutableElement element = MutableElement.create("a", MutableDocument.createForTesting(matcher));
    elementSet.add(element);
    AttributeSelector selector = AttributeSelector.create(
      "text", "onati", AttributeType.CONTAINS);
    matcher.onNodeAdded(element);
    matcher.addSelectorReference(selector);
    element.addAttribute("text", "onceuponabadtime");
    Assertions.assertEquals(Set.of(), matcher.match(selector).raw());
  }

}
