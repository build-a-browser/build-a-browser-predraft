package net.buildabrowser.babbrowser.html.form;

import static net.buildabrowser.babbrowser.html.util.HTMLDomUtil.isHtmlElement;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.html.events.EventLoop;
import net.buildabrowser.babbrowser.html.events.TaskSource;
import net.buildabrowser.babbrowser.html.form.XWWWFormURLEncodedSerializer.NameValuePair;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.html.HTMLFormElement;
import net.buildabrowser.babbrowser.html.html.util.NavUtil;
import net.buildabrowser.babbrowser.html.navigation.Navigable;
import net.buildabrowser.babbrowser.html.navigation.NavigateParameters;
import net.buildabrowser.babbrowser.html.navigation.NavigationHistoryBehavior;
import net.buildabrowser.babbrowser.html.navigation.PostResource;
import net.buildabrowser.babbrowser.html.navigation.UserNavigationInvolvement;

public final class FormSubmissionAlgorithm {

  private static final String DEFAULT_METHOD = "get";
  private static final List<String> VALID_METHODS = List.of(
    "get", "post", "dialog");
  private static final String DEFAULT_ENCTYPE = "application/x-www-form-urlencoded";
  private static final List<String> VALID_ENCTYPES = List.of(
    DEFAULT_ENCTYPE,
    "multipart/form-data",
    "text/plain");
  
  private FormSubmissionAlgorithm() {}

  public static void submitAForm(
    HTMLFormElement form,
    Element submitter,
    // TODO: skipped submittedFromSubmit parameter
    UserNavigationInvolvement userInvolvement
  ) {
    if (NavUtil.cannotNavigate(form)) return;
    if (form.constructingEntryList()) return;
    Document formDocument = form.nodeDocument();
    // TODO: Skipped stuff
    // TODO: Use the correct coding, pass it to construct entry list
    Charset encoding = StandardCharsets.UTF_8;
    EntryList entryList = EntryList.constructEntryList(form, submitter);
    if (NavUtil.cannotNavigate(form)) return;
    String method = getMethod(form, submitter);
    if (method.equals("dialog")) return; // TODO
    String action = getAction(form, submitter);
    if (action.length() == 0) {
      action = formDocument.url().toString();
    }
    String action_ = action;
    URI parsedAction = CommonUtil.tryOrNull(() ->
      formDocument.url().resolve(action_));
    if (parsedAction == null) return;
    String scheme = parsedAction.getScheme();
    @SuppressWarnings("unused")
    String enctype = getEnctype(form, submitter);
    // TODO: Check the form target
    Navigable targetNavigable = NavUtil.chooseANavigable(
      "", form.nodeNavigable());
    if (targetNavigable == null) return;
    NavigationHistoryBehavior historyHandling = NavigationHistoryBehavior.AUTO;
    // TODO: Replace unloaded document

    if (method.equals("post")) {
      switch (scheme) {
        // TODO: Support for other schemes
        case "http", "https" -> submitAsEntityBody(
          form, entryList, parsedAction, method, enctype, encoding,
          historyHandling, userInvolvement, submitter);
        case "data" -> planToNavigate(
          form, parsedAction, null,
          historyHandling, userInvolvement, submitter);
      }
    } else {
      switch (scheme) {
        // TODO: Support for other schemes
        default -> mutateActionURL(
          form, entryList, parsedAction, encoding,
          historyHandling, userInvolvement, submitter);
      }
    }
  }

  public static boolean isSubmitButton(Element element) {
    // TODO: Other submit button cases
    boolean isInput = isHtmlElement(element, "input");
    boolean isSubmitInput = isInput
      && "submit".equals(element.getAttribute("type"));
    boolean isImageInput = isInput
      && "image".equals(element.getAttribute("type"));
    boolean isButton = isHtmlElement(element, "button");
    boolean isSubmitTypeButton = isButton
      && (
        !element.hasAttribute("type")
        || element.getAttribute("type").length() == 0
        || "submit".equals(element.getAttribute("type")));
    return isSubmitInput || isImageInput || isSubmitTypeButton;
  }

