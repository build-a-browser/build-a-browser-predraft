package net.buildabrowser.babbrowser.css.engine.matcher.util;

public class RefCounted<T> {
  
  private final T object;

  private int refCount = 0;

  private RefCounted(T obj) {
    this.object = obj;
  }

  public void incRefCount() {
    this.refCount++;
  }

  public void decRefCount() {
    this.refCount--;
  }

  public boolean isReferenced() {
    return this.refCount > 0;
  }

  public T object() {
    return this.object;
  }

  public static <T> RefCounted<T> create(T obj) {
    return new RefCounted<T>(obj);
  }

}
