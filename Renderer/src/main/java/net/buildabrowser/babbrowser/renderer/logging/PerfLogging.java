package net.buildabrowser.babbrowser.renderer.logging;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PerfLogging {

  private static final long LONG_BOX_TIME = 100;
  private static final long LONG_STYLE_TIME = 100;
  private static final long LONG_LAYOUT_TIME = 100;
  private static final long LONG_PAINT_TIME = 100;
  private static final long LONG_WINDOW_PAINT_TIME = 16;

  private static final Logger LOGGER = LoggerFactory.getLogger(PerfLogging.class);
  
  private PerfLogging() {}

  public static void logDownloadTime(long downloadStartTime, URI pageUrl) {
    long elapsedTime = System.currentTimeMillis() - downloadStartTime;
    LOGGER.info("Page download completed in {} ms ({})", elapsedTime, pageUrl);
  }

  public static void logParseTime(long activeParseTime, URI pageUrl) {
    LOGGER.info("Page parse completed in {} ms of active time ({})", activeParseTime, pageUrl);
  }

  public static void logBoxTime(long boxStartTime) {
    long elapsedTime = System.currentTimeMillis() - boxStartTime;
    if (elapsedTime > LONG_BOX_TIME) {
      LOGGER.warn("Long boxing cycle took {} ms", elapsedTime);
    } else {
      LOGGER.trace("Boxing cycle took {} ms", elapsedTime);
    }
  }

  public static void logStyleTime(long styleStartTime) {
    long elapsedTime = System.currentTimeMillis() - styleStartTime;
    if (elapsedTime > LONG_STYLE_TIME) {
      LOGGER.warn("Long style cycle took {} ms", elapsedTime);
    } else {
      LOGGER.trace("Style cycle took {} ms", elapsedTime);
    }
  }

  public static void logLayoutTime(long layoutStartTime) {
    long elapsedTime = System.currentTimeMillis() - layoutStartTime;
    if (elapsedTime > LONG_LAYOUT_TIME) {
      LOGGER.warn("Long layout cycle took {} ms", elapsedTime);
    } else {
      LOGGER.trace("Layout cycle took {} ms", elapsedTime);
    }
  }

  public static void logPaintTime(long longTime) {
    long elapsedTime = System.currentTimeMillis() - longTime;
    if (elapsedTime > LONG_PAINT_TIME) {
      LOGGER.warn("Long document paint cycle took {} ms", elapsedTime);
    } else {
      LOGGER.trace("Document paint cycle took {} ms", elapsedTime);
    }
  }

  public static void logWindowPaintTime(long longTime) {
    long elapsedTime = System.currentTimeMillis() - longTime;
    if (elapsedTime > LONG_WINDOW_PAINT_TIME) {
      LOGGER.warn("Long window paint cycle took {} ms", elapsedTime);
    } else {
      LOGGER.trace("Window paint cycle took {} ms", elapsedTime);
    }
  }

}
