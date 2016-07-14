package scannerTest;

import javax.swing.JFrame;

import edu.umaine.cs.appletConsole.ConsoleApplet;

/**
 * Example of how a user defined <code>ConsoleApplet</code> can be created.
 * 
 * @author Mark Royer
 * 
 */
public class MyConsoleApplet extends ConsoleApplet {

	/**
	 * For serialization
	 */
	private static final long serialVersionUID = -6738610325663869016L;

	@Override
	public void init() {
		super.init();
		
		setTitle("Console Application - Echo");
		
		// We let the applet know the main program here
		setMainProgram(new ScannerTest());
	}
	
	/**
	 * @param args Not used
	 */
	public static void main(String[] args) {
		MyConsoleApplet ma =  new MyConsoleApplet();
		
	    JFrame frame = new JFrame("Console Application");
	    frame.getContentPane().add(ma);

	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.pack();
	    frame.setVisible(true);
	    
	    ma.init();
	}
}
