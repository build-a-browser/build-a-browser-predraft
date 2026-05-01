package net.buildabrowser.babbrowser.css.engine.matcher.imp;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.function.Consumer;

import com.zaxxer.sparsebits.SparseBitSet;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementRootSet;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementSetListener;
import net.buildabrowser.babbrowser.dom.Element;

public class ElementRootSetImp extends ElementSetImp implements ElementRootSet {

  private static final int NUM_INITIAL_ELEMENTS = 128;

  // TODO: Periodically remove old entries
  private final LinkedList<WeakReference<ElementSet>> childSets = new LinkedList<>();

  private int nextId = 0;
  private int broadcastedSize = NUM_INITIAL_ELEMENTS;
  private ElementSetListener listeners;

  public ElementRootSetImp(Consumer<Element> changeListener) {
    super(
      null,
      new ArrayList<>(NUM_INITIAL_ELEMENTS),
      new SparseBitSet(NUM_INITIAL_ELEMENTS));
  }

  @Override
  public ElementSet createChild() {
    ElementSet set = new ElementSetImp(this, elementList, rawSet.size());
    childSets.add(new WeakReference<>(set));
    return set;
  }

  @Override
  public ElementSet createTemporaryChild() {
    return new ElementSetImp(this, elementList, rawSet.size());
  }

  @Override
  public void addListener(ElementSetListener listener) {
    this.listeners = IntrusiveList.add(listeners, listener);
    listener.onResize(broadcastedSize);
  }

  @Override
  public boolean add(Element element) {
    resizeChildrenIfNeeded();
    assignElementId(element);
    
    boolean oldValue = rawSet.get(element.getId());
    rawSet.set(element.getId(), true);

    IntrusiveList.forEach(listeners, l -> l.onElementAdded(element));

    return !oldValue;
  }

  @Override
  public ElementRootSet root() {
    return this;
  }

  private void resizeChildrenIfNeeded() {
    // Don't need to resize children with SparseBitSet
    // but do need to make sure listeners are resized
    if (nextId >= broadcastedSize) {
      this.broadcastedSize = (int) (this.broadcastedSize * 1.5f);
      IntrusiveList.forEach(listeners, l -> l.onResize(broadcastedSize));
    }
  }

  private void assignElementId(Element element) {
    if (nextId == elementList.size()) {
      elementList.add(element);
    } else {
      elementList.set(nextId, element);
    }
    element.setId(nextId++);
  }

  // TODO: Add option to compact, in case many nodes are removed

}
