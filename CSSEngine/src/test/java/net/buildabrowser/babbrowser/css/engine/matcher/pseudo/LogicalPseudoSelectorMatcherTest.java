package net.buildabrowser.babbrowser.css.engine.matcher.pseudo;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.css.engine.matcher.CSSMatcher.CSSMatcherContext;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementRootSet;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.css.engine.matcher.imp.CSSSelectorMatcher;
import net.buildabrowser.babbrowser.cssbase.selector.ComplexSelector;
import net.buildabrowser.babbrowser.cssbase.selector.DescendantCombinator;
import net.buildabrowser.babbrowser.cssbase.selector.IdSelector;
import net.buildabrowser.babbrowser.cssbase.selector.LogicalPseudoSelector;
import net.buildabrowser.babbrowser.cssbase.selector.LogicalPseudoSelector.LogicalPseudoSelectorType;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorSpecificity;
import net.buildabrowser.babbrowser.cssbase.selector.TypeSelector;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Element;

public class LogicalPseudoSelectorMatcherTest {
  
  private ElementRootSet allElements;
  private CSSSelectorMatcher selectorMatcher;
  private LogicalPseudoSelectorMatcher matcher;

  @BeforeEach
  public void beforeEach() {
    this.allElements = ElementSet.createRoot();
    this.selectorMatcher = new CSSSelectorMatcher(
      allElements, new CSSMatcherContext() {}, _1 -> {});
    this.matcher = selectorMatcher.logicalPseudoMatchers();
  }

  @Test
  @DisplayName("Can match elements with :is")
  @SuppressWarnings("deprecation")
  public void canMatchElementsWithIs() {
    Document document = Document.create(
      selectorMatcher.documentChangeListener());
    Element firstElement = Element.create("a", document);
    Element secondElement = Element.create("b", document);
    Element thirdElement = Element.create("i", document);

    allElements.add(firstElement);
    allElements.add(secondElement);
    allElements.add(thirdElement);

    LogicalPseudoSelector selector = new LogicalPseudoSelector(
      LogicalPseudoSelectorType.IS, 
      List.of(
        new ComplexSelector(List.of(TypeSelector.create("a"))),
        new ComplexSelector(List.of(TypeSelector.create("b")))));
    
    matcher.addSelectorReference(selector);

    Assertions.assertEquals(
      Set.of(firstElement, secondElement),
      matcher.match(selector).asSet());
  }

  @Test
  @DisplayName("Can match elements with :not")
  @SuppressWarnings("deprecation")
  public void canMatchElementsWithNot() {
    Document document = Document.create(
      selectorMatcher.documentChangeListener());
    Element firstElement = Element.create("a", document);
    Element secondElement = Element.create("b", document);
    Element thirdElement = Element.create("i", document);

    allElements.add(firstElement);
    allElements.add(secondElement);
    allElements.add(thirdElement);

    LogicalPseudoSelector selector = new LogicalPseudoSelector(
      LogicalPseudoSelectorType.NOT, 
      List.of(
        new ComplexSelector(List.of(TypeSelector.create("a"))),
        new ComplexSelector(List.of(TypeSelector.create("b")))));
    
    matcher.addSelectorReference(selector);

    Assertions.assertEquals(
      Set.of(thirdElement),
      matcher.match(selector).asSet());
  }

  @Test
  @DisplayName("Can match elements with :has")
  @SuppressWarnings("deprecation")
  public void canMatchElementsWithHas() {
    Document document = Document.create(
      selectorMatcher.documentChangeListener());
    Element firstElement = Element.create("a", document);
    Element nestedElement = Element.create("s", document);
    firstElement.appendChild(nestedElement);
    Element secondElement = Element.create("b", document);
    Element thirdElement = Element.create("i", document);

    allElements.add(firstElement);
    allElements.add(nestedElement);
    allElements.add(secondElement);
    allElements.add(thirdElement);

    LogicalPseudoSelector selector = new LogicalPseudoSelector(
      LogicalPseudoSelectorType.HAS, 
      List.of(
        new ComplexSelector(List.of(
          DescendantCombinator.create(),
          TypeSelector.create("s")))));
    
    matcher.addSelectorReference(selector);

    Assertions.assertEquals(
      Set.of(firstElement),
      matcher.match(selector).asSet());
  }

  @Test
  @DisplayName("Can determine specificty of :is")
  public void canDetermineSpecificityOfIs() {
    LogicalPseudoSelector selector = new LogicalPseudoSelector(
      LogicalPseudoSelectorType.IS, 
      List.of(
        new ComplexSelector(List.of(TypeSelector.create("a"))),
        new ComplexSelector(List.of(IdSelector.create("a")))));

    Assertions.assertEquals(
      new SelectorSpecificity(1, 0, 0),
      matcher.specificity(selector));
  }

  @Test
  @DisplayName("Can determine specificty of :where")
  public void canDetermineSpecificityOfWhere() {
    LogicalPseudoSelector selector = new LogicalPseudoSelector(
      LogicalPseudoSelectorType.WHERE, 
      List.of(
        new ComplexSelector(List.of(IdSelector.create("a")))));

    Assertions.assertEquals(
      new SelectorSpecificity(0, 0, 0),
      matcher.specificity(selector));
  }

}
