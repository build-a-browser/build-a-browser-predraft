package net.buildabrowser.babbrowser.html.navigation.imp;

import java.util.LinkedList;
import java.util.List;

import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.html.events.EventLoop;
import net.buildabrowser.babbrowser.html.navigation.NavigationSteps;
import net.buildabrowser.babbrowser.html.navigation.SessionHistoryTraversalParallelQueue;

public class SessionHistoryTraversalParallelQueueImp
  implements SessionHistoryTraversalParallelQueue {

  private final List<NavigationSteps> stepList = new LinkedList<>();

  // TODO: I guarentee there will be concurrency issues here

  @Override
  public void queue(NavigationSteps steps) {
    synchronized (stepList) {
      stepList.add(steps);
      stepList.notify();
    }
  }

  @Override
  public List<NavigationSteps> asList() {
    return stepList;
  }

  public void start(EventLoop eventLoop) {
    eventLoop.runInParallel(this::runLoop);
  }

  private void runLoop() {
    while (true) {
      Runnable runTask = null;
      synchronized (stepList) {
        if (stepList.isEmpty()) {
          CommonUtil.rethrowV(() -> stepList.wait());
        }
        if (!(stepList.isEmpty())) {
          runTask = stepList.remove(0).algorithm();
        }
      }

      if (runTask != null) {
        try {
          runTask.run();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }
  
}
