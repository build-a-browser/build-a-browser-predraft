package net.buildabrowser.babbrowser.render.content.common.paint;

import java.net.URI;

import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.css.engine.styles.util.ActiveStylesUtil;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil.ManyResult;
import net.buildabrowser.babbrowser.cssbase.property.background.BackgroundAttachmentValue;
import net.buildabrowser.babbrowser.cssbase.property.background.BackgroundPositionValue;
import net.buildabrowser.babbrowser.cssbase.property.background.BackgroundPositionValue.BackgroundPositionSide;
import net.buildabrowser.babbrowser.cssbase.property.background.BackgroundRepeatValue;
import net.buildabrowser.babbrowser.cssbase.property.background.BackgroundRepeatValue.BackgroundAxisRepeatValue;
import net.buildabrowser.babbrowser.cssbase.property.background.BackgroundSizeValue;
import net.buildabrowser.babbrowser.cssbase.property.background.BackgroundSizeValue.SizedBackgroundSizeValue;
import net.buildabrowser.babbrowser.cssbase.property.box.VisualBoxValue;
import net.buildabrowser.babbrowser.cssbase.property.shared.URLValue;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.network.URLUtil;
import net.buildabrowser.babbrowser.render.content.common.SizingUtil;
import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.render.content.scroll.ScrollBoxFragment;
import net.buildabrowser.babbrowser.render.image.ImageCache;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.render.layout.Viewport;
import net.buildabrowser.babbrowser.render.paint.backend.LoadedImage;
import net.buildabrowser.babbrowser.render.paint.backend.PaintCanvas;

public class ElementBackgroundImagePainter {
  
  public static void paintBackgroundImages(PaintCanvas canvas, BoxFragment fragment) {
    ManyResult bgImages = (ManyResult) fragment.box().activeStyles().getProperty(CSSProperty.BACKGROUND_IMAGE);
    ManyResult bgRepeats = (ManyResult) fragment.box().activeStyles().getProperty(CSSProperty.BACKGROUND_REPEAT);
    ManyResult bgAttachments = (ManyResult) fragment.box().activeStyles().getProperty(CSSProperty.BACKGROUND_ATTACHMENT);
    ManyResult bgPositions = (ManyResult) fragment.box().activeStyles().getProperty(CSSProperty.BACKGROUND_POSITION);
    ManyResult bgClips = (ManyResult) fragment.box().activeStyles().getProperty(CSSProperty.BACKGROUND_CLIP);
    ManyResult bgOrigins = (ManyResult) fragment.box().activeStyles().getProperty(CSSProperty.BACKGROUND_ORIGIN);
    ManyResult bgSizes = (ManyResult) fragment.box().activeStyles().getProperty(CSSProperty.BACKGROUND_SIZE);

    HTMLDocument nodeDocument = (HTMLDocument) fragment.box().element().nodeDocument();
    ImageCache imageCache = fragment.box().layoutContext().global().imageCache();

    boolean isInitial = true;
    for (int i = bgImages.values().size() - 1; i >= 0; i--) {
      clipCanvas(
        canvas, fragment,
        getBGLayerProperty(bgClips, i));

      if (isInitial) {
        canvas.alterPaint(paint -> paint.setColor(ActiveStylesUtil.backgroundColor(fragment.box().activeStyles())));
        canvas.drawBox(0, 0, fragment.width(Measurement.BORDER), fragment.height(Measurement.BORDER));
        isInitial = false;
      }

      canvas.unclip();

      CSSValue backgroundURL = bgImages.values().get(i);
      if (backgroundURL.equals(CSSValue.NONE)) continue;

      // TODO: This needs to be relative to the stylesheet, not the document
      URI imageURL = CommonUtil.tryOrNull(
        () -> URLUtil.createURL(nodeDocument.baseURL(), ((URLValue) backgroundURL).value()));
      if (imageURL == null) continue;

      LoadedImage image = imageCache.getImage(
        imageURL, fragment.box().element(), InvalidationLevel.PAINT);
      
      if (image == null) continue;

      clipCanvas(
        canvas, fragment,
        getBGLayerProperty(bgClips, i));
      
      paintBackground(
        canvas, fragment, image,
        getBGLayerProperty(bgRepeats, i),
        getBGLayerProperty(bgAttachments, i),
        getBGLayerProperty(bgPositions, i),
        getBGLayerProperty(bgOrigins, i),
        getBGLayerProperty(bgSizes, i));
    
      canvas.unclip();
    }
  }

