package net.buildabrowser.babbrowser.fetch;

import net.buildabrowser.babbrowser.infra.html.ParallelQueue;

public interface FetchEngineBackend {
  
    ParallelQueue createParallelQueue();

}
