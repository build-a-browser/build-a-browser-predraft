package net.buildabrowser.babbrowser.fetch;

import net.buildabrowser.babbrowser.fetch.FetchParameters.ProcessResponse;
import net.buildabrowser.babbrowser.fetch.FetchParameters.ProcessResponseConsumeBody;
import net.buildabrowser.babbrowser.html.scripting.GlobalObject;

public record FetchParams(
  FetchRequest request,
  ProcessResponse processResponse,
  ProcessResponseConsumeBody processResponseConsumeBody,
  GlobalObject taskDestination
) {
  
}
