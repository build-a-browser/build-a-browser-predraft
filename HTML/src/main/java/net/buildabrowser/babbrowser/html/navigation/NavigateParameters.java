package net.buildabrowser.babbrowser.html.navigation;

import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Element;

public class NavigateParameters {

  public Document sourceDocument;
  
  public NavigationHistoryBehavior historyHandling = NavigationHistoryBehavior.AUTO;

  public UserNavigationInvolvement userInvolvement = UserNavigationInvolvement.NONE;

  public Element sourceElement;

}
