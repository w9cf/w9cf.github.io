/* Demonstration of PlotLive and audio.

@Author: Edward A. Lee and Christopher Hylands, based on code by Gabriele Bulfon

@Version: @(#)PlayingWave.java	1.18 10/30/97

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

/*
 * Based on:
 * @(#)PlayingWave.java    1.0 96/07/20 Gabriele Bulfon
 *
 * Copyright (c) 1996 Alu o'Neal. All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL or COMMERCIAL purposes and
 * without fee is hereby granted.
 *
 * ALU MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ALU SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * THIS SOFTWARE IS NOT DESIGNED OR INTENDED FOR USE OR RESALE AS ON-LINE
 * CONTROL EQUIPMENT IN HAZARDOUS ENVIRONMENTS REQUIRING FAIL-SAFE
 * PERFORMANCE, SUCH AS IN THE OPERATION OF NUCLEAR FACILITIES, AIRCRAFT
 * NAVIGATION OR COMMUNICATION SYSTEMS, AIR TRAFFIC CONTROL, DIRECT LIFE
 * SUPPORT MACHINES, OR WEAPONS SYSTEMS, IN WHICH THE FAILURE OF THE
 * SOFTWARE COULD LEAD DIRECTLY TO DEATH, PERSONAL INJURY, OR SEVERE
 * PHYSICAL OR ENVIRONMENTAL DAMAGE ("HIGH RISK ACTIVITIES").  ALU
 * SPECIFICALLY DISCLAIMS ANY EXPRESS OR IMPLIED WARRANTY OF FITNESS FOR
 * HIGH RISK ACTIVITIES.
 */

package ptplot.demo.audio;
import ptplot.*;
import java.applet.Applet;
import java.awt.*;
import java.lang.InterruptedException;
import java.util.Vector;
import sun.audio.*;

/** An example of look-up wave table
 * to produce audio in real time.
 * See the 
 * <A HREF="http://users.iol.it/gabriele.bulfon/PlayingWave/index.html">The Playing Wave home page</A>.
 *
 * @author Gabriele Bulfon, Christopher Hylands
 * @version @(#)PlayingWave.java	1.18 10/30/97, based on Gabriele Bulfon's version: 1.0, 20 Jul 1996
 */

//////////////////////////////////////////////////////////////////////////
//// SliderArea

/** This class implements the sliding portion of the
 * Slider class.
 */
class SliderArea extends Panel {

    /** The constructor.
     * @param val the initial value of the slider
     * @param max the maximum value of the slider (0-max)
     * @param w the preferred width to be assumed
     * @param h the preferred height to be assumed 
     * @param so the observer to be notified upon slider changes.
     */
    SliderArea(int val,int max,int w, int h,SliderObserver so) {
        obs=so;
        setLayout(null);
        this.max=max;
        value=val;
        sWidth=w;
        sHeight=h;
    }

    /** The preferred size.
     * @deprecated As of JDK1.1 in java.awt.component, but we need 
     * to compile under 1.0.2 for netscape3.x compatibility.
     */
    public Dimension preferredSize() {
        return new Dimension(sWidth,sHeight);
    }

    /** The minimum size.
     * @deprecated As of JDK1.1 in java.awt.component, but we need 
     * to compile under 1.0.2 for netscape3.x compatibility.
     */
    public Dimension minimumSize() {
        return new Dimension(sWidth,sHeight);
    }

    /** I prefer to use my update, so that no clean will be performed
     * before my paint routine.
     */
    public void update(Graphics g) {
        paint(g);
    }

