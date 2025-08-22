package net.buildabrowser.babbrowser.browser.render.imp.box;

import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.browser.render.core.box.Box;
import net.buildabrowser.babbrowser.browser.render.core.box.BoxGenerator;
import net.buildabrowser.babbrowser.spec.dom.Document;
import net.buildabrowser.babbrowser.spec.dom.Element;
import net.buildabrowser.babbrowser.spec.dom.Node;
import net.buildabrowser.babbrowser.spec.dom.Text;

public class BoxGeneratorImp implements BoxGenerator {
  
  @Override
  public List<Box> box(Node node) {
    Box box = switch (node) {
      case Text text -> new TextBox(text);
      case Element element -> createElementBox(element);
      case Document document -> new DocumentBox(document, createChildBoxes(document.children()));
      default -> throw new UnsupportedOperationException("Unsupported Box Type");
    };

    return List.of(box);
  }

  private Box createElementBox(Element element) {
    return switch (element.name()) {
      case "img" -> new ImageBox(element);
      default -> new ElementBox(element, createChildBoxes(element.children()));
    };
  }

  private List<Box> createChildBoxes(List<Node> children) {
    List<Box> childBoxes = new ArrayList<>(children.size());
    for (Node childNode: children) {
      childBoxes.addAll(box(childNode));
    }

    return childBoxes;
  }

}
