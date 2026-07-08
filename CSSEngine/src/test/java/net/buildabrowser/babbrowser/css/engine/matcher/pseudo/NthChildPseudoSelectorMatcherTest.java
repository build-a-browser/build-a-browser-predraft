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
import net.buildabrowser.babbrowser.cssbase.microsyntax.ANPlusB;
import net.buildabrowser.babbrowser.cssbase.selector.ComplexSelector;
import net.buildabrowser.babbrowser.cssbase.selector.NthChildPseudoSelector;
import net.buildabrowser.babbrowser.cssbase.selector.NthChildPseudoSelector.NthChildPseudoSelectorType;
import net.buildabrowser.babbrowser.cssbase.selector.TypeSelector;
import net.buildabrowser.babbrowser.cssbase.selector.UniversalSelector;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Element;

public class NthChildPseudoSelectorMatcherTest {

  private ElementRootSet allElements;
  private CSSSelectorMatcher selectorMatcher;
  private NthChildPseudoSelectorMatcher matcher;

  @BeforeEach
  public void beforeEach() {
    this.allElements = ElementSet.createRoot();
    this.selectorMatcher = new CSSSelectorMatcher(
      allElements, new CSSMatcherContext() {}, _1 -> {});
    this.matcher = selectorMatcher.childPseudoMatchers();
  }

  @Test
  @DisplayName("Can match elements with :first-child")
  @SuppressWarnings("deprecation")
  public void canMatchElementsWithFirstChild() {
    Document document = Document.create(
      selectorMatcher.documentChangeListener());
    Element firstElement = Element.create("div", document);
    Element secondElement = Element.create("div", document);
    Element thirdElement = Element.create("div", document);

    document.appendChild(firstElement);
    document.appendChild(secondElement);
    document.appendChild(thirdElement);

    allElements.add(firstElement);
    allElements.add(secondElement);
    allElements.add(thirdElement);

    NthChildPseudoSelector selector = new NthChildPseudoSelector(
      NthChildPseudoSelectorType.NTH,
      new ANPlusB(0, 1),
      UniversalSelector.AS_COMPLEX_SELECTOR);

    matcher.addSelectorReference(selector);

    Assertions.assertEquals(
      Set.of(firstElement),
      matcher.match(selector).asSet());
  }

  @Test
  @DisplayName("Can match elements with :last-child")
  @SuppressWarnings("deprecation")
  public void canMatchElementsWithLastChild() {
    Document document = Document.create(
      selectorMatcher.documentChangeListener());
    Element firstElement = Element.create("div", document);
    Element secondElement = Element.create("div", document);
    Element thirdElement = Element.create("div", document);

    document.appendChild(firstElement);
    document.appendChild(secondElement);
    document.appendChild(thirdElement);

    allElements.add(firstElement);
    allElements.add(secondElement);
    allElements.add(thirdElement);

    NthChildPseudoSelector selector = new NthChildPseudoSelector(
      NthChildPseudoSelectorType.NTH_LAST,
      new ANPlusB(0, 1),
      UniversalSelector.AS_COMPLEX_SELECTOR);

    matcher.addSelectorReference(selector);

    Assertions.assertEquals(
      Set.of(thirdElement),
      matcher.match(selector).asSet());
  }

  @Test
  @DisplayName("Can match elements with :only-child")
  @SuppressWarnings("deprecation")
  public void canMatchElementsWithOnlyChild() {
    Document document = Document.create(
      selectorMatcher.documentChangeListener());
    Element onlyElement = Element.create("div", document);

    document.appendChild(onlyElement);

    allElements.add(onlyElement);

    NthChildPseudoSelector selector = new NthChildPseudoSelector(
      NthChildPseudoSelectorType.ONLY_CHILD,
      null,
      UniversalSelector.AS_COMPLEX_SELECTOR);

    matcher.addSelectorReference(selector);

    Assertions.assertEquals(
      Set.of(onlyElement),
      matcher.match(selector).asSet());
  }

