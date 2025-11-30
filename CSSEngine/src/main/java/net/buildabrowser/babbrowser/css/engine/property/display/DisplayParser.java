package net.buildabrowser.babbrowser.css.engine.property.display;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParser;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParserUtil.AnyOrderResult;
import net.buildabrowser.babbrowser.css.engine.property.display.DisplayValue.DisplayUnionValue;
import net.buildabrowser.babbrowser.css.engine.property.display.DisplayValue.InnerDisplayValue;
import net.buildabrowser.babbrowser.css.engine.property.display.DisplayValue.OuterDisplayValue;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
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
    "contents", new DisplayUnionValue(OuterDisplayValue.CONTENTS, InnerDisplayValue.FLOW),
    "none", new DisplayUnionValue(OuterDisplayValue.NONE, InnerDisplayValue.FLOW)
  );

  private static final Map<String, CSSValue> LEGACY_VALUES = Map.of(
    "inline-block", new DisplayUnionValue(OuterDisplayValue.INLINE, InnerDisplayValue.FLOW_ROOT),
    "inline-table", new DisplayUnionValue(OuterDisplayValue.INLINE, InnerDisplayValue.TABLE),
    "inline-flex", new DisplayUnionValue(OuterDisplayValue.INLINE, InnerDisplayValue.FLEX),
    "inline-grid", new DisplayUnionValue(OuterDisplayValue.INLINE, InnerDisplayValue.GRID)
  );

  // TOOO: Listitem and internal types
  @Override
  public CSSValue parse(SeekableCSSTokenStream stream, ActiveStyles activeStyles) throws IOException {
    CSSValue result = PropertyValueParserUtil.parseLongest(stream,
      (stream1, _) -> parseTuple(stream),
      (stream1, _) -> PropertyValueParserUtil.parseIdentMap(stream, BOX_VALUES),
      (stream1, _) -> PropertyValueParserUtil.parseIdentMap(stream, LEGACY_VALUES));
    
      if (result instanceof DisplayUnionValue unionValue) {
        activeStyles.setOuterDisplayValue(unionValue.outerDisplayValue());
        activeStyles.setInnerDisplayValue(unionValue.innerDisplayValue());
      }

      return result;
  }

  private CSSValue parseTuple(SeekableCSSTokenStream stream) throws IOException {
    CSSValue result = PropertyValueParserUtil.parseAnyOrder(stream,
      (stream1, _) -> PropertyValueParserUtil.parseIdentMap(stream1, OUTER_VALUES),
      (stream1, _) -> PropertyValueParserUtil.parseIdentMap(stream1, INNER_VALUES));
    if (!(result instanceof AnyOrderResult anyOrderResult)) return result;

    OuterDisplayValue outerDisplayValue = (OuterDisplayValue) anyOrderResult.values()[0];
    InnerDisplayValue innerDisplayValue = (InnerDisplayValue) anyOrderResult.values()[1];

    if (innerDisplayValue == null) innerDisplayValue = InnerDisplayValue.FLOW;
    if (outerDisplayValue == null) outerDisplayValue =
      innerDisplayValue == InnerDisplayValue.RUBY ? OuterDisplayValue.INLINE : OuterDisplayValue.BLOCK;

    return new DisplayUnionValue(outerDisplayValue, innerDisplayValue);
  }
  
}
