package net.buildabrowser.babbrowser.renderer.paint;

import java.util.function.Consumer;

public class VpIntersection {
  
  private final int vpW, vpH;
  private int vpScrollX, vpScrollY;
  private int bufferX, bufferY;
  private int bufferW, bufferH;
  private int elX, elY;

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

  public int elVpX() {
    return this.elX - this.vpScrollX;
  }

  public int elVpY() {
    return this.elY - this.vpScrollY;
  }

  // TODO: Make a version also accounting for element width/height (adjusted for ink if needed)
  public void enterEl(
    float x, float y,
    Consumer<VpIntersection> elFunc
  ) {
    int oldElX = elX, oldElY = elY;
    elX += x;
    elY += y;
    elFunc.accept(this);
    
    elX = oldElX; elY = oldElY;
  }

  public void enterOffset(
    float x, float y, float scrollX, float scrollY,
    Consumer<VpIntersection> elFunc
  ) {
    int oldScrollX = vpScrollX, oldScrollY = vpScrollY;
    vpScrollX += scrollX;
    vpScrollY += scrollY;
    enterEl(x, y, elFunc);
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
