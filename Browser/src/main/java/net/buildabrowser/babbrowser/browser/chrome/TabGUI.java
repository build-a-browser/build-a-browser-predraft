package net.buildabrowser.babbrowser.browser.chrome;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.buildabrowser.babbrowser.browser.uistate.Tab;
import net.buildabrowser.babbrowser.browser.uistate.event.TabMutationEventListener;

public class TabGUI extends JPanel implements TabMutationEventListener {
  
  private final Tab tab;
  private final TabButtonGUI tabButtonGUI;
  private final URLBarGUI urlBarGUI;

  private boolean addedCallbacks = false;

  private TabGUI(Tab tab) {
    this.tab = tab;
    this.tabButtonGUI = new TabButtonGUI(tab);
    this.urlBarGUI = URLBarGUI.create(tab);

    tab.addTabMutationEventListener(this, true);
    this.setLayout(new BorderLayout());

    addURLBar();
  }

  public Tab tab() {
    return this.tab;
  }

  public TabButtonGUI tabButtonGUI() {
    return this.tabButtonGUI;
  }

  public void activate(Component renderedContent) {
    // TODO
    if (!addedCallbacks) {
      addedCallbacks = true;
      tab.getFrame().getRenderer().addRepaintListener(
        () -> SwingUtilities.invokeLater(() -> {
          renderedContent.revalidate();
          renderedContent.repaint();
        }));
    }
  }

  private void addURLBar() {
    this.add(urlBarGUI, BorderLayout.CENTER);
  }
  
  public static TabGUI create(Tab tab) {
    return new TabGUI(tab);
  }

}
