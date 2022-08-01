package ptplot;

import java.awt.*;
//import java.io.*;
import java.util.*;

public class KSPlotNS extends KSPlotBoxNS {

    //////////////////////////////////////////////////////////////////////////
    ////                         public methods                           ////
   
    /**
     * In the specified data set, add the specified x,y point to the
     * plot.  Data set indices begin with zero.  If the dataset
     * argument is out of range, ignore.  The number of data sets is
     * given by calling *setNumSets()*.  The fourth argument indicates
     * whether the point should be connected by a line to the previous
     * point.
     */
    public synchronized void addPoint(int dataset, double x, double y,
            boolean connected) {
        _addPoint(dataset, x, y, connected);
    }
    
    /**
     * Draw the axes and then plot all points.  This is synchronized
     * to prevent multiple threads from drawing the plot at the same
     * time.  It calls <code>notify()</code> at the end so that a
     * thread can use <code>wait()</code> to prevent it plotting
     * points before the axes have been first drawn.  If the argument
     * is true, clear the display first.
     */
    public synchronized void drawPlot(Graphics graphics,
            boolean clearfirst) {
        super.drawPlot(graphics, clearfirst);
        // Plot the points
        for (int dataset = 0; dataset < _numsets; dataset++) {
            // FIXME: Make the following iteration more efficient.
            Vector data = _points[dataset];
            for (int pointnum = 0; pointnum < data.size(); pointnum++) {
                _drawPlotPoint(graphics, dataset, pointnum);
            }
        }
        notify();
    }
    
    /** 
     * Erase the point at the given index in the given dataset.  If
     * lines are being drawn, also erase the line to the next points
     * (note: not to the previous point).  The point is not checked to
     * see whether it is in range, so care must be taken by the caller
     * to ensure that it is.
     */
    public synchronized void erasePoint(int dataset, int index) {
        _erasePoint(dataset, index);  
    }

    /**
     * Return the maximum number of datasets
     */
    public int getMaxDataSets() {
        return _MAX_DATASETS;
    }

    /**
     * Initialize the plotter
     */
    public synchronized void init() {
        setNumSets(_numsets);
        _currentdataset = -1;
        super.init();
    }

    /**
     * Draw the axes and the accumulated points.
     */
    public void update(Graphics g) {
       drawPlot(g,true);
    }

    public void paintComponents(Graphics g) {
       update(g);
    }

    /** 
     * Resize the plot.
     * @deprecated As of JDK1.1 in java.awt.component, but we need 
     * to compile under 1.0.2 for netscape3.x compatibility.
     */
    public void setSize(int width, int height) {
        _width = width;
        _height = height;
        super.setSize(width,height);
    }

    /**
     * Turn bars on or off.
     */
    public void setBars (boolean on) {
        _bars = on;
    }

    /** 
     * Turn bars on and set the width and offset.  Both are specified
     * in units of the x axis.  The offset is the amount by which the
     * i<sup>th</sup> data set is shifted to the right, so that it
     * peeks out from behind the earlier data sets.
     */
    public void setBars (double width, double offset) {
        _barwidth = width;
        _baroffset = offset;
        _bars = true;
    }
    
    /** 
     * If the argument is true, then the default is to connect
     * subsequent points with a line.  If the argument is false, then
     * points are not connected.  When points are by default
     * connected, individual points can be not connected by giving the
     * appropriate argument to <code>addPoint()</code>.
     */
    public void setConnected (boolean on) {
        _connected = on;
    }
    
    /** 
     * If the argument is true, then a line will be drawn from any
     * plotted point down to the x axis.  Otherwise, this feature is
     * disabled.
     */
    public void setImpulses (boolean on) {
        _impulses = on;
    }
    
    /**
     * Set the marks style to "none", "points", "dots", or "various".
     * In the last case, unique marks are used for the first ten data
     * sets, then recycled.
     */
    public void setMarksStyle (String style) {
        if (style.equalsIgnoreCase("none")) {
            _marks = 0;
        } else if (style.equalsIgnoreCase("points")) {
            _marks = 1;
        } else if (style.equalsIgnoreCase("dots")) {
            _marks = 2;
        } else if (style.equalsIgnoreCase("various")) {
            _marks = 3;
        }
    }

