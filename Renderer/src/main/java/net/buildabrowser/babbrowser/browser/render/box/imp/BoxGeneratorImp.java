package net.buildabrowser.babbrowser.browser.render.box.imp;

import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.browser.render.box.Box;
import net.buildabrowser.babbrowser.browser.render.box.BoxGenerator;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.NodeList;
import net.buildabrowser.babbrowser.dom.Text;

public class BoxGeneratorImp implements BoxGenerator {
  
  @Override
  public List<Box> box(Node node) {
    Box box = switch (node) {
      case Text text -> new TextBox(text);
      case Element element -> createElementBox(element);
      case Document document -> new DocumentBox(document, createChildBoxes(document.childNodes()));
      default -> throw new UnsupportedOperationException("Unsupported Box Type");
    };

    return List.of(box);
  }

  private Box createElementBox(Element element) {
    return switch (element.name()) {
      case "img" -> new ImageBox(element);
      default -> new ElementBox(element, createChildBoxes(element.childNodes()));
    };
  }

  private List<Box> createChildBoxes(NodeList children) {
    List<Box> childBoxes = new ArrayList<>((int) children.length());
    for (Node childNode: children) {
      childBoxes.addAll(box(childNode));
    }

    return childBoxes;
  }

}
