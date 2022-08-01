package sr.cliphist;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import sr.cliphist.dao.ClipDAO;

public class ClipboardMonitor {

  // TODO easy GUI to access last clips
  // TODO run as a service, so it will get restarted if somehow got terminated
  public static void main(final String[] args) throws Exception {
    new ClipboardMonitor().readAndSave();
  }

  private final ClipDAO clipDAO = new ClipDAO();

  public void readAndSave() throws UnsupportedFlavorException, IOException, InterruptedException {
    final Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();

    Object last = null;

    while (true) {
      try {
        final Object data = sysClip.getData(DataFlavor.stringFlavor);
        if (!data.equals(last)) {
          this.clipDAO.insertClip(String.valueOf(data));
          System.out.println(data);
          last = data;
        }

      } catch (final Exception e) {
        e.printStackTrace();
      }
      Thread.sleep(1000);
    }
  }

}
