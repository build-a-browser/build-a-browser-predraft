package net.buildabrowser.babbrowser.html;

import net.buildabrowser.babbrowser.html.events.RelevantAgent;
import net.buildabrowser.babbrowser.html.events.WindowEventLoop;

public interface SimilarOriginWindowAgent extends RelevantAgent {

  WindowEventLoop eventLoop();

}
