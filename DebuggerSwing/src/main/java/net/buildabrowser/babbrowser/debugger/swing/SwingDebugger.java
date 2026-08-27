package net.buildabrowser.babbrowser.debugger.swing;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.buildabrowser.babbrowser.debugger.core.Debugger;
import net.buildabrowser.babbrowser.debugger.core.FrameDebugger;
import net.buildabrowser.babbrowser.debugger.swing.gui.SwingDebuggerWindow;

public class SwingDebugger implements Debugger {

  private final List<FrameDebugger> attachedDebuggers = new ArrayList<>();
  private final List<SwingDebuggerListener> listeners = new ArrayList<>(1);

  public SwingDebugger() {
    SwingUtilities.invokeLater(() -> {
      JFrame.setDefaultLookAndFeelDecorated(true);
      new SwingDebuggerWindow(this);
    });
  }

  @Override
  public FrameDebugger create() {
    SwingFrameDebugger frameDebugger = new SwingFrameDebugger(this);
    attachedDebuggers.add(frameDebugger);
    forEachListener(listener -> listener.onAttach(frameDebugger));

    return frameDebugger;
  }

  public void detach(SwingFrameDebugger frameDebugger) {
    boolean wasRemoved = attachedDebuggers.remove(frameDebugger);
    if (wasRemoved) {
      forEachListener(listener -> listener.onDetach(frameDebugger));
    }
  }

  public void attachListener(SwingDebuggerListener listener) {
    listeners.add(listener);
  }

  public void removeListener(SwingDebuggerListener listener) {
    listeners.remove(listener);
  }

  public void forEachListener(
    Consumer<SwingDebuggerListener> listenerFunc
  ) {
    for (SwingDebuggerListener listener: listeners) {
      listenerFunc.accept(listener);
    }
  }

  public static interface SwingDebuggerListener {

    void onAttach(SwingFrameDebugger frameDebugger);

    void onDetach(SwingFrameDebugger frameDebugger);

  }
  
}
