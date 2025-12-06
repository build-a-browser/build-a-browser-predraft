package net.buildabrowser.babbrowser.browser.render.box.imp;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JTextField;

import net.buildabrowser.babbrowser.browser.render.box.Box;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.dom.Text;

public class TextBox implements Box {
  
  private final Text text;

  public TextBox(Text text) {
    this.text = text;
  }

  @Override
  public JComponent render(ActiveStyles parentStyles) {
    String formattedText = text.data().trim().replaceAll("[ \n\t\r]+", " ");
    JTextField textArea = new JTextField(formattedText);
    textArea.setMaximumSize(new Dimension(
      (int) textArea.getMaximumSize().getWidth(),
      (int) textArea.getPreferredSize().getHeight()));
    textArea.setEditable(false);
    textArea.setBorder(null);
    textArea.setOpaque(false);
    textArea.setForeground(new Color(parentStyles.textColor(), true));

    return textArea;
  }
  
}
