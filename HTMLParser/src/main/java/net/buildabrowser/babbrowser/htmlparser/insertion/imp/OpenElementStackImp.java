package net.buildabrowser.babbrowser.htmlparser.insertion.imp;

import java.util.LinkedList;
import java.util.List;

import net.buildabrowser.babbrowser.dom.algo.StyleAlgos;
import net.buildabrowser.babbrowser.dom.mutable.MutableElement;
import net.buildabrowser.babbrowser.dom.mutable.MutableNode;
import net.buildabrowser.babbrowser.htmlparser.insertion.OpenElementStack;
import net.buildabrowser.babbrowser.htmlparser.insertion.util.ParseElementUtil;

public class OpenElementStackImp implements OpenElementStack {

  private final List<MutableNode> stack = new LinkedList<>();

  @Override
  public void pushNode(MutableNode node) {
    stack.addFirst(node);
  }

  @Override
  public MutableNode peek() {
    return stack.getFirst();
  }

  @Override
  public MutableNode peek(int pos) {
    return stack.get(pos);
  }

  @Override
  public MutableNode popNode() {
    MutableNode node = stack.removeFirst();
    if (ParseElementUtil.isHTMLElementWithName(node, "style")) {
      StyleAlgos.updateAStyleBlock((MutableElement) node);
    }
    return node;
  }

  @Override
  public int size() {
    return stack.size();
  }
  
}
