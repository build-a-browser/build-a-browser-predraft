package net.buildabrowser.babbrowser.html.link.processors;

import java.net.URI;

import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.fetch.FetchEngine;
import net.buildabrowser.babbrowser.fetch.FetchParameters;
import net.buildabrowser.babbrowser.fetch.FetchRequest;
import net.buildabrowser.babbrowser.fetch.FetchResponse;
import net.buildabrowser.babbrowser.fetch.mutable.MutableFetchRequest;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.html.LinkElement;
import net.buildabrowser.babbrowser.html.link.LinkProcessingOptions;
import net.buildabrowser.babbrowser.html.link.LinkProcessor;
import net.buildabrowser.babbrowser.html.util.HTMLFetchUtil;

public abstract class DefaultLinkProcessor implements LinkProcessor {
  
  @Override
  public void fetchAndProcessLinkedResource(LinkElement el, FetchEngine fetchEngine) {
    LinkProcessingOptions options = createLinkOptions(el);
    // TODO: Spec says link without href or source set, but where is the right place to reject it?
    if (options.href() == null) return;
    FetchRequest request = createALinkRequest(options);
    if (request == null) return;
    // TODO: Synchronous flag
    boolean result = linkedResourceFetchSetup(el, request);
    if (!result) return;
    // TODO: Initiator type
    FetchParameters fetchParameters = new FetchParameters();
    fetchParameters.request = request;
    fetchParameters.processResponseConsumeBody = (response, success, bodyBytes) -> {
      if (bodyBytes == null) success = false;
      // TODO: Check status
      // TODO: Wait for critical subresources to finish loading
      processLinkedResource(el, success, response, bodyBytes);
    };
    fetchEngine.fetch(fetchParameters);
  }

  protected abstract boolean linkedResourceFetchSetup(LinkElement el, FetchRequest request);

  protected abstract void processLinkedResource(
    LinkElement el, boolean success, FetchResponse response, byte[] bodyBytes
  );

  private LinkProcessingOptions createLinkOptions(LinkElement el) {
    // TODO: More options to collect
    HTMLDocument document = (HTMLDocument) el.nodeDocument();
    String href = el.getAttribute("href");
    String type = el.getAttribute("type");
    return new LinkProcessingOptions(
      href,
      type,
      document.baseURL(),
      document.relevantSettingsObject(),
      document);
  }

  private FetchRequest createALinkRequest(LinkProcessingOptions options) {
    assert options.href() != null;
    // If ever a custom URL parser is needed, the module will need to include :Network
    URI url = CommonUtil.tryOrNull(() -> options.baseURL().resolve(options.href()));
    if (url == null) return null;
    MutableFetchRequest request = HTMLFetchUtil.createPotentialCORSRequest(url);
    request.setClient(options.environment());
    // TODO: Other spec stuff
    return request;
  }
  
}
