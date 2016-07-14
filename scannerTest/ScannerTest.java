package scannerTest;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import edu.umaine.cs.appletConsole.AppletConsoleProgram;

/**
 * Simple example program that can be run as an applet.
 * 
 * @author Mark Royer
 */
public class ScannerTest implements AppletConsoleProgram {

	/**
	 * The input stream that will be used by the application
	 */
	public static InputStream in;

	/**
	 * The output stream used by the application
	 */
	public static PrintStream out;

	/**
	 * @param inputStream
	 *            The stream this program will read from
	 * @param printStream
	 *            The stream this program will write to
	 */
	public void setInputOutputStreams(InputStream inputStream,
			PrintStream printStream) {
		in = inputStream;
		out = printStream;
	}

	/**
	 * The "main" method of the program.
	 */
	public void run() {

		Scanner scanner = new Scanner(in);

		out.println("I will read what you typed.");

		String str = "";
		// Read user input until the word "bye" is typed.
		while (!str.equalsIgnoreCase("bye")) {
			str = scanner.nextLine();
			out.println("You typed \"" + str + "\"");
		}

		out.println("Thanks for typing!");
	}

	/**
	 * Run the program as a stand alone application.
	 * 
	 * @param args
	 *            Not used
	 */
	public static void main(String[] args) {
		ScannerTest test = new ScannerTest();
		test.setInputOutputStreams(System.in, System.out);
		test.run();
	}

}