    /** The paint routine.
     */
    public void paint(Graphics g) {
        //It seems that the awt will be able to create
        //an off-screen image, only when the component
        //has been already displayed (...am I wrong!?).
        //So I have to create it the first time
        //the paint method is called.
        if (image==null) image=createImage(sWidth,sHeight);

        //If I have my off-screen image, I want its Graphics too!
        if (gi==null && image!=null) gi=image.getGraphics();

        //If I have its Graphics, I can begin painting the component.
        if (gi!=null) {
            int height = size().height; // FIXME: size is deprecated in 1.1,
            // but we need to compile under 1.0.2 for netscape3.x.
            int width = size().width;   // FIXME: size is deprecated.

            //Draw a 3D background.
            gi.setColor(Color.lightGray);
            gi.fill3DRect(0,0,width,height,true);

            //Calculate the position of the inset to be drawn.
            //The inset is where the cursor will slide on,
            //and is made up of two adjacent lines of a darker
            //and a brighter color.
            int y;
            if ((height%2)==0) y=height/2;
            else y=height/2+1;
            gi.setColor(Color.gray);
            gi.drawLine(4,y-1,width-5,y-1);;
            gi.setColor(Color.white);
            gi.drawLine(4,y,width-5,y);;

            //Calculate the x position of the cursor.
            //The cursor is made up of a simple 3D square.
            int x=(int)(((double)(width-14)/(double)max)*(double)value);
            gi.setColor(Color.lightGray);
            gi.fill3DRect(2+x,1,10,height-2,true);

            //Now I can draw my ready component on the screen.
            g.drawImage(image,0,0,this);
        }
    }

    /** Set a new slider value.
     * @param v the value to be forced.
     */
    public void setValue(int v) {
        value=v;
        repaint();
    }
                       
    /** Here I catch the drag events.
     */
    public boolean mouseDrag(Event evt, int x, int y) {
        int height = size().height; // FIXME: size is deprecated in 1.1,
        // but we need to compile under 1.0.2 for netscape3.x.
        int width = size().width;   // FIXME: size is deprecated.

        //Calculate the new value.
        x-=7;
        if (x<0) x=0;
        if (x>(width-14)) x=(width-14);
        value=(int)(((double)max/(double)(width-14))*(double)x);
        //This is not a good piece of code!
        //I call my parent's setValue() method,
        //so that it will update the value area.
        if (getParent() instanceof Slider)
            ((Slider)getParent()).setValue(value);
        //Notify the observer of the new value.
        if (obs!=null) obs.newSliderValue(value);
        //Redraw the component.
        repaint();
        return true;
    }

    //////////////////////////////////////////////////////////////////////////
    ////                       public variables                           ////

    /** The maximum value of the slider.
     */
    int   max;

    /** The current value of the slider.
     */
    int   value;

    /** The preferred width and height.
     */
    int   sWidth,sHeight;

    /** An off-screen image and its Graphics.
     */
    Image    image;
    Graphics gi;

    /** The observer to be notified upon slider changes.
     */
    SliderObserver obs;
}

//////////////////////////////////////////////////////////////////////////
//// ValueArea

/** I subclassed the TextField component to implement
 * the value area. Actually the ValueArea class doesn't extends
 * any method, by now.
 */
class ValueArea extends TextField {

    /** The constructor.
     * @param s the initial text.
     * @param i the length.
     */
    ValueArea(String s, int i) {
        super(s,i);
    }
}

//////////////////////////////////////////////////////////////////////////
//// SliderObserver

/** This is the interface to be implemented by any class
 * that want to be notified of slider changes.
 */
interface SliderObserver {
    public void newSliderValue(int v);
}

//////////////////////////////////////////////////////////////////////////
//// Slider

/** This is the main Slider class that wraps the
 * the SliderArea and the ValueArea.
 * This is the one to be instantiated.
 * @see #SliderArea
 * @see #SliderValue
 * @see #SliderObserver
 */
class Slider extends Panel {
    /** The complete constructor.
     * @param s the label.
     * @param val the initial value.
     * @param max the maximum value.
     * @param w the preferred width.
     * @param h the preferred height.
     * @param so the observer.
     * @see #SliderObserver
     */
    Slider(String s, int val, int max, int w, int h, SliderObserver so) {
        obs=so;
        this.max=max;
        value=new ValueArea(
                Integer.toString(val),
                String.valueOf(max).length()
                );
        slider=new SliderArea(val,max,w,h,so);
        add(new Label(s));
        add(slider);
        add(value);
    }

