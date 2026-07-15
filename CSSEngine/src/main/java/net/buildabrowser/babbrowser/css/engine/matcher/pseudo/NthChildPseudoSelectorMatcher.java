package net.buildabrowser.babbrowser.css.engine.matcher.pseudo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import net.buildabrowser.babbrowser.css.engine.matcher.ElementRootSet;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.css.engine.matcher.imp.CSSSelectorMatcher;
import net.buildabrowser.babbrowser.css.engine.matcher.util.RefCounted;
import net.buildabrowser.babbrowser.cssbase.microsyntax.ANPlusB;
import net.buildabrowser.babbrowser.cssbase.selector.NthChildPseudoSelector;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorPart;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;

public class NthChildPseudoSelectorMatcher implements DocumentChangeListener {
  
  private final ElementRootSet allElements;
  private final CSSSelectorMatcher selectorMatcher;
  private final Consumer<SelectorPart> onSelectorChanged;
  private final Map<NthChildPseudoSelector, RefCounted<ElementSet>> matchingElements;
  private final ElementSet queuedElements;

  private boolean hasItemsInQueue = false;

  public NthChildPseudoSelectorMatcher(
    ElementRootSet allElements,
    CSSSelectorMatcher selectorMatcher,
    Consumer<SelectorPart> onSelectorChanged
  ) {
    this.allElements = allElements;
    this.selectorMatcher = selectorMatcher;
    this.onSelectorChanged = onSelectorChanged;
    this.matchingElements = new HashMap<>();
    this.queuedElements = allElements.createChild();
  }

  public void addSelectorReference(NthChildPseudoSelector ref) {
    RefCounted<ElementSet> setRef = matchingElements
      .computeIfAbsent(ref, _1 -> RefCounted.create(allElements.createChild()));
    boolean didExist = setRef.isReferenced();
    setRef.incRefCount();
    selectorMatcher.registerSelector(ref.selector());
    if (!didExist) onSelectorChanged.accept(ref);
  }

  public void removeSelectorReference(NthChildPseudoSelector ref) {
    RefCounted<ElementSet> counter = matchingElements.get(ref);
    if (counter == null) return;

    counter.decRefCount();
    selectorMatcher.unregisterSelector(ref.selector());
    if (!counter.isReferenced()) {
      matchingElements.remove(ref);
    }

    onSelectorChanged.accept(ref);
  }

  @Override
  public void onNodeAdded(Node node) {
    if (!(node instanceof Element element)) return;
    boolean changed = queuedElements.add(element);
    if (changed && !hasItemsInQueue) {
      invalidateSelectors();
    }
    this.hasItemsInQueue = true;
  }

  @Override
  public void onNodeRemoved(Node node) {
    if (!(node instanceof Element element)) return;
    queuedElements.remove(element);
    for (
      Map.Entry<NthChildPseudoSelector, RefCounted<ElementSet>> entry:
      matchingElements.entrySet()
    ) {
      entry.getValue().object().remove(element);
      onSelectorChanged.accept(entry.getKey());
    }
  }

  public ElementSet match(NthChildPseudoSelector selector) {
    processQueuedItems();
    RefCounted<ElementSet> set = matchingElements.get(selector);
    if (set != null) return set.object();
    return allElements.createTemporaryChild();
  }

  // TODO: This is probably not the most efficient way to do this
  private void processQueuedItems() {
    if (!hasItemsInQueue) return;
    this.hasItemsInQueue = false;

    Map<NthChildPseudoSelector, ElementSet> applicableElementsMap = new HashMap<>();
    for (NthChildPseudoSelector selector: matchingElements.keySet()) {
      ElementSet applicableElements = 
        selectorMatcher.matchElements(selector.selector());
      applicableElementsMap.put(selector, applicableElements);
    }

    queuedElements.forEach(element -> processChildren(
      element.parentNode(), applicableElementsMap));
  }

