/* A live signal plotter applet

@Author: Edward A. Lee and Christopher Hylands

@Version: @(#)PlotLiveDemoApplet.java	1.9    10/17/97

@Copyright (c) 1997 The Regents of the University of California.
All rights reserved.

Permission is hereby granted, without written agreement and without
license or royalty fees, to use, copy, modify, and distribute this
software and its documentation for any purpose, provided that the
above copyright notice and the following two paragraphs appear in all
copies of this software.

IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY
FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES
ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
SUCH DAMAGE.

THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
ENHANCEMENTS, OR MODIFICATIONS.

                                                PT_COPYRIGHT_VERSION_2
                                                COPYRIGHTENDKEY
*/
package ptplot.demo;

import ptplot.*;
import java.applet.Applet;
import java.awt.*;

//////////////////////////////////////////////////////////////////////////
//// PlotLiveDemoApplet
/** 
 * An Applet that demonstrates the PlotLiveDemo class. 
 * 
 * @author Edward A. Lee, Christopher Hylands
 * @version @(#)PlotLiveDemoApplet.java	1.9 10/17/97
 */
public class PlotLiveDemoApplet extends PlotApplet implements Runnable {

    /**
     * Return a string describing this applet.
     */
    public String getAppletInfo() {
        return "PlotLiveDemoApplet 1.1: Demo of PlotLive.\n" +
            "By: Edward A. Lee, eal@eecs.berkeley.edu\n" +
            "    Christopher Hylands, @eecs.berkeley.edu\n" +
            "(@(#)PlotLiveDemoApplet.java	1.9 10/17/97)";
    }

    /** 
     * Initialize the applet.
     */
    public void init() {
        if (_debug > 8) System.out.println("PlotLiveDemoApplet: init");
        int width,height;
        setLayout(new BorderLayout());

        newPlot();              // Create a PlotLive to operate on.
        add("Center",plot());

        super.init();
    }

    /** 
     * Paint the graphics.
     */
    public void paint(Graphics graphics) {
        if (_debug > 8) System.out.println("PlotLiveDemoApplet: paint");
        plot().paint(graphics);
    }

    /** 
     * Resize the plot.
     * @deprecated As of JDK1.1 in java.awt.component, but we need 
     * to compile under 1.0.2 for netscape3.x compatibility.
     */
    public void resize(int width, int height) {
        if (_debug > 8)
            System.out.println("PlotLiveDemoApplet: resize "+width+" "+height);
        super.resize(width,height); // FIXME: resize() is deprecated.
    }

    /** 
     * Paint the graphics.
     */
    public void run () {
        if (_debug > 8) System.out.println("PlotLiveDemoApplet: run");
// 	while (true) {
// 	    try {
// 		Thread.currentThread().sleep(speed);
// 	    } catch (InterruptedException e) {
// 	    }
        if (_debug > 10)
            System.out.println("PlotLiveDemoApplet: run calling repaint");
	repaint();
    }

    /** Start the plot
     */
    public void start () {
        if (_debug > 8) System.out.println("PlotLiveDemoApplet: start");
	_plotLiveDemoAppletThread = new Thread(this);
        _plotLiveDemoAppletThread.start();
        plot().start();
        super.start();
    }

    /** Stop the plot.
     */
    public void stop () {
        if (_debug > 8) System.out.println("PlotLiveDemoApplet: stop");
        _plotLiveDemoAppletThread.stop();
        super.stop();
    }

//     public void update (Graphics graphics) {
//         if (_debug > 8) System.out.println("PlotLiveDemoApplet: update");
//         paint(graphics);
//         super.update(graphics);
//     }

    //////////////////////////////////////////////////////////////////////////
    ////                         protected methods                        ////

    /** Create a new Plot object to operate on.
     */
    protected void newPlot() {
        _myPlotLiveDemo = new PlotLiveDemo();
    }

    /** Return the Plot object to operate on.
     */  
    public Plot plot() {
        return _myPlotLiveDemo;
    }

    //////////////////////////////////////////////////////////////////////////
    ////                         private variables                        ////

    // The Plot component we are running.
    private PlotLiveDemo _myPlotLiveDemo;

    // Thread for this applet.
    private Thread _plotLiveDemoAppletThread;
}
