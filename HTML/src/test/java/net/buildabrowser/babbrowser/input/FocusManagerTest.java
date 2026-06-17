package net.buildabrowser.babbrowser.input;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.html.input.FocusManager;
import net.buildabrowser.babbrowser.html.input.FocusOptions;

public class FocusManagerTest {
  
  @Test
  @DisplayName("Can focus next element when there is no existing focus")
  public void canFocusNextElementWhenThereIsNoExistingFocus() {
    Document document = Document.create();
    HTMLElement firstElement = HTMLElement.create("a", document);
    HTMLElement secondElement = HTMLElement.create("input", document);
    document.appendChild(firstElement);
    document.appendChild(secondElement);
    
    FocusManager focusManager = FocusManager.create(document);
    focusManager.focusNext(FocusOptions.createDefault());
    Assertions.assertEquals(firstElement, focusManager.focused());
  }

  @Test
  @DisplayName("Can focus previous element when there is no existing focus")
  public void canFocusPreviousElementWhenThereIsNoExistingFocus() {
    Document document = Document.create();
    HTMLElement firstElement = HTMLElement.create("a", document);
    HTMLElement secondElement = HTMLElement.create("input", document);
    document.appendChild(firstElement);
    document.appendChild(secondElement);
    
    FocusManager focusManager = FocusManager.create(document);
    focusManager.focusPrevious(FocusOptions.createDefault());
    Assertions.assertEquals(secondElement, focusManager.focused());
  }

  @Test
  @DisplayName("Can advance and loop focus")
  public void canAdvanceAndLoopFocus() {
    Document document = Document.create();
    HTMLElement firstElement = HTMLElement.create("a", document);
    HTMLElement secondElement = HTMLElement.create("ignored", document);
    HTMLElement thirdElement = HTMLElement.create("input", document);
    document.appendChild(firstElement);
    document.appendChild(secondElement);
    document.appendChild(thirdElement);
    
    FocusManager focusManager = FocusManager.create(document);
    focusManager.focusNext(FocusOptions.createDefault());
    Assertions.assertEquals(firstElement, focusManager.focused());
    focusManager.focusNext(FocusOptions.createDefault());
    Assertions.assertEquals(thirdElement, focusManager.focused());
    focusManager.focusNext(FocusOptions.createDefault());
    Assertions.assertEquals(firstElement, focusManager.focused());
    focusManager.focusPrevious(FocusOptions.createDefault());
    Assertions.assertEquals(thirdElement, focusManager.focused());
  }

  @Test
  @DisplayName("Can focus in a custom order")
  public void canFocusInACustomOrder() {
    Document document = Document.create();
    HTMLElement firstElement = HTMLElement.create("a", document);
    firstElement.addAttribute("tabindex", "2");
    HTMLElement secondElement = HTMLElement.create("ignored", document);
    HTMLElement thirdElement = HTMLElement.create("notignored", document);
    thirdElement.addAttribute("tabindex", "1");
    HTMLElement fourthElement = HTMLElement.create("input", document);
    fourthElement.addAttribute("tabindex", "0");
    HTMLElement fifthElement = HTMLElement.create("a", document);
    fourthElement.addAttribute("tabindex", "-1");
    document.appendChild(firstElement);
    document.appendChild(secondElement);
    document.appendChild(thirdElement);
    document.appendChild(fourthElement);
    document.appendChild(fifthElement);
    
    FocusManager focusManager = FocusManager.create(document);
    focusManager.focusNext(FocusOptions.createDefault());
    Assertions.assertEquals(thirdElement, focusManager.focused());
    focusManager.focusNext(FocusOptions.createDefault());
    Assertions.assertEquals(firstElement, focusManager.focused());
    focusManager.focusNext(FocusOptions.createDefault());
    Assertions.assertEquals(fifthElement, focusManager.focused());
    focusManager.focusNext(FocusOptions.createDefault());
    Assertions.assertEquals(thirdElement, focusManager.focused());
    focusManager.focusPrevious(FocusOptions.createDefault());
    Assertions.assertEquals(fifthElement, focusManager.focused());
    focusManager.focusPrevious(FocusOptions.createDefault());
    Assertions.assertEquals(firstElement, focusManager.focused());
  }

  @Test
  @DisplayName("Can advance and loop focus with nested elements")
  public void canAdvanceAndLoopFocusWithNestedElements() {
    Document document = Document.create();
    HTMLElement firstElement = HTMLElement.create("a", document);
    HTMLElement secondElement = HTMLElement.create("ignored", document);
    HTMLElement thirdElement = HTMLElement.create("textarea", document);
    HTMLElement fourthElement = HTMLElement.create("input", document);
    document.appendChild(firstElement);
    document.appendChild(secondElement);
    secondElement.appendChild(thirdElement);
    document.appendChild(fourthElement);
    
    FocusManager focusManager = FocusManager.create(document);
    focusManager.focusNext(FocusOptions.createDefault());
    Assertions.assertEquals(firstElement, focusManager.focused());
    focusManager.focusNext(FocusOptions.createDefault());
    Assertions.assertEquals(thirdElement, focusManager.focused());
    focusManager.focusNext(FocusOptions.createDefault());
    Assertions.assertEquals(fourthElement, focusManager.focused());
    focusManager.focusPrevious(FocusOptions.createDefault());
    Assertions.assertEquals(thirdElement, focusManager.focused());
  }

}
