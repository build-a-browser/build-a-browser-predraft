package net.buildabrowser.babbrowser.html.navigation;

import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.html.html.RenderableDocument;

public class NavigateParameters {

  public RenderableDocument sourceDocument;

  public Object documentResource; // PostResource or String
  
  public NavigationHistoryBehavior historyHandling = NavigationHistoryBehavior.AUTO;

  public UserNavigationInvolvement userInvolvement = UserNavigationInvolvement.NONE;

  public Element sourceElement;

}
