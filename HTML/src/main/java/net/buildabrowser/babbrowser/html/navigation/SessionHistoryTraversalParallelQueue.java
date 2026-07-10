package net.buildabrowser.babbrowser.html.navigation;

import java.util.List;

import net.buildabrowser.babbrowser.html.navigation.imp.SessionHistoryTraversalParallelQueueImp;
import net.buildabrowser.babbrowser.html.scripting.GlobalObject;

public interface SessionHistoryTraversalParallelQueue {
  
  void queue(NavigationSteps steps);

  List<NavigationSteps> asList();

  public static SessionHistoryTraversalParallelQueue create(
    GlobalObject globalObject
  ) {
    SessionHistoryTraversalParallelQueueImp queue
      = new SessionHistoryTraversalParallelQueueImp();
    queue.start(globalObject.agent().eventLoop());
    return queue;
  }

}