    /** A shorter version of the constructor.
     * This one has default values for preferred width and height.
     * @param s the label.
     * @param val the initial value.
     * @param max the maximum value.
     * @param so the observer.
     * @see #SliderObserver
     */
    Slider(String s,int val, int max, SliderObserver so) {
        this(s,val,max,100,10,so);
    }

    /** Sets the value of the SliderValue text.
     */
    public void setValue(int v) {
        value.setText(String.valueOf(v));
    }

    /** Here I catch the action event on the SliderValue.
     * @deprecated As of JDK1.1 in java.awt.component, but we need 
     * to compile under 1.0.2 for netscape3.x compatibility.
     */
    public boolean action(Event e, Object o) {
        return changed(e);
    }

    /** Here I know when something is being typed
     * in the SliderValue.
     * @deprecated As of JDK1.1 in java.awt.component, but we need 
     * to compile under 1.0.2 for netscape3.x compatibility.
     */
    public boolean keyUp(Event e, int key) {
        return changed(e);
    }

    /** This is called any time something might have
     * changed the SliderValue, so to test the validity
     * of the number, update the slider and notify the observer.
     */
    public boolean changed(Event e) {
        if (e.target instanceof ValueArea) {
            int v;

            try {
                v=(new Integer(value.getText())).intValue();
            } catch(NumberFormatException exc) {
                v=-1;
            }

            if (v>max) {
                v=max;
                setValue(v);
            } else if (v<0) {
                v=0;
                setValue(v);
            }
            slider.setValue(v);

            if (obs!=null) obs.newSliderValue(v);

            return true;
        }
        return false;
    }
    //////////////////////////////////////////////////////////////////////////
    ////                       public variables                           ////

    /** The sub-components that constitute the slider.
     */
    SliderArea  slider;
    ValueArea   value;

    /** The maximum value.
     */
    int         max;

    /** The observer.
     */
    SliderObserver obs;
}

//////////////////////////////////////////////////////////////////////////
//// Action

/** This class holds data for the applet.
 * This tells the applet what to do when the Action button
 * is pressed.
 */
class Action {
    /** Constants for the type of sample to be generated
     */
    public static final int	RANDOM=0;
    public static final int	SINE=1;

    /** Constants for the method of generation (replace the current
     * sample, add to the current sample).
     */
    public static final int	REPLACE=0;
    public static final int	ADD=1;

    /** A constructor without parameter.
     * @param m the method.
     * @param t the type.
     */
    Action(int m, int t) {
        _method = m;
        _type = t;
    }

    /** A constructor with parameter.
     * @param m the method.
     * @param t the type.
     * @param s the parameter (the period of the sine wave, etc.).
     */
    Action(int m, int t, int s) {
        _method = m;
        _type = t;
        _signalParam = s;
    }

    /** Gets the method of generation.  The value returned is either
     * either <code>Action.REPLACE</code> or <code>Action.ADD</code>.
     */
    public int getMethod() {
        return _method;
    }

    /** Gets the type of sample to be generated.  The value returned is
     * either <code>Action.RANDOM</code> or <code>Action.SINE</code>,
     * which corresponds to a random wave or a sine wave.
     */
    public int getType() {
        return _type;
    }

    /** Gets the parameter (the period of the sine wave, etc.).
     */
    public int getSignalParam() {
        return _signalParam;
    }

    //////////////////////////////////////////////////////////////////////////
    ////                       private variables                          ////

    // The method 
    int	_method;
    // The type
    int	_type;
    // The eventual parameter (the period of the sine wave, etc.).
    int	_signalParam;
}

//////////////////////////////////////////////////////////////////////////
//// WaveCanvas 

/** This is the component that implements the real-time wave.
 * It implements the Runnable interface, so it can be started
 * as an independent Thread.
 */
class WaveCanvas extends Panel implements Runnable {
    /** Constructor.
     */
    public WaveCanvas(int w, int h, double v) {
        super();
        _actionQueue = new Vector();
        _waveWidth = w;
        _waveHeight = h;
        _dampValue = v;
        _length = w;
        _xpixel = new int[_length];
        _ypixel = new double[_length];
        _tpixel = new double[_length];
        _zpixel = new int[_length];
        _sample = new byte[_length];
        _sampleData = new AudioData(_sample);
        _sampleStream = new ContinuousAudioDataStream(_sampleData);
        action(new Action(Action.REPLACE,Action.RANDOM));
    }


