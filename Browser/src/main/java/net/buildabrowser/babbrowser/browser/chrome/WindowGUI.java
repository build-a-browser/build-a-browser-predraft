package net.buildabrowser.babbrowser.browser.chrome;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.buildabrowser.babbrowser.browser.uistate.Tab;
import net.buildabrowser.babbrowser.browser.uistate.Window;
import net.buildabrowser.babbrowser.browser.uistate.Window.WindowOptions;
import net.buildabrowser.babbrowser.browser.uistate.event.TabMutationEventListener;
import net.buildabrowser.babbrowser.browser.uistate.event.WindowMutationEventListener;
import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.network.URLUtil;
import net.buildabrowser.babbrowser.painter.core.CanvasCallbacks;
import net.buildabrowser.babbrowser.painter.core.ComponentPainter;
import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.renderer.GraphicalDocumentRenderer;
import net.buildabrowser.babbrowser.renderer.imp.NoOpGraphicalDocumentRenderer;

public class WindowGUI extends JFrame implements WindowMutationEventListener {

  private static final String NEW_TAB_PAGE = "https://buildabrowser.net/";

  private static final GraphicalDocumentRenderer NO_OP_RENDERER = new NoOpGraphicalDocumentRenderer();
  
  private final JTabbedPane tabbedPane;

  private final Window window;
  private final Component sharedRenderedContent;

  private WindowGUI(
    Window window,
    ComponentPainter<Component> painter
  ) {
    super("BuildABrowser Test Program");
    this.window = window;

    this.setLayout(new GridBagLayout());
    this.setSize(new Dimension(800, 500));

    this.tabbedPane = createTabPane();
    this.sharedRenderedContent = createSharedRenderedContent(painter);
    
    this.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        window.close();
      }
    });

    addNewTabButton();
    addMenu();
    window.addWindowMutationEventListener(this, true);
  }

	public void showWindow() {
    this.setVisible(true);
  }
  
  @Override
  public void onTabAdded(Window window, Tab tab) {
    TabGUI tabGUI = TabGUI.create(tab);
    tabbedPane.addTab(tab.getTitle(), tabGUI);

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

  private JTabbedPane createTabPane() {
    JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

    GridBagConstraints tabPaneConstraints = new GridBagConstraints();
    tabPaneConstraints.fill = GridBagConstraints.HORIZONTAL;
    tabPaneConstraints.weightx = 1;
    tabPaneConstraints.weighty = 0;
    tabPaneConstraints.gridx = 0;
    tabPaneConstraints.gridy = 0;
		this.add(tabbedPane, tabPaneConstraints);
    tabbedPane.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        if (tabbedPane.getSelectedComponent() instanceof TabGUI tabGUI) {
          tabGUI.activate(sharedRenderedContent);
          SwingUtilities.invokeLater(() -> {
            sharedRenderedContent.revalidate();
            sharedRenderedContent.repaint();
          });
        }
      }
    });

    return tabbedPane;
	}

  private Component createSharedRenderedContent(ComponentPainter<Component> painter) {
    Component panel = painter.createComponent(new CanvasCallbacks() {

      @Override
      public void layout(float width, float height) {
        GraphicalDocumentRenderer activeRenderer = activeRenderer();
        if (activeRenderer == null) return;
        // TODO: Make renderer accept float instead?
        activeRenderer.resize((int) Math.ceil(width), (int) Math.ceil(height));
      }

      @Override
      public void paint(PaintCanvas canvas) {
        GraphicalDocumentRenderer activeRenderer = activeRenderer();
        if (activeRenderer == null) return;
        activeRenderer.draw(canvas);
      }

      // TODO: Handle invalidation listener
      
    });

    RendererMouseInputAdapter inputHandler = new RendererMouseInputAdapter(() -> activeRenderer());
    panel.addMouseListener(inputHandler);
    panel.addMouseMotionListener(inputHandler);
    panel.addMouseWheelListener(inputHandler);
    
    GridBagConstraints renderedContentConstraints = new GridBagConstraints();
    renderedContentConstraints.fill = GridBagConstraints.BOTH;
    renderedContentConstraints.weightx = 1;
    renderedContentConstraints.weighty = 1;
    renderedContentConstraints.gridx = 0;
    renderedContentConstraints.gridy = 1;
    this.add(panel, renderedContentConstraints);

    return panel;
  }

  private GraphicalDocumentRenderer activeRenderer() {
    if (!(
      tabbedPane.getSelectedComponent() instanceof TabGUI tabGUI
    )) return NO_OP_RENDERER;

    return tabGUI.tab().getFrame().getRenderer();
  }

  public static WindowGUI create(
    Window window,
    ComponentPainter<Component> painter
  ) {
    JFrame.setDefaultLookAndFeelDecorated(true);
    return new WindowGUI(window, painter);
  }

}
