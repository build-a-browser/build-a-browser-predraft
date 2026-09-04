package net.buildabrowser.babbrowser.renderer.input.imp;

import net.buildabrowser.babbrowser.html.events.EventLoop;
import net.buildabrowser.babbrowser.html.events.TaskSource;
import net.buildabrowser.babbrowser.html.scripting.GlobalObject;
import net.buildabrowser.babbrowser.renderer.input.OffThreadWriteTextController;
import net.buildabrowser.babbrowser.renderer.input.WriteTextController;

public class OffThreadWriteTextControllerImp implements OffThreadWriteTextController {

  private final WriteTextController target;
  private final GlobalObject globalObject;

  public OffThreadWriteTextControllerImp(
    WriteTextController target,
    GlobalObject globalObject
  ) {
    this.target = target;
    this.globalObject = globalObject;
  }

  @Override
  public void setCursorFlat(int cursorFlat) {
    inSession(() -> target.setCursorFlat(cursorFlat));
  }

  @Override
  public void insertText(String text) {
    inSession(() -> target.insertText(text));
    // TODO: Need to run onUpdateSelection
  }

  @Override
  public void replaceText(String text) {
    inSession(() -> target.replaceText(text));
  }

  @Override
  public void moveCursorForward(int i) {
    inSession(() -> target.moveCursorForward(i));
  }

  @Override
  public void moveCursorDownward(int i) {
    inSession(() -> target.moveCursorDownward(i));
  }

  @Override
  public void submit() {
    inSession(() -> target.submit());
  }

  private void inSession(Runnable action) {
    // TODO: Batch items into single task
    EventLoop.queueGlobalTask(
      TaskSource.USER_INTERACTION, globalObject, action);
  }
  
}
