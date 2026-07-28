package net.buildabrowser.babbrowser.browser;

import java.awt.Component;
import java.net.URI;
import java.util.List;
import java.util.function.Supplier;

import net.buildabrowser.babbrowser.browser.util.FileUtil;
import net.buildabrowser.babbrowser.cookies.CookieStore;
import net.buildabrowser.babbrowser.cookies.PublicSuffixList;
import net.buildabrowser.babbrowser.cookies.stores.InMemoryCookieStore;
import net.buildabrowser.babbrowser.cookies.stores.NoOpCookieStore;
import net.buildabrowser.babbrowser.painter.core.ComponentPainter;
import net.buildabrowser.babbrowser.painter.java2d.Java2DPainter;
import net.buildabrowser.babbrowser.painter.skija.SkijaAWTPainter;
import net.buildabrowser.cookies.stores.sqlite.JDBCUtil;
import net.buildabrowser.cookies.stores.sqlite.SQLiteCookieStore;
import net.buildabrowser.jflags.ArgumentCommand;
import net.buildabrowser.jflags.ArgumentParser;
import net.buildabrowser.jflags.DuplicateFlagStrategy;
import net.buildabrowser.jflags.Flag;
import net.buildabrowser.jflags.FlagComponent;
import net.buildabrowser.jflags.FlagResults;
import net.buildabrowser.jflags.types.ListFlagType;
import net.buildabrowser.jflags.types.OptionFlagType;
import net.buildabrowser.jflags.types.URIFlagType;

public record BrowserArguments(
  Supplier<ComponentPainter<Component>> painter,
  CookieStoreSupplier cookieStore,
  URI profilePath,
  List<URI> launchPaths
) {

  private static final String STARTUP_PAGE = "https://buildabrowser.net/";
  private static final String CONFIG_NAME_SHORT = "babbrowser";
  private static final String CONFIG_NAME_LONG = "BuildABrowser Browser";

  private static final Supplier<ComponentPainter<Component>> PAINTER_SKIJA
    = () -> new SkijaAWTPainter(false, false);
  private static final Supplier<ComponentPainter<Component>> PAINTER_SKIJA_SOFTWARE
    = () -> new SkijaAWTPainter(true, false);
  private static final Supplier<ComponentPainter<Component>> PAINTER_JAVA2D
    = () -> new Java2DPainter();

  private static final CookieStoreSupplier COOKIE_STORE_SQLITE
    = (uri, suffixList) -> new SQLiteCookieStore(
      JDBCUtil.jdbcURL(uri.resolve("profile.db")),
      suffixList);
  private static final CookieStoreSupplier COOKIE_STORE_IN_MEMORY
    = (_, suffixList) -> new InMemoryCookieStore(suffixList);
  private static final CookieStoreSupplier COOKIE_STORE_NO_OP
    = (_, suffixList) -> new NoOpCookieStore(suffixList);

  public static BrowserArguments parse(String[] args) {
    String osName = System.getProperty("os.name").toLowerCase();
    // TODO: Also need to check which native variants were bundled
    boolean isSupportedOS = osName.contains("win") || osName.contains("linux");
    String configName =
      osName.contains("linux")
      || osName.contains("unix")
      || osName.contains("bsd") ?
      CONFIG_NAME_SHORT : CONFIG_NAME_LONG;

    Flag<Supplier<ComponentPainter<Component>>> graphicsBackendFlag = Flag
      .<Supplier<ComponentPainter<Component>>>builder()
      .name("graphics-backend")
      .alias("gbe")
      .helpText("Select which graphics API to use")
      .flagType(OptionFlagType.<Supplier<ComponentPainter<Component>>>builder()
        .option("skija", PAINTER_SKIJA)
        .option("skija-nogpu", PAINTER_SKIJA_SOFTWARE)
        .option("java2d", PAINTER_JAVA2D)
        .build())
      .defaultValue(isSupportedOS ? PAINTER_SKIJA : PAINTER_JAVA2D)
      .build();
    
    Flag<CookieStoreSupplier> cookieStoreFlag = Flag.<CookieStoreSupplier>builder()
      .name("cookie-store")
      .alias("cs")
      .helpText("Select how to store cookies")
      .flagType(OptionFlagType.<CookieStoreSupplier>builder()
        .option("sqlite", COOKIE_STORE_SQLITE)
        .option("memory", COOKIE_STORE_IN_MEMORY)
        .option("disabled", COOKIE_STORE_NO_OP)
        .build())
      .defaultValue(isSupportedOS ? COOKIE_STORE_SQLITE : COOKIE_STORE_IN_MEMORY)
      .build();

    Flag<URI> profileURI = Flag.<URI>builder()
      .name("profile-path")
      .alias("pfp")
      .flagType(URIFlagType.relative())
      .defaultValue(FileUtil.appConfigDirectory(configName))
      .build();
    
    Flag<Void> helpFlag = Flag.<Void>builder()
      .name("help")
      .alias("h")
      .helpText("Display this screen")
      .duplicateStrategy(DuplicateFlagStrategy.last())
      .build();

    Flag<Void> versionFlag = Flag.<Void>builder()
      .name("version")
      .helpText("Display the browser version")
      .duplicateStrategy(DuplicateFlagStrategy.last())
      .build();

    FlagComponent<List<URI>> launchPathsFlag = FlagComponent
      .withDefault(
        ListFlagType.wrapZeroOrMore(URIFlagType.relative()),
        List.of(URI.create(STARTUP_PAGE)));

    ArgumentCommand<Void> defaultComment = ArgumentCommand.<Void>builder()
      .helpHeader("BuildABrowser Browser is an experimental browser with a custom rendering engine.")
      .flag(graphicsBackendFlag)
      .flag(cookieStoreFlag)
      .flag(profileURI)
      .flag(helpFlag)
      .flag(versionFlag)
      .loose(launchPathsFlag)
      .build();
    
    FlagResults<Void> results = ArgumentParser.create(defaultComment).parse(args);

    if (results.present(versionFlag)) {
      System.out.println(BrowserVersion.asVersionString());
    }

    boolean failureOrHelpShown = results
      .printFailureOrHelp(List.of(helpFlag), System.out, true, false);
    if (failureOrHelpShown) return null;

    if (results.present(versionFlag)) {
      return null;
    }

    return new BrowserArguments(
      results.value(graphicsBackendFlag).get(),
      results.value(cookieStoreFlag).get(),
      results.value(profileURI).get(),
      results.value(launchPathsFlag).get()
    );
  }

  public static interface CookieStoreSupplier {

    CookieStore get(URI profilePath, PublicSuffixList suffixList);

  }
  
}
