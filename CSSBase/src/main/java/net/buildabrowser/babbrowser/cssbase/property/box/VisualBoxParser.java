package net.buildabrowser.babbrowser.cssbase.property.box;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil;

public class VisualBoxParser implements PropertyValueParser {

  private final Map<String, CSSValue> VISUAL_BOX_VALUES = Map.of(
    "border-box", VisualBoxValue.BORDER_BOX,
    "padding-box", VisualBoxValue.PADDING_BOX,
    "content-box", VisualBoxValue.CONTENT_BOX
  );

  @Override
  public CSSValue parse(CSSTokenStream stream) throws IOException {
    return PropertyValueParserUtil.parseIdentMap(stream, VISUAL_BOX_VALUES);
  }
  
}
