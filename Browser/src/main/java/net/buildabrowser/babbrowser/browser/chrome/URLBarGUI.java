package net.buildabrowser.babbrowser.browser.chrome;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.buildabrowser.babbrowser.browser.uistate.Tab;
import net.buildabrowser.babbrowser.browser.uistate.event.TabMutationEventListener;
import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.network.URLUtil;

public class URLBarGUI extends JPanel implements TabMutationEventListener {

  private static final String SEARCH_QUERY = "https://html.duckduckgo.com/html/?q=%s";

  private final JButton backButton = new JButton("<-");
  private final JButton reloadButton = new JButton("o");
  private final JButton forwardButton = new JButton("->");
  private final JTextField urlField = new JTextField();
  private final JButton navButton = new JButton("GO");

  // Sometimes the URLBarGUI is held in the clipboard after the tab is closed
  // which leads to tab not being garbage collected
  private final WeakReference<Tab> tab;

  private URLBarGUI(Tab tab) {
    this.tab = new WeakReference<>(tab);
    this.setLayout(new GridBagLayout());

    addButton(backButton, () -> this.tab.get().getFrame().back());
    addButton(reloadButton, () -> this.tab.get().getFrame().reload());
    addButton(forwardButton, () -> this.tab.get().getFrame().forward());
    addURLField();
    addButton(navButton, this::navigateToURL);
    tab.addTabMutationEventListener(this, true);
  }

  @Override
  public void onNavigate(Tab tab, URI url) {
    urlField.setText(url.toString());
  }

  private void addURLField() {
    GridBagConstraints urlFieldConstraints = new GridBagConstraints();
    urlFieldConstraints.fill = GridBagConstraints.BOTH;
    urlFieldConstraints.weightx = 1;
    urlFieldConstraints.weighty = 1;
    this.add(urlField, urlFieldConstraints);

    urlField.addActionListener(_ -> navigateToURL());
  }

  private void addButton(JButton button, Runnable action) {
    GridBagConstraints buttonConstraints = new GridBagConstraints();
    buttonConstraints.fill = GridBagConstraints.VERTICAL;
    buttonConstraints.weightx = 0;
    buttonConstraints.weighty = 1;
    this.add(button, buttonConstraints);

    button.addActionListener(_ -> action.run());
  }

  private void navigateToURL() {
    String urlText = urlField.getText();
    if (urlText == null) return;
    URI uri = CommonUtil.tryOrNull(() -> URLUtil.createURL(
      // Some websites don't support https, so use http and hope we get redirected
      urlText.contains(":") ? urlText : "http://" + urlText));
    
    boolean couldBeDataURL =
      urlText.startsWith("data:")
      && urlText.indexOf('/') < urlText.indexOf(',')
      && urlText.indexOf('/') != -1;
    if (
      uri == null
      || !(urlText.contains(".") || couldBeDataURL)
      || urlText.contains(" ")
    ) {
      String searchQuery = URLEncoder.encode(urlText, StandardCharsets.UTF_8);
      String searchURL = String.format(SEARCH_QUERY, searchQuery);
      uri = CommonUtil.rethrow(() -> URLUtil.createURL(searchURL));
    }

    tab.get().navigate(uri);
  }

  public static URLBarGUI create(Tab tab) {
    return new URLBarGUI(tab);
  }

}
