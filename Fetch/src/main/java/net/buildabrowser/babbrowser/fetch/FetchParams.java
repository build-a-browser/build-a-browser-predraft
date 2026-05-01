package net.buildabrowser.babbrowser.fetch;

import net.buildabrowser.babbrowser.fetch.FetchParameters.ProcessResponse;
import net.buildabrowser.babbrowser.fetch.FetchParameters.ProcessResponseConsumeBody;

public record FetchParams(
  FetchRequest request,
  ProcessResponse processResponse,
  ProcessResponseConsumeBody processResponseConsumeBody,
  FetchDestinatation taskDestination,
  FetchController controller
) {
  
}