  // TODO: Wow, this needs a ton of parameters (maybe refactor?)
  private static void paintBackground(
    PaintCanvas canvas,
    BoxFragment fragment,
    LoadedImage image,
    BackgroundRepeatValue repeatValue,
    BackgroundAttachmentValue attachmentValue,
    BackgroundPositionValue positionValue,
    VisualBoxValue originValue,
    CSSValue sizeValue
  ) {
    ScrollBoxFragment scrollBoxFragment = fragment instanceof ScrollBoxFragment scrollBoxFragment_ ? scrollBoxFragment_ : null;
    boolean isFixed = attachmentValue.equals(BackgroundAttachmentValue.FIXED);
    boolean isLocal = attachmentValue.equals(BackgroundAttachmentValue.LOCAL) && scrollBoxFragment != null;
    Viewport viewport = fragment.box().layoutContext().global().viewport();

    float vpW = fragment.width(Measurement.BORDER);
    float vpH = fragment.height(Measurement.BORDER);
    if (isFixed) {
      vpW = viewport.width();
      vpH = viewport.height();
    }

    LayoutContext layoutContext = fragment.box().layoutContext();
    float imgW = scaleImage(image, sizeValue, layoutContext, vpW, vpH, true);
    float imgH = scaleImage(image, sizeValue, layoutContext, vpW, vpH, false);

    boolean isXRound = repeatValue.xAxisRepeat().equals(BackgroundAxisRepeatValue.ROUND);
    boolean isYRound = repeatValue.yAxisRepeat().equals(BackgroundAxisRepeatValue.ROUND);
    imgW = isXRound ? (vpW / (Math.max(1, Math.round(vpW / imgW)))) : imgW;
    imgH = isYRound ? (vpH / (Math.max(1, Math.round(vpH / imgH)))) : imgH;

    float imgX = positionImage(
      imgW, vpW,
      positionValue.horizontalSide(), positionValue.horizontalLength(),
      layoutContext);
    if (!isFixed) {
      imgX = offsetImageX(imgX, fragment, originValue);
    }
    if (isLocal) {
      imgX -= scrollBoxFragment.box().scrollX();
    }

    float imgY = positionImage(
      imgH, vpH,
      positionValue.verticalSide(), positionValue.verticalLength(),
      layoutContext);
    if (!isFixed) {
      imgY = offsetImageY(imgY, fragment, originValue);
    }
    if (isLocal) {
      imgY -= scrollBoxFragment.box().scrollY();
    }

    if (isFixed) {
      float imgX_ = imgX, imgY_ = imgY;
      float imgW_ = imgW, imgH_ = imgH;
      float vpW_ = vpW, vpH_ = vpH;
      canvas.withMark(c -> drawRepeatingImage(
        canvas, image,
        repeatValue,
        imgX_, imgY_,
        imgW_, imgH_,
        vpW_, vpH_));
    } else {
      drawRepeatingImage(
        canvas, image,
        repeatValue,
        imgX, imgY,
        imgW, imgH,
        vpW, vpH);
    }
  }

  private static float offsetImageX(float imgX, BoxFragment fragment, VisualBoxValue originValue) {
    return switch (originValue) {
      case BORDER_BOX -> imgX;
      case PADDING_BOX -> imgX + fragment.posX(Measurement.PADDING) - fragment.posX(Measurement.BORDER);
      case CONTENT_BOX -> imgX + fragment.posX(Measurement.CONTENT) - fragment.posX(Measurement.BORDER);
    };
  }

  private static float offsetImageY(float imgY, BoxFragment fragment, VisualBoxValue originValue) {
    return switch (originValue) {
      case BORDER_BOX -> imgY;
      case PADDING_BOX -> imgY + fragment.posY(Measurement.PADDING) - fragment.posY(Measurement.BORDER);
      case CONTENT_BOX -> imgY + fragment.posY(Measurement.CONTENT) - fragment.posY(Measurement.BORDER);
    };
  }

  // TODO: Return both coordinates in one computation (but without an allocation)
  private static float scaleImage(
    LoadedImage image,
    CSSValue sizeValue,
    LayoutContext layoutContext,
    float vpW, float vpH,
    boolean isWidth
  ) {
    float widthAtVP = image.width() / image.height() * vpH;
    float heightAtVP = image.height() / image.width() * vpW;
    if (sizeValue.equals(BackgroundSizeValue.CONTAIN)) {
      float selectedWidth = Math.min(widthAtVP, vpW);
      float selectedHeight = Math.min(heightAtVP, vpH);
      return isWidth ? selectedWidth : selectedHeight;
    } else if (sizeValue.equals(BackgroundSizeValue.COVER)) {
      float selectedWidth = Math.max(widthAtVP, vpW);
      float selectedHeight = Math.max(heightAtVP, vpH);
      return isWidth ? selectedWidth : selectedHeight;
    }

    SizedBackgroundSizeValue outerValue = (SizedBackgroundSizeValue) sizeValue;
    CSSValue innerValue = isWidth ? outerValue.widthValue() : outerValue.heightValue();

    LayoutConstraint constraint = LayoutConstraint.of(isWidth ? image.width() : image.height());
    LayoutConstraint offsetConstraint = SizingUtil.evaluateBaseSize(layoutContext, constraint, innerValue);
    return offsetConstraint.isBounded() ?
      offsetConstraint.value() :
      constraint.value();
  }

