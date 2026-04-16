package net.buildabrowser.babbrowser.browser.chrome;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import net.buildabrowser.babbrowser.browser.uistate.Tab;
import net.buildabrowser.babbrowser.browser.uistate.event.TabMutationEventListener;
import net.buildabrowser.babbrowser.common.util.CommonUtil;

public class TabGUI extends JPanel implements TabMutationEventListener {
  
  private final Tab tab;
  private final TabButtonGUI tabButtonGUI;
  private final URLBarGUI urlBarGUI;

  private TabGUI(Tab tab) {
    this.tab = tab;
    this.tabButtonGUI = new TabButtonGUI(tab);
    this.urlBarGUI = URLBarGUI.create(tab);

    tab.addTabMutationEventListener(this, true);
    this.setLayout(new GridBagLayout());

    addURLBar();
    addRenderedContent(tab);
  }

  public Tab tab() {
    return this.tab;
  }

  public TabButtonGUI tabButtonGUI() {
    return this.tabButtonGUI;
  }

  private void addURLBar() {
    GridBagConstraints urlBarConstraints = new GridBagConstraints();
    urlBarConstraints.fill = GridBagConstraints.HORIZONTAL;
    urlBarConstraints.weightx = 1;
    urlBarConstraints.weighty = 0;
    urlBarConstraints.gridx = 0;
    urlBarConstraints.gridy = 0;
    this.add(urlBarGUI, urlBarConstraints);
  }

  private void addRenderedContent(Tab tab) {
    Component renderedContent = CommonUtil.rethrow(() -> tab.getFrame().getRenderer().render());
    GridBagConstraints renderedContentConstraints = new GridBagConstraints();
    renderedContentConstraints.fill = GridBagConstraints.BOTH;
    renderedContentConstraints.weightx = 1;
    renderedContentConstraints.weighty = 1;
    renderedContentConstraints.gridx = 0;
    renderedContentConstraints.gridy = 1;
    this.add(renderedContent, renderedContentConstraints);
  }
  
  public static TabGUI create(Tab tab) {
    return new TabGUI(tab);
  }

}
