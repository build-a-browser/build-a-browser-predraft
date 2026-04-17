package net.buildabrowser.babbrowser.html.util;

import java.net.URI;

import net.buildabrowser.babbrowser.fetch.FetchRequest;
import net.buildabrowser.babbrowser.fetch.mutable.MutableFetchRequest;

public final class HTMLFetchUtil {
  
  private HTMLFetchUtil() {}

	public static MutableFetchRequest createPotentialCORSRequest(URI url) {
		MutableFetchRequest request = FetchRequest.createMutable();
		request.appendURL(url);
		return request;
	}



}
