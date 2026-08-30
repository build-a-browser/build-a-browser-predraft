package net.buildabrowser.babbrowser.renderer.content.table.test;

import static net.buildabrowser.babbrowser.renderer.content.common.test.CommonBoxTestUtil.flowInlineBox;
import static net.buildabrowser.babbrowser.renderer.content.common.test.CommonBoxTestUtil.tableBlockBox;

import java.util.List;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue.InnerDisplayValue;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue.OuterDisplayValue;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.painter.core.FontMetrics;
import net.buildabrowser.babbrowser.painter.core.ResourceLoader;
import net.buildabrowser.babbrowser.renderer.box.Box;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.ElementBox.BoxLevel;
import net.buildabrowser.babbrowser.renderer.box.test.TestElementBox;
import net.buildabrowser.babbrowser.renderer.box.test.TestTextBox;
import net.buildabrowser.babbrowser.renderer.content.flow.FlowRootContent;
import net.buildabrowser.babbrowser.renderer.content.table.TableContent;
import net.buildabrowser.babbrowser.renderer.fragment.FragmentFactory;
import net.buildabrowser.babbrowser.renderer.fragment.table.TableBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.GlobalLayoutContext;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutContext;
import net.buildabrowser.babbrowser.renderer.layout.LayoutContextGenerator;
import net.buildabrowser.babbrowser.renderer.layout.Viewport;
import net.buildabrowser.babbrowser.renderer.paint.test.TestFontMetrics;
import net.buildabrowser.babbrowser.renderer.paint.test.TestResourceLoader;

public final class TableLayoutUtil {
  
  private TableLayoutUtil() {}

  public static TableBoxFragment doLayout(ElementBox parentBox) {
    return doLayoutConstrained(parentBox, LayoutConstraint.AUTO, LayoutConstraint.AUTO);
  }

  public static TableBoxFragment doLayoutSized(ElementBox parentBox, float width) {
    return doLayoutConstrained(parentBox, LayoutConstraint.of(width), LayoutConstraint.AUTO);
  }

  public static TableBoxFragment doLayoutSized(ElementBox parentBox, float width, float height) {
    return doLayoutConstrained(parentBox, LayoutConstraint.of(width), LayoutConstraint.of(height));
  }

  public static TableBoxFragment doLayoutConstrained(
    ElementBox parentBox,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    FontMetrics testMetrics = TestFontMetrics.create(10, 5);
    ResourceLoader resourceLoader = new TestResourceLoader(() -> testMetrics);
    Viewport viewport = new Viewport(0, 0, (int) widthConstraint.value(), (int) heightConstraint.value());
    FragmentFactory fragmentFactory = FragmentFactory.createDefault();
    LayoutContext layoutContext = new LayoutContext(
      new GlobalLayoutContext(
        resourceLoader, resourceLoader.fontLoader()::load,
        (m, s) -> m.stringWidth(s),
        viewport, null, null, null, fragmentFactory),
      () -> testMetrics, testMetrics);
    LayoutContextGenerator.generateLayoutContexts(parentBox, layoutContext);
    TableContent content = (TableContent) parentBox.content();

    content.fixupChildren(parentBox);
    TableBoxFragment dimensionFrag = (TableBoxFragment) parentBox.layout(widthConstraint, heightConstraint);
    return dimensionFrag;
  }

  public static ElementBox table(ElementBox... children) {
    return tableBlockBox(List.of(children));
  }

  public static ElementBox rowGroup(ElementBox... children) {
    ActiveStyles boxStyles = ActiveStyles.create();
    boxStyles.setProperty(CSSProperty.DISPLAY, DisplayValue.create(
      OuterDisplayValue.TABLE_ROW_GROUP,
      InnerDisplayValue.TABLE_ROW_GROUP));
    return flowInlineBox(boxStyles, List.of(children));
  }

  public static ElementBox row(ElementBox... children) {
    ActiveStyles boxStyles = ActiveStyles.create();
    boxStyles.setProperty(CSSProperty.DISPLAY, DisplayValue.create(
      OuterDisplayValue.TABLE_ROW,
      InnerDisplayValue.TABLE_ROW));
    return flowInlineBox(boxStyles, List.of(children));
  }

  public static ElementBox cell(String text) {
    ActiveStyles boxStyles = ActiveStyles.create();
    boxStyles.setProperty(CSSProperty.DISPLAY, DisplayValue.create(
      OuterDisplayValue.TABLE_CELL,
      InnerDisplayValue.FLOW));
    return flowInlineBox(boxStyles, List.of(
      new TestTextBox(text)));
  }

  public static ElementBox cellRS(int rowspan, String text) {
    Document dummyDocument = Document.create();
    HTMLElement htmlElement = HTMLElement.create("td", dummyDocument);
    htmlElement.addAttribute("rowspan", String.valueOf(rowspan));

    return createElementCell(text, htmlElement);
  }

  public static ElementBox cellCS(int colspan, String text) {
    Document dummyDocument = Document.create();
    HTMLElement htmlElement = HTMLElement.create("td", dummyDocument);
    htmlElement.addAttribute("colspan", String.valueOf(colspan));

    return createElementCell(text, htmlElement);
  }

  public static ElementBox cellRSCS(int rowspan, int colspan, String text) {
    Document dummyDocument = Document.create();
    HTMLElement htmlElement = HTMLElement.create("td", dummyDocument);
    htmlElement.addAttribute("rowspan", String.valueOf(rowspan));
    htmlElement.addAttribute("colspan", String.valueOf(colspan));

    return createElementCell(text, htmlElement);
  }

  private static ElementBox createElementCell(String text, HTMLElement htmlElement) {
    ActiveStyles boxStyles = ActiveStyles.create();
    boxStyles.setProperty(CSSProperty.DISPLAY, DisplayValue.create(
      OuterDisplayValue.TABLE_CELL,
      InnerDisplayValue.FLOW));
    List<Box> children = List.of(new TestTextBox(text));
    return new TestElementBox(
      box -> FlowRootContent.get(),
      BoxLevel.INLINE_LEVEL, boxStyles, children, htmlElement);
  }

}
