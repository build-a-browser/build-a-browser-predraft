package net.buildabrowser.babbrowser.css.engine.matcher.simple;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.css.engine.matcher.ElementRootSet;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.css.engine.matcher.util.RefCounted;
import net.buildabrowser.babbrowser.cssbase.selector.AttributeSelector;
import net.buildabrowser.babbrowser.cssbase.selector.AttributeSelector.AttributeType;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorPart;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;

public class AttributeSelectorMatcher implements SimpleSelectorMatcher<AttributeSelector> {

  private final Map<String, Map<AttributeSelector, RefCounted<ElementSet>>> matchingElements = new HashMap<>(1);

  private final ElementRootSet allElements;
  private final Consumer<SelectorPart> onSelectorChanged;

  public AttributeSelectorMatcher(
    ElementRootSet allElements,
    Consumer<SelectorPart> onSelectorChanged
  ) {
    this.allElements = allElements;
    this.onSelectorChanged = onSelectorChanged;
  }

  @Override
  public void addSelectorReference(AttributeSelector ref) {
    if (ref.type().equals(AttributeType.ONE_OF)) return;

    RefCounted<ElementSet> setRef = matchingElements
      .computeIfAbsent(ref.attrName(), _ -> new HashMap<>(1))
      .computeIfAbsent(ref, _ -> RefCounted.create(allElements.createChild()));
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
    if (ref.type().equals(AttributeType.ONE_OF)) return;

    Map<AttributeSelector, RefCounted<ElementSet>> map = matchingElements.get(ref.attrName());
    if (map == null) return;
    RefCounted<ElementSet> counter = map.get(ref);
    if (counter == null) return;

    counter.decRefCount();
    if (!counter.isReferenced()) {
      map.remove(ref);
      if (map.isEmpty()) {
        matchingElements.remove(ref.attrName());
      }
    }
  }

  @Override
  public void onNodeAdded(Node node) {
    nodeAction(node, (s, el) -> s.add(el));
  }

  @Override
  public void onNodeRemoved(Node node) {
    nodeAction(node, (s, el) -> s.remove(el));
  }

  @Override
  public void onAttributeChanged(Element element, String attrName, String prevValue, String newValue) {
    Map<AttributeSelector, RefCounted<ElementSet>> map = matchingElements.get(attrName);
    if (map == null) return;
    
    for (Map.Entry<AttributeSelector, RefCounted<ElementSet>> entry: map.entrySet()) {
      RefCounted<ElementSet> set = entry.getValue();
      boolean changed = matches(element, entry.getKey()) ?
        set.object().add(element) :
        set.object().remove(element);
      if (changed) {
        onSelectorChanged.accept(entry.getKey());
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

    for (Map.Entry<String, Map<AttributeSelector, RefCounted<ElementSet>>> attrAndMap: matchingElements.entrySet()) {
      String attrValue = element.attributes().get(attrAndMap.getKey());
      if (attrValue == null) continue;

      for (Map.Entry<AttributeSelector, RefCounted<ElementSet>> entry: attrAndMap.getValue().entrySet()) {
        RefCounted<ElementSet> set = entry.getValue();
        if (matches(element, entry.getKey())) {
          boolean changed = action.apply(set.object(), element);
          if (changed) {
            onSelectorChanged.accept(entry.getKey());
          }
        }
      }
    }
  }

  private RefCounted<ElementSet> matchRef(AttributeSelector selector) {
    if (selector.type().equals(AttributeType.ONE_OF)) return null;

    Map<AttributeSelector, RefCounted<ElementSet>> valuesSet = matchingElements.get(selector.attrName());
    if (valuesSet == null) return null;

    RefCounted<ElementSet> setRef = valuesSet.get(selector);
    return setRef;
  }

  private boolean matches(Element element, AttributeSelector ref) {
    if (ref.type().equals(AttributeType.ONE_OF)) return false;
    String attrValue = element.attributes().get(ref.attrName());
    if (attrValue == null) return false;
    
    return switch (ref.type()) {
      case HAS_ATTR -> true;
      case EXACTLY -> attrValue.equals(ref.attrValue());
      case ONE_OF -> false;
      case PREFIX -> attrValue.equals(ref.attrValue()) || attrValue.startsWith(ref.attrValue() + "-");
      case STARTS_WITH -> attrValue.startsWith(ref.attrValue());
      case ENDS_WITH -> attrValue.endsWith(ref.attrValue());
      case CONTAINS -> attrValue.contains(ref.attrValue());
      default -> throw new UnsupportedOperationException("Don't recognize attribute type: " + ref.type());
    };
  }

  public static interface MatchChecker {

    public boolean check(AttributeSelector ref, String attrValue);

  }

}
