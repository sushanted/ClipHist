package sr.cliphist.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ClipDAO {

  // TODO create indexes for required columns

  public static void main(final String[] args) {
    // new ClipDAO().insertClip("tester");
    new ClipDAO().getRecentClips("admin",0, 10).forEach(System.out::println);
  }

  Connection con = ConnectionManager.INSTANCE.getConnection();

  public void insertClip(String clip) {
    // check if clip already exists
    // yes : modify the clip count/time etc
    // no : insert the clip

    clip = clip.replace("'", "''");
    clip = clip.replace("\\", "\\\\");

    try {
      final Statement stmt = this.con.createStatement();

      // TODO better have a prepared statement
      final int count = stmt
          .executeUpdate(String.format("update clips set frequency = frequency + 1,lastAccessed = %d where clip = '%s'",
              System.currentTimeMillis(), clip));

      if (count == 0) {
        stmt.execute(String.format("insert into clips values('%s',%d,%d)", clip, 1, System.currentTimeMillis()));
      }

      stmt.close();

    } catch (final Exception e) {
      e.printStackTrace();
      System.out.println("Reconnecting to db...");
      this.con = ConnectionManager.INSTANCE.getConnection();
    }
  }

  public List<String> getRecentClips(final String search, final int page, final int pageSize) {
    
	 //TODO Avoid script injection here
    final String format = String.format("select clip from clips where clip like '%%%s%%' order by lastAccessed desc limit %d,%d", search,
        page * pageSize, pageSize);

    System.out.println(format);

    return getQueryResults(
        format);
  }

  public List<String> getRecentClips(final int page, final int pageSize) {
    return getQueryResults(
        String.format("select clip from clips order by lastAccessed desc limit %d,%d", page * pageSize, pageSize));
  }

  private List<String> getQueryResults(final String query) {
    try {
      final Statement stmt = this.con.createStatement();

      // TODO better have a prepared statement
      final ResultSet results = stmt.executeQuery(query);

      return Optional.ofNullable(results).map(this::getClips).orElse(Collections.emptyList());

    } catch (final Exception e) {
      e.printStackTrace();
      System.out.println("Reconnecting to db...");
      this.con = ConnectionManager.INSTANCE.getConnection();
    }

    return Collections.emptyList();
  }

  private List<String> getClips(final ResultSet r) {

    final List<String> clips = new ArrayList<>();

    try {
      while (r.next()) {
        clips.add(r.getString("clip"));
      }

    } catch (final Exception e) {
      e.printStackTrace();
      System.out.println("Reconnecting to db...");
      this.con = ConnectionManager.INSTANCE.getConnection();
    }

    return clips;
  }

}
