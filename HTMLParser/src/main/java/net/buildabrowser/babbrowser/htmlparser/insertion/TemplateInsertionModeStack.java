package net.buildabrowser.babbrowser.htmlparser.insertion;

import net.buildabrowser.babbrowser.htmlparser.insertion.imp.TemplateInsertionModeStackImp;

public interface TemplateInsertionModeStack {
  
  void push(InsertionMode insertionMode);

  InsertionMode pop();

  boolean isEmpty();

  InsertionMode current();

  static TemplateInsertionModeStack create() {
    return new TemplateInsertionModeStackImp();
  }

}
