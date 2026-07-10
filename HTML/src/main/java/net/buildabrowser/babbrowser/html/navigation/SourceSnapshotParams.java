package net.buildabrowser.babbrowser.html.navigation;

import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.fetch.FetchClient;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.html.RenderableDocument;

public record SourceSnapshotParams(
  FetchClient fetchClient
) {

  public static SourceSnapshotParams snapshot(RenderableDocument sourceDocument) {
    if (sourceDocument instanceof HTMLDocument htmlDocument) {
      return new SourceSnapshotParams(htmlDocument.relevantSettingsObject());
    } else {
      // TODO: This is only supposed to handle the null case, but currently also handles
      // a Document that is not HTMLDocument. Problem is, there is currently no way to get
      // the relevant settings object of a raw Document.
      return new SourceSnapshotParams(null);
    }
  }

  public static SourceSnapshotParams snapshot(Document sourceDocument) {
    if (sourceDocument instanceof HTMLDocument htmlDocument) {
      return new SourceSnapshotParams(htmlDocument.relevantSettingsObject());
    } else {
      // TODO: Same as above
      return new SourceSnapshotParams(null);
    }
  }
  
}
