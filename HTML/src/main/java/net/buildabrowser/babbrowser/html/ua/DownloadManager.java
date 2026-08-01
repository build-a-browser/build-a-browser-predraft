package net.buildabrowser.babbrowser.html.ua;

import net.buildabrowser.babbrowser.fetch.FetchResponse;

public interface DownloadManager {

  void startDownload(FetchResponse response, String suggestedFilename);

}
