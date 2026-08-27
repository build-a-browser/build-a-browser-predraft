package net.buildabrowser.babbrowser.cssbase.property;

import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil.ManyResult;
import net.buildabrowser.babbrowser.cssbase.property.align.AlignContentValue;
import net.buildabrowser.babbrowser.cssbase.property.align.AlignItemsValue;
import net.buildabrowser.babbrowser.cssbase.property.align.GapValue;
import net.buildabrowser.babbrowser.cssbase.property.align.JustifyContentValue;
import net.buildabrowser.babbrowser.cssbase.property.background.BackgroundAttachmentValue;
import net.buildabrowser.babbrowser.cssbase.property.background.BackgroundPositionValue;
import net.buildabrowser.babbrowser.cssbase.property.background.BackgroundPositionValue.BackgroundPositionSide;
import net.buildabrowser.babbrowser.cssbase.property.background.BackgroundRepeatValue;
import net.buildabrowser.babbrowser.cssbase.property.background.BackgroundRepeatValue.BackgroundAxisRepeatValue;
import net.buildabrowser.babbrowser.cssbase.property.background.BackgroundSizeValue.SizedBackgroundSizeValue;
import net.buildabrowser.babbrowser.cssbase.property.box.VisualBoxValue;
import net.buildabrowser.babbrowser.cssbase.property.color.ColorValue.SRGBAColor;
import net.buildabrowser.babbrowser.cssbase.property.content.ContentValue;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue.InnerDisplayValue;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue.OuterDisplayValue;
import net.buildabrowser.babbrowser.cssbase.property.display.OrderValue;
import net.buildabrowser.babbrowser.cssbase.property.flex.FlexDirectionValue;
import net.buildabrowser.babbrowser.cssbase.property.flex.FlexGrowValue;
import net.buildabrowser.babbrowser.cssbase.property.flex.FlexShrinkValue;
import net.buildabrowser.babbrowser.cssbase.property.flex.FlexWrapValue;
import net.buildabrowser.babbrowser.cssbase.property.font.FontNameValue;
import net.buildabrowser.babbrowser.cssbase.property.font.FontNamedSizeValue;
import net.buildabrowser.babbrowser.cssbase.property.font.FontWeightValue;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridAutoFlowValue;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridAutoFlowValue.GridAutoFlowDirection;
import net.buildabrowser.babbrowser.cssbase.property.overflow.OverflowValue;
import net.buildabrowser.babbrowser.cssbase.property.position.PositionValue;
import net.buildabrowser.babbrowser.cssbase.property.size.BoxSizingValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue;
import net.buildabrowser.babbrowser.cssbase.property.size.PercentageValue;
import net.buildabrowser.babbrowser.cssbase.property.table.BorderCollapseValue;
import net.buildabrowser.babbrowser.cssbase.property.table.BorderSpacingValue;
import net.buildabrowser.babbrowser.cssbase.property.table.CaptionSideValue;
import net.buildabrowser.babbrowser.cssbase.property.text.LineHeightValue;
import net.buildabrowser.babbrowser.cssbase.property.text.TextAlignValue;
import net.buildabrowser.babbrowser.cssbase.property.text.TextWrapModeValue;
import net.buildabrowser.babbrowser.cssbase.property.visibility.VisibilityValue;
import net.buildabrowser.babbrowser.cssbase.property.whitespace.WhiteSpaceCollapseValue;

public enum CSSProperty {
  
  COLOR(nextId(), true, InvalidationLevel.PAINT, SRGBAColor.create(0, 0, 0, 255)),

