package net.buildabrowser.babbrowser.css.engine.matcher.simple;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.selector.IdSelector;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Element;

public class IdSelectorMatcherTest {
  
  private IdSelectorMatcher matcher;

  @BeforeEach
  public void beforeEach() {
    this.matcher = new IdSelectorMatcher();
  }

  @Test
  @DisplayName("Can match valid IDs")
  @SuppressWarnings("deprecation")
  public void canMatchValidIds() {
    Element element = Element.create("b", Document.create(matcher));
    IdSelector selector = IdSelector.create("a");
    matcher.addSelectorReference(selector);
    element.addAttribute("id", "a");
    matcher.onNodeAdded(element);
    Assertions.assertEquals(Set.of(element), matcher.match(selector).raw());
  }

  @Test
  @DisplayName("Can not match invalid IDs")
  @SuppressWarnings("deprecation")
  public void canNotMatchInvalidIds() {
    Element element = Element.create("b", Document.create(matcher));
    IdSelector selector = IdSelector.create("c");
    matcher.addSelectorReference(selector);
    element.addAttribute("id", "a");
    matcher.onNodeAdded(element);
    Assertions.assertEquals(Set.of(), matcher.match(selector).raw());
  }

}