  private static void mutateActionURL(
    HTMLFormElement form,
    EntryList entryList,
    URI parsedAction,
    Charset encoding,
    NavigationHistoryBehavior hisoryHandling,
    UserNavigationInvolvement userInvolvement,
    Element submitter
  ) {
    List<NameValuePair> pairs = entryList.toNameValuePairs();
    String query = XWWWFormURLEncodedSerializer.serialize(pairs, encoding);
    URI parsedAction2 = CommonUtil.rethrow(() -> new URI(
      parsedAction.getScheme(),
      parsedAction.getAuthority(),
      parsedAction.getPath(),
      query,
      parsedAction.getFragment()));
    planToNavigate(
      form, parsedAction2, null,
      hisoryHandling, userInvolvement, submitter);
  }

  private static void submitAsEntityBody(
    HTMLFormElement form,
    EntryList entryList,
    URI url,
    String method,
    String enctype,
    Charset encoding,
    NavigationHistoryBehavior hisoryHandling,
    UserNavigationInvolvement userInvolvement,
    Element submitter
  ) {
    assert method.equals("post");
    ByteBuffer body = null;
    String mimeType = null;
    switch (enctype) {
      case "application/x-www-form-urlencoded" -> {
        List<NameValuePair> pairs = entryList.toNameValuePairs();
        String bodyStr = XWWWFormURLEncodedSerializer.serialize(pairs, encoding);
        body = encoding.encode(bodyStr);
        mimeType = "application/x-www-form-urlencoded";
      }
      // TODO: Support multipart/form-data
      case "text/plain" -> {
        List<NameValuePair> pairs = entryList.toNameValuePairs();
        String bodyStr = TextPlainEncodedSerializer.serialize(pairs);
        body = encoding.encode(bodyStr);
        mimeType = "text/plain";
      }
      default -> throw new UnsupportedOperationException(
        "Unsupported form encoding: " + enctype);
    }

    PostResource postResource = new PostResource(body, mimeType);
    planToNavigate(
      form, url, postResource,
      hisoryHandling, userInvolvement, submitter);
  }

  private static void planToNavigate(
    HTMLFormElement form,
    URI url,
    PostResource postResource,
    NavigationHistoryBehavior hisoryHandling,
    UserNavigationInvolvement userInvolvement,
    Element submitter
  ) {
    // TODO: Set the referrer
    EventLoop eventLoop = form.nodeNavigable().activeWindow().agent().eventLoop();
    HTMLDocument nodeDocument = (HTMLDocument) form.nodeDocument();
    eventLoop.queueTask(() -> {
      NavigateParameters navigateParameters = new NavigateParameters();
      navigateParameters.sourceDocument = nodeDocument;
      navigateParameters.historyHandling = hisoryHandling;
      navigateParameters.userInvolvement = userInvolvement;
      navigateParameters.sourceElement = submitter;
      navigateParameters.documentResource = postResource;
      form.nodeNavigable().navigate(url, navigateParameters);
    }, TaskSource.DOM, nodeDocument);
  }

  private static String getAction(
    HTMLFormElement formOwner,
    Element element
  ) {
    if (
      isSubmitButton(element)
      && element.hasAttribute("formaction")
    ) {
      return element.getAttribute("formaction");
    } else if (
      formOwner != null
      && formOwner.getAttribute("action") != null
    ) {
      return formOwner.getAttribute("action");
    } else {
      return "";
    }
  }

  private static String getMethod(
    HTMLFormElement formOwner,
    Element submitter
  ) {
    return getValueOrFallback(
      formOwner, submitter,
      "formmethod", "method",
      VALID_METHODS, DEFAULT_METHOD);
  }

  private static String getEnctype(
    HTMLFormElement formOwner,
    Element submitter
  ) {
    return getValueOrFallback(
      formOwner, submitter,
      "formenctype", "enctype",
      VALID_ENCTYPES, DEFAULT_ENCTYPE);
  }

  private static String getValueOrFallback(
    HTMLFormElement formOwner,
    Element submitter,
    String subAttr,
    String formAttr,
    List<String> allowed,
    String fallback
  ) {
    String value = fallback;
    if (
      isSubmitButton(submitter)
      && submitter.hasAttribute(subAttr)
    ) {
      value = submitter.getAttribute(subAttr);
    } else if (
      formOwner != null
      && formOwner.hasAttribute(formAttr)
    ) {
      value = formOwner.getAttribute(formAttr);
    }

    value = value.toLowerCase();
    if (!allowed.contains(value)) {
      return fallback;
    }
    return value;
  }

}
