package net.buildabrowser.babbrowser.renderer.paint;

import java.util.function.Consumer;

public class VpIntersection {
  
  private final int vpW, vpH;
  private int vpScrollX, vpScrollY;
  private int elX, elY, elW, elH;

  public VpIntersection(float vpW, float vpH) {
    this.vpW = this.elW =  (int) Math.ceil(vpW);
    this.vpH = this.elH =  (int) Math.ceil(vpH);
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

  public int elX() {
    return this.elX;
  }

  public int elY() {
    return this.elY;
  }

  public int elWidth() {
    return this.elW;
  }

  public int elHeight() {
    return this.elH;
  }

  // TODO: Also intersect width and height
  public void enterEl(
    float x, float y, float w, float h,
    Consumer<VpIntersection> elFunc
  ) {
    int vX = elX, vY = elY, vW = elW, vH = elH;
    elX -= x;
    elY -= y;
    elFunc.accept(this);
    
    elX = vX; elY = vY; elW = vW; elH = vH;
  }

  public void enterOffset(
    float x, float y, float scrollX, float scrollY,
    Consumer<VpIntersection> elFunc
  ) {
    int oldScrollX = vpScrollX, oldScrollY = vpScrollY;
    vpScrollX += scrollX;
    vpScrollY += scrollY;
    enterEl(x + scrollX, y + scrollY, elW - x, elH - y, elFunc);
    vpScrollX = oldScrollX; vpScrollY = oldScrollY;
  }

  public void enterCustom(
    float x, float y, float w, float h,
    Consumer<VpIntersection> elFunc
  ) {
    int vX = elX, vY = elY, vW = elW, vH = elH;
    // TODO: More precise rounding
    elX = (int) Math.floor(x);
    elY = (int) Math.floor(y);
    elW = (int) Math.ceil(w);
    elH = (int) Math.ceil(h);
    elFunc.accept(this);
    
    elX = vX; elY = vY; elW = vW; elH = vH;
  }

}
