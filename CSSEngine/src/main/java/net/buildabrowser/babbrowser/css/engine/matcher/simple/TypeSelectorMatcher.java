package net.buildabrowser.babbrowser.css.engine.matcher.simple;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.css.engine.matcher.ElementRootSet;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.css.engine.matcher.util.RefCounted;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorPart;
import net.buildabrowser.babbrowser.cssbase.selector.TypeSelector;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;

public class TypeSelectorMatcher implements SimpleSelectorMatcher<TypeSelector> {

  private final Map<TypeSelector, RefCounted<ElementSet>> matchingElements = new HashMap<>();

  private final ElementRootSet allElements;
  private final Consumer<SelectorPart> onSelectorChanged;

  public TypeSelectorMatcher(
    ElementRootSet allElements,
    Consumer<SelectorPart> onSelectorChanged
  ) {
    this.allElements = allElements;
    this.onSelectorChanged = onSelectorChanged;
  }

  @Override
  public void addSelectorReference(TypeSelector ref) {
    RefCounted<ElementSet> setRef = matchingElements
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
    TypeSelector selector = TypeSelector.create(element.name());
    RefCounted<ElementSet> set = matchingElements.get(selector);
    if (set == null) return;
    boolean changed = set.object().add(element);
    if (changed) {
      onSelectorChanged.accept(selector);
    }
  }

  @Override
  public void onNodeRemoved(Node node) {
    if (!(node instanceof Element element)) return;
    TypeSelector selector = TypeSelector.create(element.name());
    RefCounted<ElementSet> set = matchingElements.get(selector);
    if (set == null) return;
    boolean changed = set.object().remove(element);
    if (changed) {
      onSelectorChanged.accept(selector);
    }
  }

  @Override
  public ElementSet match(TypeSelector selector) {
    RefCounted<ElementSet> set = matchingElements.get(selector);
    if (set == null) return allElements.createTemporaryChild();
    return set.object();
  }

  private boolean matches(Element element, TypeSelector ref) {
    return element.name().equals(ref.tagName());
  }

}
