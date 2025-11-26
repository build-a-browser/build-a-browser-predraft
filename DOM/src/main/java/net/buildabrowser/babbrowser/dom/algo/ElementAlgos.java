package net.buildabrowser.babbrowser.dom.algo;

import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.Text;

public final class ElementAlgos {
  
  private ElementAlgos() {}

  public static String childTextContent(Element element) {
    StringBuilder textElements = new StringBuilder();
    for (Node child: element.children()) {
      if (child instanceof Text text) {
        textElements.append(text.text());
      }
    }

    return textElements.toString();
  }

}
