package net.buildabrowser.babbrowser.html.ua;

import net.buildabrowser.babbrowser.fetch.FetchRequest;
import net.buildabrowser.babbrowser.fetch.FetchResponse;

public interface DownloadManager {

  // Not guaranteed to be called
  default boolean allowDownload(FetchRequest request) {
    return true;
  }

  default boolean allowDownload(FetchResponse response) {
    return true;
  }

  void startDownload(FetchResponse response, String suggestedFilename);

}
