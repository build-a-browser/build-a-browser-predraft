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
import net.buildabrowser.babbrowser.htmlparser.insertion.modes.InTemplateInsertionMode;
import net.buildabrowser.babbrowser.htmlparser.insertion.modes.InitialInsertionMode;
import net.buildabrowser.babbrowser.htmlparser.insertion.modes.TextInsertionMode;

public final class InsertionModes {
  
  private InsertionModes() {}

  public static final InsertionMode INITIAL_INSERTION_MODE = new InitialInsertionMode();
  public static final InsertionMode BEFORE_HTML_INSERTION_MODE = new BeforeHTMLInsertionMode();
  public static final InsertionMode BEFORE_HEAD_INSERTION_MODE = new BeforeHeadInsertionMode();
  public static final InsertionMode IN_HEAD_INSERTION_MODE = new InHeadInsertionMode();
  public static final InsertionMode AFTER_HEAD_INSERTION_MODE = new AfterHeadInsertionMode();
  public static final InsertionMode IN_BODY_INSERTION_MODE = new InBodyInsertionMode();
  public static final InsertionMode TEXT_INSERTION_MODE = new TextInsertionMode();
  public static final InsertionMode IN_TABLE_INSERTION_MODE = new InTableInsertionMode();
  public static final InsertionMode IN_TABLE_TEXT_INSERTION_MODE = new InTableTextInsertionMode();
  public static final InsertionMode IN_CAPTION_INSERTION_MODE = new InCaptionInsertionMode();
  public static final InsertionMode IN_COLUMN_GROUP_INSERTION_MODE = new InColumnGroupInsertionMode();
  public static final InsertionMode IN_TABLE_BODY_INSERTION_MODE = new InTableBodyInsertionMode();
  public static final InsertionMode IN_ROW_INSERTION_MODE = new InRowInsertionMode();
  public static final InsertionMode IN_CELL_INSERTION_MODE = new InCellInsertionMode();
  public static final InsertionMode IN_TEMPLATE_INSERTION_MODE = new InTemplateInsertionMode();
  public static final InsertionMode AFTER_BODY_INSERTION_MODE = new AfterBodyInsertionMode();
  public static final InsertionMode AFTER_AFTER_BODY_INSERTION_MODE = new AfterAfterBodyInsertionMode();

}
