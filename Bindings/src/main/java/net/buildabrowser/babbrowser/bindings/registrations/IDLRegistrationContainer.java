package net.buildabrowser.babbrowser.bindings.registrations;

public interface IDLRegistrationContainer {
  
  void registerInterface(IDLInterface _interface);

  void registerPartialInterface(IDLInterface _interface);

  void registerMixin(IDLInterface mixin);

}
