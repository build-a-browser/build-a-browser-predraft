package net.buildabrowser.babbrowser.cssbase.property;

import java.util.Map;

import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.cssbase.property.align.GapParser;
import net.buildabrowser.babbrowser.cssbase.property.align.GapShorthandParser;
import net.buildabrowser.babbrowser.cssbase.property.background.BackgroundAttachmentParser;
import net.buildabrowser.babbrowser.cssbase.property.background.BackgroundClipParser;
import net.buildabrowser.babbrowser.cssbase.property.background.BackgroundColorParser;
import net.buildabrowser.babbrowser.cssbase.property.background.BackgroundImageParser;
import net.buildabrowser.babbrowser.cssbase.property.background.BackgroundOriginParser;
import net.buildabrowser.babbrowser.cssbase.property.background.BackgroundParser;
import net.buildabrowser.babbrowser.cssbase.property.background.BackgroundPositionParser;
import net.buildabrowser.babbrowser.cssbase.property.background.BackgroundRepeatParser;
import net.buildabrowser.babbrowser.cssbase.property.background.BackgroundSizeParser;
import net.buildabrowser.babbrowser.cssbase.property.border.BorderColorParser;
import net.buildabrowser.babbrowser.cssbase.property.border.BorderShorthandParser;
import net.buildabrowser.babbrowser.cssbase.property.border.BorderSideShorthandParser;
import net.buildabrowser.babbrowser.cssbase.property.border.BorderStyleParser;
import net.buildabrowser.babbrowser.cssbase.property.color.ColorBaseParser;
import net.buildabrowser.babbrowser.cssbase.property.color.ColorParser;
import net.buildabrowser.babbrowser.cssbase.property.content.ContentParser;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayParser;
import net.buildabrowser.babbrowser.cssbase.property.display.OrderParser;
import net.buildabrowser.babbrowser.cssbase.property.flex.AlignContentParser;
import net.buildabrowser.babbrowser.cssbase.property.flex.AlignItemsParser;
import net.buildabrowser.babbrowser.cssbase.property.flex.AlignSelfParser;
import net.buildabrowser.babbrowser.cssbase.property.flex.FlexBasisParser;
import net.buildabrowser.babbrowser.cssbase.property.flex.FlexDirectionParser;
import net.buildabrowser.babbrowser.cssbase.property.flex.FlexFlowParser;
import net.buildabrowser.babbrowser.cssbase.property.flex.FlexGrowParser;
import net.buildabrowser.babbrowser.cssbase.property.flex.FlexParser;
import net.buildabrowser.babbrowser.cssbase.property.flex.FlexShrinkParser;
import net.buildabrowser.babbrowser.cssbase.property.flex.FlexWrapParser;
import net.buildabrowser.babbrowser.cssbase.property.flex.JustifyContentParser;
import net.buildabrowser.babbrowser.cssbase.property.floats.ClearParser;
import net.buildabrowser.babbrowser.cssbase.property.floats.FloatParser;
import net.buildabrowser.babbrowser.cssbase.property.font.FontFamilyParser;
import net.buildabrowser.babbrowser.cssbase.property.font.FontShorthandParser;
import net.buildabrowser.babbrowser.cssbase.property.font.FontSizeParser;
import net.buildabrowser.babbrowser.cssbase.property.font.FontWeightParser;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridAreaParser;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridAutoFlowParser;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridAutoTracksParser;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridLineParser;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridParser;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTemplateAreasParser;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTemplateParser;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTrackListParser;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridLineCompositeParser;
import net.buildabrowser.babbrowser.cssbase.property.misc.AllParser;
import net.buildabrowser.babbrowser.cssbase.property.outline.OutlineColorParser;
import net.buildabrowser.babbrowser.cssbase.property.outline.OutlineShorthandParser;
import net.buildabrowser.babbrowser.cssbase.property.outline.OutlineStyleParser;
import net.buildabrowser.babbrowser.cssbase.property.overflow.OverflowParser;
import net.buildabrowser.babbrowser.cssbase.property.overflow.OverflowShorthandParser;
import net.buildabrowser.babbrowser.cssbase.property.position.PositionParser;
import net.buildabrowser.babbrowser.cssbase.property.position.ZIndexParser;
import net.buildabrowser.babbrowser.cssbase.property.shared.LineWidthParser;
import net.buildabrowser.babbrowser.cssbase.property.shared.ManySideShorthandParser;
import net.buildabrowser.babbrowser.cssbase.property.size.BoxSizingParser;
import net.buildabrowser.babbrowser.cssbase.property.size.SizeParser;
import net.buildabrowser.babbrowser.cssbase.property.table.BorderCollapseParser;
import net.buildabrowser.babbrowser.cssbase.property.table.BorderSpacingParser;
import net.buildabrowser.babbrowser.cssbase.property.table.CaptionSideParser;
import net.buildabrowser.babbrowser.cssbase.property.table.TableLayoutParser;
import net.buildabrowser.babbrowser.cssbase.property.text.LineHeightParser;
import net.buildabrowser.babbrowser.cssbase.property.text.TextAlignParser;
import net.buildabrowser.babbrowser.cssbase.property.text.TextWrapModeParser;
import net.buildabrowser.babbrowser.cssbase.property.visibility.VisibilityParser;
import net.buildabrowser.babbrowser.cssbase.property.whitespace.WhiteSpaceCollapseParser;
import net.buildabrowser.babbrowser.cssbase.property.whitespace.WhiteSpaceParser;

