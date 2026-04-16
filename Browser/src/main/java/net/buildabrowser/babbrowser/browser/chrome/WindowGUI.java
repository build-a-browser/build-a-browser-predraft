package net.buildabrowser.babbrowser.browser.chrome;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import net.buildabrowser.babbrowser.browser.uistate.Tab;
import net.buildabrowser.babbrowser.browser.uistate.Window;
import net.buildabrowser.babbrowser.browser.uistate.Window.WindowOptions;
import net.buildabrowser.babbrowser.browser.uistate.event.TabMutationEventListener;
import net.buildabrowser.babbrowser.browser.uistate.event.WindowMutationEventListener;
import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.network.URLUtil;

public class WindowGUI extends JFrame implements WindowMutationEventListener {

  private static final String NEW_TAB_PAGE = "https://buildabrowser.net/";
  
  private final JTabbedPane tabbedPane = new JTabbedPane();

  private final Window window;

  private WindowGUI(Window window) {
    super("BuildABrowser Test Program");
    this.window = window;

    this.setSize(new Dimension(800, 500));
    this.setMaximumSize(new Dimension(800, 500));
    addNewTabButton();
    addMenu();
    window.addWindowMutationEventListener(this, true);
    
    this.add(tabbedPane);
    this.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        window.close();
      }
    });
  }

  public void showWindow() {
    this.setVisible(true);
  }
  
  @Override
  public void onTabAdded(Window window, Tab tab) {
    TabGUI tabGUI = TabGUI.create(tab);
    tabbedPane.addTab(tab.getName(), tabGUI);

    int tabIndex = tabbedPane.indexOfComponent(tabGUI);
    assert tabIndex != -1;
    tabbedPane.setTabComponentAt(tabIndex, tabGUI.tabButtonGUI());
    tabbedPane.setSelectedIndex(tabIndex);

    tab.addTabMutationEventListener(new TabMutationEventListener() {
      @Override
      public void onClose(Tab tab) {
        SwingUtilities.invokeLater(() -> {
          int tabIndex = tabbedPane.indexOfComponent(tabGUI);
          assert tabIndex != -1;
          tabbedPane.remove(tabIndex);
        });
      }
    }, false);
  }

  @Override
  public void onClose(Window window) {
    dispose();
  }

  private void addMenu() {
    JMenuBar menuBar = new JMenuBar();
    JMenu menu = new JMenu("File");
    menuBar.add(menu);

    JMenuItem newTabItem = new JMenuItem("New Tab");
    newTabItem.addActionListener(_ -> openTab());
    menu.add(newTabItem);

    JMenuItem closeTabItem = new JMenuItem("Close Tab");
    closeTabItem.addActionListener(_ -> closeTab());
    menu.add(closeTabItem);

    menu.add(new JSeparator());

    JMenuItem newWindowItem = new JMenuItem("New Window");
    newWindowItem.addActionListener(_ -> window.relatedWindowSet().openWindow(new WindowOptions(false)));
    menu.add(newWindowItem);

    JMenuItem closeWindowItem = new JMenuItem("Close Window");
    closeWindowItem.addActionListener(_ -> window.close());
    menu.add(closeWindowItem);

    JMenuItem exitItem = new JMenuItem("Exit");
    exitItem.addActionListener(_ -> window.relatedWindowSet().close());
    menu.add(exitItem);

    setJMenuBar(menuBar);
  }

  private void addNewTabButton() {
    JPanel dummyPanel = new JPanel();
    tabbedPane.addTab("+", dummyPanel);

    JButton newTabButton = new JButton("+");
    newTabButton.addActionListener(_ -> openTab());

    int tabIndex = tabbedPane.indexOfComponent(dummyPanel);
    assert tabIndex != -1;
    tabbedPane.setTabComponentAt(tabIndex, newTabButton);
  }

  private void openTab() {
    Tab tab = window.openTab();
    tab.navigate(CommonUtil.rethrow(() -> URLUtil.createURL(NEW_TAB_PAGE)));
  }

  private void closeTab() {
    int tabIndex = tabbedPane.getSelectedIndex();
    Component tabComponent = tabbedPane.getComponentAt(tabIndex);
    if (!(tabComponent instanceof TabGUI tabGUI)) return;
    tabGUI.tab().close();
  }

  public static WindowGUI create(Window window) {
    JFrame.setDefaultLookAndFeelDecorated(true);
    return new WindowGUI(window);
  }

}
