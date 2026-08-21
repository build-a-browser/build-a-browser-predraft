package net.buildabrowser.babbrowser.htmlparser.insertion.imp;

import java.util.LinkedList;
import java.util.List;

import net.buildabrowser.babbrowser.htmlparser.insertion.InsertionMode;
import net.buildabrowser.babbrowser.htmlparser.insertion.TemplateInsertionModeStack;

public class TemplateInsertionModeStackImp implements TemplateInsertionModeStack {

  private final List<InsertionMode> stack = new LinkedList<>();

  @Override
  public void push(InsertionMode insertionMode) {
    stack.add(0, insertionMode);
  }

  @Override
  public InsertionMode pop() {
    return stack.remove(0);
  }

  @Override
  public boolean isEmpty() {
    return stack.size() == 0;
  }

  @Override
  public InsertionMode current() {
    return stack.get(0);
  }
  
}