    /** Handle an Action.
     * @deprecated As of JDK1.1 in java.awt.component, but we need 
     * to compile under 1.0.2 for netscape3.x compatibility.
     */
    public void action(Action a) {
        _actionQueue.addElement(a);
    }

    /** Copy the _ypixel array into _dstsample
     */
    public synchronized void getYPixels(double dstsample[]) {
        System.arraycopy(_ypixel, 0, dstsample, 0, _length);
    }

    /** Handle an event.
     * @deprecated As of JDK1.1 in java.awt.component, but we need 
     * to compile under 1.0.2 for netscape3.x compatibility.
     */
    public boolean handleEvent(Event evt) {
        if ((evt.id == Event.MOUSE_DOWN || evt.id == Event.MOUSE_DRAG) &&
                evt.x < _length && evt.x >= 0) {
                synchronized (this) {
                    _ypixel[evt.x] = ((double)evt.y/(double)_waveHeight)-0.5;
                }
        }
        return true;
    }

    /** Get the minimum size.
     * @deprecated As of JDK1.1 in java.awt.component, but we need 
     * to compile under 1.0.2 for netscape3.x compatibility.
     */
    public Dimension minimumSize() {
        return new Dimension(_waveWidth,_waveHeight);
    }

    /** Get the preferred size.
     * @deprecated As of JDK1.1 in java.awt.component, but we need 
     * to compile under 1.0.2 for netscape3.x compatibility.
     */
    public Dimension preferredSize() {
        return new Dimension(_waveWidth,_waveHeight);
    }

    /** Start the widget.
     */   
    public void start() {
        _image = createImage(_waveWidth,_waveHeight);
        if (_image != null) _imageGraphics=_image.getGraphics();

        // Start thread.
        if (_waveCanvasThread == null) {
            _waveCanvasThread = new Thread(this, "WaveCanvas Thread");
            _waveCanvasThread.start();
        }
        AudioPlayer.player.start(_sampleStream);
    }

    /** Stop the widget
     */   
    public void stop() {
        if (_waveCanvasThread != null) {
            //_waveCanvasThread.stop();
            _waveCanvasThread = null;
        }
        AudioPlayer.player.stop(_sampleStream);
    }

    /** Set the damp value.
     */
    public synchronized void setDampValue(double v) {
        _dampValue = v;
    }

    /** Run the widget.
     */   
    public void run() {
        while (Thread.currentThread() == _waveCanvasThread) {
            repaint();
            if (_actionQueue.size()>0) {
                Action a = (Action)_actionQueue.lastElement();
                    
                switch(a.getType()) {
                case Action.RANDOM:
                    for (int x=0;x<_length;++x) {
                        _tpixel[x]=Math.random()-0.5;
                    }
                    break;
                case Action.SINE:
                    double dx = a.getSignalParam()*(2.0*Math.PI)/_length;
                    for (int x = 0; x < _length; ++x) {
                        _tpixel[x]=Math.sin(dx*x)/2.0;
                    }
                    break;
                }
                    
                synchronized (this) {
                    switch(a.getMethod()) {
                    case Action.REPLACE:
                        for (int x = 0;x<_length;++x) {
                            _xpixel[x] = x;
                            _ypixel[x] = _tpixel[x];
                            _sample[x] = (byte)((_ypixel[x]+0.5)*_WAVESCALE);
                        }
                        break;
                    case Action.ADD:
                        for (int x = 0;x<_length;++x) {
                            _xpixel[x] = x;
                            _ypixel[x] += _tpixel[x];
                            _sample[x] = (byte)((_ypixel[x]+0.5)*_WAVESCALE);
                        }
                        break;
                    }
                }
                    
                _actionQueue.removeElementAt(_actionQueue.size()-1);
                    
            } else {
                synchronized (this) {
                    int ox;
                    for (int x = 0;x<_length;++x) {
                        if (x == _length-1) ox = 0;
                        else ox = x+1;
                        _ypixel[x] = (_ypixel[x]+_ypixel[ox])/_dampValue;
                        _sample[x] = (byte)((_ypixel[x]+0.5)*_WAVESCALE);
                    }
                }
            }
            Thread.yield();
//             try {
//                 Thread.sleep(100);
//                 //_waveCanvasThread.sleep(100);
//             } catch(InterruptedException e) {
//                 System.out.println("Exception: sleep interrupted");
//             }
        }
    }