  private void processChildren(
    Node parentNode,
    Map<NthChildPseudoSelector, ElementSet> applicableElementsMap
  ) {
    for (
      Map.Entry<NthChildPseudoSelector, RefCounted<ElementSet>> entry:
      matchingElements.entrySet()
    ) {
      ElementSet applicableElements = 
        applicableElementsMap.get(entry.getKey());
      processChildren(
        entry.getKey(),
        entry.getValue().object(),
        parentNode,
        applicableElements);
    }

    Node currentNode = parentNode.firstChild();
    while (currentNode != null) {
      if (currentNode instanceof Element currentElement) {
        queuedElements.remove(currentElement);
      }
      currentNode = currentNode.nextSibling();
    }
  }

  private void processChildren(
    NthChildPseudoSelector selector,
    ElementSet matchedElements,
    Node parentNode,
    ElementSet applicableSet
  ) {
    switch (selector.type()) {
      case NTH -> processChildren(
        selector, matchedElements,
        parentNode.firstChild(),
        Node::nextSibling,
        applicableSet);
      case NTH_LAST -> processChildren(
        selector, matchedElements, parentNode.lastChild(),
        Node::previousSibling,
        applicableSet);
      case ONLY_CHILD -> processOnlyChild(matchedElements, parentNode);
      default -> throw new UnsupportedOperationException(
        "Unrecognized selector type: " + selector.type());
    }
  }

  private void processChildren(
    NthChildPseudoSelector selector,
    ElementSet matchedElements,
    Node nextNode,
    Function<Node, Node> nextNodeFunc,
    ElementSet applicableElements
  ) {
    int numChildren = countRelevantChildren(selector, nextNode, nextNodeFunc, applicableElements);

    List<Integer> indexList =
      selector.index().a() == 0 ? List.of(selector.index().b()) :
      selector.index().a() < 0 ? reverseIndexList(selector.index(), numChildren) :
      indexList(selector.index(), numChildren);

    int currentIndex = 1;
    while (nextNode != null) {
      Node currentNode = nextNode;
      nextNode = nextNodeFunc.apply(currentNode);
      if (!(
        currentNode instanceof Element element
        && applicableElements.contains(element)
      )) continue;

      if (indexList.contains(currentIndex)) {
        matchedElements.add((Element) currentNode);
      }
      currentIndex++;
    }
  }

  private int countRelevantChildren(
    NthChildPseudoSelector selector,
    Node nextNode,
    Function<Node, Node> nextNodeFunc,
    ElementSet applicableElements
  ) {
    int numChildren = 0;
    while (nextNode != null) {
      if (
        nextNode instanceof Element element
        && applicableElements.contains(element)
      ) {
        numChildren++;
      }
      nextNode = nextNodeFunc.apply(nextNode);
    }
    return numChildren;
  }

  // TODO: Allocating lists is not so efficient
  private List<Integer> indexList(
    ANPlusB index, int numChildren
  ) {
    List<Integer> list = new ArrayList<>();
    int i = index.b();
    while (i <= numChildren) {
      if (i > 0) list.add(i);
      i += index.a();
    }

    return list;
  }

  private List<Integer> reverseIndexList(
    ANPlusB index, int numChildren
  ) {
    List<Integer> list = new ArrayList<>();
    int i = index.b();
    while (i >= 0) {
      if (i <= numChildren) list.add(i);
      i += index.a();
    }

    return list;
  }

  private void processOnlyChild(ElementSet matchedElements, Node parentNode) {
    Node currentNode = parentNode.firstChild();
    while (
      currentNode != null
      && !(currentNode instanceof Element)
    ) currentNode = currentNode.nextSibling();
    if (currentNode == null) return;
    Element element = (Element) currentNode;

    Node nextNode = currentNode.nextSibling();
    while (
      nextNode != null
      && !(nextNode instanceof Element)
    ) nextNode = nextNode.nextSibling();

    if (nextNode == null) {
      matchedElements.add(element);
    } else {
      matchedElements.remove(element);
    }
  }

  private void invalidateSelectors() {
    for (SelectorPart ref: matchingElements.keySet()) {
      onSelectorChanged.accept(ref);
    }
  }

}
