package net.buildabrowser.babbrowser.html.html.util;

import static net.buildabrowser.babbrowser.html.util.HTMLDomUtil.isHtmlElement;

import java.net.URI;

import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.html.html.AnchorElement;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.html.html.RenderableDocument;
import net.buildabrowser.babbrowser.html.navigation.Navigable;
import net.buildabrowser.babbrowser.html.navigation.NavigateParameters;
import net.buildabrowser.babbrowser.html.navigation.UserNavigationInvolvement;

public final class NavUtil {
  
  private NavUtil() {}

  public static void followHyperlink(
    HTMLElement subject, String hyperlinkSuffix,
    UserNavigationInvolvement userInvolvement,
    boolean forceBlank // NOSPEC: Add option to force blank
  ) {
    // TODO: Check if cannot navigate
    String targetAttributeValue = "";
    if (
      isHtmlElement(subject, "a")
      || isHtmlElement(subject, "area")
    ) {
      targetAttributeValue = getElementTarget(subject, null);
    }

    // NOSPEC: Force blank
    if (forceBlank) {
      targetAttributeValue = "_blank";
    }

    // TODO: Proper way to parse a URL
    URI urlRecord = CommonUtil.tryOrNull(() -> resolveURL(
      subject.getAttribute("href"), subject.nodeDocument()));
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

  public static URI resolveURL(String url, Document relation) {
    URI baseURL = relation instanceof HTMLDocument htmlDocument ?
      htmlDocument.baseURL() :
      relation.url();
    return baseURL.resolve(url);
  }

  public static boolean cannotNavigate(Element element) {
    // TODO: Check if document is fully active
    return
      !(element instanceof AnchorElement)
      // TODO: Use shadowIncludingRoot
      && element.nodeDocument() == null;
  }

  public static Navigable chooseANavigable(
    String name, Navigable currentNavigable
  ) {
    // TODO: Proper way to obtain the navigable
    return switch (name) {
      case "", "_self" -> currentNavigable;
      case "_parent" -> currentNavigable.parent() != null ?
        currentNavigable.parent() : currentNavigable;
      case "_top" -> currentNavigable.traversable();
      // TODO: Support named navigables
      default -> {
        // NOSPEC: Call UI layer instead of createNewTopLevelTraversable
        yield currentNavigable.uaNavigableOptions()
          .uiFeatures().addTopLevelTraversable(currentNavigable);
      }
    };
  }

  private static String getElementTarget(HTMLElement subject, String target) {
    if (target == null) {
      if (subject.hasAttribute("target")) {
        target = subject.getAttribute("target");
      }
      // TODO: Check if there is a base element
    } else if (
      target.indexOf('<') != -1
      && (
        target.indexOf('\t') != -1
        || target.indexOf('\n') != -1
      )
    ) {
      target = "_blank";
    }

    // NOSPEC: null-check
    if (target == null) {
      return "";
    }
    return target;
  }

}