    /** Update the display.
     */
    public void update(Graphics g) {
        if (_imageGraphics!=null) {
            _imageGraphics.setColor(getBackground());
            _imageGraphics.fillRect(0,0,_waveWidth,_waveHeight);
            synchronized (this) {
                for (int x = 0; x<_length; ++x) {
                    _zpixel[x] = (int)((_ypixel[x]+0.5)*(double)_waveHeight);
                }
            }
            _imageGraphics.setColor(getForeground());
            _imageGraphics.drawPolygon(_xpixel,_zpixel,_length);
            g.drawImage(_image,0,0,null);
        }
    }

    //////////////////////////////////////////////////////////////////////////
    ////                       private variables                          ////

    // Scale Y values by this.
    private static final double _WAVESCALE = 63.0;

    private Thread          _waveCanvasThread = null;
    private int             _xpixel[];
    private double          _ypixel[];
    private double	    _tpixel[];
    private int             _zpixel[];
    private byte	    _sample[];
    private int             _length;
    private double          _dampValue;
    private int             _waveWidth;
    private int             _waveHeight;
    private Image           _image;
    private Graphics        _imageGraphics;
    private Vector	    _actionQueue;
    private AudioData	    _sampleData;
    private ContinuousAudioDataStream	_sampleStream;
}

//////////////////////////////////////////////////////////////////////////
//// PlotLiveWave

/* Create a PlotLive component that plots the data from the wave.
 */
class PlotLiveWave extends PlotLive {

    /** Create a new PlotLiveWave object that is associated with wavecanvas.
     */   
    PlotLiveWave (WaveCanvas wavecanvas) {
        _debug = 0;             // For debugging, set this to a positive #.
        if (_debug >8 ) System.out.println("PlotLiveWave: Constructor");
        _wave = wavecanvas;
        Dimension dim = _wave.preferredSize();
        _waveWidth = dim.width;
        _ypixel = new double[_waveWidth];
        resize(dim.width*3,dim.height);  // FIXME: resize() is deprecated.
    }

    /** Called by the parent class to add points to the plot.
     */
    public void addPoints() {
        _wave.getYPixels(_ypixel);
        addPoint(0,0,-_ypixel[0],false);
        for(int i=1; i<_waveWidth;i++) {
            addPoint(0,i,-_ypixel[i],true);
        }
    }

    /** Define static properties of the plot, such as the title and
     * axis labels.  This also calls the base class
     * <code>init()</code>, which performs various initialization
     * functions and reads commands from a file given by a URL, if the
     * <i>dataurl</i> applet parameter is given.  Since the base class
     * <code>init()</code> is called after the static plot parameters
     * are defined, then the commands from the file will override the
     * static ones given here.  This method also creates start and
     * stop buttons to control the plot.
     */
    public void init () {
        if (_debug > 8 ) System.out.println("PlotLiveWave: init");

        setTitle("");
        setNumSets(1);
        setXRange(0,_waveWidth - 1);
        setYRange(-0.64,0.64);
        setPointsPersistence(_waveWidth);
        // Give the user direct control over starting and stopping.
        makeButtons();

        super.init();
    }

    //////////////////////////////////////////////////////////////////////////
    ////                       private variables                          ////

    private WaveCanvas _wave = null;
    private int _pointcount = 0;
    private double _ypixel[];
    private int _waveWidth = 0;
    private double _miny = Double.MAX_VALUE;
    private double _maxy = - Double.MAX_VALUE;
    private double _oldminy = Double.MAX_VALUE;
    private double _oldmaxy = - Double.MAX_VALUE;

}

