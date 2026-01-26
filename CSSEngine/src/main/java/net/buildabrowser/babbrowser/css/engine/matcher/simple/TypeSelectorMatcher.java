package net.buildabrowser.babbrowser.css.engine.matcher.simple;

import java.util.HashMap;
import java.util.Map;

import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.css.engine.matcher.util.RefCounted;
import net.buildabrowser.babbrowser.cssbase.selector.TypeSelector;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;

public class TypeSelectorMatcher implements SimpleSelectorMatcher<TypeSelector> {

  private final Map<TypeSelector, RefCounted<ElementSet>> matchingElements = new HashMap<>();

  private final ElementSet allElements;

  public TypeSelectorMatcher(ElementSet allElements) {
    this.allElements = allElements;
  }

  @Override
  public void addSelectorReference(TypeSelector ref) {
    RefCounted<ElementSet> setRef = matchingElements
      .computeIfAbsent(ref, _ -> RefCounted.create(ElementSet.create()));
    boolean didExist = setRef.isReferenced();
    setRef.incRefCount();

    if (didExist) return;
    ElementSet set = setRef.object();
    for (Element element: allElements) {
      if (matches(element, ref)) {
        set.add(element);
      }
    }
  }

  @Override
  public void removeSelectorReference(TypeSelector ref) {
    RefCounted<ElementSet> counter = matchingElements.get(ref);
    if (counter == null) return;

    counter.decRefCount();
    if (!counter.isReferenced()) {
      matchingElements.remove(ref);
    }
  }

  @Override
  public void onNodeAdded(Node node) {
    if (!(node instanceof Element element)) return;
    RefCounted<ElementSet> set = matchingElements.get(TypeSelector.create(element.name()));
    if (set == null) return;
    set.object().add(element);
  };

  @Override
  public void onNodeRemoved(Node node) {
    if (!(node instanceof Element element)) return;
    RefCounted<ElementSet> set = matchingElements.get(TypeSelector.create(element.name()));
    if (set == null) return;
    set.object().remove(element);
  };

  @Override
  public ElementSet match(TypeSelector selector) {
    RefCounted<ElementSet> set = matchingElements.get(selector);
    if (set == null) return ElementSet.create();
    return set.object();
  }

  private boolean matches(Element element, TypeSelector ref) {
    return element.name().equals(ref.tagName());
  }

}
