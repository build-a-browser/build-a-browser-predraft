package net.buildabrowser.babbrowser.cssbase.property.background;

import java.io.IOException;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.cssbase.property.box.VisualBoxParser;

public class BackgroundClipParser implements PropertyValueParser {

  private final VisualBoxParser visualBoxParser = new VisualBoxParser();

  @Override
  public CSSValue parse(CSSTokenStream stream) throws IOException {
    return PropertyValueParserUtil.parseCommaRepeat(stream, visualBoxParser);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.BACKGROUND_CLIP;
  }
  
}
