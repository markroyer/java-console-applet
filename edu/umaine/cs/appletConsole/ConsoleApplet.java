/**
 * 
 */
package edu.umaine.cs.appletConsole;

import java.applet.AppletContext;
import java.applet.AppletStub;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.Formatter;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Element;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * An applet that behaves like a console. A user can extend this class so that a
 * typical console application can run in a web browser. The user should
 * implement <code>AppletProgram</code> and override the <code>init</code>
 * method. A typical example is shown below:
 * 
 * 
 * <pre>
 * public void init() {
 * 	super.init();
 * 	setMainProgram(new MyProgram());
 * }
 * </pre>
 * 
 * Here, the user has defined a class <code>MyProgram</code> that extends
 * <code>AppletProgram</code> and will be run in the applet's console window.
 * 
 * @author Mark Royer
 */
public class ConsoleApplet extends JApplet implements KeyListener,
		ActionListener {

	/**
	 * For serialization
	 */
	private static final long serialVersionUID = 923827727768381468L;

	/**
	 * Input stream the applet will read from
	 */
	private AppletInputStream in;

	/**
	 * Output will be displayed using this stream
	 */
	private AppletPrintStream out;

	/**
	 * Used to display the text
	 */
	private JTextPane editorPane;

	/**
	 * Describes the style of the text
	 */
	private StyledDocument styleDocument;

	/**
	 * Contains all of the applet contents
	 */
	private JPanel mainPanel;

	/**
	 * Reloads the page containing the applet when clicked by the user
	 */
	private JButton restartButton;

	/**
	 * The most recent position written to by the output stream on the
	 * editorPane
	 */
	private int lastDisplayedPos;

	/**
	 * System dependent new line character sequence
	 */
	private static final String NEWLINE = System.getProperty("line.separator");

	/**
	 * The program to be executed
	 */
	private AppletConsoleProgram program;

	/**
	 * The thread the program is run in
	 */
	private Thread programThread;

	/**
	 * Creates a new applet that behaves like a console
	 */
	public ConsoleApplet() {
		super();

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(1, 1));
		editorPane = new JTextPane();
		editorPane.setEditable(true);

		JScrollPane scrollPane = new JScrollPane(editorPane);
		topPanel.add(scrollPane);

		styleDocument = editorPane.getStyledDocument();

		this.in = new AppletInputStream();
		this.out = new AppletPrintStream(new ByteArrayOutputStream(1));

		mainPanel = new JPanel();

		this.getContentPane().add(mainPanel);
		mainPanel.setLayout(new BorderLayout(5, 5));
		mainPanel.add(topPanel, BorderLayout.CENTER);

		this.restartButton = new JButton("Restart");
		this.restartButton.addActionListener(this);
		JPanel bottomPanel = new JPanel();
		bottomPanel.add(restartButton);
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);

		this.setPreferredSize(new Dimension(800, 600));

		setTitle("Applet Console");

		// The panels will inherit the background color
		topPanel.setBackground(null);
		bottomPanel.setBackground(null);
		mainPanel.setBackground(null);
		this.getContentPane().setBackground(null);

		this.validate();

		editorPane.addKeyListener(this);

	}

	/**
	 * @param title
	 *            The title of the program
	 */
	public void setTitle(String title) {
		mainPanel.setBorder(BorderFactory.createTitledBorder(title));
	}

	/**
	 * Sets the input color of the applet to the given color.
	 * 
	 * @param color
	 *            The color of the input
	 */
	public void setInputColor(Color color) {
		this.in.setColor(color);
	}

	/**
	 * Sets the output color of the applet to the given color.
	 * 
	 * @param color
	 *            The color of the output
	 */
	public void setOutputColor(Color color) {
		this.out.setColor(color);
	}

	/**
	 * @return The stream that the applet will read from
	 */
	public InputStream getInputStream() {
		return this.in;
	}

	/**
	 * @return The stream that the applet will display to
	 */
	public PrintStream getPrintStream() {
		return this.out;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed(KeyEvent e) {
		updateCaret(e);

		/*
		 * If the enter key is pressed, the current line is shipped to the input
		 * stream.
		 */
		if (KeyEvent.VK_ENTER == e.getKeyCode()) {

			Element lastParagraph = styleDocument
					.getParagraphElement(styleDocument.getLength());

			try {

				// We don't want to break the line if the caret is in the
				// middle of some text, so we move it to the end.
				editorPane.setCaretPosition(styleDocument.getLength());

				int start = getLastDisplayedPos();
				int end = lastParagraph.getEndOffset();
				String str = styleDocument.getText(start, end - start);
				this.in.setInput(str); // Give the string to the input stream

			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped(KeyEvent e) {
		// Don't do anything
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased(KeyEvent e) {
		// Don't do anything
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@SuppressWarnings(value = { "deprecation" })
	public void actionPerformed(ActionEvent e) {

		Object source = e.getSource();

		if (source == restartButton) {
			// We want to stop the program thread. Unfortunately, this is the
			// only way to do it without changing the underlying program.
			programThread.stop();
			this.restart();
		}

	}

	/**
	 * @return The last position displayed by the output stream in the editor
	 *         pane
	 */
	private synchronized int getLastDisplayedPos() {
		return lastDisplayedPos;
	}

	/**
	 * @param lastDisplayedPos
	 *            The last position written out to in the editor pane by the
	 *            output stream
	 */
	private synchronized void setLastDisplayedPos(int lastDisplayedPos) {
		this.lastDisplayedPos = lastDisplayedPos;
	}

	/**
	 * Update the caret depending on where it previously was.
	 * 
	 * @param e
	 *            The key event that was triggered
	 */
	private void updateCaret(KeyEvent e) {

		// Input to ignore
		if (inputToIgnore(e)) {
			return;
		}

		Caret caret = editorPane.getCaret();
		int start = getLastDisplayedPos();

		ensureInputeColorCorrect();

		/*
		 * If the caret is not on the last line or we've made a selection, make
		 * sure the caret is on the last line.
		 */
		if (caret.getDot() < getLastDisplayedPos()
				|| caret.getDot() != caret.getMark()) {

			Element currentParagraph = styleDocument.getParagraphElement(caret
					.getDot());

			caret.setDot((editorPane.getCaretPosition() - currentParagraph
					.getStartOffset())
					+ start);

		}
	}

	/**
	 * @param e
	 *            The last keys input
	 * @return true iff the keyboard command is not a special event
	 */
	private boolean inputToIgnore(KeyEvent e) {

		int mask = e.getModifiers();
		int code = e.getKeyCode();

		// Don't let a backspace delete contents if not the input text
		if (editorPane.getCaret().getDot() <= getLastDisplayedPos()
				&& e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			e.consume();
		}

		return ((mask & KeyEvent.CTRL_MASK) == KeyEvent.CTRL_MASK && code != KeyEvent.VK_V)
				|| code == KeyEvent.VK_UP
				|| code == KeyEvent.VK_DOWN
				|| code == KeyEvent.VK_LEFT || code == KeyEvent.VK_RIGHT;

	}

	/**
	 * Make sure the user input displays as the correct color
	 */
	private void ensureInputeColorCorrect() {
		// Make sure input is the correct color
		Style style = styleDocument.getStyle(AppletInputStream.STYLE);

		int start = getLastDisplayedPos();
		int end = styleDocument.getLength() + 1;
		int length = end - start + 1;

		// Update the input to the correct color
		styleDocument.setCharacterAttributes(start, length, style, false);
		styleDocument.setParagraphAttributes(start, length, style, false);

	}

	@Override
	public void init() {
		super.init();
		out.clear();
		editorPane.setEnabled(true);

		// If the user specifies a background color use it otherwise use the
		// standard background color.

		if (JPanel.class.equals(this.getParent().getClass())) {
			this.setStub(new AppletStub() {

				public void appletResize(int width, int height) {
					// Do nothing
				}

				public AppletContext getAppletContext() {
					return null;
				}

				public URL getCodeBase() {
					return null;
				}

				public URL getDocumentBase() {
					return null;
				}

				public String getParameter(String name) {
					return "";
				}

				public boolean isActive() {
					return false;
				}
			});
		}

		String bColor = this.getParameter("backgroundColor");
		if (bColor != null) {
			try {
				setBackground(Color.decode(bColor));
			} catch (Exception e) {
				setBackground(new JPanel().getBackground());
			}
		} else {
			setBackground(new JPanel().getBackground());
		}
	}

	/**
	 * The user program is started immediately. It is run in another thread so
	 * that the GUI won't block when redrawing and handle input events.
	 * 
	 * @param program
	 *            The program that will be executed
	 */
	public void setMainProgram(AppletConsoleProgram program) {
		program.setInputOutputStreams(in, out); // Set the streams
		this.program = program;
		restart();
	}

	public void restart() {
		out.clear();
		editorPane.setEnabled(true);

		try {
			AppletConsoleProgram newProgram = program.getClass().newInstance();
			newProgram.setInputOutputStreams(in, out);

			programThread = new Thread(newProgram);
		} catch (InstantiationException e1) {
			e1.printStackTrace();
			System.exit(-1);
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
			System.exit(-1);
		}
		programThread.setDaemon(true);
		programThread.start(); // Start the program in the new thread

		/*
		 * We'll use a thread to check every second if the user program has
		 * halted. If the user program has halted, we'll disable the editor
		 * pane.
		 */
		Thread stopApp = new Thread(new Runnable() {

			private Thread thread = programThread;

			public void run() {
				while (thread.isAlive()) {
					try {
						Thread.sleep(1000); // Sleep for a second
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				if (!programThread.isAlive()) {
					editorPane.setEnabled(false);
				}
			}
		}); // Create the thread and get it going

		stopApp.setDaemon(true);
		stopApp.start();

	}

	/**
	 * <code>AppletPrintStream</code> is similar to the standard
	 * <code>System.out</code> stream, but it displays in the applet.
	 * 
	 * @author Mark Royer
	 */
	public class AppletPrintStream extends PrintStream {

		public static final String STYLE = "output";

		/**
		 * Used by the format methods
		 */
		private Formatter formatter;

		/**
		 * Set's the stream's text color to the one given.
		 * 
		 * @param color
		 *            The color of the text
		 */
		public void setColor(Color color) {
			StyledDocument sd = editorPane.getStyledDocument();
			Style style = sd.getStyle(STYLE);
			StyleConstants.setForeground(style, color);
		}

		/**
		 * We make this constructor private so classes outside of
		 * <code>ConsoleApplet</code> can use <code>AppletPrintStream</code>
		 * objects, but can't create them.
		 * 
		 * The default color is blue.
		 * 
		 * @param out
		 */
		private AppletPrintStream(OutputStream out) {
			super(out);
			StyledDocument sd = editorPane.getStyledDocument();
			Style def = sd.getLogicalStyle(editorPane.getCaretPosition());
			sd.addStyle(STYLE, def);
			setColor(Color.blue);
		}

		/**
		 * Remove all of the text in the console.
		 */
		public void clear() {
			editorPane.setText("");
		}

		@Override
		public void print(String s) {
			super.print(s);
			printString(s);
		}

		@Override
		public void println() {
			super.println();
			printString(NEWLINE);
		}

		@Override
		public void println(String x) {
			super.println(x);
			printString(NEWLINE);
		}

		@Override
		public void print(char c) {
			super.print(c);
			printString(String.valueOf(c));
		}

		@Override
		public void print(boolean b) {
			super.print(b);
			printString(String.valueOf(b));
		}

		@Override
		public void print(char[] s) {
			super.print(s);
			printString(String.valueOf(s));
		}

		@Override
		public void print(double d) {
			super.print(d);
			printString(String.valueOf(d));
		}

		@Override
		public void print(float f) {
			super.print(f);
			printString(String.valueOf(f));
		}

		@Override
		public void print(int i) {
			super.print(i);
			printString(String.valueOf(i));
		}

		@Override
		public void print(long l) {
			super.print(l);
			printString(String.valueOf(l));
		}

		@Override
		public void print(Object obj) {
			super.print(obj);
			printString(String.valueOf(obj));
		}

		@Override
		public PrintStream printf(Locale l, String format, Object... args) {
			super.printf(l, format, args);

			if ((formatter == null) || (formatter.locale() != l)) {
				formatter = new Formatter(this, l);
			}

			formatter.format(l, format, args);

			return this;
		}

		@Override
		public PrintStream printf(String format, Object... args) {
			super.printf(format, args);

			if ((formatter == null)
					|| (formatter.locale() != Locale.getDefault())) {
				formatter = new Formatter((Appendable) this);
			}
			formatter.format(Locale.getDefault(), format, args);

			return this;
		}

		@Override
		public void println(boolean x) {
			super.println(x);
			printString(NEWLINE);
		}

		@Override
		public void println(char x) {
			super.println(x);
			printString(NEWLINE);
		}

		@Override
		public void println(char[] x) {
			super.println(x);
			printString(NEWLINE);
		}

		@Override
		public void println(double x) {
			super.println(x);
			printString(NEWLINE);
		}

		@Override
		public void println(float x) {
			super.println(x);
			printString(NEWLINE);
		}

		@Override
		public void println(int x) {
			super.println(x);
			printString(NEWLINE);
		}

		@Override
		public void println(long x) {
			super.println(x);
			printString(NEWLINE);
		}

		@Override
		public void println(Object x) {
			super.println(x);
			printString(NEWLINE);
		}

		/**
		 * Print a string in the editor pane with this streams current color.
		 * 
		 * @param str
		 *            String to display
		 */
		private synchronized void printString(String str) {
			try {
				styleDocument.insertString(styleDocument.getLength(), str,
						styleDocument.getStyle(AppletPrintStream.STYLE));
				setLastDisplayedPos(styleDocument.getLength());
				scrollToEnd();
			} catch (BadLocationException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}

		/**
		 * Make the caret go to the end of the text.
		 */
		private void scrollToEnd() {
			// We use the Swing utility to make it go to the end of the text
			// when it's convenient for swing. In other words, when important
			// stuff like events and redraws are finished.
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					synchronized (styleDocument) {
						editorPane.setCaretPosition(styleDocument.getLength());
					}
				}

			});
		}
	}

	/**
	 * <code>AppletInputStream</code> behaves like <code>System.in</code>,
	 * but reads from the applet.
	 * 
	 * @author Mark Royer
	 */
	public class AppletInputStream extends InputStream {

		public static final String STYLE = "input";

		/**
		 * The buffer to read in from
		 */
		private char[] buf = null;

		/**
		 * The current position in the buffer
		 */
		private int index = 0;

		/**
		 * We make the constructor private so that others can use
		 * <code>AppletInputStream</code> objects, but they can't create them.
		 * 
		 * The default input color is a dark green.
		 */
		private AppletInputStream() {
			StyledDocument sd = editorPane.getStyledDocument();
			Style def = sd.getLogicalStyle(editorPane.getCaretPosition());
			sd.addStyle(STYLE, def);
			setColor(Color.green.darker());
		}

		/**
		 * The input text will be set to the given color.
		 * 
		 * @param color
		 *            The color input will appear as
		 */
		public void setColor(Color color) {
			StyledDocument sd = editorPane.getStyledDocument();
			Style style = sd.getStyle(STYLE);
			StyleConstants.setForeground(style, color);
		}

		@Override
		public int read() throws IOException {

			waitForInput();

			// If we have something to read and the input is valid return the
			// current character.
			if (buf != null && index >= 0 && index < buf.length) {
				return buf[index++];
			} else {
				// The current buffer is exhausted, so reset the input
				index = -1;
				buf = null;
				return -1;
			}
		}

		/**
		 * Used to make the read stream block until the user inputs some text.
		 */
		private void waitForInput() {
			while (buf == null) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		/**
		 * Set the input stream to read from the given string.
		 * 
		 * @param str
		 *            The string the input stream will read from
		 */
		protected synchronized void setInput(String str) {
			buf = str.toCharArray();
			index = 0;
		}

	}
}
