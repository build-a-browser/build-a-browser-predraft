package net.buildabrowser.babbrowser.html.events.imp;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.buildabrowser.babbrowser.common.util.GCUtil;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.html.events.EventLoop;
import net.buildabrowser.babbrowser.html.events.Task;
import net.buildabrowser.babbrowser.html.events.TaskSource;

public class EventLoopImp implements EventLoop {

  private static final Logger LOGGER = LoggerFactory.getLogger(EventLoopImp.class);

  private static final int LONG_RENDER_DURATION = 32;

  private final Map<TaskSource, TaskQueue> tasks = new HashMap<>();
  // For now, just doing round-robin. Will change in the future
  private final Set<TaskSource> taskOrder = new LinkedHashSet<>();

  private final ExecutorService threadGroup = Executors.newWorkStealingPool(
    Math.max(Runtime.getRuntime().availableProcessors(), 4));

  @SuppressWarnings("unused")
  private Task currentlyRunningTask;

  private int numTasks = 0;

  private float lastRenderDuration = 16;
  private long lastRenderTime = 0;;

  protected AtomicBoolean hasStarted = new AtomicBoolean(false);
  protected AtomicBoolean isClosing = new AtomicBoolean(false);

  // TODO: Properly shut down the event loop

  @Override
  public void start() {
    hasStarted.set(true);
    // TODO: Need to do timing and stuff
    while (!isClosing.get()) {
      TaskQueue taskQueue = null;
      Task oldestTask = null;
      synchronized(tasks) {
        taskQueue = chooseTaskQueue();
        if (taskQueue != null) {
          oldestTask = taskQueue.tasks().iterator().next();
          taskQueue.tasks().remove(oldestTask);
        }
      }

      if (oldestTask != null) {
        currentlyRunningTask = oldestTask;

        // Include some extra non-spec timing code for scheduling heuristics
        long taskStart = System.currentTimeMillis();
        try {
          oldestTask.steps().run();
        } catch (Exception e) {
          LOGGER.error("Task running in main event loop thread failed!", e);
        }
        if (oldestTask.source().equals(TaskSource.RENDERING)) {
          this.lastRenderTime = taskStart;
          this.lastRenderDuration = System.currentTimeMillis() - lastRenderTime;
        }
        
        currentlyRunningTask = null;
        // TODO: Run microtasks
        GCUtil.fastGC();
      }

      runLoopSpecificTask();
    
      // TODO: Properly start an idle period

      if (numTasks <= 1) {
        GCUtil.slowGC();
      }
      synchronized (tasks) {
        if (numTasks > 0) numTasks--;
        if (numTasks == 0) {
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
    threadGroup.submit(() -> {
      try {
        runnable.run();
      } catch (Throwable e) {
        LOGGER.error("Task running in parallel failed!", e);
      }
  });
  }

  @Override
  public void queueTask(Runnable steps, TaskSource source, Document document) {
    synchronized(tasks) { 
      Task task = new Task(steps, source, document);
      TaskQueue queue = tasks.computeIfAbsent(source,
        _ -> new TaskQueue(new LinkedHashSet<>()));
      queue.tasks().add(task);
      taskOrder.add(source);
      numTasks++;
      tasks.notifyAll();
    }
  }

  protected void runLoopSpecificTask() {}

  // Rendering can take a long time, so we'll let other tasks fill a similar duration
  private TaskQueue chooseTaskQueue() {
    TaskQueue renderingQueue = tasks.get(TaskSource.RENDERING);
    boolean hasRenderingTasks = renderingQueue != null && !renderingQueue.tasks().isEmpty();
    if (
      hasRenderingTasks
      && lastRenderDuration > 32
      && System.currentTimeMillis() - lastRenderTime > lastRenderDuration * 2
    ) {
      return renderingQueue;
    }

    for (TaskSource source: taskOrder) {
      if (
        source.equals(TaskSource.RENDERING)
        && lastRenderDuration > LONG_RENDER_DURATION
      ) continue;
      TaskQueue queue = tasks.get(source);
      if (!queue.tasks().isEmpty()) {
        taskOrder.remove(source);
        taskOrder.add(source);
        return queue;
      }
    }

    if (hasRenderingTasks) {
      return renderingQueue;
    }

    return null;
  }

  // A wrapper in case this needs extended in the future
  private static record TaskQueue(Set<Task> tasks) {}

}