  private static float positionImage(
    float imgSize, float vpSize,
    BackgroundPositionSide side,
    CSSValue sideLength,
    LayoutContext layoutContext
  ) {
    float centerPos = vpSize / 2 - imgSize / 2;
    if (side.equals(BackgroundPositionSide.CENTER)) {
      return centerPos;
    }

    float excludedSize = vpSize - imgSize;
    LayoutConstraint constraint = LayoutConstraint.of(excludedSize);
    LayoutConstraint offsetConstraint = SizingUtil.evaluateBaseSize(layoutContext, constraint, sideLength);
    if (!offsetConstraint.isBounded()) {
      return centerPos;
    }

    float offset = offsetConstraint.value();

    return switch (side) {
      case CENTER -> centerPos;
      case TOP, LEFT -> offset;
      case BOTTOM, RIGHT -> excludedSize - offset;
    };
  }

  private static void drawRepeatingImage(
    PaintCanvas canvas,
    LoadedImage image,
    BackgroundRepeatValue repeatValue,
    float imgX, float imgY,
    float imgW, float imgH,
    float vpW, float vpH
  ) {
    // TODO: What if imgX or imgY is negative?
    
    boolean isXSpace = repeatValue.xAxisRepeat().equals(BackgroundAxisRepeatValue.SPACE);
    boolean isYSpace = repeatValue.yAxisRepeat().equals(BackgroundAxisRepeatValue.SPACE);
    float adjX = adjustPosForRepeat(imgX, imgW, vpW, repeatValue.xAxisRepeat());
    float adjY = adjustPosForRepeat(imgY, imgH, vpH, repeatValue.yAxisRepeat());
    int repeatTimesX = determineRepeatTimes(adjX, imgW, vpW, repeatValue.xAxisRepeat());
    int repeatTimesY = determineRepeatTimes(adjY, imgH, vpH, repeatValue.yAxisRepeat());
    float strideX = isXSpace && repeatTimesX > 1 ? imgW + (vpW % imgW) / (repeatTimesX - 1) : imgW;
    float strideY = isYSpace && repeatTimesY > 1 ? imgH + (vpH % imgH) / (repeatTimesY - 1) : imgH;

    for (int tileX = 0; tileX < repeatTimesX; tileX++) {
      for (int tileY = 0; tileY < repeatTimesY; tileY++) {
        float tilePosX = adjX + tileX * strideX;
        float tilePosY = adjY + tileY * strideY;
        canvas.drawImage(tilePosX, tilePosY, imgW, imgH, image);
      }
    }
  }

  // TODO: Seems to match Firefox but not Chromium for ROUND... Which is right?
  private static float adjustPosForRepeat(
    float pos, float size, float vpSize,
    BackgroundAxisRepeatValue backgroundAxisRepeat
  ) {
    return switch (backgroundAxisRepeat) {
      case REPEAT, ROUND -> pos - (float) Math.ceil(pos / size) * size;
      case SPACE -> 0;
      case NO_REPEAT -> pos;
    };
  }

  private static int determineRepeatTimes(
    float pos, float size, float vpSize,
    BackgroundAxisRepeatValue backgroundAxisRepeat
  ) {
    return switch (backgroundAxisRepeat) {
      case REPEAT, ROUND -> (int) Math.ceil((vpSize - pos) / size);
      case SPACE -> Math.max(1, (int) (vpSize / size));
      case NO_REPEAT -> 1;
    };
  }

  private static void clipCanvas(
    PaintCanvas canvas, BoxFragment fragment,
    VisualBoxValue bgClip
  ) {
    float vpX = offsetImageX(0, fragment, bgClip);
    float vpY = offsetImageY(0, fragment, bgClip);
    float vpW = switch (bgClip) {
      case BORDER_BOX -> fragment.width(Measurement.BORDER);
      case PADDING_BOX -> fragment.width(Measurement.PADDING);
      case CONTENT_BOX -> fragment.width(Measurement.CONTENT);
    };
    float vpH = switch (bgClip) {
      case BORDER_BOX -> fragment.height(Measurement.BORDER);
      case PADDING_BOX -> fragment.height(Measurement.PADDING);
      case CONTENT_BOX -> fragment.height(Measurement.CONTENT);
    };
    canvas.clip(vpX, vpY, vpW, vpH);
  }

  @SuppressWarnings("unchecked")
  private static <T> T getBGLayerProperty(ManyResult values, int i) {
    return (T) values.values().get(i % values.values().size());
  }

}
