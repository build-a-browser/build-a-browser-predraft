package net.buildabrowser.babbrowser.render.box;

import java.util.ListIterator;

public interface ElementBoxIterator extends Iterable<Box>, ListIterator<Box>, Cloneable {
  
  // TODO: Mostly exists for operations that must backtrack (since this is singly-linked)
  // Probably should find a more reliable method
  ElementBoxIterator clone();

}
