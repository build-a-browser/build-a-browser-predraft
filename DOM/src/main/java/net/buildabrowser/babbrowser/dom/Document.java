package net.buildabrowser.babbrowser.dom;

import java.net.URI;

import net.buildabrowser.babbrowser.cssbase.cssom.DocumentOrShadowRoot;

public interface Document extends Node, DocumentOrShadowRoot {

  URI url();

}
