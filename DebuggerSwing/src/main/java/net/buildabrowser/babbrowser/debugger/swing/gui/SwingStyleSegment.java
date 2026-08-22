package net.buildabrowser.babbrowser.debugger.swing.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class SwingStyleSegment extends JPanel {

  private final JLabel headerLabel;
  private final JPanel contentPane;
  private final Map<String, JLabel> valueLabels = new LinkedHashMap<>();

  private boolean isExpanded;
  private String baseTitle;

  public SwingStyleSegment(String title, boolean expandedInitially) {
    this.baseTitle = title;
    this.isExpanded = expandedInitially;

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setAlignmentX(Component.LEFT_ALIGNMENT);
    setOpaque(false);

    this.headerLabel = new JLabel();
    headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD));

    JPanel headerPanel = getHeaderPanel();
    this.contentPane = createContentPane();

    add(headerPanel);
    add(contentPane);
    updateHeader();
  }

  public void setTitle(String title) {
    this.baseTitle = title;
    updateHeader();
  }

  public void toggleExpanded() {
    isExpanded = !isExpanded;
    contentPane.setVisible(isExpanded);
    updateHeader();
    revalidate();
    repaint();
  }

  public void updateContent(Map<String, String> data) {
    removeUnusedEntries(data);

    for (Map.Entry<String, String> entry : data.entrySet()) {
      String key = entry.getKey();
      String val = entry.getValue();

      addOrUpdateDeclaration(key, val);
    }

    updateHeader();
  }

  @Override
  public Dimension getMaximumSize() {
    return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
  }

  private void removeUnusedEntries(Map<String, String> data) {
    valueLabels.keySet().removeIf(key -> {
      if (data.containsKey(key)) return false;

      for (Component c : contentPane.getComponents()) {
        if (c.getName() != null && c.getName().equals(key)) {
          contentPane.remove(c);
          break;
        }
      }
      return true;
    });
  }

  private void addOrUpdateDeclaration(String key, String val) {
    if (valueLabels.containsKey(key)) {
      JLabel valueLabel = valueLabels.get(key);
      if (!valueLabel.getText().equals(val)) {
        valueLabel.setText(val);
      }
    } else {
      JPanel row = new JPanel(new BorderLayout(8, 0));
      row.setName(key);
      row.setOpaque(false);
      row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

      JLabel keyLabel = new JLabel(key + ":");
      keyLabel.setForeground(UIManager.getColor("Label.disabledForeground"));
      keyLabel.setFont(keyLabel.getFont().deriveFont(Font.PLAIN, 12f));

      JLabel valLabel = new JLabel(val);
      valLabel.setFont(valLabel.getFont().deriveFont(Font.PLAIN, 12f));

      row.add(keyLabel, BorderLayout.WEST);
      row.add(valLabel, BorderLayout.CENTER);

      valueLabels.put(key, valLabel);
      contentPane.add(row);
    }
  }

  private JPanel getHeaderPanel() {
    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    headerPanel.setBorder(new EmptyBorder(6, 10, 6, 10));
    headerPanel.setBackground(UIManager.getColor("Panel.background"));
    headerPanel.add(headerLabel, BorderLayout.CENTER);

    headerPanel.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        toggleExpanded();
      }
    });
    return headerPanel;
  }

  private JPanel createContentPane() {
    JPanel contentPane = new JPanel();
    contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
    contentPane.setBorder(new EmptyBorder(0, 20, 8, 10));
    contentPane.setOpaque(false);
    contentPane.setVisible(isExpanded);
    return contentPane;
  }

  private void updateHeader() {
    String arrow = isExpanded ? " \u25BC " : " \u25B6 ";
    headerLabel.setText(arrow + baseTitle + " (" + valueLabels.size() + ")");
  }
  
}