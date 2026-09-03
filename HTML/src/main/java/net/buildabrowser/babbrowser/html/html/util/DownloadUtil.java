package net.buildabrowser.babbrowser.html.html.util;

import static net.buildabrowser.babbrowser.html.util.HTMLDomUtil.isHtmlElement;

import java.net.URI;

import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.common.util.StringUtil;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.fetch.FetchClient;
import net.buildabrowser.babbrowser.fetch.FetchEngine;
import net.buildabrowser.babbrowser.fetch.FetchParameters;
import net.buildabrowser.babbrowser.fetch.FetchRequest;
import net.buildabrowser.babbrowser.fetch.FetchResponse;
import net.buildabrowser.babbrowser.fetch.mutable.MutableFetchRequest;
import net.buildabrowser.babbrowser.html.events.EventLoop;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.navigation.Navigable;
import net.buildabrowser.babbrowser.html.navigation.UserNavigationInvolvement;
import net.buildabrowser.babbrowser.html.ua.DownloadManager;

public final class DownloadUtil {
  
  private DownloadUtil() {}

  public static void downloadHyperlink(
    Element subject,
    String hyperlinkSuffix,
    UserNavigationInvolvement userInvolvement
  ) {
    if (NavUtil.cannotNavigate(subject)) return;
    // TODO: Active sandboxing
    String urlString = CommonUtil.tryOrNull(() -> NavUtil.resolveURL(
      subject.getAttribute("href"), subject.nodeDocument()).toString());
    if (urlString == null) return;
    if (hyperlinkSuffix != null) {
      urlString += hyperlinkSuffix;
    }
    String urlString_ = urlString;
    // TODO: Fire events and stuff
    // TODO: Prefer RenderableDocument
    HTMLDocument document = (HTMLDocument) subject.nodeDocument();
    Navigable nodeNavigable = document.nodeNavigable();
    EventLoop eventLoop = nodeNavigable.activeWindow().agent().eventLoop();
    eventLoop.runInParallel(() -> {
      // TODO: Better way to get the environment settings object
      FetchClient client = document.relevantSettingsObject();

      // TODO: Screen the download
      MutableFetchRequest request = FetchRequest.createMutable();
      request.appendURL(URI.create(urlString_));
      request.setClient(client);
      // TODO: Set other flags

      DownloadManager downloadManager =
        nodeNavigable.uaNavigableOptions().uiFeatures().downloadManager();
      if (!downloadManager.allowDownload(request)) return;

      FetchParameters fetchParameters = new FetchParameters();
      fetchParameters.request = request;
      // NOSPEC: The synchronous flag seems to have been removed, use processResponse instead
      fetchParameters.processResponse = response -> {
        handleAsDownload(response, nodeNavigable, null, subject);
      };
      FetchEngine fetchEngine = document.uaNavigableOptions().fetchEngine();
      fetchEngine.fetch(fetchParameters);
    });
  }

  public static void handleAsDownload(
    FetchResponse response,
    Navigable nodeNavigable,
    String navigationId,
    Element initiator // NOSPEC: Accept initiator as param
  ) {
    // TODO: WebDriver BiDi stuff
    DownloadManager downloadManager =
      nodeNavigable.uaNavigableOptions().uiFeatures().downloadManager();
    // NOSPEC: Check if download allowed
    if (!downloadManager.allowDownload(response)) {
      // TODO: Abort the response
      return;
    }
    String suggestedFilename = getSuggestedFileName(
      response, initiator);
    downloadManager.startDownload(response, suggestedFilename);
  }

  private static String getSuggestedFileName(
    FetchResponse response,
    Element initiator // NOSPEC: Accept initiator as param
  ) {
    String filename = null;
    // TODO: Check content disposition, trusted, other stuff
    String downloadAttr = null;
    if (
      (
        isHtmlElement(initiator, "a")
        || isHtmlElement(initiator, "area"))
      && (downloadAttr = initiator.getAttribute("download")) != null
      && downloadAttr.length() > 0
    ) {
      filename = downloadAttr;
    } else {
      String urlString = response.url().getPath();
      String[] pathParts = urlString == null ?
        new String[0] :
        StringUtil.chSplit(urlString, '/');
      if (
        pathParts.length > 0
        && pathParts[pathParts.length - 1].trim().length() > 0
      ) {
        filename = pathParts[pathParts.length - 1].trim().replace(' ', '_');
      } else {
        return "file.txt";
      }
    }

    // TODO: Sanitize
    return filename;
  }

}
