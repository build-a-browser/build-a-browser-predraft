package net.buildabrowser.babbrowser.html.events;

import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.html.events.imp.WindowEventLoopImp;
import net.buildabrowser.babbrowser.html.navigation.UANavigableOptions;
import net.buildabrowser.babbrowser.html.scripting.GlobalObject;
import net.buildabrowser.babbrowser.html.scripting.Window;

public interface EventLoop {

  void start();

  void shutdown();

  void runInParallel(Runnable runnable);

  void queueTask(Runnable steps, TaskSource source, Document document);

  static void queueGlobalTask(TaskSource source, GlobalObject global, Runnable steps) {
    EventLoop eventLoop = global.agent().eventLoop();
    Document document = global instanceof Window window ?
      window.associatedDocument() :
      null;
    eventLoop.queueTask(steps, source, document);
  }

  static WindowEventLoop createWindowEventLoop(UANavigableOptions navigableOptions) {
    return new WindowEventLoopImp(navigableOptions);
  }

}
