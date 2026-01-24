package net.buildabrowser.babbrowser.browser.chrome;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.io.IOException;
import java.net.URL;

import javax.swing.JPanel;

import net.buildabrowser.babbrowser.browser.uistate.Tab;
import net.buildabrowser.babbrowser.browser.uistate.event.TabMutationEventListener;

public class TabGUI extends JPanel implements TabMutationEventListener {
  
  @SuppressWarnings("unused")
  private final Tab tab;

  private TabGUI(Tab tab) {
    this.tab = tab;
    tab.addTabMutationEventListener(this, true);
    this.setLayout(new GridLayout());
  }

  @Override
  public void onNavigate(Tab tab, URL url) {
    try {
      this.removeAll();
      Component renderedContent = tab.getFrame().getCurrentRenderer().render();

      this.add(renderedContent);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
  }

  public static TabGUI create(Tab tab) {
    return new TabGUI(tab);
  }

}
