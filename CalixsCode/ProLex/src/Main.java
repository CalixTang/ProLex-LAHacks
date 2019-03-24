import java.awt.*;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
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
	private JButton save;
	private JLabel title;
	private JButton logo;
	public static final String fileName = "data" + FileIO.fileSep + "def.txt";
	private ImageIcon clearImg = new ImageIcon("data" + FileIO.fileSep + "clear.png");
	private ImageIcon copyImg = new ImageIcon("data" + FileIO.fileSep + "copy.png");
	private ImageIcon exitImg = new ImageIcon("data" + FileIO.fileSep + "exit.png");
	private ImageIcon filterImg = new ImageIcon("data" + FileIO.fileSep + "filter.png");
	private ImageIcon listImg = new ImageIcon("data" + FileIO.fileSep + "list.png");
	private ImageIcon pasteImg = new ImageIcon("data" + FileIO.fileSep + "paste.png");
	private ImageIcon logoImg = new ImageIcon("data" + FileIO.fileSep + "logowithname.png");
	private ImageIcon saveImg = new ImageIcon("data" + FileIO.fileSep + "save.png");
	
	public Main() {
		title = new JLabel();
		title.setFont(title.getFont().deriveFont(Font.PLAIN, 75));

		UIManager.put("nimbusBase", new Color(232, 219, 249));
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

		JLabel jl=new JLabel();
	    jl.setIcon(new javax.swing.ImageIcon("data" + FileIO.fileSep + "logowithname.png"));
	    this.add(jl);
		
		frmMomentum = new JFrame();
		menu = new JPanel();
		menu.setBounds(0, 0, 1100 - 350, 900);
		menu.setVisible(true);
		menu.setBackground(new Color(52, 53, 57));
		menu.setLayout(null);

		exit = new JButton(exitImg);
		copy = new JButton(copyImg);
		paste = new JButton(pasteImg);
		words = new JButton(listImg);
		clear = new JButton(clearImg);
		run = new JButton(filterImg);
		logo = new JButton(logoImg);
		save = new JButton(saveImg);

		frmMomentum.setTitle("Prolex");
		frmMomentum.setBounds(100, 100, 1100, 900);
		frmMomentum.setBackground(new Color(255));
		frmMomentum.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmMomentum.getContentPane().setLayout(new BorderLayout(0, 0));
		frmMomentum.setResizable(false);

		editor = new JTextArea(100, 100);
		editor.setBounds(350, 150, 720, 700);
		editor.setBackground(new Color(255, 255, 255));
		editor.setSelectedTextColor(new Color(78, 120, 237));
		editor.setSelectionColor(Color.WHITE);
		editor.setForeground(Color.BLACK);
		editor.setCaretColor(Color.BLACK);
		editor.setFont(new Font("Avenir", Font.PLAIN, 20));
		scroll = new JScrollPane(editor, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setBounds(350, 150, 720, 700);
//		scroll.add(editor);

		frmMomentum.add(scroll);
		frmMomentum.add(menu);
		title.setBounds(150, 20, 200, 200);
		title.setForeground(Color.WHITE);
//		frmMomentum.add(title);

		run.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String str = editor.getText().toLowerCase();
				ArrayList<BadWord> foundWords = FileIO.parseForBadWords(str);
				/*
				 * for(BadWord b : foundWords) { System.out.println(b); }
				 */
				// minor detail: the current config has it such that it will do alphabetic
				// first, then index in string.
				// if we want to primarily sort by index in string, we need to sort the
				// foundwords by index
				// to do that, we could implement the comparable interface in FileIO
				Object[] o = { "Replace", "Keep" };
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
					ArrayList<String> lines = FileIO.readFile(fileName);
					for (String line : lines)
						if (!line.equals(""))
							str += line + FileIO.lineSep;
					editor.setText(str);
					editor.setEditable(false);
				}
			}
		});
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String badWord = JOptionPane.showInputDialog(null, "The Bad Word:", "New Word", JOptionPane.QUESTION_MESSAGE);
				if(badWord==null) {
					return;
				}
				String replacement = JOptionPane.showInputDialog(null, "The Replacement Word:", "New Word", JOptionPane.QUESTION_MESSAGE);
				if(replacement==null) {
					return;
				}
				String message = JOptionPane.showInputDialog(null, "The Message:", "New Word", JOptionPane.QUESTION_MESSAGE);
				if(message==null) {
					return;
				}
				badWord = badWord.toLowerCase();
				
				int n = -1;
				try {
					n = Integer.parseInt(message);
				}catch(NumberFormatException e) {
					message = "#" + message;
				}
				PrintWriter writer = null;
				try {
					writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName,true)));
					writer.println("");
					if(n==-1) {
						writer.print(badWord+ FileIO.valueSep+replacement+FileIO.valueSep+message);
						badWords.add(new BadWord(badWord,replacement,message));
					}else {
						writer.print(badWord + FileIO.valueSep + replacement + FileIO.valueSep + n);
						badWords.add(new BadWord(badWord,replacement,""+n));
					}
					writer.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally {
					try {
						writer.close();
					}catch(Exception e) {
						
					}
				}
				
			}
		});

		int x = 100;
		
		run.setBounds(15 + 25, 70 + x, 220, 43);
		copy.setBounds(15 + 25, 170 + x, 227, 43);
		paste.setBounds(15 + 25, 270 + x, 227, 43);
		clear.setBounds(15 + 25, 370 + x, 227, 43);
		words.setBounds(15 + 22, 470 + x, 227, 43);
		save.setBounds(15 + 28, 570 + x, 210, 43);
		exit.setBounds(15 + 25, 670 + x, 227, 43);
		logo.setBounds(135, 35, 817, 97);
		menu.add(run);
		menu.add(paste);
		menu.add(words);
		menu.add(exit);
		menu.add(clear);
		menu.add(copy);
		menu.add(logo);
		menu.add(save);

	}

	public static void main(String[] args) {
		ArrayList<String> strings = FileIO.readFile(fileName);
		badWords = FileIO.readBadWords(strings);

		//

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frmMomentum.setVisible(true);
					window.setBounds(100, 100, 1100, 800);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