  BACKGROUND_COLOR(nextId(), false, InvalidationLevel.PAINT, SRGBAColor.create(0, 0, 0, 0)),
  BACKGROUND_IMAGE(nextId(), false, InvalidationLevel.PAINT, ManyResult.createCommas(CSSValue.NONE)),
  BACKGROUND_REPEAT(nextId(), false, InvalidationLevel.PAINT, ManyResult.createCommas(BackgroundRepeatValue.create(
    BackgroundAxisRepeatValue.REPEAT, BackgroundAxisRepeatValue.REPEAT))),
  // Unfortunately layout as stacking contexts (generated during layout) need regenerated
  BACKGROUND_ATTACHMENT(nextId(), false, ManyResult.createCommas(BackgroundAttachmentValue.SCROLL)),
  BACKGROUND_POSITION(nextId(), false, InvalidationLevel.PAINT, ManyResult.createCommas(BackgroundPositionValue.create(
    BackgroundPositionSide.LEFT, PercentageValue.create(0),
    BackgroundPositionSide.TOP, PercentageValue.create(0)))),
  BACKGROUND_CLIP(nextId(), false, InvalidationLevel.PAINT, ManyResult.createCommas(VisualBoxValue.BORDER_BOX)),
  BACKGROUND_ORIGIN(nextId(), false, InvalidationLevel.PAINT, ManyResult.createCommas(VisualBoxValue.PADDING_BOX)),
  BACKGROUND_SIZE(nextId(), false, InvalidationLevel.PAINT, ManyResult.createCommas(
    SizedBackgroundSizeValue.create(CSSValue.AUTO, CSSValue.AUTO))),
  BACKGROUND(new CSSProperty[] {
    BACKGROUND_COLOR, BACKGROUND_IMAGE, BACKGROUND_REPEAT, BACKGROUND_ATTACHMENT,
    BACKGROUND_POSITION, BACKGROUND_CLIP, BACKGROUND_ORIGIN, BACKGROUND_SIZE }),

  WIDTH(nextId(), false, CSSValue.AUTO),
  MIN_WIDTH(nextId(), false, LengthValue.AUTO),
  MAX_WIDTH(nextId(), false, CSSValue.NONE),

  HEIGHT(nextId(), false, CSSValue.AUTO),
  MIN_HEIGHT(nextId(), false, LengthValue.AUTO),
  MAX_HEIGHT(nextId(), false, CSSValue.NONE),
  
  BOX_SIZING(nextId(), false, BoxSizingValue.CONTENT_BOX),
  DISPLAY(nextId(), false, InvalidationLevel.BOX, DisplayValue.create(OuterDisplayValue.INLINE, InnerDisplayValue.FLOW)),
  VISIBILITY(nextId(), true, InvalidationLevel.PAINT, VisibilityValue.VISIBLE),
  // Float is BOX because it affects the BoxContent-sharing policy, which is determined at box-time
  // TODO: Maybe rewrite it so it can invalidate at LAYOUT level
  FLOAT(nextId(), false, InvalidationLevel.BOX, CSSValue.NONE),
  CLEAR(nextId(), false, CSSValue.NONE),

  WHITE_SPACE_COLLAPSE(nextId(), true, WhiteSpaceCollapseValue.COLLAPSE),
  TEXT_WRAP_MODE(nextId(), true, TextWrapModeValue.WRAP),
  WHITE_SPACE_TRIM(nextId(), false, CSSValue.NONE),
  WHITE_SPACE(new CSSProperty[] {
    WHITE_SPACE_COLLAPSE, TEXT_WRAP_MODE}), // TODO: WHITE_SPACE_TRIM
  LINE_HEIGHT(nextId(), true, LineHeightValue.NORMAL),
  TEXT_ALIGN(nextId(), true, TextAlignValue.START),

  FONT_FAMILY(nextId(), true, ManyResult.createCommas(List.of(FontNameValue.create("sans-serif")))),
  FONT_WEIGHT(nextId(), true, FontWeightValue.create(400)),
  FONT_SIZE(nextId(), true, FontNamedSizeValue.MEDIUM),
  // TODO: There are still other properties to support...
  FONT(new CSSProperty[] { FONT_WEIGHT, FONT_SIZE, LINE_HEIGHT, FONT_FAMILY }),

