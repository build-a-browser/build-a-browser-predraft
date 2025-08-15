package net.buildabrowser.babbrowser.browser.dom;

import java.util.List;

public record Document(List<Node> children) implements Node {
  
}
