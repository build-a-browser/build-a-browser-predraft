package net.buildabrowser.babbrowser.fetch;

public interface FetchDestinatation {

  void queueFetchTask(Runnable task);

  void runInParallel(Runnable task);
  
}
