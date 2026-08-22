package net.buildabrowser.babbrowser.renderer.box.test;

import java.util.List;

import net.buildabrowser.babbrowser.debugger.core.DebugBox;
import net.buildabrowser.babbrowser.debugger.core.DebugSnapshot;
import net.buildabrowser.babbrowser.dom.Text;
import net.buildabrowser.babbrowser.renderer.box.TextBox;
import net.buildabrowser.babbrowser.renderer.box.imp.AbstractBoxImp;

public class TestTextBox extends AbstractBoxImp implements TextBox {

  private final String text;

  public TestTextBox(String text) {
    this.text = text;
  }

  @Override
  public String text() {
    return this.text;
  }

  @Override
  public Text textNode() {
    return null;
  }

  // Debugger stuff

  @Override
  public Text relatedNode() {
    return null;
  }

  @Override
  public List<DebugBox> childDebugBoxes() {
    return List.of();
  }

  @Override
  public DebugSnapshot snapshotDebugInfo() {
    return DebugSnapshot.builder().build();
  }

  @Override
  public DebugBoxType debugBoxType() {
    return DebugBoxType.TEXT;
  }
  
}
