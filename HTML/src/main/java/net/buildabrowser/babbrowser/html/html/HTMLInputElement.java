package net.buildabrowser.babbrowser.html.html;

import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.html.imp.HTMLInputElementImp;
import net.buildabrowser.babbrowser.infra.Namespace;

public interface HTMLInputElement extends FormAssociatedElement {

  boolean checked();

  void setChecked(boolean checked);

  boolean disabled();

  String type();

  void setType(String type);
  
  String value();

  void setValue(String value);

  // Extensions

  void setCheckedRaw(boolean checked);

  public static HTMLElement create(
    String name, Node parentNode
  ) {
    return new HTMLInputElementImp(
      name, Namespace.HTML_NAMESPACE, parentNode);
  }

}
