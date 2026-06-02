package net.buildabrowser.babbrowser.painter.skija;

import java.util.function.Consumer;

import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.skija.Picture;
import io.github.humbleui.skija.PictureRecorder;
import io.github.humbleui.types.Rect;
import net.buildabrowser.babbrowser.painter.core.PaintCanvas;

public class SkijaCommandList implements SkijaPaintBitMap {

  private final PictureRecorder recorder = new PictureRecorder();

  private final Rect sizeRect;

  private Picture picture;

  public SkijaCommandList(int width, int height) {
    this.sizeRect = Rect.makeWH(width, height);
  }

  @Override
  public void withCanvas(Consumer<PaintCanvas> paintFunc) {
    Canvas recordingCanvas = recorder.beginRecording(sizeRect);
    paintFunc.accept(new SkijaPaintCanvas(recordingCanvas));

    this.picture = recorder.finishRecordingAsPicture();
  }

  @Override
  public void draw(Canvas canvas, Paint paint, int x, int y) {
    canvas.save();
    canvas.translate(x, y);
    canvas.drawPicture(picture);
    canvas.restore();
  }

}
