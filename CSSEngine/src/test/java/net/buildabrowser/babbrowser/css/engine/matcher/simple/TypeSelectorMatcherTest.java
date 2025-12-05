package net.buildabrowser.babbrowser.css.engine.matcher.simple;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.cssbase.selector.TypeSelector;
import net.buildabrowser.babbrowser.dom.mutable.MutableDocument;
import net.buildabrowser.babbrowser.dom.mutable.MutableElement;

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
    MutableElement element = MutableElement.create("a", MutableDocument.create(matcher));
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
    MutableElement element = MutableElement.create("b", MutableDocument.create(matcher));
    elementSet.add(element);
    TypeSelector selector = TypeSelector.create("a");
    matcher.onNodeAdded(element);
    matcher.addSelectorReference(selector);
    Assertions.assertEquals(Set.of(), matcher.match(selector).raw());
  }

}
