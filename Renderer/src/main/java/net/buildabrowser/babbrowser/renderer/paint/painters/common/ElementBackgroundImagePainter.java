package net.buildabrowser.babbrowser.renderer.paint.painters.common;

import java.net.URI;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
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
import net.buildabrowser.babbrowser.cssbase.util.PropertiesUtil;
import net.buildabrowser.babbrowser.network.URLUtil;
import net.buildabrowser.babbrowser.painter.core.LoadedImage;
import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.renderer.content.common.SizingUtil;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.scroll.ScrollBoxFragment;
import net.buildabrowser.babbrowser.renderer.image.ImageCache;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutContext;
import net.buildabrowser.babbrowser.renderer.paint.VpIntersection;

public class ElementBackgroundImagePainter {

  public static void paintBackgroundImagesAdjusted(
    PaintCanvas canvas,
    BoxFragment<?> fragment,
    VpIntersection vpIntersection,
    PropertyContainer properties,
    float fragmentWidth,
    float fragmentHeight
  ) {
    ManyResult bgImages = (ManyResult) properties.get(CSSProperty.BACKGROUND_IMAGE);
    ManyResult bgRepeats = (ManyResult) properties.get(CSSProperty.BACKGROUND_REPEAT);
    ManyResult bgAttachments = (ManyResult) properties.get(CSSProperty.BACKGROUND_ATTACHMENT);
    ManyResult bgPositions = (ManyResult) properties.get(CSSProperty.BACKGROUND_POSITION);
    ManyResult bgClips = (ManyResult) properties.get(CSSProperty.BACKGROUND_CLIP);
    ManyResult bgOrigins = (ManyResult) properties.get(CSSProperty.BACKGROUND_ORIGIN);
    ManyResult bgSizes = (ManyResult) properties.get(CSSProperty.BACKGROUND_SIZE);

    ImageCache imageCache = fragment.box().layoutContext().global().imageCache();

    boolean isInitial = true;
    for (int i = bgImages.values().size() - 1; i >= 0; i--) {
      int layer = i;
      if (isInitial) {
        clipCanvas(
          canvas, fragment,
          fragmentWidth, fragmentHeight,
          getBGLayerProperty(bgClips, layer),
          _1 -> canvas.withPaint(
            paint -> paint.setColor(PropertiesUtil.backgroundColor(properties)),
            _2 -> canvas.drawBox(0, 0, fragmentWidth, fragmentHeight)
          ));
        
        isInitial = false;
      }

      CSSValue backgroundURL = bgImages.values().get(i);
      if (backgroundURL.equals(CSSValue.NONE)) continue;

      // TODO: This needs to be relative to the stylesheet, not the document
      URLValue urlValue = (URLValue) backgroundURL;
      URI imageURL = CommonUtil.tryOrNull(
        () -> URLUtil.createURL(urlValue.refURL(), urlValue.value()));
      if (imageURL == null) continue;

      LoadedImage image = imageCache.getImage(
        imageURL, fragment.box().context(), InvalidationLevel.PAINT);
      
      if (image == null) continue;

      clipCanvas(
        canvas, fragment,
        fragmentWidth, fragmentHeight,
        getBGLayerProperty(bgClips, layer),
        c -> paintBackground(
          c, fragment, vpIntersection, image,
          getBGLayerProperty(bgRepeats, layer),
          getBGLayerProperty(bgAttachments, layer),
          getBGLayerProperty(bgPositions, layer),
          getBGLayerProperty(bgOrigins, layer),
          getBGLayerProperty(bgSizes, layer),
          fragmentWidth,
          fragmentHeight));
    }
  }

