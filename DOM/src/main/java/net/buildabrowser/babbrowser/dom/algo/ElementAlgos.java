package net.buildabrowser.babbrowser.dom.algo;

import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Text;

public final class ElementAlgos {
  
  private ElementAlgos() {}

  public static String childTextContent(Element element) {
    StringBuilder textElements = new StringBuilder();
    element.forEachChild(child -> {
      if (child instanceof Text text) {
        textElements.append(text.data());
      }
    });

    return textElements.toString();
  }

}