    /** 
     * Specify the number of data sets to be plotted together.
     * Allocate a Vector to store each data set.  Note that calling
     * this causes any previously plotted points to be forgotten.
     * This method should be called before
     * <code>setPointsPersistence</code>.
     * @exception java.io.NumberFormatException if the number is less
     * than 1 or greater than an internal limit (usually 63).
     */
    public void setNumSets (int numsets) throws NumberFormatException {
        if (numsets < 1) {
            throw new NumberFormatException("Number of data sets ("+
                    numsets + ") must be greater than 0.");

        }
        if (numsets > _MAX_DATASETS) {
            throw new NumberFormatException("Number of data sets (" +
                    numsets + ") must be less than the internal limit of " +
                    _MAX_DATASETS + "To increase this value, edit " +
                    "_MAX_DATASETS and recompile");
        }

        this._numsets = numsets;
        _points = new Vector[numsets];
        _prevx = new long[numsets];
        _prevy = new long[numsets];
        for (int i=0; i<numsets; i++) {
            _points[i] = new Vector();
        }
    }
    
    /** 
     * Calling this method with a positive argument sets the
     * persistence of the plot to the given number of points.  Calling
     * with a zero argument turns off this feature, reverting to
     * infinite memory (unless sweeps persistence is set).  If both
     * sweeps and points persistence are set then sweeps take
     * precedence.  This method should be called after
     * <code>setNumSets()</code>.  
     * FIXME: No file format yet.
     */
    public void setPointsPersistence (int persistence) {
        _pointsPersistence = persistence;
        if (persistence > 0) {
            for (int i = 0; i < _numsets; i++) {
                _points[i].setSize(persistence);
            }
        }
    }
    
    /** 
     * A sweep is a sequence of points where the value of X is
     * increasing.  A point that is added with a smaller x than the
     * previous point increments the sweep count.  Calling this method
     * with a non-zero argument sets the persistence of the plot to
     * the given number of sweeps.  Calling with a zero argument turns
     * off this feature.  If both sweeps and points persistence are
     * set then sweeps take precedence.
     * FIXME: No file format yet.
     * FIXME: Not implemented yet.
     */
    public void setSweepsPersistence (int persistence) {
        _sweepsPersistence = persistence;
    }

    //////////////////////////////////////////////////////////////////////////
    ////                          protected methods                       ////
  
        
    /**
     * Draw bar from the specified point to the y axis.
     * If the specified point is below the y axis or outside the
     * x range, do nothing.  If the <i>clip</i> argument is true,
     * then do not draw above the y range.
     */
    protected void _drawBar (Graphics graphics, int dataset,
            long xpos, long ypos, boolean clip) {
        if (clip) {
            if (ypos < _uly) {
                ypos = _uly;
            } if (ypos > _lry) {
                ypos = _lry;
            }
        }
        if (ypos <= _lry && xpos <= _lrx && xpos >= _ulx) {
            // left x position of bar.
            int barlx = (int)(xpos - _barwidth * _xscale/2 +
                    (_currentdataset - dataset - 1) *
                    _baroffset * _xscale);
            // right x position of bar
            int barrx = (int)(barlx + _barwidth * _xscale);
            if (barlx < _ulx) barlx = _ulx;
            if (barrx > _lrx) barrx = _lrx;
            // Make sure that a bar is always at least one pixel wide.
            if (barlx >= barrx) barrx = barlx+1;
            // The y position of the zero line.
            long zeroypos = _lry - (long) ((0-_yMin) * _yscale);
            if (_lry < zeroypos) zeroypos = _lry;
            if (_uly > zeroypos) zeroypos = _uly;


            if (_yMin >= 0 || ypos <= zeroypos) {
                graphics.fillRect(barlx, (int)ypos,
                        barrx - barlx, (int)(zeroypos - ypos));
            } else {
                graphics.fillRect(barlx, (int)zeroypos,
                        barrx - barlx, (int)(ypos - zeroypos));
            }
        }
    }

    /**
     * Draw an impulse from the specified point to the y axis.
     * If the specified point is below the y axis or outside the
     * x range, do nothing.  If the <i>clip</i> argument is true,
     * then do not draw above the y range.
     */
    protected void _drawImpulse (Graphics graphics,
            long xpos, long ypos, boolean clip) {
        if (clip) {
            if (ypos < _uly) {
                ypos = _uly;
            } if (ypos > _lry) {
                ypos = _lry;
            }
        }
        if (ypos <= _lry && xpos <= _lrx && xpos >= _ulx) {
            // The y position of the zero line.
            double zeroypos = _lry - (long) ((0-_yMin) * _yscale);
            if (_lry < zeroypos) zeroypos = _lry;
            if (_uly > zeroypos) zeroypos = _uly;
            graphics.drawLine((int)xpos, (int)ypos, (int)xpos,
                    (int)zeroypos);
        }
    }

