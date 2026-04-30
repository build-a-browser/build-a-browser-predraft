package net.buildabrowser.babbrowser.css.engine.property.background;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParser;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParserUtil.AnyOrderResult;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParserUtil.ListResult;
import net.buildabrowser.babbrowser.css.engine.property.background.BackgroundValue.BackgroundLayer;
import net.buildabrowser.babbrowser.css.engine.property.box.VisualBoxParser;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.DelimToken;

public class BackgroundParser implements PropertyValueParser {

  private static final CSSFailure BG_COLOR_FAILURE = new CSSFailure("Only the last layer can have a bg color!");

  private final BackgroundImageParser backgroundImageParser = new BackgroundImageParser();
  private final BackgroundPositionParser backgroundPositionParser = new BackgroundPositionParser();
  private final BackgroundSizeParser backgroundSizeParser = new BackgroundSizeParser();
  private final BackgroundRepeatParser backgroundRepeatParser = new BackgroundRepeatParser();
  private final BackgroundAttachmentParser backgroundAttachmentParser = new BackgroundAttachmentParser();
  private final VisualBoxParser visualBoxParser = new VisualBoxParser();
  private final BackgroundColorParser backgroundColorParser = new BackgroundColorParser();

  private final PropertyValueParser[] bgLayerParser = new PropertyValueParser[] {
    backgroundImageParser::parseInternal,
    this::parsePositionSize,
    backgroundRepeatParser::parseInternal,
    backgroundAttachmentParser::parseInternal,
    visualBoxParser,
    visualBoxParser,
    backgroundColorParser::parseInternal
  };
  
  @Override
  @SuppressWarnings({"rawtypes", "unchecked"})
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    CSSValue result = PropertyValueParserUtil.parseCommaRepeat(stream, this::parseLayer);
    if (result.isFailure()) return result;

    List<BackgroundLayer> bgLayers = (List<BackgroundLayer>) (List) ((ListResult) result).values();

    BackgroundLayer lastLayer = bgLayers.getLast();
    for (BackgroundLayer layer: bgLayers) {
      if (layer == lastLayer) continue;
      if (layer.bgColor() != null) {
        return BG_COLOR_FAILURE;
      }
    }

    return BackgroundValue.create(bgLayers);
  }

  @Override
  public void updateProperty(CSSValue result, ActiveStyles activeStyles) {
    List<BackgroundLayer> bgLayers = ((BackgroundValue) result).bgLayers();
    updateSpecificProperty(CSSProperty.BACKGROUND_IMAGE, activeStyles, bgLayers, BackgroundLayer::bgImage);
    updateSpecificProperty(CSSProperty.BACKGROUND_POSITION, activeStyles, bgLayers, BackgroundLayer::bgPosition);
    updateSpecificProperty(CSSProperty.BACKGROUND_SIZE, activeStyles, bgLayers, BackgroundLayer::bgSize);
    updateSpecificProperty(CSSProperty.BACKGROUND_REPEAT, activeStyles, bgLayers, BackgroundLayer::repeatStyle);
    updateSpecificProperty(CSSProperty.BACKGROUND_ATTACHMENT, activeStyles, bgLayers, BackgroundLayer::attachment);
    updateSpecificProperty(CSSProperty.BACKGROUND_ORIGIN, activeStyles, bgLayers, BackgroundLayer::bgOrigin);
    updateSpecificProperty(CSSProperty.BACKGROUND_CLIP, activeStyles, bgLayers, BackgroundLayer::bgClip);

    CSSValue bgColor = bgLayers.getLast().bgColor();
    if (bgColor == null) {
      bgColor = CSSProperty.BACKGROUND_COLOR.initial();
    }
    activeStyles.setProperty(CSSProperty.BACKGROUND_COLOR, bgColor);
  }

  private void updateSpecificProperty(
    CSSProperty property,
    ActiveStyles activeStyles,
    List<BackgroundLayer> bgLayers,
    Function<BackgroundLayer, CSSValue> valueGetter
  ) {
    List<CSSValue> propLayers = new ArrayList<>(bgLayers.size());
    for (BackgroundLayer layer: bgLayers) {
      CSSValue value = valueGetter.apply(layer);
      if (value == null) {
        value = property.initial();
      }
    }

    ListResult valueList = new ListResult(propLayers);
    activeStyles.setProperty(property, valueList);
  }

  private CSSValue parseLayer(SeekableCSSTokenStream stream) throws IOException {
    CSSValue anyOrderResult = PropertyValueParserUtil.parseAnyOrder(stream, bgLayerParser);
    if (anyOrderResult.isFailure()) return anyOrderResult;
    CSSValue[] results = ((AnyOrderResult) anyOrderResult).values();

    CSSValue position = null;
    CSSValue size = null;
    if (results[1] != null) {
      BackgroundPositionSizeValue psValue = (BackgroundPositionSizeValue) results[1];
      position = psValue.position();
      size = psValue.size();
    }

    CSSValue bgOrigin = results[4];
    CSSValue bgClip = results[5];
    if (bgClip == null) {
      bgClip = bgOrigin;
    }

    return BackgroundLayer.create(
      results[0], position, size, results[2], results[3],
      bgOrigin, bgClip, results[6]);
  }

  private CSSValue parsePositionSize(SeekableCSSTokenStream stream) throws IOException {
    CSSValue positionValue = backgroundPositionParser.parseInternal(stream);
    if (positionValue.isFailure()) return positionValue;
    
    if (!(
      stream.peek() instanceof DelimToken delimToken
      && delimToken.ch() == '/'
    )) {
      return new BackgroundPositionSizeValue(positionValue, null);
    }
    stream.read();

    CSSValue sizeValue = backgroundSizeParser.parseInternal(stream);
    if (sizeValue.isFailure()) return sizeValue;
    
    return new BackgroundPositionSizeValue(positionValue, sizeValue);
  }

  private static record BackgroundPositionSizeValue(
    CSSValue position,
    CSSValue size
  ) implements CSSValue {}
  
}
