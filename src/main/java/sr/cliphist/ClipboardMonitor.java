package sr.cliphist;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import sr.cliphist.dao.ClipDAO;

public class ClipboardMonitor {

  //TODO easy GUI to access last clips
  //TODO run as a service, so it will get restarted if somehow got terminated
  public static void main(String[] args) throws Exception{
    new ClipboardMonitor().readAndSave();
  }

  private ClipDAO clipDAO = new ClipDAO();
  
  private  void readAndSave() throws UnsupportedFlavorException, IOException, InterruptedException {
    Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
    
    Object last = null;
    
    while(true) {
      Object data = sysClip.getData(DataFlavor.stringFlavor);
      if(!data.equals(last)) {
        clipDAO.insertClip(String.valueOf(data));
        System.out.println(data);
        last = data;
      }
      Thread.sleep(1000);
    }
  }
  
}
