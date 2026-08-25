package net.buildabrowser.babbrowser.cssbase.property.grid;

import java.io.IOException;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil;

public class GridAutoTracksParser implements PropertyValueParser {

  private final GridTrackSizeParser trackSizeParser = new GridTrackSizeParser(false);
  
  private final CSSProperty relatedProperty;

  public GridAutoTracksParser(CSSProperty relatedProperty) {
    this.relatedProperty = relatedProperty;
  }

  @Override
  public CSSValue parse(CSSTokenStream stream) throws IOException {
    return PropertyValueParserUtil.parseOneOrMore(stream, trackSizeParser);
  }

  @Override
  public CSSProperty relatedProperty() {
    return relatedProperty;
  }

}
