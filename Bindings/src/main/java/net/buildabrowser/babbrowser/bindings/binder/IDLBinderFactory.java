package net.buildabrowser.babbrowser.bindings.binder;

import java.util.List;

public interface IDLBinderFactory<T> {
  
  IDLBinder<T> createByScanning(
    List<IDLDefinitionScanner> definitionScanners,
    List<IDLBindingScanner> bindingScanners
  );

}
