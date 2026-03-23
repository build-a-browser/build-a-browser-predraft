package net.buildabrowser.babbrowser.infra.html;

public interface ParallelQueue {
  
  void queue(Runnable runnable);

}
