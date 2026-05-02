package net.buildabrowser.babbrowser.cssbase.property.display;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.cssbase.parser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil.AnyOrderResult;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue.InnerDisplayValue;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue.OuterDisplayValue;

public class DisplayParser implements PropertyValueParser {

  private static final Map<String, CSSValue> OUTER_VALUES = Map.of(
    "block", OuterDisplayValue.BLOCK,
    "inline", OuterDisplayValue.INLINE,
    "run-in", OuterDisplayValue.RUN_IN
  );  

  private static final Map<String, CSSValue> INNER_VALUES = Map.of(
    "flow", InnerDisplayValue.FLOW,
    "flow-root", InnerDisplayValue.FLOW_ROOT,
    "table", InnerDisplayValue.TABLE,
    "flex", InnerDisplayValue.FLEX,
    "grid", InnerDisplayValue.GRID,
    "ruby", InnerDisplayValue.RUBY
  );

  private static final Map<String, CSSValue> BOX_VALUES = Map.of(
    "contents", DisplayValue.create(OuterDisplayValue.CONTENTS, InnerDisplayValue.FLOW),
    "none", DisplayValue.create(OuterDisplayValue.NONE, InnerDisplayValue.FLOW)
  );

  private static final Map<String, CSSValue> LEGACY_VALUES = Map.of(
    "inline-block", DisplayValue.create(OuterDisplayValue.INLINE, InnerDisplayValue.FLOW_ROOT),
    "inline-table", DisplayValue.create(OuterDisplayValue.INLINE, InnerDisplayValue.TABLE),
    "inline-flex", DisplayValue.create(OuterDisplayValue.INLINE, InnerDisplayValue.FLEX),
    "inline-grid", DisplayValue.create(OuterDisplayValue.INLINE, InnerDisplayValue.GRID)
  );

  private static final Map<String, CSSValue> INTERNAL_VALUES = Map.of(
    "table-row-group", DisplayValue.create(OuterDisplayValue.TABLE_ROW_GROUP, InnerDisplayValue.TABLE_ROW_GROUP),
    "table-header-group", DisplayValue.create(OuterDisplayValue.TABLE_HEADER_GROUP, InnerDisplayValue.TABLE_HEADER_GROUP),
    "table-footer-group", DisplayValue.create(OuterDisplayValue.TABLE_FOOTER_GROUP, InnerDisplayValue.TABLE_FOOTER_GROUP),
    "table-row", DisplayValue.create(OuterDisplayValue.TABLE_ROW, InnerDisplayValue.TABLE_ROW),
    "table-cell", DisplayValue.create(OuterDisplayValue.TABLE_CELL, InnerDisplayValue.FLOW_ROOT),
    "table-column-group", DisplayValue.create(OuterDisplayValue.TABLE_COLUMN_GROUP, InnerDisplayValue.TABLE_COLUMN_GROUP),
    "table-column", DisplayValue.create(OuterDisplayValue.TABLE_COLUMN, InnerDisplayValue.TABLE_COLUMN),
    "table-caption", DisplayValue.create(OuterDisplayValue.TABLE_CAPTION, InnerDisplayValue.FLOW_ROOT)
  );

  // TOOO: Listitem and internal types
  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    return PropertyValueParserUtil.parseLongest(stream,
      stream1 -> parseTuple(stream1),
      stream1 -> PropertyValueParserUtil.parseIdentMap(stream1, BOX_VALUES),
      stream1 -> PropertyValueParserUtil.parseIdentMap(stream1, LEGACY_VALUES),
      stream1 -> PropertyValueParserUtil.parseIdentMap(stream, INTERNAL_VALUES));
  }

  private CSSValue parseTuple(SeekableCSSTokenStream stream) throws IOException {
    CSSValue result = PropertyValueParserUtil.parseAnyOrder(stream,
      stream1 -> PropertyValueParserUtil.parseIdentMap(stream1, OUTER_VALUES),
      stream1 -> PropertyValueParserUtil.parseIdentMap(stream1, INNER_VALUES));
    if (!(result instanceof AnyOrderResult anyOrderResult)) return result;

    OuterDisplayValue outerDisplayValue = (OuterDisplayValue) anyOrderResult.values()[0];
    InnerDisplayValue innerDisplayValue = (InnerDisplayValue) anyOrderResult.values()[1];

    if (innerDisplayValue == null) innerDisplayValue = InnerDisplayValue.FLOW;
    if (outerDisplayValue == null) outerDisplayValue =
      innerDisplayValue == InnerDisplayValue.RUBY ? OuterDisplayValue.INLINE : OuterDisplayValue.BLOCK;

    return DisplayValue.create(outerDisplayValue, innerDisplayValue);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.DISPLAY;
  }
  
}
