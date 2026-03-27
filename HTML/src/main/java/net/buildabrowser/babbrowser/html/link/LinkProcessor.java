package net.buildabrowser.babbrowser.html.link;

import java.util.Map;

import net.buildabrowser.babbrowser.fetch.FetchEngine;
import net.buildabrowser.babbrowser.html.html.LinkElement;
import net.buildabrowser.babbrowser.html.link.processors.StyleSheetLinkProcessor;

public interface LinkProcessor {

  static final Map<String, LinkProcessor> PROCESSORS = Map.of(
    "stylesheet", new StyleSheetLinkProcessor()
  );

  // Extra fetchEngine parameter needed to actually fetch
  void fetchAndProcessLinkedResource(LinkElement element, FetchEngine fetchEngine);

  static void processLink(LinkElement element, FetchEngine fetchEngine) {
    String rel = element.attributes().get("rel");
    if (rel == null) return;
    LinkProcessor processor = PROCESSORS.get(rel);
    if (processor == null) return;

    processor.fetchAndProcessLinkedResource(element, fetchEngine);
  }

}
