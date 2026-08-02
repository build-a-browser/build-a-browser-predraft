package net.buildabrowser.babbrowser.bindings.binder;

public interface IDLTypedBinder<T, U> {
  
  T bind(U object);

}
