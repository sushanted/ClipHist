package sr.cliphist;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListModel;
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

  private Frame frame;

  private String searchString;

  @SuppressWarnings("serial")
  public void showHistory() {

    frame = new JFrame() {
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

                addMouseMotionListener(newToolTipShower());
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

      frame.setState(Frame.ICONIFIED);
      // System.exit(0);
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
        searchString = e.getDocument().getText(0, e.getDocument().getLength());
        System.out.println(searchString);

        // TODO make these constants
        clipConsumerReference.get().accept(clipDAO.getRecentClips(searchString, 0, 30));

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

  private MouseMotionAdapter newToolTipShower() {
    return new MouseMotionAdapter() {
      @Override
      public void mouseMoved(MouseEvent e) {
        JList<String> l = (JList<String>) e.getSource();
        ListModel m = l.getModel();
        int index = l.locationToIndex(e.getPoint());
        if (index > -1) {
          l.setToolTipText(format(m.getElementAt(index).toString()));
        }
      }
    };
  }

  private String format(String content) {
    if (searchString != null && !searchString.isEmpty()) {
      content = content.replaceAll("(?i)(" + Pattern.quote(searchString)+")",
          "<font color='green'><b>$1</b></font>");
    }

    return "<html><pre>" + content + "</pre></html>";
  }
}
