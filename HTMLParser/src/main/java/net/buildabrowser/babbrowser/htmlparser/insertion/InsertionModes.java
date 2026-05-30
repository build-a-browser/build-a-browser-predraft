package net.buildabrowser.babbrowser.htmlparser.insertion;

import net.buildabrowser.babbrowser.htmlparser.insertion.modes.AfterAfterBodyInsertionMode;
import net.buildabrowser.babbrowser.htmlparser.insertion.modes.AfterBodyInsertionMode;
import net.buildabrowser.babbrowser.htmlparser.insertion.modes.AfterHeadInsertionMode;
import net.buildabrowser.babbrowser.htmlparser.insertion.modes.BeforeHTMLInsertionMode;
import net.buildabrowser.babbrowser.htmlparser.insertion.modes.BeforeHeadInsertionMode;
import net.buildabrowser.babbrowser.htmlparser.insertion.modes.InBodyInsertionMode;
import net.buildabrowser.babbrowser.htmlparser.insertion.modes.InCaptionInsertionMode;
import net.buildabrowser.babbrowser.htmlparser.insertion.modes.InCellInsertionMode;
import net.buildabrowser.babbrowser.htmlparser.insertion.modes.InColumnGroupInsertionMode;
import net.buildabrowser.babbrowser.htmlparser.insertion.modes.InHeadInsertionMode;
import net.buildabrowser.babbrowser.htmlparser.insertion.modes.InRowInsertionMode;
import net.buildabrowser.babbrowser.htmlparser.insertion.modes.InTableBodyInsertionMode;
import net.buildabrowser.babbrowser.htmlparser.insertion.modes.InTableInsertionMode;
import net.buildabrowser.babbrowser.htmlparser.insertion.modes.InTableTextInsertionMode;
import net.buildabrowser.babbrowser.htmlparser.insertion.modes.InitialInsertionMode;
import net.buildabrowser.babbrowser.htmlparser.insertion.modes.TextInsertionMode;

public final class InsertionModes {
  
  private InsertionModes() {}

  public static final InsertionMode initialInsertionMode = new InitialInsertionMode();
  public static final InsertionMode beforeHTMLInsertionMode = new BeforeHTMLInsertionMode();
  public static final InsertionMode beforeHeadInsertionMode = new BeforeHeadInsertionMode();
  public static final InsertionMode inHeadInsertionMode = new InHeadInsertionMode();
  public static final InsertionMode afterHeadInsertionMode = new AfterHeadInsertionMode();
  public static final InsertionMode inBodyInsertionMode = new InBodyInsertionMode();
  public static final InsertionMode textInsertionMode = new TextInsertionMode();
  public static final InsertionMode inTableInsertionMode = new InTableInsertionMode();
  public static final InsertionMode inTableTextInsertionMode = new InTableTextInsertionMode();
  public static final InsertionMode inCaptionInsertionMode = new InCaptionInsertionMode();
  public static final InsertionMode inColumnGroupInsertionMode = new InColumnGroupInsertionMode();
  public static final InsertionMode inTableBodyInsertionMode = new InTableBodyInsertionMode();
  public static final InsertionMode inRowInsertionMode = new InRowInsertionMode();
  public static final InsertionMode inCellInsertionMode = new InCellInsertionMode();

  public static final InsertionMode afterBodyInsertionMode = new AfterBodyInsertionMode();
  public static final InsertionMode afterAfterBodyInsertionMode = new AfterAfterBodyInsertionMode();
}
