package net.buildabrowser.babbrowser.css.engine.property;

import java.io.IOException;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;

public interface PropertyValueParser {
 
  CSSValue parse(SeekableCSSTokenStream stream, ActiveStyles activeStyles) throws IOException;

}
