package net.buildabrowser.babbrowser.html.navigation;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.fetch.FetchController;
import net.buildabrowser.babbrowser.fetch.FetchParameters;
import net.buildabrowser.babbrowser.fetch.FetchRequest;
import net.buildabrowser.babbrowser.fetch.FetchRequest.RedirectMode;
import net.buildabrowser.babbrowser.fetch.FetchRequest.RequestMode;
import net.buildabrowser.babbrowser.fetch.FetchResponse;
import net.buildabrowser.babbrowser.fetch.FetchUtil;
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
    request.appendURL(entry.url());
    request.setClient(sourceSnapshotParams.fetchClient());
    request.setRedirectMode(RedirectMode.MANUAL);
    request.setMode(RequestMode.NAVIGATE);
    // TODO: Other stuff
    
    // TODO: The spec defines this code in a blocking fashion, it runs in parallel so that works
    // but in the future maybe change it to be callback oriented
    // Also annoying AtomicReference since I can't reset the future, and Java finality rules
    AtomicReference<CompletableFuture<FetchResponse>> response = new AtomicReference<>();
    FetchController fetchController = null;
    URI currentURL = request.currentURL();
    while (true) {
      response.set(new CompletableFuture<>());
      if (fetchController == null) {
        FetchParameters fetchParameters = new FetchParameters();
        fetchParameters.request = request;
        fetchParameters.processResponse = fetchedResponse -> response.get().complete(fetchedResponse);
        fetchController = uaNavigableOptions.fetchEngine().fetch(fetchParameters);
      } else {
        assert fetchController.nextManualRedirectSteps != null;
        fetchController.nextManualRedirectSteps.run();
      }
      CommonUtil.rethrowV(() -> response.get().get());

      URI locationURL = null;
      try {
        locationURL = response.get().get().locationURL(currentURL.getFragment());
      } catch (URISyntaxException e) {
        break;
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
      if (locationURL == null) break;
      assert locationURL instanceof URI;
      // TODO: Update the document state
      if (!FetchUtil.isHTTPScheme(locationURL.getScheme())) {
        // TODO: Set resource to null
        break;
      }

      currentURL = locationURL;
      entry.setURL(currentURL);
    }

    return new NavigationParams(
      navigable,
      CommonUtil.rethrow(() -> response.get().get()));
  }

}