public final class PropertyParsers {
  
  private PropertyParsers() {}

    // TODO: A number of these need CalcParser added. Things handled by SizeParser get this free.
  public static final Map<String, PropertyValueParser> PROPERTY_PARSERS = CommonUtil.mapOf(
    "color", new ColorParser(),
    
    "background-color", new BackgroundColorParser(),
    "background-image", new BackgroundImageParser(),
    "background-repeat", new BackgroundRepeatParser(),
    "background-attachment", new BackgroundAttachmentParser(),
    "background-position", new BackgroundPositionParser(),
    "background-clip", new BackgroundClipParser(),
    "background-origin", new BackgroundOriginParser(),
    "background-size", new BackgroundSizeParser(),
    "background", new BackgroundParser(),

    "clear", new ClearParser(),
    "float", new FloatParser(),

    "display", new DisplayParser(),
    "visibility", new VisibilityParser(),

    "width", SizeParser.forNormal(CSSProperty.WIDTH),
    "min-width", SizeParser.forMin(CSSProperty.MIN_WIDTH),
    "max-width", SizeParser.forMin(CSSProperty.MAX_WIDTH),
    
    "height", SizeParser.forNormal(CSSProperty.HEIGHT),
    "min-height", SizeParser.forMin(CSSProperty.MIN_HEIGHT),
    "max-height", SizeParser.forMin(CSSProperty.MAX_HEIGHT),
    
    "box-sizing", new BoxSizingParser(),

    "font-family", new FontFamilyParser(),
    "font-weight", new FontWeightParser(),
    "font-size", new FontSizeParser(),
    "font", new FontShorthandParser(),

    "padding-top", SizeParser.forPadding(CSSProperty.PADDING_TOP),
    "padding-bottom", SizeParser.forPadding(CSSProperty.PADDING_BOTTOM),
    "padding-left", SizeParser.forPadding(CSSProperty.PADDING_LEFT),
    "padding-right", SizeParser.forPadding(CSSProperty.PADDING_RIGHT),
    "padding", new ManySideShorthandParser(new SizeParser(false, false, null),
      new CSSProperty[] { CSSProperty.PADDING_TOP, CSSProperty.PADDING_RIGHT, CSSProperty.PADDING_BOTTOM, CSSProperty.PADDING_LEFT },
      CSSProperty.PADDING),
    
    "border-top-width", new LineWidthParser(CSSProperty.BORDER_TOP_WIDTH),
    "border-bottom-width", new LineWidthParser(CSSProperty.BORDER_BOTTOM_WIDTH),
    "border-left-width", new LineWidthParser(CSSProperty.BORDER_LEFT_WIDTH),
    "border-right-width", new LineWidthParser(CSSProperty.BORDER_RIGHT_WIDTH),
    "border-width", new ManySideShorthandParser(new LineWidthParser(null),
      new CSSProperty[] { CSSProperty.BORDER_TOP_WIDTH, CSSProperty.BORDER_RIGHT_WIDTH, CSSProperty.BORDER_BOTTOM_WIDTH, CSSProperty.BORDER_LEFT_WIDTH },
      CSSProperty.BORDER_WIDTH),

    "border-top-color", new BorderColorParser(CSSProperty.BORDER_TOP_COLOR),
    "border-bottom-color", new BorderColorParser(CSSProperty.BORDER_BOTTOM_COLOR),
    "border-left-color", new BorderColorParser(CSSProperty.BORDER_LEFT_COLOR),
    "border-right-color", new BorderColorParser(CSSProperty.BORDER_RIGHT_COLOR),
    "border-color", new ManySideShorthandParser(new ColorBaseParser(),
      new CSSProperty[] { CSSProperty.BORDER_TOP_COLOR, CSSProperty.BORDER_RIGHT_COLOR, CSSProperty.BORDER_BOTTOM_COLOR, CSSProperty.BORDER_LEFT_COLOR },
      CSSProperty.BORDER_COLOR),

    "border-top-style", new BorderStyleParser(CSSProperty.BORDER_TOP_STYLE),
    "border-bottom-style", new BorderStyleParser(CSSProperty.BORDER_BOTTOM_STYLE),
    "border-left-style", new BorderStyleParser(CSSProperty.BORDER_LEFT_STYLE),
    "border-right-style", new BorderStyleParser(CSSProperty.BORDER_RIGHT_STYLE),
    "border-style", new ManySideShorthandParser(new BorderStyleParser(null),
      new CSSProperty[] { CSSProperty.BORDER_TOP_STYLE, CSSProperty.BORDER_RIGHT_STYLE, CSSProperty.BORDER_BOTTOM_STYLE, CSSProperty.BORDER_LEFT_STYLE },
      CSSProperty.BORDER_STYLE),

    "border-top", new BorderSideShorthandParser(CSSProperty.BORDER_TOP, CSSProperty.BORDER_TOP_WIDTH, CSSProperty.BORDER_TOP_COLOR, CSSProperty.BORDER_TOP_STYLE),
    "border-bottom", new BorderSideShorthandParser(CSSProperty.BORDER_BOTTOM, CSSProperty.BORDER_BOTTOM_WIDTH, CSSProperty.BORDER_BOTTOM_COLOR, CSSProperty.BORDER_BOTTOM_STYLE),
    "border-left", new BorderSideShorthandParser(CSSProperty.BORDER_LEFT, CSSProperty.BORDER_LEFT_WIDTH, CSSProperty.BORDER_LEFT_COLOR, CSSProperty.BORDER_LEFT_STYLE),
    "border-right", new BorderSideShorthandParser(CSSProperty.BORDER_RIGHT, CSSProperty.BORDER_RIGHT_WIDTH, CSSProperty.BORDER_RIGHT_COLOR, CSSProperty.BORDER_RIGHT_STYLE),
    "border", new BorderShorthandParser(),

    "margin-top", SizeParser.forMargin(CSSProperty.MARGIN_TOP),
    "margin-bottom", SizeParser.forMargin(CSSProperty.MARGIN_BOTTOM),
    "margin-left", SizeParser.forMargin(CSSProperty.MARGIN_LEFT),
    "margin-right", SizeParser.forMargin(CSSProperty.MARGIN_RIGHT),
    "margin", new ManySideShorthandParser(new SizeParser(false, true, null),
      new CSSProperty[] { CSSProperty.MARGIN_TOP, CSSProperty.MARGIN_RIGHT, CSSProperty.MARGIN_BOTTOM, CSSProperty.MARGIN_LEFT },
      CSSProperty.MARGIN),
    
    "outline-width", new LineWidthParser(CSSProperty.OUTLINE_WIDTH),
    "outline-style", new OutlineStyleParser(),
    "outline-color", new OutlineColorParser(),
    "outline-offset", SizeParser.forOutline(CSSProperty.OUTLINE_OFFSET),
    "outline", new OutlineShorthandParser(),
    
    "top", SizeParser.forInset(CSSProperty.TOP),
    "bottom", SizeParser.forInset(CSSProperty.BOTTOM),
    "left", SizeParser.forInset(CSSProperty.LEFT),
    "right", SizeParser.forInset(CSSProperty.RIGHT),

    "position", new PositionParser(),
    "z-index", new ZIndexParser(),

    "white-space-collapse", new WhiteSpaceCollapseParser(),
    "text-wrap-mode", new TextWrapModeParser(),
    "white-space", new WhiteSpaceParser(),
    "line-height", new LineHeightParser(),
    "text-align", new TextAlignParser(),

    "order", new OrderParser(),

    "flex-direction", new FlexDirectionParser(),
    "flex-wrap", new FlexWrapParser(),
    "flex-flow", new FlexFlowParser(),
    "flex", new FlexParser(),
    "flex-grow", new FlexGrowParser(),
    "flex-shrink", new FlexShrinkParser(),
    "flex-basis", new FlexBasisParser(),
    "justify-content", new JustifyContentParser(),
    "align-items", new AlignItemsParser(),
    "align-self", new AlignSelfParser(),
    "align-content", new AlignContentParser(),

    "grid-template-rows", new GridTrackListParser(CSSProperty.GRID_TEMPLATE_ROWS),
    "grid-template-columns", new GridTrackListParser(CSSProperty.GRID_TEMPLATE_COLUMNS),
    "grid-template-areas", new GridTemplateAreasParser(),
    "grid-template", new GridTemplateParser(),
    "grid-auto-rows", new GridAutoTracksParser(CSSProperty.GRID_AUTO_ROWS),
    "grid-auto-columns", new GridAutoTracksParser(CSSProperty.GRID_AUTO_COLUMNS),
    "grid-auto-flow", new GridAutoFlowParser(),
    "grid", new GridParser(),
    "grid-row-start", new GridLineParser(CSSProperty.GRID_ROW_START),
    "grid-column-start", new GridLineParser(CSSProperty.GRID_COLUMN_START),
    "grid-row-end", new GridLineParser(CSSProperty.GRID_ROW_END),
    "grid-column-end", new GridLineParser(CSSProperty.GRID_COLUMN_END),
    "grid-row", new GridLineCompositeParser(
      CSSProperty.GRID_ROW, CSSProperty.GRID_ROW_START, CSSProperty.GRID_ROW_END),
    "grid-column", new GridLineCompositeParser(
      CSSProperty.GRID_COLUMN, CSSProperty.GRID_COLUMN_START, CSSProperty.GRID_COLUMN_END),
    "grid-area", new GridAreaParser(),

    "row-gap", new GapParser(CSSProperty.ROW_GAP),
    "column-gap", new GapParser(CSSProperty.COLUMN_GAP),
    "gap", new GapShorthandParser(),

    "table-layout", new TableLayoutParser(),
    "border-collapse", new BorderCollapseParser(),
    "border-spacing", new BorderSpacingParser(),
    "caption-side", new CaptionSideParser(),

    "overflow-x", new OverflowParser(CSSProperty.OVERFLOW_X),
    "overflow-y", new OverflowParser(CSSProperty.OVERFLOW_Y),
    // TODO: Proper implementation
    "overflow-block", new OverflowParser(CSSProperty.OVERFLOW_X),
    "overflow-inline", new OverflowParser(CSSProperty.OVERFLOW_Y),
    "overflow", new OverflowShorthandParser(),

    "content", new ContentParser(),

    "all", new AllParser()
  );

}
