package net.buildabrowser.babbrowser.css.engine.matcher.simple;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.css.engine.matcher.ElementRootSet;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.cssbase.selector.IdSelector;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorPart;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;

public class IdSelectorMatcher implements SimpleSelectorMatcher<IdSelector> {

  private final ElementRootSet allElements;
  private final Map<String, Element> idElements = new HashMap<>();
  private final Consumer<SelectorPart> onSelectorChanged;

  public IdSelectorMatcher(
    ElementRootSet allElements,
    Consumer<SelectorPart> onSelectorChanged
  ) {
    this.allElements = allElements;
    this.onSelectorChanged = onSelectorChanged;
  }

  @Override
  public void addSelectorReference(IdSelector ref) {}

  @Override
  public void removeSelectorReference(IdSelector ref) {}

  @Override
  public void onNodeAdded(Node node) {
    if (!(node instanceof Element element)) return;
    String id = element.getAttribute("id");
    if (id == null) return;
    Element newElement = idElements.put(id, element);
    if (newElement != element) {
      onSelectorChanged.accept(IdSelector.create(id));
    }
  }

  @Override
  public void onNodeRemoved(Node node) {
    if (!(node instanceof Element element)) return;
    String id = element.getAttribute("id");
    if (id == null) return;
    Element oldEntry = idElements.remove(id);
    if (oldEntry != null) {
      onSelectorChanged.accept(IdSelector.create(id));
    }
  }

  @Override
  public ElementSet match(IdSelector selector) {
    ElementSet matches = allElements.createTemporaryChild();
    Element match = idElements.get(selector.id());
    if (match != null) {
      matches.add(match);
    }

    return matches;
  }

  public void onAttributeChanged(Element element, String attrName, String prevValue, String newValue) {
    if (!attrName.equals("id")) return;
    if (prevValue != null) {
      idElements.remove(prevValue);
      onSelectorChanged.accept(IdSelector.create(prevValue));
    }
    if (newValue != null) {
      idElements.put(newValue, element);
      onSelectorChanged.accept(IdSelector.create(newValue));
    }
  }

}