    /**
     * Draw a line from the specified starting point to the specified
     * ending point.  The current color is used.  If the <i>clip</i> argument
     * is true, then draw only that portion of the line that lies within the
     * plotting rectangle.
     */
    protected void _drawLine (Graphics graphics,
            int dataset, long startx, long starty, long endx, long endy,
            boolean clip) {

        if (clip) {
            // Rule out impossible cases.
            if (!((endx <= _ulx && startx <= _ulx) ||
                    (endx >= _lrx && startx >= _lrx) ||
                    (endy <= _uly && starty <= _uly) ||
                    (endy >= _lry && starty >= _lry))) {
                // If the end point is out of x range, adjust
                // end point to boundary.
                // The integer arithmetic has to be done with longs so as
                // to not loose precision on extremely close zooms.
                if (startx != endx) {
                    if (endx < _ulx) {
                        endy = (int)(endy + ((long)(starty - endy) *
                                (_ulx - endx))/(startx - endx));
                        endx = _ulx;
                    } else if (endx > _lrx) {
                        endy = (int)(endy + ((long)(starty - endy) *
                                (_lrx - endx))/(startx - endx));
                        endx = _lrx;
                    }
                }
                    
                // If end point is out of y range, adjust to boundary.
                // Note that y increases downward
                if (starty != endy) {
                    if (endy < _uly) {
                        endx = (int)(endx + ((long)(startx - endx) *
                                (_uly - endy))/(starty - endy));
                        endy = _uly;
                    } else if (endy > _lry) {
                        endx = (int)(endx + ((long)(startx - endx) *
                                (_lry - endy))/(starty - endy));
                        endy = _lry;
                    }
                }
                    
                // Adjust current point to lie on the boundary.
                if (startx != endx) {
                    if (startx < _ulx) {
                        starty = (int)(starty + ((long)(endy - starty) *
                                (_ulx - startx))/(endx - startx));
                        startx = _ulx;
                    } else if (startx > _lrx) {
                        starty = (int)(starty + ((long)(endy - starty) *
                                (_lrx - startx))/(endx - startx));
                        startx = _lrx;
                    }
                }
                if (starty != endy) {
                    if (starty < _uly) {
                        startx = (int)(startx + ((long)(endx - startx) *
                                (_uly - starty))/(endy - starty));
                        starty = _uly;
                    } else if (starty > _lry) {
                        startx = (int)(startx + ((long)(endx - startx) *
                                (_lry - starty))/(endy - starty));
                        starty = _lry;
                    }
                }
            }
                 
            // Are the new points in range?
            if (endx >= _ulx && endx <= _lrx &&
                    endy >= _uly && endy <= _lry &&
                    startx >= _ulx && startx <= _lrx &&
                    starty >= _uly && starty <= _lry) {
                graphics.drawLine((int)startx, (int)starty,
                        (int)endx, (int)endy);
            }
        } else {
            // draw unconditionally.
            graphics.drawLine((int)startx, (int)starty,
                    (int)endx, (int)endy);
        }
    }

