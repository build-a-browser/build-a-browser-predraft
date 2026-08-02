package net.buildabrowser.babbrowser.bindings.registrations;

import java.util.List;

public interface IDLInterfaceOrMixin extends IDLRegistration {
  
  List<IDLInterfaceMember> members();
  
}
