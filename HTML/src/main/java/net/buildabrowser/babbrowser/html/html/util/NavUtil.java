package net.buildabrowser.babbrowser.html.html.util;

import java.net.URI;

import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.html.html.RenderableDocument;
import net.buildabrowser.babbrowser.html.navigation.Navigable;
import net.buildabrowser.babbrowser.html.navigation.NavigateParameters;
import net.buildabrowser.babbrowser.html.navigation.UserNavigationInvolvement;

public final class NavUtil {
  
  private NavUtil() {}

  public static void followHyperlink(
    HTMLElement subject, String hyperlinkSuffix, UserNavigationInvolvement userInvolvement
  ) {
    // TODO: Check if cannot navigate
    String targetAttributeValue = "";

    // TODO: Check target

    // TODO: Proper way to parse a URL
    URI urlRecord = CommonUtil.tryOrNull(() -> subject.nodeDocument().url().resolve(
      subject.getAttribute("href")));
    if (urlRecord == null) return;
    
    // TODO: Noopener
    Navigable targetNavigable = chooseANavigable(targetAttributeValue, subject.nodeNavigable());
    if (targetNavigable == null) return;

    String urlString = urlRecord.toString();
    if (hyperlinkSuffix != null) {
      urlString += hyperlinkSuffix;
    }

    // TODO: Also pass referrer policy
    NavigateParameters navParameters = new NavigateParameters();
    navParameters.sourceDocument = (RenderableDocument) subject.nodeDocument();
    navParameters.userInvolvement = userInvolvement;
    navParameters.sourceElement = subject;
    
    targetNavigable.navigate(URI.create(urlString), navParameters);
  }

  private static Navigable chooseANavigable(
    String targetAttributeValue, Navigable nodeNavigable
  ) {
    // TODO: Proper way to obtain the navigable
    return nodeNavigable;
  }

}
