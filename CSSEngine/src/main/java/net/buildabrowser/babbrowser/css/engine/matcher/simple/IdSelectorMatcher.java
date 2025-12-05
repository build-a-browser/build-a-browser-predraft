package net.buildabrowser.babbrowser.css.engine.matcher.simple;

import java.util.HashMap;
import java.util.Map;

import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.cssbase.selector.IdSelector;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;

public class IdSelectorMatcher implements SimpleSelectorMatcher<IdSelector> {

  private final Map<String, Element> idElements = new HashMap<>();

  @Override
  public void addSelectorReference(IdSelector ref) {}

  @Override
  public void removeSelectorReference(IdSelector ref) {}

  @Override
  public void onNodeAdded(Node node) {
    if (!(node instanceof Element element)) return;
    String id = element.attributes().get("id");
    if (id == null) return;
    idElements.put(id, element);
  };

  @Override
  public void onNodeRemoved(Node node) {
    if (!(node instanceof Element element)) return;
    String id = element.attributes().get("id");
    if (id == null) return;
    idElements.remove(id);
  };

  @Override
  public ElementSet match(IdSelector selector) {
    ElementSet matches = ElementSet.create();
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
    }
    if (newValue != null) {
      idElements.put(newValue, element);
    }
  }

}
