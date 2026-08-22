package net.buildabrowser.babbrowser.debugger.core;

import java.util.List;

import net.buildabrowser.babbrowser.dom.Node;

public interface DebugBox extends DebugObject {

  Node relatedNode();

  List<DebugBox> childDebugBoxes();

  DebugBoxType debugBoxType();

  static enum DebugBoxType {
    DOCUMENT, ELEMENT, TEXT, UNKNOWN;
  }

}