    /**
     * Put a mark corresponding to the specified dataset at the
     * specified x and y position. The mark is drawn in the current
     * color. What kind of mark is drawn depends on the _marks
     * variable and the dataset argument. If the fourth argument is
     * true, then check the range and plot only points that
     * are in range.
     */
    protected void _drawPoint(Graphics graphics,
            int dataset, long xpos, long ypos,
            boolean clip) {

        // If the point is not out of range, draw it.
        if (!clip || (ypos <= _lry && ypos >= _uly &&
                xpos <= _lrx && xpos >= _ulx)) {
            int xposi = (int)xpos;
            int yposi = (int)ypos;
            switch (_marks) {
            case 0:
                // If no mark style is given, draw a filled rectangle.
                // This is used, for example, to draw the legend.
                graphics.fillRect(xposi-6, yposi-6, 6, 6);
                break;
            case 1:
                // points -- use 3-pixel ovals.
                graphics.fillOval(xposi-1, yposi-1, 3, 3);
                break;
            case 2:
                // dots
                graphics.fillOval(xposi-_radius, yposi-_radius,
                        _diameter, _diameter); 
                break;
            case 3:
                // marks
                int xpoints[], ypoints[];
                // Points are only distinguished up to _MAX_MARKS data sets.
                int mark = dataset % _MAX_MARKS;
                switch (mark) {
                case 0:
                    // filled circle
                    graphics.fillOval(xposi-_radius, yposi-_radius,
                            _diameter, _diameter); 
                    break;
                case 1:
                    // cross
                    graphics.drawLine(xposi-_radius, yposi-_radius,
                            xposi+_radius, yposi+_radius); 
                    graphics.drawLine(xposi+_radius, yposi-_radius,
                            xposi-_radius, yposi+_radius); 
                    break;
                case 2:
                    // square
                    graphics.drawRect(xposi-_radius, yposi-_radius,
                            _diameter, _diameter); 
                    break;
                case 3:
                    // filled triangle
                    xpoints = new int[4];
                    ypoints = new int[4];
                    xpoints[0] = xposi; ypoints[0] = yposi-_radius;
                    xpoints[1] = xposi+_radius; ypoints[1] = yposi+_radius;
                    xpoints[2] = xposi-_radius; ypoints[2] = yposi+_radius;
                    xpoints[3] = xposi; ypoints[3] = yposi-_radius;
                    graphics.fillPolygon(xpoints, ypoints, 4);
                    break;
                case 4:
                    // diamond
                    xpoints = new int[5];
                    ypoints = new int[5];
                    xpoints[0] = xposi; ypoints[0] = yposi-_radius;
                    xpoints[1] = xposi+_radius; ypoints[1] = yposi;
                    xpoints[2] = xposi; ypoints[2] = yposi+_radius;
                    xpoints[3] = xposi-_radius; ypoints[3] = yposi;
                    xpoints[4] = xposi; ypoints[4] = yposi-_radius;
                    graphics.drawPolygon(xpoints, ypoints, 5);
                    break;
                case 5:
                    // circle
                    graphics.drawOval(xposi-_radius, yposi-_radius,
                            _diameter, _diameter); 
                    break;
                case 6:
                    // plus sign
                    graphics.drawLine(xposi, yposi-_radius, xposi,
                            yposi+_radius); 
                    graphics.drawLine(xposi-_radius, yposi, xposi+_radius,
                            yposi); 
                    break;
                case 7:
                    // filled square
                    graphics.fillRect(xposi-_radius, yposi-_radius,
                            _diameter, _diameter); 
                    break;
                case 8:
                    // triangle
                    xpoints = new int[4];
                    ypoints = new int[4];
                    xpoints[0] = xposi; ypoints[0] = yposi-_radius;
                    xpoints[1] = xposi+_radius; ypoints[1] = yposi+_radius;
                    xpoints[2] = xposi-_radius; ypoints[2] = yposi+_radius;
                    xpoints[3] = xposi; ypoints[3] = yposi-_radius;
                    graphics.drawPolygon(xpoints, ypoints, 4);
                    break;
                case 9:
                    // filled diamond
                    xpoints = new int[5];
                    ypoints = new int[5];
                    xpoints[0] = xposi; ypoints[0] = yposi-_radius;
                    xpoints[1] = xposi+_radius; ypoints[1] = yposi;
                    xpoints[2] = xposi; ypoints[2] = yposi+_radius;
                    xpoints[3] = xposi-_radius; ypoints[3] = yposi;
                    xpoints[4] = xposi; ypoints[4] = yposi-_radius;
                    graphics.fillPolygon(xpoints, ypoints, 5);
                    break;
                }
                break;
            default:
                // none
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////
    ////                       protected variables                        ////
    
    // The current dataset
    protected int _currentdataset = -1;
    
    // A vector of datasets.
    protected Vector[] _points;

    // An indicator of the marks style.  See _parseLine method for
    // interpretation.
    protected int _marks;
    
    protected int _numsets = _MAX_DATASETS;

    //////////////////////////////////////////////////////////////////////////
    ////                       private methods                            ////

    /* Add a legend if necessary, return the value of the connected flag.
     */
    private boolean _addLegendIfNecessary(boolean connected) {
        if (! _sawfirstdataset  || _currentdataset < 0) {
            // We did not set a DataSet line, but
            // we did get called with -<digit> args
            _sawfirstdataset = true;
            ++_currentdataset;
        }
        if (getLegend(_currentdataset) == null) {
            // We did not see a "DataSet" string yet,
            // nor did we call addLegend().
            _firstinset = true;
            _sawfirstdataset = true;
            addLegend(_currentdataset,
                    new String("Set "+ _currentdataset));
        }
        if (_firstinset) {
            connected = false;
            _firstinset = false;
        }
        return connected;
    }
    
    /* In the specified data set, add the specified x,y point to the
     * plot.  Data set indices begin with zero.  If the dataset
     * argument is out of range, ignore.  The number of data sets is
     * given by calling *setNumSets()*.  The fourth argument indicates
     * whether the point should be connected by a line to the previous
     * point.
     */
    private synchronized void _addPoint(
            int dataset, double x, double y,
            boolean connected) {
        if (dataset >= _numsets || dataset < 0) return;
        
        // For auto-ranging, keep track of min and max.
        if (x < _xBottom) _xBottom = x;
        if (x > _xTop) _xTop = x;
        if (y < _yBottom) _yBottom = y;
        if (y > _yTop) _yTop = y;

        // FIXME: Ignoring sweeps for now.
        PlotPoint pt = new PlotPoint();
        pt.x = x;
        pt.y = y;
        pt.connected = connected;
        Vector pts = _points[dataset];
        pts.addElement(pt);
        if (_pointsPersistence > 0) {
            if (pts.size() > _pointsPersistence) erasePoint(dataset,0);
        }
    }

    /* Draw the specified point and associated lines, if any.
     */
    private synchronized void _drawPlotPoint(Graphics graphics,
            int dataset, int index) {
        // Set the color
        if (_pointsPersistence > 0) {
            // To allow erasing to work by just redrawing the points.
            graphics.setXORMode(_background);
        }

        if (_usecolor) {
            int color = dataset % _colors.length;
            graphics.setColor(_colors[color]);
        } else {
            graphics.setColor(_foreground);
        }

        Vector pts = _points[dataset];
        PlotPoint pt = (PlotPoint)pts.elementAt(index);
        // Use long here because these numbers can be quite large
        // (when we are zoomed out a lot).
        long ypos = _lry - (long) ((pt.y - _yMin) * _yscale);
        long xpos = _ulx + (long) ((pt.x - _xMin) * _xscale);

        // Draw the line to the previous point.
        if (pt.connected) _drawLine(graphics, dataset, xpos, ypos,
                _prevx[dataset], _prevy[dataset], true);

        // Save the current point as the "previous" point for future
        // line drawing.
        _prevx[dataset] = xpos;
        _prevy[dataset] = ypos;

        // Draw the point & associated decorations, if appropriate.
        if (_marks != 0) _drawPoint(graphics, dataset, xpos, ypos, true);
        if (_impulses) _drawImpulse(graphics, xpos, ypos, true);
        if (_bars) _drawBar(graphics, dataset, xpos, ypos, true);

        // Restore the color, in case the box gets redrawn.
        graphics.setColor(_foreground);
        if (_pointsPersistence > 0) {
            // Restore paint mode in case axes get redrawn.
            graphics.setPaintMode();
        }
    }
    
    /** 
     * Erase the point at the given index in the given dataset.  If
     * lines are being drawn, also erase the line to the next points
     * (note: not to the previous point).
     */
    private synchronized void _erasePoint(
            int dataset, int index) {
        // Set the color
        Vector pts = _points[dataset];
        PlotPoint pt = (PlotPoint)pts.elementAt(index);
        long ypos = _lry - (long) ((pt.y - _yMin) * _yscale);
        long xpos = _ulx + (long) ((pt.x - _xMin) * _xscale);

        // Erase line to the next point, if appropriate.
        if (index < pts.size() - 1) {
            PlotPoint nextp = (PlotPoint)pts.elementAt(index+1);
            int nextx = _ulx + (int) ((nextp.x - _xMin) * _xscale);
            int nexty = _lry - (int) ((nextp.y - _yMin) * _yscale);
            nextp.connected = false;
        }
        pts.removeElementAt(index);
    }

    //////////////////////////////////////////////////////////////////////////
    ////                       private variables                          ////
    
    private int _pointsPersistence = 0;
    private int _sweepsPersistence = 0;
    private boolean _bars = false;
    private double _barwidth = 0.5;
    private double _baroffset = 0.05;
    private boolean _connected = true;
    private boolean _impulses = false;
    private boolean _firstinset = true; // Is this the first datapoint in a set
    private int _filecount = 0;         // Number of files read in.
    // Have we seen a DataSet line in the current data file?
    private boolean _sawfirstdataset = false;
    
    // Give both radius and diameter of a point for efficiency.
    private int _radius = 3;
    private int _diameter = 6;
    
    private Vector _dataurls = null;

    // Information about the previously plotted point.
    private long _prevx[], _prevy[];

    // Maximum number of _datasets.
    private static final int _MAX_DATASETS = 63;

    // Maximum number of different marks
    // NOTE: There are 11 colors in the base class.  Combined with 10
    // marks, that makes 110 unique signal identities.
    private static final int _MAX_MARKS = 10;
}
/* Original copyright follows */
/* A signal plotter.

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
