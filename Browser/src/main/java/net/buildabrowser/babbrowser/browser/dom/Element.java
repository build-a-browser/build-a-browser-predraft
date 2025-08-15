package net.buildabrowser.babbrowser.browser.dom;

import java.util.List;

public record Element(String name, List<Node> children) implements Node {
  
}