  PADDING_TOP(nextId(), false, LengthValue.ZERO),
  PADDING_BOTTOM(nextId(), false, LengthValue.ZERO),
  PADDING_LEFT(nextId(), false, LengthValue.ZERO),
  PADDING_RIGHT(nextId(), false, LengthValue.ZERO),
  PADDING(new CSSProperty[] { PADDING_TOP, PADDING_BOTTOM, PADDING_LEFT, PADDING_RIGHT }),

  BORDER_TOP_WIDTH(nextId(), false, LengthValue.ZERO),
  BORDER_BOTTOM_WIDTH(nextId(), false, LengthValue.ZERO),
  BORDER_LEFT_WIDTH(nextId(), false, LengthValue.ZERO),
  BORDER_RIGHT_WIDTH(nextId(), false, LengthValue.ZERO),
  BORDER_WIDTH(new CSSProperty[] { BORDER_TOP_WIDTH, BORDER_BOTTOM_WIDTH, BORDER_LEFT_WIDTH, BORDER_RIGHT_WIDTH }),

  // Use NONE, but then resolve the real value in code, for the default case
  BORDER_TOP_COLOR(nextId(), false, InvalidationLevel.PAINT, CSSValue.NONE),
  BORDER_BOTTOM_COLOR(nextId(), false, InvalidationLevel.PAINT, CSSValue.NONE),
  BORDER_LEFT_COLOR(nextId(), false, InvalidationLevel.PAINT, CSSValue.NONE),
  BORDER_RIGHT_COLOR(nextId(), false, InvalidationLevel.PAINT, CSSValue.NONE),
  BORDER_COLOR(new CSSProperty[] { BORDER_TOP_COLOR, BORDER_BOTTOM_COLOR, BORDER_LEFT_COLOR, BORDER_RIGHT_COLOR }),

  // TODO: A value of NONE affects layout vs other values... maybe add a way to conditionally give a level
  BORDER_TOP_STYLE(nextId(), false, CSSValue.NONE),
  BORDER_BOTTOM_STYLE(nextId(), false, CSSValue.NONE),
  BORDER_LEFT_STYLE(nextId(), false, CSSValue.NONE),
  BORDER_RIGHT_STYLE(nextId(), false, CSSValue.NONE),
  BORDER_STYLE(new CSSProperty[] { BORDER_TOP_STYLE, BORDER_BOTTOM_STYLE, BORDER_LEFT_STYLE, BORDER_RIGHT_STYLE }),

  BORDER_TOP(new CSSProperty[] { BORDER_TOP_WIDTH, BORDER_TOP_COLOR, BORDER_TOP_STYLE }),
  BORDER_BOTTOM(new CSSProperty[] { BORDER_BOTTOM_WIDTH, BORDER_BOTTOM_COLOR, BORDER_BOTTOM_STYLE }),
  BORDER_LEFT(new CSSProperty[] { BORDER_LEFT_WIDTH, BORDER_LEFT_COLOR, BORDER_LEFT_STYLE }),
  BORDER_RIGHT(new CSSProperty[] { BORDER_RIGHT_WIDTH, BORDER_RIGHT_COLOR, BORDER_RIGHT_STYLE }),
  BORDER(new CSSProperty[] { BORDER_TOP, BORDER_BOTTOM, BORDER_LEFT, BORDER_RIGHT }),
  
  MARGIN_TOP(nextId(), false, LengthValue.ZERO),
  MARGIN_BOTTOM(nextId(), false, LengthValue.ZERO),
  MARGIN_LEFT(nextId(), false, LengthValue.ZERO),
  MARGIN_RIGHT(nextId(), false, LengthValue.ZERO),
  MARGIN(new CSSProperty[] { MARGIN_TOP, MARGIN_BOTTOM, MARGIN_LEFT, MARGIN_RIGHT }),

