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

public class LinkSelectorMatcherTest {
  
  private ElementRootSet allElements;
  private LinkSelectorMatcher matcher;

  @BeforeEach
  public void beforeEach() {
    this.allElements = ElementSet.createRoot();
    this.matcher = new LinkSelectorMatcher(allElements, _1 -> {});
  }

  @Test
  @DisplayName("Can match valid links")
  @SuppressWarnings("deprecation")
  public void canMatchValidLinks() {
    Element element = Element.create("a", Document.create(matcher));
    element.addAttribute("href", "#");
    allElements.add(element);
    SimplePseudoSelector selector = SimplePseudoSelector.LINK;
    matcher.onNodeAdded(element);
    matcher.addSelectorReference(selector);
    Assertions.assertEquals(Set.of(element), matcher.match(selector).asSet());
  }

  @Test
  @DisplayName("Can not match non-links")
  @SuppressWarnings("deprecation")
  public void cannotMatchNonLink() {
    Element element = Element.create("b", Document.create(matcher));
    allElements.add(element);
    SimplePseudoSelector selector = SimplePseudoSelector.LINK;
    matcher.onNodeAdded(element);
    matcher.addSelectorReference(selector);
    Assertions.assertEquals(Set.of(), matcher.match(selector).asSet());
  }

  @Test
  @DisplayName("Can not match link without HREF")
  @SuppressWarnings("deprecation")
  public void cannotMatchLinkWithoutHref() {
    Element element = Element.create("a", Document.create(matcher));
    allElements.add(element);
    SimplePseudoSelector selector = SimplePseudoSelector.LINK;
    matcher.onNodeAdded(element);
    matcher.addSelectorReference(selector);
    Assertions.assertEquals(Set.of(), matcher.match(selector).asSet());
  }

}
