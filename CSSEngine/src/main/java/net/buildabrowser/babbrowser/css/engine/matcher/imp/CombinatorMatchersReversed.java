package net.buildabrowser.babbrowser.css.engine.matcher.imp;

import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;

public final class CombinatorMatchersReversed {
  
  // TODO: Better way to obtain new ElementSet

  private final ElementIdTreeListener idTree;

  public CombinatorMatchersReversed(ElementIdTreeListener idTree) {
    this.idTree = idTree;
  }

  public ElementSet matchDescendants(ElementSet currentMatches) {
    ElementSet newMatches = currentMatches.root().createTemporaryChild();

    currentMatches.forEachElementId(elementId -> {
      int parentId = idTree.getElementParentId(elementId);
      while (parentId != -1) {
        newMatches.addById(parentId);
        parentId = idTree.getElementParentId(parentId);
      }
    });

    return newMatches;
  }

  public ElementSet matchChild(ElementSet currentMatches) {
    ElementSet newMatches = currentMatches.root().createTemporaryChild();
    currentMatches.forEachElementId(elementId -> {
      int parentId = idTree.getElementParentId(elementId);
      if (parentId != -1) {
        newMatches.addById(parentId);
      }
    });

    return newMatches;
  }

  public ElementSet matchNextSibling(ElementSet currentMatches) {
    ElementSet newMatches = currentMatches.root().createTemporaryChild();
    for (Element matchedElement: currentMatches) {
      Node nextNode = matchedElement.nextSibling();
      while (
        nextNode != null
        && !(nextNode instanceof Element)
      ) {
        nextNode = nextNode.nextSibling();
      }

      if (nextNode != null) {
        newMatches.add((Element) nextNode);
      }
    }

    return newMatches;
  }

  public ElementSet matchSubsequentSibling(ElementSet currentMatches) {
    ElementSet newMatches = currentMatches.root().createTemporaryChild();
    for (Element matchedElement: currentMatches) {
      Node nextNode = matchedElement.nextSibling();
      while (nextNode != null) {
        if (nextNode instanceof Element nextEl) {
          newMatches.add(nextEl);
        }

        nextNode = nextNode.nextSibling();
      }
    }

    return newMatches;
  }

}
