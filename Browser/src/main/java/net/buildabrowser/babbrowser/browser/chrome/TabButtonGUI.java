package net.buildabrowser.babbrowser.browser.chrome;

import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.buildabrowser.babbrowser.browser.uistate.Tab;

public class TabButtonGUI extends JPanel {
  
  private final JLabel titleLabel = new JLabel("Untitled Document");
  private final JButton closeButton = new JButton("x");

  public TabButtonGUI(Tab tab) {
    this.add(titleLabel);
    this.add(closeButton);

    closeButton.setMargin(new Insets(0, 0, 0, 0));
    closeButton.setPreferredSize(new Dimension(16, 16));
    closeButton.addActionListener(_ -> tab.close());
  }

}
