package net.buildabrowser.babbrowser.html.navigation;

import java.util.concurrent.CompletableFuture;

import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.fetch.FetchParameters;
import net.buildabrowser.babbrowser.fetch.FetchRequest;
import net.buildabrowser.babbrowser.fetch.FetchResponse;
import net.buildabrowser.babbrowser.fetch.mutable.MutableFetchRequest;

public record NavigationParams(
  Navigable navigable,
  FetchResponse response
) {

  public static NavigationParams createByFetching(
    UANavigableOptions uaNavigableOptions, // UA extension
    SessionHistoryEntry entry, Navigable navigable,
    SourceSnapshotParams sourceSnapshotParams,
    UserNavigationInvolvement userInvolvement
  ) {
    MutableFetchRequest request = FetchRequest.createMutable();
    request.setURL(entry.url());
    request.setClient(sourceSnapshotParams.fetchClient());
    // TODO: Other stuff
    
    // TODO: The spec defines this code in a blocking fashion, it runs in parallel so that works
    // but in the future maybe change it to be callback oriented
    CompletableFuture<FetchResponse> response;
    // TODO: Track the fetch controller
    while (true) {
      response = new CompletableFuture<>();
      CompletableFuture<FetchResponse> response_ = response; // Java annoyance
      FetchParameters fetchParameters = new FetchParameters();
      fetchParameters.request = request;
      fetchParameters.processResponse = fetchedResponse -> response_.complete(fetchedResponse);
      uaNavigableOptions.fetchEngine().fetch(fetchParameters);
      CommonUtil.rethrowV(() -> response_.get());
      // TODO: Other steps
      // TODO: This currently defeats the point of the loop, but later location redirects will be added
      break;
    }

    CompletableFuture<FetchResponse> response_ = response;
    return new NavigationParams(
      navigable,
      CommonUtil.rethrow(() -> response_.get()));
  }

}