//////////////////////////////////////////////////////////////////////////
//// PlayingWave

/** A realtime audio applet that uses live plotting.
 */
public class PlayingWave extends Applet implements SliderObserver {
    /** Handle an action.
     * @deprecated As of JDK1.1 in java.awt.component, but we need 
     * to compile under 1.0.2 for netscape3.x compatibility.
     */
    public boolean action(Event evt, Object obj) {
        if (evt.target == _actionButton) {
            _wave.action(new Action(
                    _method.getSelectedIndex(),
                    _type.getSelectedIndex(),
                    Integer.parseInt(_signalParam.getText())));
            return true;
        } else {
            return super.action (evt, obj);  // FIXME: action() is deprecated
            // in 1.1 but we need to compile under jdk1.0.2 for netscape3.x
        }
    }

    /** Initialize the applet.
     */
    public void init() {
        int wavewidth = 128;
        int waveheight = 300;

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout(gridbag);

        _method=new Choice();
        _method.addItem("Replace");
        _method.addItem("Add");
        gridbag.setConstraints(_method,c);
        add(_method);

        _type=new Choice();
        _type.addItem("Random");
        _type.addItem("Sine");
        gridbag.setConstraints(_type,c);
        add(_type);

        _signalParam=new TextField("10");
        gridbag.setConstraints(_signalParam,c);
        add(_signalParam);

        _actionButton = new Button("Action");
        gridbag.setConstraints(_actionButton,c);
        add(_actionButton);

        // The damper slider spans 4 columns
        _damper = new Slider("Damp:",50,100,200,15,this); 
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 4;
        gridbag.setConstraints(_damper,c);
        add(_damper);

        // If we are calling this from an application, then getParameter
        // will fail. 
        try {
            wavewidth = Integer.parseInt(getParameter("WAVEWIDTH"));
        } catch (NullPointerException e) {}
        try {
            waveheight = Integer.parseInt(getParameter("WAVEHEIGHT"));
        } catch (NullPointerException e) {}
        _wave=new WaveCanvas(wavewidth, waveheight,2.0);
        // The WaveCanvas widget spans 4 columns.
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 4;
        gridbag.setConstraints(_wave,c);
        add(_wave);

        // The PlotLive widget spans 3 rows and fills all available space
        _plotLiveWave = new PlotLiveWave(_wave);
        c.gridx = 4;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 3;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        gridbag.setConstraints(_plotLiveWave,c);
        add(_plotLiveWave);

        validate();
        _plotLiveWave.init();
    }

    public void newSliderValue(int v) {
        _wave.setDampValue((100.0/(double)(v+1)));
    }

    /** Used for testing as an application.  Run with:
     * <pre>
     java -classpath ../../..:/opt/jdk1.1.4/lib/classes.zip ptplot.demo.audio.PlayingWave
     </pre>
     */
    public static void main(String args[]) {
        PlayingWave pwave;

        Frame f = new Frame("The Wave");
        f.setBackground(Color.lightGray);
        f.resize(800,400);      // FIXME: resize() is deprecated.
        pwave = new PlayingWave();
        f.add("Center",pwave);
        f.show();
        pwave.init();
        pwave.start();
    }

    /** Run the applet.
     */   
    public void run() {
        //_plotLiveWave.run();
        //        super.run();
    }

    /** Start the applet.
     */   
    public void start() {
        _wave.start();
        _plotLiveWave.start();
        _plotLiveWave.setPlotting(true);
        super.start();
    }

    /** Stop the applet.
     */   
    public void stop() {
        _wave.stop();
        _plotLiveWave.stop();
    }

    //////////////////////////////////////////////////////////////////////////
    ////                       private variables                          ////

    private WaveCanvas _wave;
    private PlotLiveWave _plotLiveWave;
    private Choice _method;
    private Choice _type;
    private TextField _signalParam;
    private Button _actionButton;
    private Slider _damper;
}
