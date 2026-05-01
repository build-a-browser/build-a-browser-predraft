package net.buildabrowser.babbrowser.css.engine.matcher.imp;

import net.buildabrowser.babbrowser.css.engine.matcher.ElementRootSet;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;

public final class CombinatorMatchers {
  
  // TODO: Better way to obtain new ElementSet

  private final ElementIdTreeListener idTree;

  public CombinatorMatchers(ElementRootSet allElements) {
    this.idTree = new ElementIdTreeListener(allElements);
    allElements.addListener(idTree);
    idTree.resync();
  }

  public ElementSet matchDescendants(ElementSet priorMatches, ElementSet nextMatches) {
    ElementSet newMatches = priorMatches.root().createTemporaryChild();
    nextMatches.forEachElementId(elementId -> {
      int parentId = idTree.getElementParentId(elementId);
      while (parentId != -1) {
        if (priorMatches.containsById(parentId)) {
          newMatches.addById(elementId);
          break;
        }
        parentId = idTree.getElementParentId(parentId);
      }
    });

    return newMatches;
  }

  public ElementSet matchChild(ElementSet priorMatches, ElementSet nextMatches) {
    ElementSet newMatches = priorMatches.root().createTemporaryChild();
    nextMatches.forEachElementId(elementId -> {
      int parentId = idTree.getElementParentId(elementId);
      if (
        parentId != -1
        && priorMatches.containsById(parentId)
      ) {
        newMatches.addById(elementId);
      }
    });

    return newMatches;
  }

  public ElementSet matchNextSibling(ElementSet priorMatches, ElementSet nextMatches) {
    ElementSet newMatches = priorMatches.root().createTemporaryChild();
    for (Element matchedElement: nextMatches) {
      Node prevNode = matchedElement.previousSibling();
      while (
        prevNode != null
        && !(prevNode instanceof Element)
      ) {
        prevNode = prevNode.previousSibling();
      }

      if (
        prevNode != null
        && priorMatches.contains((Element) prevNode)
      ) {
        newMatches.add(matchedElement);
      }
    }

    return newMatches;
  }

  public ElementSet matchSubsequentSibling(ElementSet priorMatches, ElementSet nextMatches) {
    ElementSet newMatches = priorMatches.root().createTemporaryChild();
    for (Element matchedElement: nextMatches) {
      Node prevNode = matchedElement.previousSibling();
      while (prevNode != null) {
        if (
          prevNode instanceof Element element
          && priorMatches.contains(element)
        ) {
          newMatches.add(matchedElement);
          break;
        }
        prevNode = prevNode.previousSibling();
      }
    }

    return newMatches;
  }

}
