package sr.cliphist.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ClipDAO {

  // TODO create indexes for required columns

  public static void main(String[] args) {
    //new ClipDAO().insertClip("tester");
    new ClipDAO().getRecentClips(10).forEach(System.out::println);
  }

  Connection con = ConnectionManager.INSTANCE.getConnection();

  public void insertClip(String clip) {
    // check if clip already exists
    // yes : modify the clip count/time etc
    // no : insert the clip

    clip = clip.replace("'", "''");

    try {
      Statement stmt = con.createStatement();

      // TODO better have a prepared statement
      int count = stmt
          .executeUpdate(String.format("update clips set frequency = frequency + 1,lastAccessed = %d where clip = '%s'",
              System.currentTimeMillis(), clip));

      if (count == 0) {
        stmt.execute(String.format("insert into clips values('%s',%d,%d)", clip, 1, System.currentTimeMillis()));
      }

      stmt.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public List<String> getRecentClips(int n) {
    try {
      Statement stmt = con.createStatement();

      // TODO better have a prepared statement
      ResultSet results = stmt
          .executeQuery(String.format("select clip from clips order by lastAccessed desc limit %d", n));

      return Optional.ofNullable(results).map(this::getClips).orElse(Collections.emptyList());

    } catch (Exception e) {
      e.printStackTrace();
    }

    return Collections.emptyList();
  }

  private List<String> getClips(ResultSet r) {

    List<String> clips = new ArrayList<>();

    try {
      while(r.next()) {
        clips.add(r.getString("clip"));
      }

    }catch(Exception e) {
      e.printStackTrace();
    }

    return clips;
  }



}
