package net.buildabrowser.babbrowser.a11y.accesskit;

import net.buildabrowser.ak4j.AKRole;
import net.buildabrowser.babbrowser.a11y.core.aom.AriaRole;

public final class AKRoleMapper {

  private AKRoleMapper() {}

  public static AKRole map(AriaRole role) {
    return switch (role) {
      case ALERT -> AKRole.ALERT;
      case ALERTDIALOGUE -> AKRole.ALERT_DIALOG;
      case APPLICATION -> AKRole.APPLICATION;
      case ARTICLE -> AKRole.ARTICLE;
      case BANNER -> AKRole.BANNER;
      case BLOCKQUOTE -> AKRole.BLOCKQUOTE;
      case BUTTON -> AKRole.BUTTON;
      case CAPTION -> AKRole.CAPTION;
      case CELL -> AKRole.CELL;
      case CHECKBOX -> AKRole.CHECK_BOX;
      case CODE -> AKRole.CODE;
      case COLUMNHEADER -> AKRole.COLUMN_HEADER;
      case COMBOBOX -> AKRole.COMBO_BOX;
      case COMMENT -> AKRole.COMMENT;
      case COMPLEMENTARY -> AKRole.COMPLEMENTARY;
      case CONTENTINFO -> AKRole.CONTENT_INFO;
      case DEFINITION -> AKRole.DEFINITION;
      case DELETION -> AKRole.CONTENT_DELETION;
      case DIALOG -> AKRole.DIALOG;
      case DIRECTORY -> AKRole.LIST;
      case DOCUMENT -> AKRole.DOCUMENT;
      case EMPHASIS -> AKRole.EMPHASIS;
      case FEED -> AKRole.FEED;
      case FIGURE -> AKRole.FIGURE;
      case FORM -> AKRole.FORM;
      case GENERIC -> AKRole.GENERIC_CONTAINER;
      case GRID -> AKRole.GRID;
      case GRIDCELL -> AKRole.GRID_CELL;
      case GROUP -> AKRole.GROUP;
      case HEADING -> AKRole.HEADING;
      case IMAGE -> AKRole.IMAGE;
      case IMG -> AKRole.IMAGE;
      case INSERTION -> AKRole.CONTENT_INSERTION;
      case LINK -> AKRole.LINK;
      case LIST -> AKRole.LIST;
      case LISTBOX -> AKRole.LIST_BOX;
      case LISTITEM -> AKRole.LIST_ITEM;
      case LOG -> AKRole.LOG;
      case MAIN -> AKRole.MAIN;
      case MARK -> AKRole.MARK;
      case MARQUEE -> AKRole.MARQUEE;
      case MATH -> AKRole.MATH;
      case MENU -> AKRole.MENU;
      case MENUBAR -> AKRole.MENU_BAR;
      case MENUITEM -> AKRole.MENU_ITEM;
      case MENUITEMCHECKBOX -> AKRole.MENU_ITEM_CHECK_BOX;
      case MENUITEMRADIO -> AKRole.MENU_ITEM_RADIO;
      case METER -> AKRole.METER;
      case NAVIGATION -> AKRole.NAVIGATION;
      case NONE -> AKRole.GENERIC_CONTAINER; // TODO: There is no proper mapping
      case NOTE -> AKRole.NOTE;
      case OPTION -> AKRole.LIST_BOX_OPTION; // TODO: There are two possible mappings
      case PARAGRAPH -> AKRole.PARAGRAPH;
      case PRESENTATION -> AKRole.GENERIC_CONTAINER; // TODO: There is no proper mapping
      case PROGRESSBAR -> AKRole.PROGRESS_INDICATOR;
      case RADIO -> AKRole.RADIO_BUTTON;
      case RADIOGROUP -> AKRole.RADIO_GROUP;
      case REGION -> AKRole.REGION;
      case ROW -> AKRole.ROW;
      case ROWGROUP -> AKRole.ROW_GROUP;
      case ROWHEADER -> AKRole.ROW_HEADER;
      case SCROLLBAR -> AKRole.SCROLL_BAR;
      case SEARCH -> AKRole.SEARCH;
      case SEARCHBOX -> AKRole.SEARCH_INPUT;
      case SECTIONFOOTER -> AKRole.SECTION_FOOTER;
      case SECTIONHEADER -> AKRole.SECTION_HEADER;
      case SEPARATOR -> AKRole.SPLITTER;
      case SLIDER -> AKRole.SLIDER;
      case SPINBUTTON -> AKRole.SPIN_BUTTON;
      case STATUS -> AKRole.STATUS;
      case STRONG -> AKRole.STRONG;
      case SUBSCRIPT -> AKRole.GENERIC_CONTAINER; // TODO: There is no proper mapping
      case SUGGESTION -> AKRole.SUGGESTION;
      case SUPERSCRIPT -> AKRole.GENERIC_CONTAINER; // TODO: There is no proper mapping
      case SWITCH -> AKRole.SWITCH;
      case TAB -> AKRole.TAB;
      case TABLE -> AKRole.TABLE;
      case TABLIST -> AKRole.TAB_LIST;
      case TABPANEL -> AKRole.TAB_PANEL;
      case TERM -> AKRole.TERM;
      case TEXTBOX -> AKRole.TEXT_INPUT;
      case TIME -> AKRole.TIME;
      case TIMER -> AKRole.TIMER;
      case TOOLBAR -> AKRole.TOOLBAR;
      case TOOLTIP -> AKRole.TOOLTIP;
      case TREE -> AKRole.TREE;
      case TREEGRID -> AKRole.TREE_GRID;
      case TREEITEM -> AKRole.TREE_ITEM;

      // TODO: Label is semantically incorrect, but AccessKit removed StaticText
      // (and text run is ignored)
      case STATICTEXT -> AKRole.LABEL;
      
      default -> throw new UnsupportedOperationException("Unsupported role: " + role);
    };
  }

}
