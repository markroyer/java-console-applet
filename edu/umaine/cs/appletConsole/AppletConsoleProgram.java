/**
 * 
 */
package edu.umaine.cs.appletConsole;

import java.io.InputStream;
import java.io.PrintStream;

/**
 * An <code>AppletConsoleProgram</code> can be run inside of an applet in a
 * web browser, and it will appear to behave as if it was running in a typical
 * operating system console.
 * 
 * @author Mark Royer
 */
public interface AppletConsoleProgram extends Runnable {

	/**
	 * A user should use this method to set his or her program's streams.
	 * 
	 * @param in
	 *            The input stream used by the applet
	 * @param out
	 *            The output stream used by the applet
	 */
	public void setInputOutputStreams(InputStream in, PrintStream out);

}
