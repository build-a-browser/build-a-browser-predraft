package net.buildabrowser.cookies.stores.sqlite;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.buildabrowser.babbrowser.cookies.exception.CookieStoreException;

public final class JDBCUtil {

  private static final String EXPECTED_EXACTLY_ONE_RESULT = "Expected exactly one result!";
  
  private JDBCUtil() {}

  public static <T> Optional<T> queryMaybe(
    String connectionURL,
    String query,
    RowMapper<T> rowMapper,
    Object... params
  ) throws CookieStoreException {
    try (
      Connection connection = DriverManager.getConnection(connectionURL);
      PreparedStatement statement = connection.prepareStatement(query);
    ) {
      fillStatementParams(statement, params);
      ResultSet resultSet = statement.executeQuery();
      if (!resultSet.next()) {
        return Optional.empty();
      }
      T result = rowMapper.map(resultSet);
      if (resultSet.next()) {
        throw new CookieStoreException(EXPECTED_EXACTLY_ONE_RESULT);
      }
      
      return Optional.of(result);
    } catch (SQLException e) {
      throw new CookieStoreException(e);
    }
  }
  
  public static <T> T queryOne(
    String connectionURL,
    String query,
    RowMapper<T> rowMapper,
    Object... params
  ) throws CookieStoreException {
    Optional<T> result = queryMaybe(connectionURL, query, rowMapper, params);
    if (result.isEmpty()) {
      throw new CookieStoreException(EXPECTED_EXACTLY_ONE_RESULT);
    }

    return result.get();
  }

  public static <T> List<T> queryMany(
    String connectionURL,
    String query,
    RowMapper<T> rowMapper,
    Object... params
  ) throws CookieStoreException {
    try (
      Connection connection = DriverManager.getConnection(connectionURL);
      PreparedStatement statement = connection.prepareStatement(query);
    ) {
      fillStatementParams(statement, params);
      ResultSet resultSet = statement.executeQuery();
      List<T> results = new ArrayList<>();
      while (resultSet.next()) {
        results.add(rowMapper.map(resultSet));
      }

      return List.copyOf(results);
    } catch (SQLException e) {
      throw new CookieStoreException(e);
    }
  }

  public static void execute(
    String connectionURL,
    String query,
    Object... params
  ) throws CookieStoreException {
    try (
      Connection connection = DriverManager.getConnection(connectionURL);
      PreparedStatement statement = connection.prepareStatement(query);
    ) {
      fillStatementParams(statement, params);
      statement.execute();
    } catch (SQLException e) {
      throw new CookieStoreException(e);
    }
  }

  public static String jdbcURL(URI uri) {
    return "jdbc:sqlite:" + uri.getSchemeSpecificPart();
  }

  private static void fillStatementParams(
    PreparedStatement statement,
    Object[] params
  ) throws SQLException {
    for (int i = 0; i < params.length; i++) {
      switch (params[i]) {
        case String str -> statement.setString(i + 1, str);
        case Integer integer -> statement.setInt(i + 1, integer);
        case Boolean bool -> statement.setBoolean(i + 1, bool);
        case ZonedDateTime dateTime -> statement.setObject(
          i + 1, dateTime.toOffsetDateTime());
        default -> throw new IllegalArgumentException(
          "Unrecognized argument: " + params[i]);
      }
    }
  }

  public static interface RowMapper<T> {
    
    T map(ResultSet resultSet) throws SQLException;

  }

}
