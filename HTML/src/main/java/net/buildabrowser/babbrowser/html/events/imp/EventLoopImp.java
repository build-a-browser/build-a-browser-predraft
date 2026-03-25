package net.buildabrowser.babbrowser.html.events.imp;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.html.events.EventLoop;
import net.buildabrowser.babbrowser.html.events.Task;
import net.buildabrowser.babbrowser.html.events.TaskSource;

public class EventLoopImp implements EventLoop {

  private final Map<TaskSource, TaskQueue> tasks = new HashMap<>();
  // For now, just doing round-robin. Will change in the future
  private final Set<TaskSource> taskOrder = new LinkedHashSet<>();

  private final ExecutorService threadGroup = Executors.newWorkStealingPool(
    Math.max(Runtime.getRuntime().availableProcessors(), 4));

  @SuppressWarnings("unused")
  private Task currentlyRunningTask;

  protected AtomicBoolean isClosing = new AtomicBoolean(false);

  // TODO: Properly shut down the event loop

  @Override
  public void start() {
    // TODO: Need to do timing and stuff
    while (!isClosing.get()) {
      synchronized(tasks) {
        TaskQueue taskQueue = chooseTaskQueue();
        if (taskQueue != null) {
          Task oldestTask = taskQueue.tasks().iterator().next();
          currentlyRunningTask = oldestTask;
          taskQueue.tasks().remove(oldestTask);
          oldestTask.steps().run();
          currentlyRunningTask = null;
          // TODO: Run microtasks
        }

        runLoopSpecificTask();
        // TODO: Properly start an idle period

        if (taskQueue == null) {
          try {
            tasks.wait();
          } catch (InterruptedException e) {}
        }
      }
    }
  }

  @Override
  public void shutdown() {
    // TODO: Check spec for proper way to shutdown
    isClosing.set(true);
    threadGroup.shutdown();
  }

  @Override
  public void runInParallel(Runnable runnable) {
    threadGroup.submit(runnable);
  }

  @Override
  public void queueTask(Runnable steps, TaskSource source, Document document) {
    synchronized(tasks) { 
      Task task = new Task(steps, source, document);
      TaskQueue queue = tasks.computeIfAbsent(source,
        _ -> new TaskQueue(new LinkedHashSet<>()));
      queue.tasks().add(task);
      taskOrder.add(source);
      tasks.notifyAll();
    }
  }

  protected void runLoopSpecificTask() {}

  private TaskQueue chooseTaskQueue() {
    for (TaskSource source: taskOrder) {
      TaskQueue queue = tasks.get(source);
      if (!queue.tasks().isEmpty()) {
        taskOrder.remove(source);
        taskOrder.add(source);
        return queue;
      }
    }

    return null;
  }

  // A wrapper in case this needs extended in the future
  private static record TaskQueue(Set<Task> tasks) {}

}
