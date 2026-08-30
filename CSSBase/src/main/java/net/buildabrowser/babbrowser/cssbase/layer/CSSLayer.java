package net.buildabrowser.babbrowser.cssbase.layer;

import net.buildabrowser.babbrowser.cssbase.layer.imp.CSSLayerImp;

public interface CSSLayer {

  CSSLayer parentLayer();

  String longName();

  int childIndex(CSSLayer child);

  CSSLayer childLayer(String name);

  CSSLayer createAnonymousChild();

  void resetOrder();
  
  void markChildUse(CSSLayer child);

  int depth();

  static CSSLayer create() {
    return new CSSLayerImp(null, "", 0);
  }

  public static int compareOrder(CSSLayer a, CSSLayer b) {
    if (a == b) return 0;
    if (isRoot(a) && isRoot(b)) return 0;
    if (isRoot(a) && !isRoot(b)) return 1;
    if (!isRoot(a) && isRoot(b)) return -1;
    assert a != null && b != null;

    int aDepth = a.depth();
    int bDepth = b.depth();
    int commonDepth = Math.min(aDepth, bDepth);
    while (a.depth() > commonDepth) {
      a = a.parentLayer();
    }
    while (b.depth() > commonDepth) {
      b = b.parentLayer();
    }

    if (a == b) {
      return
        aDepth < bDepth ? 1 :
        aDepth > bDepth ? -1 :
        0;
    }

    while (
      a.parentLayer() != null
      && b.parentLayer() != null
    ) {
      if (a.parentLayer() == b.parentLayer()) {
        CSSLayer parent = a.parentLayer();
        return Integer.compare(
          parent.childIndex(a),
          parent.childIndex(b));
      }

      a = a.parentLayer();
      b = b.parentLayer();
    }

    assert false : "Layers have no common ancestor!";
    return 0;
  }

  private static boolean isRoot(CSSLayer layer) {
    return
      layer == null
      || layer.parentLayer() == null;
  }

}
