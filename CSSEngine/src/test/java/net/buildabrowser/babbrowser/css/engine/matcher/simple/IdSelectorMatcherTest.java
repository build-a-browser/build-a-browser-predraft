package net.buildabrowser.babbrowser.css.engine.matcher.simple;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.css.engine.matcher.ElementRootSet;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.cssbase.selector.IdSelector;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Element;

public class IdSelectorMatcherTest {
  
  private ElementRootSet allElements;
  private IdSelectorMatcher matcher;

  @BeforeEach
  public void beforeEach() {
    this.allElements = ElementSet.createRoot();
    this.matcher = new IdSelectorMatcher(allElements);
  }

  @Test
  @DisplayName("Can match valid IDs")
  @SuppressWarnings("deprecation")
  public void canMatchValidIds() {
    Element element = Element.create("b", Document.create(matcher));
    allElements.add(element);
    IdSelector selector = IdSelector.create("a");
    matcher.addSelectorReference(selector);
    element.addAttribute("id", "a");
    matcher.onNodeAdded(element);
    Assertions.assertEquals(Set.of(element), matcher.match(selector).asSet());
  }

  @Test
  @DisplayName("Can not match invalid IDs")
  @SuppressWarnings("deprecation")
  public void canNotMatchInvalidIds() {
    Element element = Element.create("b", Document.create(matcher));
    allElements.add(element);
    IdSelector selector = IdSelector.create("c");
    matcher.addSelectorReference(selector);
    element.addAttribute("id", "a");
    matcher.onNodeAdded(element);
    Assertions.assertEquals(Set.of(), matcher.match(selector).asSet());
  }

}
