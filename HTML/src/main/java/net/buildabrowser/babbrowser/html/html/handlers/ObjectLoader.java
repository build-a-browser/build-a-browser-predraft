package net.buildabrowser.babbrowser.html.html.handlers;

import net.buildabrowser.babbrowser.fetch.FetchResponse;
import net.buildabrowser.babbrowser.html.html.HTMLObjectElement;
import net.buildabrowser.babbrowser.html.html.HTMLObjectElement.ObjectRepresentation;

public interface ObjectLoader {
  
  boolean supportsMimeType(String mimeType);

  ObjectRepresentation load(
    FetchResponse response,
    HTMLObjectElement element
  );

}
