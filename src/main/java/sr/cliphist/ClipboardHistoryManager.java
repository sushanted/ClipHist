package sr.cliphist;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

import sr.cliphist.dao.ClipDAO;

public class ClipboardHistoryManager {
  public static void main(String[] args) {
    new ClipboardHistoryManager().showHistory();
  }

  private ClipDAO clipDAO = new ClipDAO();

  @SuppressWarnings("serial")
  public void showHistory() {

    new JFrame() {
      {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setBounds(200, 200, 400, 600);

        /*add(new JLabel("Hello") {
          {
            setVisible(true);
          }

        });

        add(new JTextField("Search") {
          {
            //setBounds(200, 200, 400, 600);
            setVisible(true);
            setText("type regex");
          }
        });*/

        add(new JPanel() {
          {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));


            // setBounds(0, 0, 100, 100);
            add(new JTextField("Search") {
              {
                //setBounds(200, 200, 400, 600);
                setVisible(true);
                setMaximumSize(new Dimension(1000, 20));
              }
            });


            add(new JList<String>() {
              {
                setVisible(true);
                //setBounds(200, 200, 400, 600);
                // setBounds(0, 31, 300, 100);
                setModel(new DefaultListModel<String>() {
                  {
                    clipDAO.getRecentClips(0,30).forEach(this::addElement);
                  }
                });
                addMouseListener(new ListListener());
              }
            });


            add(new JPanel() {
              {
                add(new JButton("<"));
                add(new JButton(">"));
              }
            });


          }

        });

        setVisible(true);

      }

    };

  }

  private static class ListListener extends MouseAdapter {

    @Override
    public void mouseClicked(MouseEvent e) {
      System.out.println("Clicked:" + e.getClickCount());
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
