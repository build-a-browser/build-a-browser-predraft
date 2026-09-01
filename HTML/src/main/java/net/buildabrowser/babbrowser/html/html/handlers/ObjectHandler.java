package net.buildabrowser.babbrowser.html.html.handlers;

import java.net.URI;
import java.util.concurrent.atomic.AtomicBoolean;

import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.fetch.FetchEngine;
import net.buildabrowser.babbrowser.fetch.FetchParameters;
import net.buildabrowser.babbrowser.fetch.FetchRequest;
import net.buildabrowser.babbrowser.fetch.FetchRequest.RequestMode;
import net.buildabrowser.babbrowser.fetch.FetchResponse;
import net.buildabrowser.babbrowser.fetch.mutable.MutableFetchRequest;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.html.HTMLObjectElement;
import net.buildabrowser.babbrowser.html.html.HTMLObjectElement.ChildrenRepresentation;
import net.buildabrowser.babbrowser.html.html.HTMLObjectElement.ObjectRepresentation;

public final class ObjectHandler {

  private static final ObjectRepresentation CHILDREN_REPRESENTATION
    = new ChildrenRepresentation();

  private ObjectHandler() {}

  public static void determineObjectRepresentation(
    HTMLObjectElement element,
    FetchEngine fetchEngine
  ) {
    HTMLDocument htmlDocument =
      element.nodeDocument() instanceof HTMLDocument htmlDocument1 ?
        htmlDocument1 : null;
    if (htmlDocument == null) {
      useFallback(element);
      return;
    }


    // TODO: This will break if multi-renderer support is added in the future
    ObjectLoader objectLoader = htmlDocument.renderer().objectLoader();

    // TODO: Check fallback preference
    // TODO: Check ancestors
    String dataAttr = element.getAttribute("data");
    if (dataAttr != null && !dataAttr.isEmpty()) {
      String typeAttr = element.getAttribute("type");
      if (
        typeAttr != null
        && !objectLoader.supportsMimeType(typeAttr)
      ) {
        useFallback(element);
        return;
      }

      URI url = CommonUtil.tryOrNull(
        () -> htmlDocument.baseURL().resolve(dataAttr));
      if (url == null) {
        // TODO: Fire error event
        useFallback(element);
        return;
      }

      MutableFetchRequest request = FetchRequest.createMutable();
      request.appendURL(url);
      request.setClient(htmlDocument.relevantSettingsObject());
      // TODO: Set destination, credentials mode, initiator type
      request.setMode(RequestMode.NAVIGATE);

      AtomicBoolean didRespond = new AtomicBoolean();
      FetchParameters parameters = new FetchParameters() {};
      parameters.request = request;
      parameters.processResponse = response -> {
        didRespond.set(true);
        
        String resourceType = determineResourceType(response, element);
        // TODO: Other mime types, check enables enabled, check XML
        if (
          resourceType != null
          && resourceType.startsWith("image/")
        ) {
          ObjectRepresentation representation = objectLoader.load(response, element);
          if (representation != null) {
            element.setRepresentation(representation);
            return;
          }
        }

        useFallback(element);
      };
      fetchEngine.fetch(parameters);
      // TODO: Delay load event
      if (!didRespond.get()) {
        useFallback(element);
      }
    }
  }

  private static void useFallback(HTMLObjectElement element) {
    element.setRepresentation(CHILDREN_REPRESENTATION);
    // TODO: Destroy a child navigable
  }

  private static String determineResourceType(
    FetchResponse response,
    HTMLObjectElement element
  ) {
    String contentType = normalizeMimeType(
      response.headerList().get("Content-Type"));
    String attrType = normalizeMimeType(element.getAttribute("type"));
    if (contentType != null) {
      boolean binary = false;
      // TODO: Sniff binary for text/plain
      binary |= contentType.equals("application/octet-stream");
      if (!binary) {
        return contentType;
      }

      if (
        attrType != null
        && !attrType.endsWith("application/octet-stream")
      ) {
        if (
          attrType.startsWith("image/")
          && !attrType.endsWith("+xml")
        ) {
          return attrType;
        }

        return null;
      }
    } else {
      // TODO: Sniff type if not specified
      if (
        !("application/octet-stream".equals(attrType))
      ) {
        return attrType;
      }
    }

    // TODO: Match against URL
    return null;
  }

  // TODO: Better handling (and there's probably already a better function
  // defined in the spec)
  private static String normalizeMimeType(String mimeType) {
    if (mimeType == null) return null;

    int semiIndex = mimeType.indexOf(';');
    if (semiIndex != -1) {
      // TODO: Better handling
      mimeType = mimeType.substring(0, semiIndex);
    }

    return mimeType;
  }
  
}
