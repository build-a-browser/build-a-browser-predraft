package net.buildabrowser.babbrowser.html.navigation;

public record TargetSnapshotParams() {
  
  public static TargetSnapshotParams snapshot(Navigable targetNavigable) {
    // TODO: Capture sandboxing info
    return new TargetSnapshotParams();
  }

}