  OUTLINE_WIDTH(nextId(), false, InvalidationLevel.PAINT, LengthValue.MEDIUM),
  OUTLINE_STYLE(nextId(), false, InvalidationLevel.PAINT, CSSValue.NONE),
  OUTLINE_COLOR(nextId(), false, InvalidationLevel.PAINT, CSSValue.AUTO),
  OUTLINE_OFFSET(nextId(), false, InvalidationLevel.PAINT, LengthValue.ZERO),
  OUTLINE(new CSSProperty[] { OUTLINE_WIDTH, OUTLINE_STYLE, OUTLINE_COLOR }),
  
  TOP(nextId(), false, CSSValue.AUTO),
  BOTTOM(nextId(), false, CSSValue.AUTO),
  LEFT(nextId(), false, CSSValue.AUTO),
  RIGHT(nextId(), false, CSSValue.AUTO),
  
  POSITION(nextId(), false, PositionValue.STATIC),
  
  // Can determine what elements get a layer
  Z_INDEX(nextId(), false, CSSValue.AUTO),
  
  ORDER(nextId(), false, OrderValue.create(0)),
  
  FLEX_DIRECTION(nextId(), false, FlexDirectionValue.ROW),
  FLEX_WRAP(nextId(), false, FlexWrapValue.NOWRAP),
  FLEX_FLOW(new CSSProperty[] { CSSProperty.FLEX_DIRECTION, CSSProperty.FLEX_WRAP }),
  
  FLEX_GROW(nextId(), false, FlexGrowValue.create(0)),
  FLEX_SHRINK(nextId(), false, FlexShrinkValue.create(1)),
  FLEX_BASIS(nextId(), false, CSSValue.AUTO),
  FLEX(new CSSProperty[] { CSSProperty.FLEX_GROW, CSSProperty.FLEX_SHRINK, CSSProperty.FLEX_BASIS }),
  
  JUSTIFY_CONTENT(nextId(), false, JustifyContentValue.NORMAL),
  ALIGN_ITEMS(nextId(), false, AlignItemsValue.STRETCH),
  ALIGN_SELF(nextId(), false, CSSValue.AUTO),
  ALIGN_CONTENT(nextId(), false, AlignContentValue.NORMAL),

  GRID_TEMPLATE_COLUMNS(nextId(), false, CSSValue.NONE),
  GRID_TEMPLATE_ROWS(nextId(), false, CSSValue.NONE),
  GRID_TEMPLATE_AREAS(nextId(), false, CSSValue.NONE),
  GRID_TEMPLATE(new CSSProperty[] {
    CSSProperty.GRID_TEMPLATE_ROWS, CSSProperty.GRID_TEMPLATE_COLUMNS, CSSProperty.GRID_TEMPLATE_AREAS}),
  
  GRID_AUTO_COLUMNS(nextId(), false, CSSValue.AUTO),
  GRID_AUTO_ROWS(nextId(), false, CSSValue.AUTO),
  GRID_AUTO_FLOW(nextId(), false, GridAutoFlowValue.create(
    GridAutoFlowDirection.ROW, false)),
  
  GRID(new CSSProperty[] {
    CSSProperty.GRID_TEMPLATE_ROWS, CSSProperty.GRID_TEMPLATE_COLUMNS, CSSProperty.GRID_TEMPLATE_AREAS,
    CSSProperty.GRID_AUTO_ROWS, CSSProperty.GRID_AUTO_COLUMNS, CSSProperty.GRID_AUTO_FLOW}),
  
  GRID_ROW_START(nextId(), false, CSSValue.AUTO),
  GRID_COLUMN_START(nextId(), false, CSSValue.AUTO),
  GRID_ROW_END(nextId(), false, CSSValue.AUTO),
  GRID_COLUMN_END(nextId(), false, CSSValue.AUTO),

  GRID_ROW(new CSSProperty[] { CSSProperty.GRID_ROW_START, CSSProperty.GRID_ROW_END }),
  GRID_COLUMN(new CSSProperty[] { CSSProperty.GRID_COLUMN_START, CSSProperty.GRID_COLUMN_END }),
  GRID_AREA(new CSSProperty[] {
    CSSProperty.GRID_ROW_START, CSSProperty.GRID_ROW_END,
    CSSProperty.GRID_COLUMN_START, CSSProperty.GRID_COLUMN_END }),

