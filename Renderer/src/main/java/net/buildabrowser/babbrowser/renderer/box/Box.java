package net.buildabrowser.babbrowser.renderer.box;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.debugger.core.DebugBox;

public sealed interface Box extends IntrusiveList<Box>, DebugBox
  permits ElementBox, TextBox, DocumentBox {

}
