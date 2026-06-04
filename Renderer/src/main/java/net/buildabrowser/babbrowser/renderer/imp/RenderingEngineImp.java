package net.buildabrowser.babbrowser.renderer.imp;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.cssbase.cssom.StyleSheetList;
import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStreamSource;
import net.buildabrowser.babbrowser.cssbase.property.color.ColorValue;
import net.buildabrowser.babbrowser.cssbase.property.color.ColorValue.SRGBAColor;
import net.buildabrowser.babbrowser.cssbase.property.color.NamedColorParser;
import net.buildabrowser.babbrowser.fetch.FetchEngine;
import net.buildabrowser.babbrowser.html.events.WindowEventLoop;
import net.buildabrowser.babbrowser.html.navigation.DocumentRenderer.DocumentRendererEventListener;
import net.buildabrowser.babbrowser.html.navigation.Navigable;
import net.buildabrowser.babbrowser.html.navigation.util.TraversableUtil;
import net.buildabrowser.babbrowser.html.scripting.Window;
import net.buildabrowser.babbrowser.painter.core.Painter;
import net.buildabrowser.babbrowser.renderer.RenderingEngine;
import net.buildabrowser.babbrowser.renderer.context.resolver.LegacyBGColorAttributeResolver;
import net.buildabrowser.babbrowser.renderer.loader.DocumentLoaderRegistry;
import net.buildabrowser.babbrowser.renderer.uistate.Frame;

public class RenderingEngineImp implements RenderingEngine {

  private final FetchEngine fetchEngine;
  private final Supplier<ExecutorService> threadGroupSupplier;
  private final Painter painter;
  private final DocumentLoaderRegistry documentLoaderRegistry;
  private final ResourceResolver resourceResolver;

  public RenderingEngineImp(
    FetchEngine fetchEngine,
    Supplier<ExecutorService> threadGroupSupplier,
    Painter painter,
    DocumentLoaderRegistry documentLoaderRegistry,
    ResourceResolver resourceResolver
  ) {
    this.fetchEngine = fetchEngine;
    this.threadGroupSupplier = threadGroupSupplier;
    this.painter = painter;
    this.documentLoaderRegistry = documentLoaderRegistry;
    this.resourceResolver = resourceResolver;
    initNamedColors();
  }

  @Override
  public Frame createFrame() {
    return Frame.create(this);
  }

  @Override
  public NavigableRendererPair createNavigable(
    DocumentRendererEventListener eventListener
  ) {
    Navigable navigable = TraversableUtil.createNewTopLevelTraversable(
      new UANavigableOptionsImp(
        fetchEngine, threadGroupSupplier, this::loadUAStyleSheets,
        documentLoaderRegistry, painter, eventListener));

    // TODO: Where does this code actually go?
    Window window = navigable.activeDocument().browsingContext().activeWindow();
    WindowEventLoop eventLoop = window.agent().eventLoop();
    eventLoop.runInParallel(() -> eventLoop.start());
    eventLoop.addNavigable(navigable);
    // TODO: Shutdown

    return new NavigableRendererPair(navigable, new DelegatingGraphicalDocumentRenderer(navigable));
  }

  private StyleSheetList loadUAStyleSheets() {
    try (
      Reader reader = new InputStreamReader(
        resourceResolver.resolve("ua/ua.css"))
    ) {
      CSSTokenStreamSource source = new CSSTokenStreamSource(
        CommonUtil.rethrow(() -> new URI("about:blank")));
      return StyleSheetList.createFromReader(source, reader);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void initNamedColors() {
    Map<String, ColorValue> colorNames = loadNamedColors();
    NamedColorParser.setNamedColors(colorNames);

    Map<String, Integer> colorNumNames = colorNames
      .entrySet().stream()
      .collect(Collectors.toMap(
        Map.Entry::getKey,
        e -> e.getValue().asSARGB()));
    LegacyBGColorAttributeResolver.setColorMap(colorNumNames);
  }

  private Map<String, ColorValue> loadNamedColors() {
    JsonObject refObj = JsonParser.parseReader(new InputStreamReader(
      resourceResolver.resolve("ua/colors.json")))
      .getAsJsonObject();
    Map<String, ColorValue> refMap = new HashMap<>();
    for (Entry<String, JsonElement> entry: refObj.entrySet()) {
      if (entry.getKey().startsWith("_")) continue;
      JsonArray arr = entry.getValue().getAsJsonArray();
      refMap.put(
        entry.getKey(),
        SRGBAColor.create(
          arr.get(0).getAsInt(),
          arr.get(1).getAsInt(),
          arr.get(2).getAsInt(),
          arr.get(3).getAsInt()));
    }

    return Collections.unmodifiableMap(refMap);
  }
  
}
