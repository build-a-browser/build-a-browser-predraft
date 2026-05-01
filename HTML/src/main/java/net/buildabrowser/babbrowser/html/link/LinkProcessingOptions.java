package net.buildabrowser.babbrowser.html.link;

import java.net.URI;

import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.fetch.FetchClient;

public record LinkProcessingOptions(
  String href,
  String type,
  URI baseURL,
  FetchClient environment,
  Document document
) {
  
}
