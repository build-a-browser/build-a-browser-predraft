package net.buildabrowser.babbrowser.css.engine.matcher.simple;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.cssbase.selector.TypeSelector;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Element;

public class TypeSelectorMatcherTest {
  
  private ElementSet elementSet;
  private TypeSelectorMatcher matcher;

  @BeforeEach
  public void beforeEach() {
    this.elementSet = ElementSet.create();
    this.matcher = new TypeSelectorMatcher(elementSet);
  }

  @Test
  @DisplayName("Can match valid types")
  @SuppressWarnings("deprecation")
  public void canMatchValidTypes() {
    Element element = Element.create("a", Document.create(matcher));
    elementSet.add(element);
    TypeSelector selector = TypeSelector.create("a");
    matcher.onNodeAdded(element);
    matcher.addSelectorReference(selector);
    Assertions.assertEquals(Set.of(element), matcher.match(selector).raw());
  }

  @Test
  @DisplayName("Can not match invalid types")
  @SuppressWarnings("deprecation")
  public void cannotMatchInvalidValidTypes() {
    Element element = Element.create("b", Document.create(matcher));
    elementSet.add(element);
    TypeSelector selector = TypeSelector.create("a");
    matcher.onNodeAdded(element);
    matcher.addSelectorReference(selector);
    Assertions.assertEquals(Set.of(), matcher.match(selector).raw());
  }

}
