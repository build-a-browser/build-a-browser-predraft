package net.buildabrowser.babbrowser.cssbase.media;

import java.util.List;
import java.util.function.Function;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.calc.CalcEvaluation;

public record MediaContext(
  List<String> mediaTypes,
  Function<CSSValue, CalcEvaluation> calcFallback,
  int docWidth, int docHeight
) {
  
}
