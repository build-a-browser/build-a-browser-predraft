package net.buildabrowser.babbrowser.renderer.paint;

import java.util.function.Consumer;

public class VpIntersection {
  
  private final int vpW, vpH;
  private int vpScrollX, vpScrollY;
  private int bufferVpX, bufferVpY;
  private int bufferX, bufferY;
  private int bufferW, bufferH;

  public VpIntersection(float vpW, float vpH) {
    this.vpW = (int) Math.ceil(vpW);
    this.vpH = (int) Math.ceil(vpH);
    this.bufferW = this.vpW;
    this.bufferH = this.vpH;
  }

  public int vpWidth() {
    return this.vpW;
  }

  public int vpHeight() {
    return this.vpH;
  }

  public int vpScrollX() {
    return this.vpScrollX;
  }

  public int vpScrollY() {
    return this.vpScrollY;
  }

  public int bufferVpX() {
    return this.bufferVpX - this.vpScrollX;
  }

  public int bufferVpY() {
    return this.bufferVpY - this.vpScrollY;
  }

  public int bufferX() {
    return this.bufferX;
  }

  public int bufferY() {
    return this.bufferY;
  }

  public int bufferWidth() {
    return this.bufferW;
  }

  public int bufferHeight() {
    return this.bufferH;
  }

  public void enterOffset(
    float x, float y, float scrollX, float scrollY,
    Consumer<VpIntersection> elFunc
  ) {
    int oldScrollX = vpScrollX, oldScrollY = vpScrollY;
    int oldBufferVpX = bufferVpX, oldBufferVpY = bufferVpY;
    vpScrollX += scrollX;
    vpScrollY += scrollY;
    bufferVpX += x;
    bufferVpY += y;
    elFunc.accept(this);
    bufferVpX = oldBufferVpX; bufferVpY = oldBufferVpY;
    vpScrollX = oldScrollX; vpScrollY = oldScrollY;
  }

  public void enterBuffer(
    float x, float y, float w, float h,
    Consumer<VpIntersection> elFunc
  ) {
    int oldBufferX = bufferX, oldBufferY = bufferY;
    int oldBufferW = bufferW, oldBufferH = bufferH;
    // TODO: More precise rounding
    bufferX = (int) Math.floor(x);
    bufferY = (int) Math.floor(y);
    bufferW = (int) Math.ceil(w);
    bufferH = (int) Math.ceil(h);
    elFunc.accept(this);
    
    bufferX = oldBufferX; bufferY = oldBufferY;
    bufferW = oldBufferW; bufferH = oldBufferH;
  }

}
