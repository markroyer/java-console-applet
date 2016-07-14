# java-console-applet

A java applet library that allows sharing of console based applications.

Author: Mark Royer

# Applet Console

## Description

The AppletConsole allows a stand-alone program to be run in a browser
window as if it were running in a console.  In order to make this
work, a user must extend two classes, AppletConsoleProgram.java and
ConsoleApplet.java.

AppletConsoleProgram: The user's "main" program must be made to extend
this class.  A method to set the input and output streams for the
program must be implemented.

ConsoleApplet: ConsoleApplet represents the applet and the entrance
to the program on a web browser.  A user must extend this class and
define an init method to initialize the program.


A test program called ScannerTest, has been included to show how
applet console can be used.  Also a sample index.html page has been
included to show how one may embed an applet into an html page.

## Compilation

The code should have come with binaries in the jar files.  To compile
the source files, one must unjar the contents and then invoke javac
from the command line.  Make sure that all the files are on the class path.

## Execution

The sample applet that simply echos back what a user types can be
executed by opening the index.html file in a browser equipped with a
java plugin.

The background color can be set with the backgroundColor attribute
being being set to the corresponding color specified in hexadecimal.

The pageName attribute must correspond to the page name that the
applet is embeddded into in order for the reset button to work
properly.


If you have any questions or comments, contact mroyer@cs.umaine.edu.

<!--  LocalWords:  AppletConsole AppletConsoleProgram ConsoleApplet
 -->
<!--  LocalWords:  init ScannerTest html unjar javac backgroundColor
 -->
<!--  LocalWords:  pageName embeddded
 -->
