package sr.cliphist.dao;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;

public class ClipboardHistoryManager {
  public static void main(String[] args) {
    new ClipboardHistoryManager().showHistory();
  }

  private ClipDAO clipDAO = new ClipDAO();

  @SuppressWarnings("serial")
  public void showHistory() {

    new JFrame() {
      {
        setVisible(true);
        setBounds(200,200,400, 600);


        //setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        /*
         * add(new JTextArea() { { setBounds(0, 0, 300, 20); setVisible(true); }
         * });
         */



        add(new JList<String>() {
          {
            setVisible(true);
            setBounds(0, 31, 300, 100);
            setModel(new DefaultListModel<String>() {
              {
                clipDAO.getRecentClips(20).forEach(this::addElement);
              }
            });
            addMouseListener(new ListListener());
          }
        });
      }

    };

  }

  private static class ListListener extends MouseAdapter {

    @Override
    public void mouseClicked(MouseEvent e) {
      System.out.println("Clicked:"+e.getClickCount());
      JList list = (JList) e.getSource();
      String selectedValue = (String) list.getModel().getElementAt(list.locationToIndex(e.getPoint()));
      System.out.println(selectedValue);
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      StringSelection clipboardTarget = new StringSelection(selectedValue);
      clipboard.setContents(clipboardTarget, clipboardTarget);
      System.exit(0);
    }
  }
}
