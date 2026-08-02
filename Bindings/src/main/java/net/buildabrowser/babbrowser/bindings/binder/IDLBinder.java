package net.buildabrowser.babbrowser.bindings.binder;

public interface IDLBinder<T> {
  
  T bind(Object o);

  <U> IDLTypedBinder<T, U> typedBinder(String interfaceName);

}
