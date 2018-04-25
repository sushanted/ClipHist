package sr.cliphist;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import sr.cliphist.dao.ClipDAO;
import sr.cliphist.models.Clips;

public class ClipboardHistoryManager {
  public static void main(String[] args) {
    new ClipboardHistoryManager().showHistory();
  }

  private ClipDAO clipDAO = new ClipDAO();

  private AtomicReference<Consumer<List<String>>> clipsConsumer = new AtomicReference<Consumer<List<String>>>();

  @SuppressWarnings("serial")
  public void showHistory() {

    new JFrame() {
      {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setBounds(200, 200, 400, 600);

        setTitle("cliphist");

        add(new JPanel() {
          {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            add(new JTextField("Search") {
              {
                setMaximumSize(new Dimension(1000, 20));
                this.getDocument().addDocumentListener(new SearchListener(clipsConsumer));
              }
            });

            add(new JList<String>() {
              {
                setVisible(true);

                clipsConsumer.set(clips -> {

                  setModel(new DefaultListModel<String>() {
                    {
                      clips.forEach(this::addElement);
                    }
                  });

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

    clipsConsumer.get().accept(clipDAO.getRecentClips(0, 30));

  }

  private class ListListener extends MouseAdapter {

    @Override
    public void mouseClicked(MouseEvent e) {
      System.out.println("Clicked:" + e.getClickCount());
      JList list = (JList) e.getSource();
      String selectedValue = (String) list.getModel().getElementAt(list.locationToIndex(e.getPoint()));
      System.out.println(selectedValue);
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      StringSelection clipboardTarget = new StringSelection(selectedValue);
      clipboard.setContents(clipboardTarget, clipboardTarget);
      //System.exit(0);
    }
  }

  private class SearchListener implements DocumentListener {

    private AtomicReference<Consumer<List<String>>> clipConsumerReference;

    public SearchListener(AtomicReference<Consumer<List<String>>> clipConsumerReference) {
      this.clipConsumerReference = clipConsumerReference;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
      process(e);
    }

    private void process(DocumentEvent e) {
      try {
        String searchText = e.getDocument().getText(0, e.getDocument().getLength());
        System.out.println(searchText);

        //TODO make these constants
        clipConsumerReference.get().accept(clipDAO.getRecentClips(searchText, 0, 30));

      } catch (BadLocationException e1) {
        e1.printStackTrace();
      }

    }

    @Override
    public void removeUpdate(DocumentEvent e) {
      process(e);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
      process(e);
    }

  }
}