  @Test
  @DisplayName("Can match elements with :nth-child(even)")
  @SuppressWarnings("deprecation")
  public void canMatchElementsWithNthChildEven() {
    Document document = Document.create(
      selectorMatcher.documentChangeListener());
    Element firstElement = Element.create("div", document);
    Element secondElement = Element.create("div", document);
    Element thirdElement = Element.create("div", document);
    Element fourthElement = Element.create("div", document);

    document.appendChild(firstElement);
    document.appendChild(secondElement);
    document.appendChild(thirdElement);
    document.appendChild(fourthElement);

    allElements.add(firstElement);
    allElements.add(secondElement);
    allElements.add(thirdElement);
    allElements.add(fourthElement);

    NthChildPseudoSelector selector = new NthChildPseudoSelector(
      NthChildPseudoSelectorType.NTH,
      new ANPlusB(2, 0),
      UniversalSelector.AS_COMPLEX_SELECTOR);

    matcher.addSelectorReference(selector);

    Assertions.assertEquals(
      Set.of(secondElement, fourthElement),
      matcher.match(selector).asSet());
  }

  @Test
  @DisplayName("Can match elements with :nth-last-child(-n+2)")
  @SuppressWarnings("deprecation")
  public void canMatchElementsWithNthLastChildNegativeAnPlusB() {
    Document document = Document.create(
      selectorMatcher.documentChangeListener());
    Element firstElement = Element.create("div", document);
    Element secondElement = Element.create("div", document);
    Element thirdElement = Element.create("div", document);
    Element fourthElement = Element.create("div", document);
    Element fifthElement = Element.create("div", document);

    document.appendChild(firstElement);
    document.appendChild(secondElement);
    document.appendChild(thirdElement);
    document.appendChild(fourthElement);
    document.appendChild(fifthElement);

    allElements.add(firstElement);
    allElements.add(secondElement);
    allElements.add(thirdElement);
    allElements.add(fourthElement);
    allElements.add(fifthElement);

    NthChildPseudoSelector selector = new NthChildPseudoSelector(
      NthChildPseudoSelectorType.NTH_LAST,
      new ANPlusB(-1, 2),
      UniversalSelector.AS_COMPLEX_SELECTOR);

    matcher.addSelectorReference(selector);

    Assertions.assertEquals(
      Set.of(fourthElement, fifthElement),
      matcher.match(selector).asSet());
  }

  @Test
  @DisplayName("Can match elements with :nth-child(B)")
  @SuppressWarnings("deprecation")
  public void canMatchElementsWithNthChildExactIndex() {
    Document document = Document.create(
      selectorMatcher.documentChangeListener());
    Element firstElement = Element.create("div", document);
    Element secondElement = Element.create("div", document);
    Element thirdElement = Element.create("div", document);
    Element fourthElement = Element.create("div", document);
    Element fifthElement = Element.create("div", document);

    document.appendChild(firstElement);
    document.appendChild(secondElement);
    document.appendChild(thirdElement);
    document.appendChild(fourthElement);
    document.appendChild(fifthElement);

    allElements.add(firstElement);
    allElements.add(secondElement);
    allElements.add(thirdElement);
    allElements.add(fourthElement);
    allElements.add(fifthElement);

    NthChildPseudoSelector selector = new NthChildPseudoSelector(
      NthChildPseudoSelectorType.NTH,
      new ANPlusB(0, 5),
      UniversalSelector.AS_COMPLEX_SELECTOR);

    matcher.addSelectorReference(selector);

    Assertions.assertEquals(
      Set.of(fifthElement),
      matcher.match(selector).asSet());
  }

  @Test
  @DisplayName("Can match elements with :nth-child(1 of span)")
  @SuppressWarnings("deprecation")
  public void canMatchElementsWithNthChildOfSelector() {
    Document document = Document.create(
      selectorMatcher.documentChangeListener());
    Element firstElement = Element.create("div", document);
    Element secondElement = Element.create("span", document);
    Element thirdElement = Element.create("span", document);
    Element fourthElement = Element.create("div", document);

    document.appendChild(firstElement);
    document.appendChild(secondElement);
    document.appendChild(thirdElement);
    document.appendChild(fourthElement);

    allElements.add(firstElement);
    allElements.add(secondElement);
    allElements.add(thirdElement);
    allElements.add(fourthElement);

    NthChildPseudoSelector selector = new NthChildPseudoSelector(
      NthChildPseudoSelectorType.NTH,
      new ANPlusB(0, 1),
      new ComplexSelector(List.of(TypeSelector.create("span"))));

    matcher.addSelectorReference(selector);

    Assertions.assertEquals(
      Set.of(secondElement),
      matcher.match(selector).asSet());
  }

}