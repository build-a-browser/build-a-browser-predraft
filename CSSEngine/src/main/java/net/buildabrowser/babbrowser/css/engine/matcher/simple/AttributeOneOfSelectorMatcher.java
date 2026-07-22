package net.buildabrowser.babbrowser.css.engine.matcher.simple;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.common.util.StringUtil;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementRootSet;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.css.engine.matcher.util.RefCounted;
import net.buildabrowser.babbrowser.cssbase.selector.AttributeSelector;
import net.buildabrowser.babbrowser.cssbase.selector.AttributeSelector.AttributeType;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorPart;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;

public class AttributeOneOfSelectorMatcher implements SimpleSelectorMatcher<AttributeSelector> {

  private final Map<String, Map<String, RefCounted<ElementSet>>> matchingElements = new HashMap<>(1);

  private final ElementRootSet allElements;
  private final Consumer<SelectorPart> onSelectorChanged;

  public AttributeOneOfSelectorMatcher(
    ElementRootSet allElements,
    Consumer<SelectorPart> onSelectorChanged
  ) {
    this.allElements = allElements;
    this.onSelectorChanged = onSelectorChanged;
    matchingElements.put("class", new HashMap<>());
  }

  @Override
  public void addSelectorReference(AttributeSelector ref) {
    if (!ref.type().equals(AttributeType.ONE_OF)) return;
    if (ref.attrName().equals("class")) return;

    RefCounted<ElementSet> setRef = matchingElements
      .computeIfAbsent(ref.attrName(), _1 -> new HashMap<>(4))
      .computeIfAbsent(ref.attrValue(), _1 -> RefCounted.create(allElements.createChild()));
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
    if (ref.attrName().equals("class")) return;

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
    if (
      node instanceof Element element
      && element.hasAttribute("class")
    ) {
      addElementToClasses(element);
    }

    nodeAction(node, (s, el) -> s.add(el));
  }

  @Override
  public void onNodeRemoved(Node node) {
    if (
      node instanceof Element element
      && element.hasAttribute("class")
    ) {
      removeElementFromClasses(element);
    }

    nodeAction(node, (s, el) -> s.remove(el));
  }

  // TODO: Fire onSelectorChanged
  private void addElementToClasses(Element element) {
    Map<String, RefCounted<ElementSet>> classMap = matchingElements.get("class");
    // TODO: Store class list in DOM node
    String[] classList = StringUtil.spaceSplit(element.getAttribute("class"));
    for (String value: classList) {
      RefCounted<ElementSet> setRef = classMap
        .computeIfAbsent(value, _1 -> RefCounted.create(allElements.createChild()));
      setRef.incRefCount();
      setRef.object().add(element);
      onSelectorChanged.accept(AttributeSelector.create("class", value, AttributeType.ONE_OF));
    }
  }

  private void removeElementFromClasses(Element element) {
    Map<String, RefCounted<ElementSet>> classMap = matchingElements.get("class");
    String[] classList = StringUtil.spaceSplit(element.getAttribute("class"));
    for (String value: classList) {
      RefCounted<ElementSet> counter = classMap.get(value);
      if (counter == null) return;

      counter.decRefCount();
      if (!counter.isReferenced()) {
        classMap.remove(value);
      } else {
        counter.object().remove(element);
      }
      onSelectorChanged.accept(AttributeSelector.create("class", value, AttributeType.ONE_OF));
    }
  }

  @Override
  public void onAttributeChanged(Element element, String attrName, String prevValue, String newValue) {
    Map<String, RefCounted<ElementSet>> map = matchingElements.get(attrName);
    if (map == null) return;
    
    String[] oldValues = prevValue == null ? new String[0] : StringUtil.spaceSplit(prevValue);
    String[] newValues = newValue == null ? new String[0] : StringUtil.spaceSplit(newValue);

    boolean isClass = attrName.equals("class");
    boolean changed = false;
    for (String value: oldValues) {
      if (containsAndRemove(newValues, value)) continue;
      RefCounted<ElementSet> set = map.get(value);
      if (set == null) continue;
      if (isClass) set.decRefCount();
      if (isClass && !set.isReferenced()) {
        map.remove(value);
      }
      changed |= changed |= set.object().remove(element);
    }

    for (String value: newValues) {
      if (value == null) continue;
      if (containsAndRemove(oldValues, value)) continue;
      RefCounted<ElementSet> set = isClass ?
        map.computeIfAbsent(value, _1 -> RefCounted.create(allElements.root().createChild())) :
        map.get(value);
      if (set == null) continue;
      if (isClass) set.incRefCount();
      changed |= set.object().add(element);
    }

    if (changed) {
      for (String value: oldValues) {
        if (value == null) continue;
        onSelectorChanged.accept(AttributeSelector.create(attrName, value, AttributeType.ONE_OF));
      }
      for (String value: newValues) {
        if (value == null) continue;
        onSelectorChanged.accept(AttributeSelector.create(attrName, value, AttributeType.ONE_OF));
      }
    }
  }

  @Override
  public ElementSet match(AttributeSelector selector) {
    RefCounted<ElementSet> setRef = matchRef(selector);
    if (setRef == null) return allElements.createTemporaryChild();

    return setRef.object();
  }

  private void nodeAction(Node node, BiFunction<ElementSet, Element, Boolean> action) {
    if (!(node instanceof Element element)) return;

    for (Map.Entry<String, Map<String, RefCounted<ElementSet>>> attrAndMap: matchingElements.entrySet()) {
      String attrValue = element.getAttribute(attrAndMap.getKey());
      if (attrValue == null) continue;

      for (String value: StringUtil.spaceSplit(attrValue)) {
        RefCounted<ElementSet> set = attrAndMap.getValue().get(value);
        if (set == null) continue;
        boolean changed = action.apply(set.object(), element);
        if (changed) {
          onSelectorChanged.accept(AttributeSelector.create(attrAndMap.getKey(), value, AttributeType.ONE_OF));
        }
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
    String attrValue = element.getAttribute(ref.attrName());
    if (attrValue == null) return false;
    
    return containsAndRemove(StringUtil.spaceSplit(attrValue), ref.attrValue());
  }

  private boolean containsAndRemove(String[] arr, String target) {
    for (int i = 0; i < arr.length; i++) {
      if (arr[i].equals(target)) {
        arr[i] = null;
        return true;
      }
    }

    return false;
  }

}
