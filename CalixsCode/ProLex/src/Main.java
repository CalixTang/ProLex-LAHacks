import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;

public class Main extends JPanel {
	public static ArrayList<BadWord> badWords;
	private JFrame frmMomentum;
	private JTextArea editor;
	private JScrollPane scroll;
	private JPanel menu;
	private JButton login;
	private JButton run;
	private JButton words;
	private JButton copy;
	private JButton paste;
	private JButton clear;
	private JButton exit;
	private JLabel title;
	private Color c = new Color(47, 47, 47);
	private Color y = new Color(0, 255, 0); // Text
	private Color p = new Color(255, 0, 0); // Highlight

	public Main() {

		title = new JLabel();
		title.setFont(title.getFont().deriveFont(Font.PLAIN, 75));

		UIManager.put("nimbusBase", new Color(0, 0, 255));
		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
			if ("Nimbus".equals(info.getName())) {
				try {
					UIManager.setLookAndFeel(info.getClassName());
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (UnsupportedLookAndFeelException e) {
					e.printStackTrace();
				}
				break;
			}
		}

		frmMomentum = new JFrame();
		menu = new JPanel();
		menu.setBounds(0, 0, 1100 - 350, 700);
		menu.setVisible(true);
		menu.setBackground(new Color(52, 53, 57));
		menu.setLayout(null);

		exit = new JButton();
		copy = new JButton();
		paste = new JButton();
		words = new JButton();
		clear = new JButton();
		run = new JButton();

		frmMomentum.setTitle("ProLex Profanity Filter");
		frmMomentum.setBounds(100, 100, 1100, 700);
		frmMomentum.setBackground(new Color(255));
		frmMomentum.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmMomentum.getContentPane().setLayout(new BorderLayout(0, 0));
		frmMomentum.setResizable(false);

		editor = new JTextArea(100, 100);
		editor.setBounds(350, 50, 720, 600);
		editor.setBackground(new Color(255, 255, 255));
		editor.setSelectedTextColor(p);
		editor.setSelectionColor(c);
		editor.setForeground(y);
		editor.setCaretColor(Color.BLACK);
		editor.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
		scroll = new JScrollPane(editor, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setBounds(350, 50, 720, 600);
//		scroll.add(editor);

		frmMomentum.add(scroll);
		frmMomentum.add(menu);
		title.setBounds(150, 20, 200, 200);
		title.setForeground(Color.WHITE);
//		frmMomentum.add(title);

		run.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String str = editor.getText();
				ArrayList<BadWord> foundWords = FileIO.parseForBadWords(str);
				/*
				 * for(BadWord b : foundWords) { System.out.println(b); }
				 */
				// minor detail: the current config has it such that it will do alphabetic
				// first, then index in string.
				// if we want to primarily sort by index in string, we need to sort the
				// foundwords by index
				// to do that, we could implement the comparable interface in FileIO
				Object[] o = { "replace", "keep" };
				Highlighter h = editor.getHighlighter();
				HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.red);

				while (!foundWords.isEmpty()) {
					BadWord b = foundWords.get(0);
					h.removeAllHighlights();
					try {
						h.addHighlight(b.getIndex(), b.getIndex() + b.getBadWord().length(), painter);
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
					int n = 2;

					if (!b.isKept())
						n = JOptionPane.showOptionDialog(null, b.getMessage() + b.getReplacement(), "Bad word found!",
								JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, o, o[1]);
					if (n == 0) {// replace
						h.removeAllHighlights();
						editor.setText(editor.getText().substring(0, b.getIndex()) + b.getReplacement()
								+ editor.getText().substring(b.getIndex() + b.getBadWord().length()));

					} else if (n == 1) {// keep
						b.setKept(true);
						h.removeAllHighlights();
					}

					h.removeAllHighlights();

					str = editor.getText();
					foundWords = FileIO.parseForBadWords(str);
				}
			}

		});

		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}

		});

		clear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				editor.setEditable(true);
				editor.setText("");
			}

		});

		paste.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String data = new String();
				try {
					data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
				} catch (HeadlessException e) {
					System.out.println("Contents could not be pasted");
				} catch (UnsupportedFlavorException e) {
					// TODO Auto-generated catch block
					System.out.println("Contents could not be pasted");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("Contents could not be pasted");
				}

				editor.setText(editor.getText() + data);

			}

		});

		copy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String str = editor.getText();
				StringSelection stringSelection = new StringSelection(str);
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(stringSelection, null);
			}
		});

		words.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				String pass = JOptionPane.showInputDialog(null, "Please enter the password to access blacklist",
						"Blacklist Login", JOptionPane.QUESTION_MESSAGE);
				if (pass != null && pass.equals("mature")) {
					String str = "";
					ArrayList<String> lines = FileIO.readFile("data" + FileIO.fileSep + "def.txt");
					for (String line : lines)
						if (!line.equals(""))
							str += line + FileIO.lineSep;
					editor.setText(str);
					editor.setEditable(false);
				}
			}
		});

		run.setBounds(15, 95, 300, 50);
		run.setFont(run.getFont().deriveFont(Font.PLAIN, 20f));
		run.setText("FILTER");
		copy.setBounds(15 + 25, 195, 250, 50);
		exit.setFont(exit.getFont().deriveFont(Font.PLAIN, 20f));
		exit.setText("Exit");
		paste.setBounds(15 + 25, 295, 250, 50);
		words.setFont(words.getFont().deriveFont(Font.PLAIN, 20f));
		words.setText("Blacklisted Words");
		clear.setBounds(15 + 25, 395, 250, 50);
		paste.setFont(paste.getFont().deriveFont(Font.PLAIN, 20f));
		paste.setText("Paste Clipboard");
		words.setBounds(15 + 25, 495, 250, 50);
		clear.setFont(clear.getFont().deriveFont(Font.PLAIN, 20f));
		clear.setText("Clear");
		exit.setBounds(15 + 25, 595, 250, 50);
		copy.setFont(copy.getFont().deriveFont(Font.PLAIN, 20f));
		copy.setText("Copy Contents");
		menu.add(run);
		menu.add(paste);
		menu.add(words);
		menu.add(exit);
		menu.add(clear);
		menu.add(copy);

	}

	public static void main(String[] args) {
		ArrayList<String> strings = FileIO.readFile("src" + FileIO.fileSep + "def.txt");
		badWords = FileIO.readBadWords(strings);

		//

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frmMomentum.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