  // TODO: Wow, this needs a ton of parameters (maybe refactor?)
  private static void paintBackground(
    PaintCanvas canvas,
    BoxFragment<?> fragment,
    VpIntersection vpIntersection,
    LoadedImage image,
    BackgroundRepeatValue repeatValue,
    BackgroundAttachmentValue attachmentValue,
    BackgroundPositionValue positionValue,
    VisualBoxValue originValue,
    CSSValue sizeValue,
    float fragmentWidth,
    float fragmentHeight
  ) {
    ScrollBoxFragment scrollBoxFragment = fragment instanceof ScrollBoxFragment scrollBoxFragment_ ? scrollBoxFragment_ : null;
    boolean isFixed = attachmentValue.equals(BackgroundAttachmentValue.FIXED);
    boolean isLocal = attachmentValue.equals(BackgroundAttachmentValue.LOCAL) && scrollBoxFragment != null;

    float vpW = fragmentWidth;
    float vpH = fragmentHeight;
    if (isFixed) {
      vpW = vpIntersection.vpWidth();
      vpH = vpIntersection.vpHeight();
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
      imgX -= scrollBoxFragment.scrollX();
    }

    float imgY = positionImage(
      imgH, vpH,
      positionValue.verticalSide(), positionValue.verticalLength(),
      layoutContext);
    if (!isFixed) {
      imgY = offsetImageY(imgY, fragment, originValue);
    }
    if (isLocal) {
      imgY -= scrollBoxFragment.scrollY();
    }

    if (isFixed) {
      float imgX_ = imgX, imgY_ = imgY;
      float imgW_ = imgW, imgH_ = imgH;
      canvas.restoreTransform(c -> drawRepeatingImage(
        c, image,
        repeatValue,
        imgX_, imgY_,
        imgW_, imgH_,
        vpIntersection.vpWidth(), vpIntersection.vpHeight(),
        vpIntersection.bufferVpX() + fragment.layerX(Measurement.BORDER),
        vpIntersection.bufferVpY() + fragment.layerY(Measurement.BORDER),
        fragmentWidth, fragmentHeight));
    } else {
      drawRepeatingImage(
        canvas, image,
        repeatValue,
        imgX, imgY,
        imgW, imgH,
        vpW, vpH,
        0, 0,
        vpW, vpH);
    }
  }

  private static float offsetImageX(float imgX, BoxFragment<?> fragment, VisualBoxValue originValue) {
    return switch (originValue) {
      case BORDER_BOX -> imgX;
      case PADDING_BOX -> imgX + fragment.posX(Measurement.PADDING) - fragment.posX(Measurement.BORDER);
      case CONTENT_BOX -> imgX + fragment.posX(Measurement.CONTENT) - fragment.posX(Measurement.BORDER);
      default -> throw new UnsupportedOperationException("Unrecognized Origin Value: " + originValue);
    };
  }

  private static float offsetImageY(float imgY, BoxFragment<?> fragment, VisualBoxValue originValue) {
    return switch (originValue) {
      case BORDER_BOX -> imgY;
      case PADDING_BOX -> imgY + fragment.posY(Measurement.PADDING) - fragment.posY(Measurement.BORDER);
      case CONTENT_BOX -> imgY + fragment.posY(Measurement.CONTENT) - fragment.posY(Measurement.BORDER);
      default -> throw new UnsupportedOperationException("Unrecognized Origin Value: " + originValue);
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
      default -> throw new UnsupportedOperationException("Unrecognized Background Position Side: " + side);
    };
  }

