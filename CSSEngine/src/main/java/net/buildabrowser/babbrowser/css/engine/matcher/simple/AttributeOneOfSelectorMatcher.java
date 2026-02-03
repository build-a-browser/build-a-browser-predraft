package net.buildabrowser.babbrowser.css.engine.matcher.simple;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.css.engine.matcher.util.RefCounted;
import net.buildabrowser.babbrowser.cssbase.selector.AttributeSelector;
import net.buildabrowser.babbrowser.cssbase.selector.AttributeSelector.AttributeType;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;

public class AttributeOneOfSelectorMatcher implements SimpleSelectorMatcher<AttributeSelector> {

  private final Map<String, Map<String, RefCounted<ElementSet>>> matchingElements = new HashMap<>(1);

  private final ElementSet allElements;

  public AttributeOneOfSelectorMatcher(ElementSet allElements) {
    this.allElements = allElements;
  }

  @Override
  public void addSelectorReference(AttributeSelector ref) {
    if (!ref.type().equals(AttributeType.ONE_OF)) return;

    RefCounted<ElementSet> setRef = matchingElements
      .computeIfAbsent(ref.attrName(), _ -> new HashMap<>(4))
      .computeIfAbsent(ref.attrValue(), _ -> RefCounted.create(ElementSet.create()));
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
  public void removeSelectorReference(AttributeSelector ref) {
    if (!ref.type().equals(AttributeType.ONE_OF)) return;

    Map<String, RefCounted<ElementSet>> map = matchingElements.get(ref.attrName());
    if (map == null) return;
    RefCounted<ElementSet> counter = map.get(ref.attrValue());
    if (counter == null) return;

    counter.decRefCount();
    if (!counter.isReferenced()) {
      map.remove(ref.attrValue());
      if (map.isEmpty()) {
        matchingElements.remove(ref.attrName());
      }
    }
  }

  @Override
  public void onNodeAdded(Node node) {
    nodeAction(node, (s, el) -> s.add(el));
  };

  @Override
  public void onNodeRemoved(Node node) {
    nodeAction(node, (s, el) -> s.remove(el));
  }

  @Override
  public void onAttributeChanged(Element element, String attrName, String prevValue, String newValue) {
    Map<String, RefCounted<ElementSet>> map = matchingElements.get(attrName);
    if (map == null) return;
    
    String[] oldValues = (prevValue == null ? "" : prevValue).split(" ");
    String[] newValues = (newValue == null ? "" : newValue).split(" ");

    for (String value: oldValues) {
      if (contains(newValues, value)) continue;
      RefCounted<ElementSet> set = map.get(value);
      if (set == null) continue;
      set.object().remove(element);
    }

    for (String value: newValues) {
      if (contains(oldValues, value)) continue;
      RefCounted<ElementSet> set = map.get(value);
      if (set == null) continue;
      set.object().add(element);
    }
  }

  @Override
  public ElementSet match(AttributeSelector selector) {
    RefCounted<ElementSet> setRef = matchRef(selector);
    if (setRef == null) return ElementSet.create();

    return setRef.object();
  }

  private void nodeAction(Node node, BiConsumer<ElementSet, Element> action) {
    if (!(node instanceof Element element)) return;

    for (Map.Entry<String, Map<String, RefCounted<ElementSet>>> attrAndMap: matchingElements.entrySet()) {
      String attrValue = element.attributes().get(attrAndMap.getKey());
      if (attrValue == null) continue;

      for (String value: attrValue.split(" ")) {
        RefCounted<ElementSet> set = attrAndMap.getValue().get(value);
        if (set == null) return;
        action.accept(set.object(), element);
      }
    }
  }

  private RefCounted<ElementSet> matchRef(AttributeSelector selector) {
    if (!selector.type().equals(AttributeType.ONE_OF)) return null;

    Map<String, RefCounted<ElementSet>> valuesSet = matchingElements.get(selector.attrName());
    if (valuesSet == null) return null;

    RefCounted<ElementSet> setRef = valuesSet.get(selector.attrValue());
    return setRef;
  }

  private boolean matches(Element element, AttributeSelector ref) {
    if (!ref.type().equals(AttributeType.ONE_OF)) return false;
    String attrValue = element.attributes().get(ref.attrName());
    if (attrValue == null) return false;
    
    return contains(attrValue.split(" "), ref.attrValue());
  }

  private boolean contains(String[] arr, String target) {
    for (String value: arr) {
      if (value.equals(target)) {
        return true;
      }
    }

    return false;
  }

}
