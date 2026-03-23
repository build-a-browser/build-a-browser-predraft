package net.buildabrowser.babbrowser.fetch;

import net.buildabrowser.babbrowser.fetch.FetchParameters.ProcessResponseConsumeBody;

public record FetchParams(
  FetchRequest request,
  ProcessResponseConsumeBody processResponseConsumeBody
) {
  
}
