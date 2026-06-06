package net.buildabrowser.babbrowser.renderer.imp;

import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.buildabrowser.babbrowser.cssbase.property.color.ColorValue;
import net.buildabrowser.babbrowser.cssbase.property.color.ColorValue.SRGBAColor;
import net.buildabrowser.babbrowser.htmlparser.HTMLParser;
import net.buildabrowser.babbrowser.cssbase.property.color.NamedColorParser;
import net.buildabrowser.babbrowser.renderer.RenderingEngine.ResourceResolver;
import net.buildabrowser.babbrowser.renderer.hintattr.LegacyBGColorAttributeResolver;

public final class RenderingEngineInit {
  
  private RenderingEngineInit() {}

  // TODO: Contains all the ugly static setters that I should refactor later

  public static void init(ResourceResolver resourceResolver) {
    initNamedColors(resourceResolver);
    HTMLParser.initialize(resourceResolver::resolve);
  }
  
  private static void initNamedColors(ResourceResolver resourceResolver) {
    Map<String, ColorValue> colorNames = loadNamedColors(resourceResolver);
    NamedColorParser.setNamedColors(colorNames);

    Map<String, Integer> colorNumNames = colorNames
      .entrySet().stream()
      .collect(Collectors.toMap(
        Map.Entry::getKey,
        e -> e.getValue().asSARGB()));
    LegacyBGColorAttributeResolver.setColorMap(colorNumNames);
  }

  private static Map<String, ColorValue> loadNamedColors(
    ResourceResolver resourceResolver
  ) {
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
