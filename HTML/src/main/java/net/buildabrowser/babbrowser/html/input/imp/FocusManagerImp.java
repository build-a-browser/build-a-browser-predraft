package net.buildabrowser.babbrowser.html.input.imp;

import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.html.HTMLOrSVGOrMathMLElement;
import net.buildabrowser.babbrowser.html.input.FocusManager;
import net.buildabrowser.babbrowser.html.input.FocusManagerContext;
import net.buildabrowser.babbrowser.html.input.FocusManagerContext.FocusIgnore;
import net.buildabrowser.babbrowser.html.input.FocusOptions;

public class FocusManagerImp implements FocusManager {

  private final Document document;

  private Node focused;
  private FocusOptions focusOptions;
  private FocusManagerContext focusManagerContext;

  public FocusManagerImp(Document document) {
    this.document = document;
  }

  @Override
  public Node focused() {
    return this.focused;
  }

  @Override
  public FocusOptions focusOptions() {
    return this.focusOptions;
  }

  @Override
  public void focus(Node node, FocusOptions focusOptions) {
    Node oldFocused = this.focused;
    this.focused = node;
    this.focusOptions = focusOptions;

    if (
      focusManagerContext != null
      && oldFocused != focused
    ) {
      focusManagerContext.onFocusChanged(oldFocused, focused);
    }
  }

  @Override
  public void focusNext(
    FocusOptions focusOptions
  ) {
    List<HTMLOrSVGOrMathMLElement> focusOrder = determineFocusOrder();
    if (focusOrder.isEmpty()) return;
    int elIndex = focused == null ? -1 : focusOrder.indexOf(focused);
    if (
      elIndex == -1
      || elIndex + 1 >= focusOrder.size()
    ) {
      focus(focusOrder.get(0), focusOptions);
    } else {
      focus(focusOrder.get(elIndex + 1), focusOptions);
    }
  }

  @Override
  public void focusPrevious(FocusOptions focusOptions) {
    List<HTMLOrSVGOrMathMLElement> focusOrder = determineFocusOrder();
    if (focusOrder.isEmpty()) return;
    int elIndex = focused == null ? -1 : focusOrder.indexOf(focused);
    if (elIndex <= 0) {
      focus(focusOrder.get(focusOrder.size() - 1), focusOptions);
    } else {
      focus(focusOrder.get(elIndex - 1), focusOptions);
    }
  }

  @Override
  public void unfocus() {
    this.focused = null;
    this.focusOptions = null;
  }

  @Override
  public void attachContext(FocusManagerContext context) {
    this.focusManagerContext = context;
  }

  // TODO: For performance, create this once in advance, update when document is updated
  private List<HTMLOrSVGOrMathMLElement> determineFocusOrder() {
    Node currentNode = document;
    List<HTMLOrSVGOrMathMLElement> focusOrder = new ArrayList<>();
    while (currentNode != null) {
      FocusIgnore focusIgnore = focusManagerContext == null ?
        FocusIgnore.NONE :
        focusManagerContext.getIgnore(currentNode);
      if (
        currentNode instanceof HTMLOrSVGOrMathMLElement element
        && (element.tabIndex() >= 0 || element == focused)
        && focusIgnore.equals(FocusIgnore.NONE)
      ) {
        focusOrder.add(element);
      }
      if (
        currentNode.firstChild() != null
        && !focusIgnore.equals(FocusIgnore.TREE)
      ) {
        currentNode = currentNode.firstChild();
        continue;
      }
      while (
        currentNode != null
        && currentNode.nextSibling() == null
      ) {
        currentNode = currentNode.parentNode();
      }
      if (currentNode != null) {
        currentNode = currentNode.nextSibling();
      }
    }

    sortFocus(focusOrder);
    return focusOrder;
  }

  private void sortFocus(
    List<HTMLOrSVGOrMathMLElement> focusOrder
  ) {
    focusOrder.sort((a, b) -> {
      Long aTabIndex = a.tabIndex();
      Long bTabIndex = b.tabIndex();
      boolean isAHigh = aTabIndex <= 0;
      boolean isBHigh = bTabIndex <= 0;
      
      return
        isAHigh && isBHigh ? 0 :
        isAHigh && !isBHigh ? 1 :
        !isAHigh && isBHigh ? -1 :
        Long.compare(aTabIndex, bTabIndex);
    });
  }
  
}
