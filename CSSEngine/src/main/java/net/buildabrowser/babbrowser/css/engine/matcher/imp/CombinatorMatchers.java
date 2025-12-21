package net.buildabrowser.babbrowser.css.engine.matcher.imp;

import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;

public final class CombinatorMatchers {
  
  // TODO: Better way to obtain new ElementSet
  // TODO: More performant way to match combinators
  //   (eg assign nodes a range, and do integer range math)

  private CombinatorMatchers() {}

  public static ElementSet matchDescendants(ElementSet priorMatches, ElementSet nextMatches) {
    ElementSet newMatches = ElementSet.create();
    for (Element matchedElement: nextMatches) {
      Node parent = matchedElement.parentNode();
      while (parent != null) {
        if (
          parent instanceof Element element
          && priorMatches.contains(element)
        ) {
          newMatches.add(matchedElement);
          break;
        }
        parent = parent.parentNode();
      }
    }

    return newMatches;
  }

  public static ElementSet matchChild(ElementSet priorMatches, ElementSet nextMatches) {
    ElementSet newMatches = ElementSet.create();
    for (Element matchedElement: nextMatches) {
      Node parent = matchedElement.parentNode();
      if (
        parent instanceof Element element
        && priorMatches.contains(element)
      ) {
        newMatches.add(matchedElement);
      }
    }

    return newMatches;
  }

  public static ElementSet matchNextSibling(ElementSet priorMatches, ElementSet nextMatches) {
    ElementSet newMatches = ElementSet.create();
    for (Element matchedElement: nextMatches) {
      Node parent = matchedElement.parentNode();
      Element lastElementMatched = null;
      for (Node childNode: parent.childNodes()) {
        if (childNode == matchedElement) {
          break;
        } else if (childNode instanceof Element element) {
          lastElementMatched = element;
        }
      }

      if (priorMatches.contains(lastElementMatched)) {
        newMatches.add(matchedElement);
        break;
      }
    }

    return newMatches;
  }

  public static ElementSet matchSubsequentSibling(ElementSet priorMatches, ElementSet nextMatches) {
    ElementSet newMatches = ElementSet.create();
    for (Element matchedElement: nextMatches) {
      Node parent = matchedElement.parentNode();
      for (Node childNode: parent.childNodes()) {
        if (childNode == matchedElement) {
          break;
        } else if (
          childNode instanceof Element element
          && priorMatches.contains(element)
        ) {
          newMatches.add(matchedElement);
          break;
        }
      }
    }

    return newMatches;
  }

}