  // TODO: Sooo many parameters, would be nice to simplify this
  // (without a record, ofc)
  private static void drawRepeatingImage(
    PaintCanvas canvas,
    LoadedImage image,
    BackgroundRepeatValue repeatValue,
    float imgX, float imgY,
    float imgW, float imgH,
    float vpW, float vpH,
    float elVpOffsetX, float elVpOffsetY,
    float elW, float elH
  ) {
    // TODO: What if imgX or imgY is negative?
    
    boolean isXSpace = repeatValue.xAxisRepeat().equals(BackgroundAxisRepeatValue.SPACE);
    boolean isYSpace = repeatValue.yAxisRepeat().equals(BackgroundAxisRepeatValue.SPACE);
    float adjX = adjustPosForRepeat(imgX, imgW, vpW, elVpOffsetX, repeatValue.xAxisRepeat());
    float adjY = adjustPosForRepeat(imgY, imgH, vpH, elVpOffsetY, repeatValue.yAxisRepeat());
    int repeatTimesX = determineRepeatTimes(adjX, imgW, vpW, elVpOffsetX, elW, repeatValue.xAxisRepeat());
    int repeatTimesY = determineRepeatTimes(adjY, imgH, vpH, elVpOffsetY, elH, repeatValue.yAxisRepeat());
    float strideX = isXSpace && repeatTimesX > 1 ? imgW + (vpW % imgW) / (repeatTimesX - 1) : imgW;
    float strideY = isYSpace && repeatTimesY > 1 ? imgH + (vpH % imgH) / (repeatTimesY - 1) : imgH;

    // TODO: Figure out how to do this without a loop
    // (Skija has a way to draw repeating images, but we need a gap between them)
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
    float pos, float size, float vpSize, float elVpOffset,
    BackgroundAxisRepeatValue backgroundAxisRepeat
  ) {
    return switch (backgroundAxisRepeat) {
      case REPEAT, ROUND -> {
        float determinedPos = pos - (float) Math.ceil(pos / size) * size;
        // Optimization to only draw repeating fixed textures near the element
        float postGap = elVpOffset - determinedPos;
        determinedPos += (int) (postGap / size) * size;
        yield determinedPos;
      }
      case SPACE -> 0;
      case NO_REPEAT -> pos;
      default -> throw new UnsupportedOperationException("Unrecognized Background Axis Repeat: " + backgroundAxisRepeat);
    };
  }

  private static int determineRepeatTimes(
    float pos, float size, float vpSize,
    float elVpOffset, float elSize,
    BackgroundAxisRepeatValue backgroundAxisRepeat
  ) {
    int repeatRepeat = (int) Math.ceil((elSize + (elVpOffset - pos)) / size);
    return switch (backgroundAxisRepeat) {
      case REPEAT, ROUND -> repeatRepeat;
      case SPACE -> {
        int spaceRepeat = Math.max(1, (int) (vpSize / size));
        yield Math.min(spaceRepeat, repeatRepeat);
      }
      case NO_REPEAT -> 1;
      default -> throw new UnsupportedOperationException("Unrecognized Background Axis Repeat: " + backgroundAxisRepeat);
    };
  }

  private static void clipCanvas(
    PaintCanvas canvas, BoxFragment<?> fragment,
    float fragmentWidth, float fragmentHeight,
    VisualBoxValue bgClip,
    Consumer<PaintCanvas> paintFunc
  ) {
    float vpX = offsetImageX(0, fragment, bgClip);
    float vpY = offsetImageY(0, fragment, bgClip);
    float vpW = switch (bgClip) {
      case BORDER_BOX -> fragmentWidth;
      case PADDING_BOX -> fragment.width(Measurement.PADDING);
      case CONTENT_BOX -> fragment.width(Measurement.CONTENT);
      default -> throw new UnsupportedOperationException("Unrecognized Background Clip: " + bgClip);
    };
    float vpH = switch (bgClip) {
      case BORDER_BOX -> fragmentHeight;
      case PADDING_BOX -> fragment.height(Measurement.PADDING);
      case CONTENT_BOX -> fragment.height(Measurement.CONTENT);
      default -> throw new UnsupportedOperationException("Unrecognized Background Clip: " + bgClip);
    };
    canvas.withClip(vpX, vpY, vpW, vpH, paintFunc);
  }

  @SuppressWarnings("unchecked")
  private static <T> T getBGLayerProperty(ManyResult values, int i) {
    return (T) values.values().get(i % values.values().size());
  }

}