  ROW_GAP(nextId(), false, GapValue.NORMAL),
  COLUMN_GAP(nextId(), false, GapValue.NORMAL),
  GAP(new CSSProperty[] { CSSProperty.ROW_GAP, CSSProperty.COLUMN_GAP }),

  TABLE_LAYOUT(nextId(), false, CSSValue.AUTO),
  BORDER_COLLAPSE(nextId(), false, BorderCollapseValue.SEPARATE),
  BORDER_SPACING(nextId(), false, BorderSpacingValue.create(LengthValue.ZERO, LengthValue.ZERO)),
  CAPTION_SIDE(nextId(), false, CaptionSideValue.TOP),
  
  OVERFLOW_X(nextId(), false, InvalidationLevel.BOX, OverflowValue.VISIBLE),
  OVERFLOW_Y(nextId(), false, InvalidationLevel.BOX, OverflowValue.VISIBLE),
  // TODO: OVERFLOW_INLINE, OVERFLOW_BLOCK
  OVERFLOW(new CSSProperty[] { CSSProperty.OVERFLOW_X, CSSProperty.OVERFLOW_Y }),
  
  CONTENT(nextId(), false, InvalidationLevel.BOX, ContentValue.NORMAL),
  
  // Dummy value for ALL, it is computed later
  ALL(new CSSProperty[0]);

  private static int propertyId = 0;
  private static CSSProperty[] allExpansions;

  private final int id;
  private final boolean inherited;
  private final CSSValue initial;
  private final CSSProperty[] expansions;
  private final short invalidationLevel;

  private CSSProperty(int id, boolean inherited, short invalidationLevel, CSSValue initial) {
    this.id = id;
    this.inherited = inherited;
    this.initial = initial;
    this.expansions = null;
    this.invalidationLevel = invalidationLevel;
  }

  private CSSProperty(int id, boolean inherited, CSSValue initial) {
    this(id, inherited, InvalidationLevel.LAYOUT, initial);
  }

  private CSSProperty(CSSProperty[] expansions) {
    this.id = Integer.MAX_VALUE;
    this.inherited = false;
    this.initial = null;
    this.expansions = expansions;
    // Shorthand invalidation levels are not used
    this.invalidationLevel = InvalidationLevel.NONE;
  }

  public int id() {
    return this.id;
  }

  public String serialize() {
    return CSSSerializerUtil.serializeEnum(this);
  }

  public boolean inherited() {
    return this.inherited;
  }

  public CSSValue initial() {
    return this.initial;
  }

  public boolean hasExpansion() {
    return this.expansions != null;
  }

  public CSSProperty[] getExpansions() {
    if (this.equals(CSSProperty.ALL)) {
      return all();
    }
    return this.expansions;
  }

  public short invalidationLevel() {
    return this.invalidationLevel;
  }

  // Because propertyId keeps getting reset to 0
  private static int propertyIdCopy = Integer.MAX_VALUE;
  public static int idCount() {
    if (propertyIdCopy == Integer.MAX_VALUE) {
      propertyIdCopy = 0;
      for (CSSProperty property: values()) {
        if (property.id != Integer.MAX_VALUE) {
          propertyIdCopy++;
        }
      }
    }
    return propertyIdCopy;
  }

  public static CSSProperty getById(int id) {
    return all()[id];
  }

  private static int nextId() {
    return propertyId++;
  }

  private static CSSProperty[] all() {
    if (allExpansions != null) {
      return allExpansions;
    }

    List<CSSProperty> allProperties = new ArrayList<>();
    for (CSSProperty property: CSSProperty.values()) {
      if (!property.hasExpansion()) {
        allProperties.add(property);
      }
    }
    return allExpansions = allProperties.toArray(new CSSProperty[0]);
  }

}
