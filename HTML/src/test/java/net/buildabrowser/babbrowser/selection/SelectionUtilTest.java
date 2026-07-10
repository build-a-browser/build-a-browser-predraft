package net.buildabrowser.babbrowser.selection;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.selection.Selection;
import net.buildabrowser.babbrowser.html.selection.Selection.SelectionDirection;
import net.buildabrowser.babbrowser.html.selection.SelectionUtil;

public class SelectionUtilTest {
  
  @Test
  @DisplayName("Can detect forward selection in simple tree")
  public void canDetectForwardSelectionInSimpleTree() {
    Document document = Document.create();
    Node firstNode = Element.create("div", document);
    Node secondNode = Element.create("div", document);
    Node thirdNode = Element.create("div", document);
    Node fourthNode = Element.create("div", document);
    document.appendChild(firstNode);
    document.appendChild(secondNode);
    document.appendChild(thirdNode);
    document.appendChild(fourthNode);

    Selection selection = Selection.create(document);
    selection.setBaseAndExtent(
      secondNode, 0,
      fourthNode, 0
    );

    SelectionDirection direction = SelectionUtil.determineSelectionDirection(selection);
    Assertions.assertEquals(SelectionDirection.FORWARD, direction);
  }

  @Test
  @DisplayName("Can detect backward selection in simple tree")
  public void canDetectBackwardSelectionInSimpleTree() {
    Document document = Document.create();
    Node firstNode = Element.create("div", document);
    Node secondNode = Element.create("div", document);
    Node thirdNode = Element.create("div", document);
    Node fourthNode = Element.create("div", document);
    document.appendChild(firstNode);
    document.appendChild(secondNode);
    document.appendChild(thirdNode);
    document.appendChild(fourthNode);

    Selection selection = Selection.create(document);
    selection.setBaseAndExtent(
      thirdNode, 0,
      firstNode, 0
    );

    SelectionDirection direction = SelectionUtil.determineSelectionDirection(selection);
    Assertions.assertEquals(SelectionDirection.BACKWARD, direction);
  }

  @Test
  @DisplayName("Can detect backward selection in complex tree")
  public void canDetectBackwardSelectionInComplexTree() {
    Document document = Document.create();
    Node firstNode = Element.create("div", document);
    Node secondNode = Element.create("div", document);
    Node thirdNode = Element.create("div", document);
    Node thirdNodeChild = Element.create("div", document);
    Node fourthNode = Element.create("div", document);
    document.appendChild(firstNode);
    document.appendChild(secondNode);
    document.appendChild(thirdNode);
    thirdNode.appendChild(thirdNodeChild);
    document.appendChild(fourthNode);

    Selection selection = Selection.create(document);
    selection.setBaseAndExtent(
      thirdNodeChild, 0,
      firstNode, 0
    );

    SelectionDirection direction = SelectionUtil.determineSelectionDirection(selection);
    Assertions.assertEquals(SelectionDirection.BACKWARD, direction);
  }

  @Test
  @DisplayName("Can get elements of forward selection in simple tree")
  public void canGetElementsOfForwardSelectionInSimpleTree() {
    Document document = Document.create();
    Node firstNode = Element.create("div", document);
    Node secondNode = Element.create("div", document);
    Node thirdNode = Element.create("div", document);
    Node fourthNode = Element.create("div", document);
    document.appendChild(firstNode);
    document.appendChild(secondNode);
    document.appendChild(thirdNode);
    document.appendChild(fourthNode);

    Selection selection = Selection.create(document);
    selection.setBaseAndExtent(
      secondNode, 0,
      fourthNode, 0
    );

    Set<Node> actual = new LinkedHashSet<>();
    SelectionUtil.determineSelectedNodes(selection, actual::add);

    Set<Node> expected = Set.of(
      secondNode, thirdNode, fourthNode);
    
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can get elements of backward selection in complex tree")
  public void canGetElementsOfBackwardSelectionInComplexTree() {
    Document document = Document.create();
    Node firstNode = Element.create("div", document);
    Node secondNode = Element.create("div", document);
    Node thirdNode = Element.create("div", document);
    Node thirdNodeFirstChild = Element.create("div", document);
    Node thirdNodeSecondChild = Element.create("div", document);
    Node thirdNodeThirdChild = Element.create("div", document);
    Node fourthNode = Element.create("div", document);
    document.appendChild(firstNode);
    document.appendChild(secondNode);
    document.appendChild(thirdNode);
    thirdNode.appendChild(thirdNodeFirstChild);
    thirdNode.appendChild(thirdNodeSecondChild);
    thirdNode.appendChild(thirdNodeThirdChild);
    document.appendChild(fourthNode);

    Selection selection = Selection.create(document);
    selection.setBaseAndExtent(
      thirdNodeSecondChild, 0,
      firstNode, 0
    );

    Set<Node> actual = new LinkedHashSet<>();
    SelectionUtil.determineSelectedNodes(selection, actual::add);

    Set<Node> expected = Set.of(
      firstNode, secondNode, thirdNodeFirstChild, thirdNodeSecondChild);
    
    Assertions.assertEquals(expected, actual);
  }

}
