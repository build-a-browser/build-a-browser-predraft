package net.buildabrowser.babbrowser.css.engine.property;

import net.buildabrowser.babbrowser.css.engine.property.color.ColorValue.SRGBAColor;
import net.buildabrowser.babbrowser.css.engine.property.display.DisplayValue;
import net.buildabrowser.babbrowser.css.engine.property.display.DisplayValue.InnerDisplayValue;
import net.buildabrowser.babbrowser.css.engine.property.display.DisplayValue.OuterDisplayValue;
import net.buildabrowser.babbrowser.css.engine.property.position.PositionValue;
import net.buildabrowser.babbrowser.css.engine.property.size.LengthValue;
import net.buildabrowser.babbrowser.css.engine.property.text.TextWrapModeValue;
import net.buildabrowser.babbrowser.css.engine.property.whitespace.WhitespaceCollapseValue;

public enum CSSProperty {
  
  COLOR(nextId(), true, SRGBAColor.create(0, 0, 0, 255)),
  BACKGROUND_COLOR(nextId(), false, SRGBAColor.create(0, 0, 0, 0)),
  BACKGROUND(new CSSProperty[] { BACKGROUND_COLOR }),
  WIDTH(nextId(), false, CSSValue.AUTO),
  HEIGHT(nextId(), false, CSSValue.AUTO),
  DISPLAY(nextId(), false, DisplayValue.create(OuterDisplayValue.INLINE, InnerDisplayValue.FLOW)),
  FLOAT(nextId(), false, CSSValue.NONE),
  CLEAR(nextId(), false, CSSValue.NONE),
  WHITE_SPACE_COLLAPSE(nextId(), true, WhitespaceCollapseValue.COLLAPSE),
  TEXT_WRAP_MODE(nextId(), true, TextWrapModeValue.WRAP),

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
  BORDER_TOP_COLOR(nextId(), false, CSSValue.NONE),
  BORDER_BOTTOM_COLOR(nextId(), false, CSSValue.NONE),
  BORDER_LEFT_COLOR(nextId(), false, CSSValue.NONE),
  BORDER_RIGHT_COLOR(nextId(), false, CSSValue.NONE),
  BORDER_COLOR(new CSSProperty[] { BORDER_TOP_COLOR, BORDER_BOTTOM_COLOR, BORDER_LEFT_COLOR, BORDER_RIGHT_COLOR }),

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
  
  TOP(nextId(), false, CSSValue.AUTO),
  BOTTOM(nextId(), false, CSSValue.AUTO),
  LEFT(nextId(), false, CSSValue.AUTO),
  RIGHT(nextId(), false, CSSValue.AUTO),
  
  POSITION(nextId(), false, PositionValue.STATIC),
  
  Z_INDEX(nextId(), false, CSSValue.AUTO);

  private static int propertyId = 0;

  private final int id;
  private final boolean inherited;
  private final CSSValue initial;
  private final CSSProperty[] expansions;

  private CSSProperty(int id, boolean inherited, CSSValue initial) {
    this.id = id;
    this.inherited = inherited;
    this.initial = initial;
    this.expansions = null;
  }

  private CSSProperty(CSSProperty[] expansions) {
    this.id = Integer.MAX_VALUE;
    this.inherited = false;
    this.initial = null;
    this.expansions = expansions;
  }

  public int id() {
    return this.id;
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
    return this.expansions;
  }

  public static int idCount() {
    // TODO: Why does propertyId seem to reset to 0?
    //   (I even tried it with volatile)
    // Manually update this for now
    return 36;
  }

  private static int nextId() {
    return propertyId++;
  }

}
