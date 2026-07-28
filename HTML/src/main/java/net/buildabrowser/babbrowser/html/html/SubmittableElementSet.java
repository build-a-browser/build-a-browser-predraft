package net.buildabrowser.babbrowser.html.html;

import java.util.List;

import net.buildabrowser.babbrowser.html.html.imp.SubmittableElementSetImp;

public interface SubmittableElementSet {

  List<FormAssociatedElement> elements();

  void addElement(FormAssociatedElement element);

  void removeElement(FormAssociatedElement element);

  static SubmittableElementSet create() {
    return new SubmittableElementSetImp();
  }
  
}
