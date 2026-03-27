package net.buildabrowser.babbrowser.html.scripting;

import net.buildabrowser.babbrowser.fetch.FetchDestinatation;
import net.buildabrowser.babbrowser.html.events.EventLoop;
import net.buildabrowser.babbrowser.html.events.RelevantAgent;
import net.buildabrowser.babbrowser.html.events.TaskSource;

public interface GlobalObject extends FetchDestinatation {
  
  RelevantAgent agent();

  default void queueFetchTask(Runnable task) {
    EventLoop.queueGlobalTask(TaskSource.NETWORKING, this, task);
  }

}
