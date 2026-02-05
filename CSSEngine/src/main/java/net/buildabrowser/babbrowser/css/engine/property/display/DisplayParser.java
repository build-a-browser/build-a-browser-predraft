package net.buildabrowser.babbrowser.css.engine.property.display;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParser;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParserUtil.AnyOrderResult;
import net.buildabrowser.babbrowser.css.engine.property.display.DisplayValue.InnerDisplayValue;
import net.buildabrowser.babbrowser.css.engine.property.display.DisplayValue.OuterDisplayValue;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;

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

  // TOOO: Listitem and internal types
  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    return PropertyValueParserUtil.parseLongest(stream,
      stream1 -> parseTuple(stream1),
      stream1 -> PropertyValueParserUtil.parseIdentMap(stream1, BOX_VALUES),
      stream1 -> PropertyValueParserUtil.parseIdentMap(stream1, LEGACY_VALUES));
  
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
