package sr.cliphist;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
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
import sr.cliphist.transform.Transformation;
import sr.cliphist.transform.Transformer;

public class ClipboardHistoryManager {
	public static void main(final String[] args) {
		new ClipboardHistoryManager().showHistory();
	}

	private final ClipDAO clipDAO = new ClipDAO();

	private final AtomicReference<Consumer<List<String>>> clipsConsumer = new AtomicReference<>();

	private Frame frame;

	private String searchString;

	@SuppressWarnings("serial")
	public void showHistory() {

		this.frame = new JFrame() {
			{

				setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				setBounds(200, 200, 400, 700);

				setTitle("cliphist");

				add(new JPanel() {
					{
						setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

						add(new JTextField("Search") {
							{
								setMaximumSize(new Dimension(1000, 20));
								this.getDocument().addDocumentListener(
										new SearchListener(ClipboardHistoryManager.this.clipsConsumer));
							}
						});

						add(new JList<String>() {
							{
								setVisible(true);

								ClipboardHistoryManager.this.clipsConsumer.set(clips -> {

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

								setLayout(new GridLayout(Transformation.values().length/5 + 1,5));

								for (Transformation transformation : Transformation.values()) {
									add(new JButton(transformation.getCaption()) {
										{
											addActionListener(e -> new Transformer().transform(transformation));
										}
									});
								}

							}
						});
					}

				});

				setVisible(true);

			}

		};

		this.clipsConsumer.get().accept(this.clipDAO.getRecentClips(0, 30));

		startClipboardMonitor();

	}

	private void startClipboardMonitor() {
		new Thread(() -> {
			try {
				new ClipboardMonitor().readAndSave();
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}).start();
	}

	private class ListListener extends MouseAdapter {

		@Override
		public void mouseClicked(final MouseEvent e) {
			System.out.println("Clicked:" + e.getClickCount());
			final JList list = (JList) e.getSource();
			final String selectedValue = (String) list.getModel().getElementAt(list.locationToIndex(e.getPoint()));
			System.out.println(selectedValue);
			final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			final StringSelection clipboardTarget = new StringSelection(selectedValue);
			clipboard.setContents(clipboardTarget, clipboardTarget);

			ClipboardHistoryManager.this.frame.setState(Frame.ICONIFIED);
			// System.exit(0);
		}
	}

	private class SearchListener implements DocumentListener {

		private final AtomicReference<Consumer<List<String>>> clipConsumerReference;

		public SearchListener(final AtomicReference<Consumer<List<String>>> clipConsumerReference) {
			this.clipConsumerReference = clipConsumerReference;
		}

		@Override
		public void insertUpdate(final DocumentEvent e) {
			process(e);
		}

		private void process(final DocumentEvent e) {
			try {
				ClipboardHistoryManager.this.searchString = e.getDocument().getText(0, e.getDocument().getLength());
				System.out.println(ClipboardHistoryManager.this.searchString);

				// TODO make these constants
				this.clipConsumerReference.get().accept(ClipboardHistoryManager.this.clipDAO
						.getRecentClips(ClipboardHistoryManager.this.searchString, 0, 30));

			} catch (final BadLocationException e1) {
				e1.printStackTrace();
			}

		}

		@Override
		public void removeUpdate(final DocumentEvent e) {
			process(e);
		}

		@Override
		public void changedUpdate(final DocumentEvent e) {
			process(e);
		}

	}

	private MouseMotionAdapter newToolTipShower() {
		return new MouseMotionAdapter() {
			@Override
			public void mouseMoved(final MouseEvent e) {
				final JList<String> l = (JList<String>) e.getSource();
				final ListModel m = l.getModel();
				final int index = l.locationToIndex(e.getPoint());
				if (index > -1) {
					l.setToolTipText(format(m.getElementAt(index).toString()));
				}
			}
		};
	}

	private String format(String content) {

		// System.out.println(content);

		if (this.searchString != null && !this.searchString.isEmpty()) {
			content = content.replaceAll("(?i)(" + Pattern.quote(this.searchString) + ")",
					"<font color='green'><b>$1</b></font>");
		}

		return "<html><pre>" + content + "</pre></html>";
	}
}
