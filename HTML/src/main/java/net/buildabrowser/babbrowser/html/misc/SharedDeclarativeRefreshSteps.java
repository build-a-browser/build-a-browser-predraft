package net.buildabrowser.babbrowser.html.misc;

import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

import net.buildabrowser.babbrowser.common.util.ASCIIUtil;
import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.navigation.NavigateParameters;
import net.buildabrowser.babbrowser.html.navigation.NavigationHistoryBehavior;
import net.buildabrowser.babbrowser.infra.StringUtil;

public final class SharedDeclarativeRefreshSteps {
  
  private SharedDeclarativeRefreshSteps() {}

  // TODO: Optionally handle meta element
  public static void run(HTMLDocument document, String input) {
    if (document.willDeclarativelyRefresh()) return;
    int[] position = new int[1];
    StringUtil.skipASCIIWhitespace(input, position);
    
    int time = 0;
    String timeString = StringUtil.collectCodePoints(input, ch -> ASCIIUtil.isDigit(ch), position);
    if (timeString.isEmpty()) {
      if (input.codePointAt(position[0]) != '.') return;
    } else {
      // TODO: Proper way to parse the number
      time = Integer.valueOf(timeString);
    }
    StringUtil.collectCodePoints(input, ch -> ASCIIUtil.isDigit(ch) || ch == '.', position);
    
    URI urlRecord = document.url();

    if (position[0] < input.length()) {
      int ch = input.codePointAt(position[0]);
      if (!(ch == ';' || ch == ',' || ASCIIUtil.isWhitespace(ch))) return;
      StringUtil.skipASCIIWhitespace(input, position);
      ch = input.codePointAt(position[0]);
      if (ch == ';' || ch == ',') position[0]++;
    }

    if (position[0] < input.length()) {
      urlRecord = ignoreURL(document, input, position);
      if (urlRecord == null) return;
    }

    document.setWillDeclarativelyRefresh(true);
    
    // TODO: Proper timer management
    URI urlRecord_ = urlRecord;
    new Timer().schedule(new TimerTask() {
      @Override
      public void run() {
        NavigateParameters navigateParameters = new NavigateParameters();
        navigateParameters.historyHandling = NavigationHistoryBehavior.REPLACE;
        // TODO: Shouldn't need to pass this parameter, but this fetch code currently gets a client this way
        navigateParameters.sourceDocument = document;
        document.nodeNavigable().navigate(urlRecord_, navigateParameters);
      }
    }, time);
  }

  private static URI ignoreURL(Document document, String input, int[] position) {
    String urlString = input.substring(position[0]);

    boolean hasU = false;
    boolean hasR = false;
    boolean hasL = false;

    int ch = input.codePointAt(position[0]);
    hasU = ch == 'U' || ch == 'u';
    if (hasU) {
      position[0]++;
      ch = input.codePointAt(position[0]);
      hasR = ch == 'R' || ch == 'r';
    }
    if (hasU) {
      position[0]++;
      ch = input.codePointAt(position[0]);
      hasL = ch == 'L' || ch == 'l';
    }
    if (hasL) {
      position[0]++;
    }

    boolean hasURL = hasU && hasR && hasL;
    boolean hasEquals = false;
    if (hasURL) {
      StringUtil.skipASCIIWhitespace(input, position);
      hasEquals = input.codePointAt(position[0]) == '=';
      if (hasEquals) position[0]++;
      StringUtil.skipASCIIWhitespace(input, position);
    }
    if (hasEquals || !hasU) {
      int quote = 0;
      ch = input.codePointAt(position[0]);
      if (ch == '\'' || ch == '"') {
        quote = ch;
        position[0]++;
      }

      urlString = input.substring(position[0]);
      int quoteIndex = urlString.indexOf(quote);
      if (quote != 0 && quoteIndex != -1) {
        urlString = urlString.substring(0, quoteIndex);
      }
    }

    // TODO: Proper encoding parse
    String urlString_ = urlString;
    URI urlRecord = CommonUtil.tryOrNull(() -> document.url().resolve(urlString_));
    if (urlRecord == null) return null;
    if (urlRecord.getScheme().equals("javascript")) return null;
    return urlRecord;
  }

}
