package net.buildabrowser.babbrowser.render.paint.backend.skija;

import java.awt.Graphics;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.awt.AWTGLCanvas;

import io.github.humbleui.skija.BackendRenderTarget;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.ColorType;
import io.github.humbleui.skija.DirectContext;
import io.github.humbleui.skija.FramebufferFormat;
import io.github.humbleui.skija.PixelGeometry;
import io.github.humbleui.skija.Surface;
import io.github.humbleui.skija.SurfaceOrigin;
import io.github.humbleui.skija.SurfaceProps;
import net.buildabrowser.babbrowser.render.paint.backend.CanvasCallbacks;
import net.buildabrowser.babbrowser.render.paint.backend.PaintCanvas;

public class SkijaGPUCanvas extends AWTGLCanvas {

  private final CanvasCallbacks callbacks;

  private DirectContext context;
  
  private boolean invalid = true;
  private int fboId;
  private Surface surface;

  public SkijaGPUCanvas(CanvasCallbacks callbacks) {
    this.callbacks = callbacks;
  }

  @Override
  public void initGL() {
    GL.createCapabilities();

    if (this.context == null) {
      this.context = DirectContext.makeGL();
    }

    this.fboId = GL31.glGetInteger(GL31.GL_FRAMEBUFFER_BINDING);
    createSurface();
  }

  @Override
  public void paintGL() {
    if (invalid) {
      callbacks.layout();
    }

    if (
      surface.getWidth() != getFramebufferWidth()
      || surface.getHeight() != getFramebufferHeight()
    ) {
      GL11.glViewport(0, 0, getFramebufferWidth(), getFramebufferHeight());
      createSurface();
    }

    Canvas rawCanvas = surface.getCanvas();
    PaintCanvas canvas = new SkijaPaintCanvas(rawCanvas);
    callbacks.paint(canvas);
    context.flush();

    this.swapBuffers();
  }

  @Override
  public void invalidate() {
    this.invalid = true;
    super.invalidate();
  }

  @Override
  public void paint(Graphics g) {
    this.render();
  }

  private void createSurface() {
    BackendRenderTarget renderTarget = BackendRenderTarget.makeGL(
      getWidth(), getHeight(),
      0, 8, fboId,
      FramebufferFormat.GR_GL_RGBA8);

    this.surface = Surface.wrapBackendRenderTarget(
      context,
      renderTarget,
      SurfaceOrigin.BOTTOM_LEFT,
      ColorType.RGBA_8888,
      io.github.humbleui.skija.ColorSpace.getDisplayP3(),
      new SurfaceProps(PixelGeometry.RGB_H));
  }

}
