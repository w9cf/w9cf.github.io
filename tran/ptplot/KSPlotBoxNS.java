package ptplot;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;
import java.awt.event.*;

public class KSPlotBoxNS extends Panel
// implements MouseListener,MouseMotionListener {
                                                {

    /**
     * Create the peer of the plot box.
     */
    public void addNotify() {
        // This method is called after our Panel is first created
        // but before it is actually displayed.
        super.addNotify();
        _measureFonts();
    }

    /** 
     * Add a legend (displayed at the upper right) for the specified
     * data set with the specified string.  Short strings generally
     * fit better than long strings.  You must call <code>init()</code>
     * before calling this method.
     */
    public void addLegend(int dataset, String legend) {
        _legendStrings.addElement(legend);
        _legendDatasets.addElement(new Integer(dataset));
    }
    
    /** 
     * Specify a tick mark for the X axis.  The label given is placed
     * on the axis at the position given by <i>position</i>. If this
     * is called once or more, automatic generation of tick marks is
     * disabled.  The tick mark will appear only if it is within the X
     * range.
     */
    public void addXTick (String label, double position) {
        if (_xticks == null) {
            _xticks = new Vector();
            _xticklabels = new Vector();
        }
        _xticks.addElement(new Double(position));
        _xticklabels.addElement(label);
    }
    
    /** 
     * Specify a tick mark for the Y axis.  The label given is placed
     * on the axis at the position given by <i>position</i>. If this
     * is called once or more, automatic generation of tick marks is
     * disabled.  The tick mark will appear only if it is within the Y
     * range.
     */
    public void addYTick (String label, double position) {
        if (_yticks == null) {
            _yticks = new Vector();
            _yticklabels = new Vector();
        }
        _yticks.addElement(new Double(position));
        _yticklabels.addElement(label);
    }
    
    /**
     * Draw the axes using the current range, label, and title information.
     * If the argument is true, clear the display before redrawing.
     */
    public synchronized void drawPlot(Graphics graphics, boolean clearfirst) {


        // Find the width and height of the total drawing area, and clear it.
//        Rectangle drawRect = getBounds();
        Rectangle drawRect = bounds();

//This royally screws up with swing set
//        graphics.setPaintMode(); 

        if (clearfirst) {
            // Clear all the way from the top so that we erase the title.
            // If we don't do this, then zooming in with the pxgraph
            // application ends up blurring the title.
            graphics.clearRect(0,0,drawRect.width, drawRect.height);
        }

        // Make sure we have an x and y range
        if (!_xRangeGiven) {
            if (_xBottom > _xTop) {
                // have nothing to go on.
                _setXRange(0,0);
            } else {
                _setXRange(_xBottom, _xTop);
            }
        }
        if (!_yRangeGiven) {
            if (_yBottom > _yTop) {
                // have nothing to go on.
                _setYRange(0,0);
            } else {
                _setYRange(_yBottom, _yTop);
            }
        }
         
        // Vertical space for title, if appropriate.
        // NOTE: We assume a one-line title.
        int titley = 0;
        int titlefontheight = _titleFontMetrics.getHeight();
        if (_title != null || _yExp != 0) {
            titley = titlefontheight + _topPadding;
        }
        
        // Number of vertical tick marks depends on the height of the font
        // for labeling ticks and the height of the window.
        graphics.setFont(_labelfont);
        int labelheight = _labelFontMetrics.getHeight();
        int halflabelheight = labelheight/2;

        // Draw scaling annotation for x axis.
        // NOTE: 5 pixel padding on bottom.
        int ySPos = drawRect.height - 5;
        if (_xExp != 0 && _xticks == null) {
            int xSPos = drawRect.width - _rightPadding;
            String superscript = Integer.toString(_xExp);
            xSPos -= _superscriptFontMetrics.stringWidth(superscript);
            graphics.setFont(_superscriptfont);
            graphics.drawString(superscript, xSPos, ySPos - halflabelheight);
            xSPos -= _labelFontMetrics.stringWidth("x10");
            graphics.setFont(_labelfont);
            graphics.drawString("x10", xSPos, ySPos);
            // NOTE: 5 pixel padding on bottom
            _bottomPadding = (3 * labelheight)/2 + 5;
        }
        
        // NOTE: 5 pixel padding on the bottom.
        if (_xlabel != null && _bottomPadding < labelheight + 5) {
            _bottomPadding = titlefontheight + 5;
        }
        
        // Compute the space needed around the plot, starting with vertical.
        // NOTE: padding of 5 pixels below title.
        _uly = titley + 5;
        // NOTE: 3 pixels above bottom labels.
        _lry = drawRect.height-labelheight-_bottomPadding-3; 
        int height = _lry-_uly;
        _yscale = height/(_yMax - _yMin);
        _ytickscale = height/(_ytickMax - _ytickMin);

        ///////////////////// vertical axis

        // Number of y tick marks.
        // NOTE: subjective spacing factor.
        int ny = 2 + height/(labelheight+10);
        // Compute y increment.
        double yStep=_roundUp((_ytickMax-_ytickMin)/(double)ny);
        
        // Compute y starting point so it is a multiple of yStep.
        double yStart=yStep*Math.ceil(_ytickMin/yStep);
        
        // NOTE: Following disables first tick.  Not a good idea?
        // if (yStart == _ytickMin) yStart+=yStep;
        
        // Define the strings that will label the y axis.
        // Meanwhile, find the width of the widest label.
        // The labels are quantized so that they don't have excess resolution.
        int widesty = 0;

        // These do not get used unless ticks are automatic, but the
        // compiler is not smart enough to allow us to reference them
        // in two distinct conditional clauses unless they are
        // allocated outside the clauses.
        String ylabels[] = new String[ny];
        int ylabwidth[] = new int[ny];

        int ind = 0;
        if (_yticks == null) {
            // automatic ticks
            // First, figure out how many digits after the decimal point
            // will be used.
            int numfracdigits = _numFracDigits(yStep);

            for (double ypos=yStart; ypos <= _ytickMax; ypos += yStep) {
                // Prevent out of bounds exceptions
                if (ind >= ny) break;
                String yl = _formatNum(ypos, numfracdigits);
                ylabels[ind] = yl;
                int lw = _labelFontMetrics.stringWidth(yl);
                ylabwidth[ind++] = lw;
                if (lw > widesty) {widesty = lw;}
            }
        } else {
            // explictly specified ticks
            Enumeration nl = _yticklabels.elements();
            while (nl.hasMoreElements()) {
                String label = (String) nl.nextElement();
                int lw = _labelFontMetrics.stringWidth(label);
                if (lw > widesty) {widesty = lw;}
            }            
        }

        // Next we do the horizontal spacing.
        if (_ylabel != null) {
            _ulx = widesty + _labelFontMetrics.stringWidth("W") + _leftPadding;
        } else {     
            _ulx = widesty + _leftPadding;
        }
        int legendwidth = _drawLegend(graphics,
                drawRect.width-_rightPadding, _uly);
        _lrx = drawRect.width-legendwidth-_rightPadding;
        int width = _lrx-_ulx;
        _xscale = width/(_xMax - _xMin);
        _xtickscale = width/(_xtickMax - _xtickMin);
        
        if (_background == null) {
                throw new Error("PlotBox.drawPlot(): _background == null\n" +
                        "Be sure to call init() before calling paint().");
        }

        // background for the plotting rectangle
        graphics.setColor(_background);
        graphics.fillRect(_ulx,_uly,width,height);

        graphics.setColor(_foreground);
        graphics.drawRect(_ulx,_uly,width,height);
        
        // NOTE: subjective tick length.
        int tickLength = 5;
        int xCoord1 = _ulx+tickLength;
        int xCoord2 = _lrx-tickLength;
        
        if (_yticks == null) {
            // auto-ticks
            ind = 0;
            for (double ypos=yStart; ypos <= _ytickMax; ypos += yStep) {
                // Prevent out of bounds exceptions
                if (ind >= ny) break;
                int yCoord1 = _lry - (int)((ypos-_ytickMin)*_ytickscale);
                // The lowest label is shifted up slightly to avoid
                // colliding with x labels.
                int offset = 0;
                if (ind > 0) offset = halflabelheight;
                graphics.drawLine(_ulx,yCoord1,xCoord1,yCoord1);
                graphics.drawLine(_lrx,yCoord1,xCoord2,yCoord1);
                if (_grid && yCoord1 != _uly && yCoord1 != _lry) {
                    graphics.setColor(Color.lightGray);
                    graphics.drawLine(xCoord1,yCoord1,xCoord2,yCoord1);
                    graphics.setColor(_foreground);
                }
                // NOTE: 4 pixel spacing between axis and labels.
                graphics.drawString(ylabels[ind],
                        _ulx-ylabwidth[ind++]-4, yCoord1+offset);
            }
        
            // Draw scaling annotation for y axis.
            if (_yExp != 0) {
                graphics.drawString("x10", 2, titley);
                graphics.setFont(_superscriptfont);
                graphics.drawString(Integer.toString(_yExp),
                        _labelFontMetrics.stringWidth("x10") + 2, 
                        titley-halflabelheight);
                graphics.setFont(_labelfont);
            }
        } else {
            // ticks have been explicitly specified
            Enumeration nt = _yticks.elements();
            Enumeration nl = _yticklabels.elements();
            while (nl.hasMoreElements()) {
                String label = (String) nl.nextElement();
                double ypos = ((Double)(nt.nextElement())).doubleValue();
                if (ypos > _yMax || ypos < _yMin) continue;
                int yCoord1 = _lry - (int)((ypos-_yMin)*_yscale);
                int offset = 0;
                if (ypos < _lry - labelheight) offset = halflabelheight;
                graphics.drawLine(_ulx,yCoord1,xCoord1,yCoord1);
                graphics.drawLine(_lrx,yCoord1,xCoord2,yCoord1);
                if (_grid && yCoord1 != _uly && yCoord1 != _lry) {
                    graphics.setColor(Color.lightGray);
                    graphics.drawLine(xCoord1,yCoord1,xCoord2,yCoord1);
                    graphics.setColor(_foreground);
                }
                // NOTE: 3 pixel spacing between axis and labels.
                graphics.drawString(label,
                        _ulx - _labelFontMetrics.stringWidth(label) - 3,
                        yCoord1+offset);
            }
        }
        
        ///////////////////// horizontal axis

        int yCoord1 = _uly+tickLength;
        int yCoord2 = _lry-tickLength;
        if (_xticks == null) {
            // auto-ticks

            // Number of x tick marks. 
            // Need to start with a guess and converge on a solution here.
            int nx = 10;
            double xStep = 0.0;
            int numfracdigits = 0;
            int charwidth = _labelFontMetrics.stringWidth("8");
            // Limit to 10 iterations
            int count = 0;
            while (count++ <= 10) {
                xStep=_roundUp((_xtickMax-_xtickMin)/(double)nx);
                // Compute the width of a label for this xStep
                numfracdigits = _numFracDigits(xStep);
                // Number of integer digits is the maximum of the two endpoints
                int intdigits = _numIntDigits(_xtickMax);
                int inttemp = _numIntDigits(_xtickMin);
                if (intdigits < inttemp) {
                    intdigits = inttemp;
                }
                // Allow two extra digits (decimal point and sign).
                int maxlabelwidth = charwidth *
                    (numfracdigits + 2 + intdigits);
                // Compute new estimate of number of ticks.
                int savenx = nx;
                // NOTE: 10 additional pixels between labels.
                // NOTE: Try to ensure at least two tick marks.
                nx = 2 + width/(maxlabelwidth+10);
                if (nx - savenx <= 1 || savenx - nx <= 1) break;
            }
            xStep=_roundUp((_xtickMax-_xtickMin)/(double)nx);
            numfracdigits = _numFracDigits(xStep);

            // Compute x starting point so it is a multiple of xStep.
            double xStart=xStep*Math.ceil(_xtickMin/xStep);
        
            // NOTE: Following disables first tick.  Not a good idea?
            // if (xStart == _xMin) xStart+=xStep;
        
            // Label the x axis.  The labels are quantized so that
            // they don't have excess resolution.
            for (double xpos=xStart; xpos <= _xtickMax; xpos += xStep) {
                String xticklabel = _formatNum(xpos, numfracdigits);
                xCoord1 = _ulx + (int)((xpos-_xtickMin)*_xtickscale);
                graphics.drawLine(xCoord1,_uly,xCoord1,yCoord1);
                graphics.drawLine(xCoord1,_lry,xCoord1,yCoord2);
                if (_grid && xCoord1 != _ulx && xCoord1 != _lrx) {
                    graphics.setColor(Color.lightGray);
                    graphics.drawLine(xCoord1,yCoord1,xCoord1,yCoord2);
                    graphics.setColor(_foreground);
                }
                int labxpos = xCoord1 -
                    _labelFontMetrics.stringWidth(xticklabel)/2;
                // NOTE: 3 pixel spacing between axis and labels.
                graphics.drawString(xticklabel, labxpos,
                        _lry + 3 + labelheight);
            }
        } else {
            // ticks have been explicitly specified
            Enumeration nt = _xticks.elements();
            Enumeration nl = _xticklabels.elements();
            while (nl.hasMoreElements()) {
                String label = (String) nl.nextElement();
                double xpos = ((Double)(nt.nextElement())).doubleValue();
                if (xpos > _xMax || xpos < _xMin) continue;
                xCoord1 = _ulx + (int)((xpos-_xMin)*_xscale);
                graphics.drawLine(xCoord1,_uly,xCoord1,yCoord1);
                graphics.drawLine(xCoord1,_lry,xCoord1,yCoord2);
                if (_grid && xCoord1 != _ulx && xCoord1 != _lrx) {
                    graphics.setColor(Color.lightGray);
                    graphics.drawLine(xCoord1,yCoord1,xCoord1,yCoord2);
                    graphics.setColor(_foreground);
                }
                int labxpos = xCoord1 - _labelFontMetrics.stringWidth(label)/2;
                // NOTE: 3 pixel spacing between axis and labels.
                graphics.drawString(label, labxpos, _lry + 3 + labelheight);
            }
        }
        
        ///////////////////// Draw title and axis labels now.
        
        // Center the title and X label over the plotting region, not
        // the window.
        graphics.setColor(_foreground);
        
        if (_title != null) {
            graphics.setFont(_titlefont);
            int titlex = _ulx +
                (width - _titleFontMetrics.stringWidth(_title))/2;
            graphics.drawString(_title,titlex,titley);
        }
        
        graphics.setFont(_labelfont);
        if (_xlabel != null) {
            int labelx = _ulx +
                (width - _labelFontMetrics.stringWidth(_xlabel))/2;
            graphics.drawString(_xlabel,labelx,ySPos);
        }
        
        int charcenter = 2 + _labelFontMetrics.stringWidth("W")/2;
        int charheight = labelheight;
        if (_ylabel != null) {
            // Vertical label is fairly complex to draw.
            int yl = _ylabel.length();
            int starty = _uly + (_lry-_uly)/2 - yl*charheight/2 + charheight;
            for (int i = 0; i < yl; i++) {
                String nchar = _ylabel.substring(i,i+1);
                int cwidth = _labelFontMetrics.stringWidth(nchar);
                graphics.drawString(nchar,charcenter - cwidth/2, starty);
                starty += charheight;
            }
        }
    }
    

    /**
     * Rescales so that the data that is currently plotted just fits.
     */
    public synchronized void fillPlot () {
        setXRange(_xBottom, _xTop);
        setYRange(_yBottom, _yTop);
        repaint();
    }

    /**
     * Get the Font by name.  
     * @deprecated: As of JDK1.1, use Font.decode() instead.
     * We need to compile under JDK1.0.2, so we use this method.
     */
    public Font getFontByName(String fullfontname) {
        // Can't use Font.decode() here, it is not present in jdk1.0.2
        //_labelfont = Font.decode(fullfontname);

        String fontname = new String ("helvetica");
        int style = Font.PLAIN;
        int size = 12;
        StringTokenizer stoken = new StringTokenizer(fullfontname,"-");
        
        if (stoken.hasMoreTokens()) {
            fontname = stoken.nextToken();
        }
        if (stoken.hasMoreTokens()) {
            String stylename = stoken.nextToken();
            // FIXME: we need to be able to mix and match these
            if (stylename.equals("PLAIN")) {
                style = Font.PLAIN;
            } else if (stylename.equals("BOLD")) {
                style = Font.BOLD;
            } else if (stylename.equals("ITALIC")) {
                style = Font.ITALIC;
            } else {
                // Perhaps this is a font size?
                try {
                    size = Integer.valueOf(stylename).intValue();
                } catch (NumberFormatException e) {}
            }
        }
        if (stoken.hasMoreTokens()) {
            try {
                size = Integer.valueOf(stoken.nextToken()).intValue();
            } catch (NumberFormatException e) {}
        }
        return new Font(fontname, style, size);
    }

    /** 
     * Get the legend for a dataset.
     */
    public String getLegend(int dataset) {
        int idx = _legendDatasets.indexOf(new Integer(dataset),0);
        if (idx != -1) {
            return (String)_legendStrings.elementAt(idx);
        } else {
            return null;
        }
    }
      
    /** Get the minimum size of this component.
     */
    public Dimension getMinimumSize() {
        return new Dimension(_width, _height);
    }


    /** Get the preferred size of this component.
     */
    public Dimension getPreferredSize() {
       return new Dimension(_width, _height);
    }

    public Dimension preferredSize() {
       return getPreferredSize();
    }


    /**
     * Set the starting point for an interactive zoom box.
     */
    public void mousePressed(MouseEvent mev) {
       int x = mev.getX();
       int y = mev.getY();
       // constrain to be in range
       if (y > _lry) y=_lry;
       if (y < _uly) y=_uly;
       if (x > _lrx) x=_lrx;
       if (x < _ulx) x=_ulx;
       _zoomx = x;
       _zoomy = y;
    }
    
    /**
     * Draw a box for an interactive zoom box.
     * Return a boolean indicating whether or not we have dealt with
     * the event.
     * @deprecated As of JDK1.1 in java.awt.component 
     * but we need to compile under 1.0.2 for netscape3.x compatibility.
     */
    public synchronized void mouseDragged(MouseEvent mev) {
       int x = mev.getX();
       int y = mev.getY();
        // We make this method synchronized so that we can draw the drag
        // box properly.  If this method is not synchronized, then
        // we could end up calling setXORMode, being interrupted
        // and having setPaintMode() called in another method.

        Graphics g = getGraphics();
        // Bound the rectangle so it doesn't go outside the box.
        if (y > _lry) y=_lry;
        if (y < _uly) y=_uly;
        if (x > _lrx) x=_lrx;
        if (x < _ulx) x=_ulx;
        // erase previous rectangle, if there was one.
        if ((_zoomx != -1 || _zoomy != -1)) {
            // Ability to zoom out added by William Wu.
            // If we are not already zooming, figure out whether we
            // are zooming in or out.
            if (_zoomin == false && _zoomout == false){
                if (y < _zoomy) {
                    _zoomout = true;
                    // Draw reference box.
                    g.drawRect(_zoomx-15, _zoomy-15, 30, 30);

                } else if (y > _zoomy) {
                    _zoomin = true; 
                }
            }

            if (_zoomin == true){   
                g.setXORMode(_background);
                // Erase the previous box if necessary.
                if ((_zoomxn != -1 || _zoomyn != -1) && (_drawn == true)) {
                    int minx = Math.min(_zoomx, _zoomxn);
                    int maxx = Math.max(_zoomx, _zoomxn);
                    int miny = Math.min(_zoomy, _zoomyn);
                    int maxy = Math.max(_zoomy, _zoomyn);
                    g.drawRect(minx, miny, maxx - minx, maxy - miny);
                }
                // Draw a new box if necessary.
                if (y > _zoomy) {
                    _zoomxn = x;
                      _zoomyn = y;
                    int minx = Math.min(_zoomx, _zoomxn);
                    int maxx = Math.max(_zoomx, _zoomxn);
                    int miny = Math.min(_zoomy, _zoomyn);
                    int maxy = Math.max(_zoomy, _zoomyn);
                    g.drawRect(minx, miny, maxx - minx, maxy - miny);
                    g.setPaintMode();
                    _drawn = true;
                    return;
                } else _drawn = false;
            } else if (_zoomout == true){
                g.setXORMode(_background);
                // Erase previous box if necessary.
                if ((_zoomxn != -1 || _zoomyn != -1) && (_drawn == true)) {
                    int x_diff = Math.abs(_zoomx-_zoomxn);
                    int y_diff = Math.abs(_zoomy-_zoomyn);
                    g.drawRect(_zoomx-15-x_diff, _zoomy-15-y_diff,
                            30+x_diff*2, 30+y_diff*2);
                }
                if (y < _zoomy){
                    _zoomxn = x;
                    _zoomyn = y;     
                    int x_diff = Math.abs(_zoomx-_zoomxn);
                    int y_diff = Math.abs(_zoomy-_zoomyn);
                    g.drawRect(_zoomx-15-x_diff, _zoomy-15-y_diff,
                            30+x_diff*2, 30+y_diff*2);
                    g.setPaintMode();
                    _drawn = true;
                    return;
                } else _drawn = false;
            }
        }
        g.setPaintMode();
    }
    public void mouseMoved(MouseEvent mev) { }
    

    /**
     * Zoom in or out based on the box that has been drawn.
     */
   public synchronized void mouseReleased(MouseEvent mev) {
      int x = mev.getX();
      int y = mev.getY();
        // We make this method synchronized so that we can draw the drag
        // box properly.  If this method is not synchronized, then
        // we could end up calling setXORMode, being interrupted
        // and having setPaintMode() called in another method.

        Graphics g = getGraphics();
        boolean handled = false;
        if ((_zoomin == true) && (_drawn == true)){  
            if (_zoomxn != -1 || _zoomyn != -1) {
                // erase previous rectangle.
                int minx = Math.min(_zoomx, _zoomxn);
                int maxx = Math.max(_zoomx, _zoomxn);
                int miny = Math.min(_zoomy, _zoomyn);
                int maxy = Math.max(_zoomy, _zoomyn);
                g.setXORMode(_background);
                g.drawRect(minx, miny, maxx - minx, maxy - miny);
                g.setPaintMode();
                // constrain to be in range
                if (y > _lry) y=_lry;
                if (y < _uly) y=_uly;
                if (x > _lrx) x=_lrx;
                if (x < _ulx) x=_ulx;
                // NOTE: ignore if total drag less than 5 pixels.
                if ((Math.abs(_zoomx-x) > 5) && (Math.abs(_zoomy-y) > 5)) {
                    double a = _xMin + (_zoomx - _ulx)/_xscale;
                    double b = _xMin + (x - _ulx)/_xscale;
                    if (a < b) setXRange(a, b);
                    else setXRange(b, a);
                    a = _yMax - (_zoomy - _uly)/_yscale;
                    b = _yMax - (y - _uly)/_yscale;
                    if (a < b) setYRange(a, b);
                    else setYRange(b, a);
                }
                repaint();
                handled = true;
            }
        } else if ((_zoomout == true) && (_drawn == true)){
            // Erase previous rectangle.
            g.setXORMode(_background);
            int x_diff = Math.abs(_zoomx-_zoomxn);
            int y_diff = Math.abs(_zoomy-_zoomyn);
            g.drawRect(_zoomx-15-x_diff, _zoomy-15-y_diff,
                    30+x_diff*2, 30+y_diff*2);
            g.setPaintMode();

            // Calculate zoom factor.
            double a = (double)(Math.abs(_zoomx - x)) / 30.0;
            double b = (double)(Math.abs(_zoomy - y)) / 30.0;
            double newx1 = _xMax + (_xMax - _xMin) * a;
            double newx2 = _xMin - (_xMax - _xMin) * a;
            if (newx1 > _xTop) newx1 = _xTop; 
            if (newx2 < _xBottom) newx2 = _xBottom; 
            double newy1 = _yMax + (_yMax - _yMin) * b;
            double newy2 = _yMin - (_yMax - _yMin) * b;
            if (newy1 > _yTop) newy1 = _yTop; 
            if (newy2 < _yBottom) newy2 = _yBottom; 
            setXRange(newx2, newx1);
            setYRange(newy2, newy1);
            repaint();
            handled = true;
        } else if (_drawn == false){
            repaint();
            handled = true;
        }
        _drawn = false;
        _zoomin = _zoomout = false;
        _zoomxn = _zoomyn = _zoomx = _zoomy = -1;
    }

    public void mouseEntered(MouseEvent mev) { }
    public void mouseExited(MouseEvent mev) { }
    public void mouseClicked(MouseEvent mev) { }



    /** Initialize the component, creating the fill button and setting
     * the colors.
     */
    public void init() {
//        addMouseListener(this);
//        addMouseMotionListener(this);
        _xticks = null;
        _xticklabels = null;
        _yticks = null;
        _yticklabels = null;

        if (_foreground != null) {
            setForeground(_foreground);
        } else {
            _foreground = Color.black;
        }

        if (_background != null) {
            setBackground(_background);
        } else {
            _background = Color.white;
        }

        // Make a button that auto-scales the plot.
        // NOTE: The button infringes on the title space.
        // If more buttons are added, we may have to find some other place
        // for them, like below the legend, stacked vertically.
        setLayout(new FlowLayout(FlowLayout.RIGHT));
        _fillButton = new Button("fill");
        add(_fillButton);
        validate();
    }
    /* 
     * Handle button presses to fill the plot.  This rescales so that
     * the data that is currently plotted just fits.
     * @deprecated As of JDK1.1 in java.awt.component 
     * but we need to compile under 1.0.2 for netscape3.x compatibility.
     */
    public boolean action (Event evt, Object arg) {
        if (evt.target == _fillButton) {
            fillPlot();
            return true;
        } else {
            return super.action (evt, arg); // action() is deprecated in 1.1
            // but we need to compile under
            // jdk1.0.2 for netscape3.x
        }
    }


        
    /** The minimum size.
     * @deprecated As of JDK1.1 in java.awt.component, but we need 
     * to compile under 1.0.2 for netscape3.x compatibility.
     */
    public Dimension minimumSize() {
        return getMinimumSize();
    }

    /** 
     * Paint the component contents, which in this base class is
     * only the axes.
     */

    public void paintComponent(Graphics g) {
       update(g);
    }

    public void paint(Graphics g) {
       update(g);
    }

    public void update(Graphics g) {
       drawPlot(g,true);
    }

    /** Reshape
     */
//    public void setBounds(int x, int y, int width, int height) {
    public void reshape(int x, int y, int width, int height) {
        _width = width;
        _height = height;
//        super.setBounds(x,y,_width,_height);
        super.reshape(x,y,_width,_height);
    }

    /** 
     * Resize the plot.
     */
//    public void setSize(int width, int height) {
    public void resize(int width, int height) {
        _width = width;
        _height = height;
//        super.setSize(width,height);
        super.resize(width,height);
    }

    /** Set the background color.
     */
    public void setBackground (Color background) {
        _background = background;
        super.setBackground(_background);
    }

    /** Set the foreground color.
     */
    public void setForeground (Color foreground) {
        _foreground = foreground;
        super.setForeground(_foreground);
    }

    /** Control whether the grid is drawn.
     */
    public void setGrid (boolean grid) {
        _grid = grid;
    }
    
    /** Set the label font, which is used for axis labels and legend labels.
     */
    public void setLabelFont (String fullfontname) {
        // Can't use Font.decode() here, it is not present in jdk1.0.2
        //_labelfont = Font.decode(fullfontname);

        _labelfont = getFontByName(fullfontname);
    }
    /**
     * Set the title of the graph.  The title will appear on the subsequent
     * call to <code>paint()</code> or <code>drawPlot()</code>.
     */
    public void setTitle (String title) {
        _title = title;
    }
    
    /** Set the title font.
     */
    public void setTitleFont (String fullfontname) {
        // Can't use Font.decode() here, it is not present in jdk1.0.2
        //_titlefont = Font.decode(fullfontname);

        _titlefont = getFontByName(fullfontname);
        _titleFontMetrics = getFontMetrics(_titlefont);
    }

    /** 
     * Set the label for the X (horizontal) axis.  The label will
     * appear on the subsequent call to <code>paint()</code> or
     * <code>drawPlot()</code>.
     */
    public void setXLabel (String label) {
        _xlabel = label;
    }

    /** 
     * Set the X (horizontal) range of the plot.  If this is not done
     * explicitly, then the range is computed automatically from data
     * available when <code>paint()</code> or <code>drawPlot()</code>
     * are called.  If min and max are identical, then the range is
     * arbitrarily spread by 1.
     */
    public void setXRange (double min, double max) {
        _setXRange(min,max);
        _xRangeGiven = true;
    }

    /** 
     * Set the label for the Y (vertical) axis.  The label will
     * appear on the subsequent call to <code>paint()</code> or
     * <code>drawPlot()</code>.
     */
    public void setYLabel (String label) {
        _ylabel = label;
    }

    /**
     * Set the Y (vertical) range of the plot.  If this is not done
     * explicitly, then the range is computed automatically from data
     * available when <code>paint()</code> or <code>drawPlot()</code>
     * are called.  If min and max are identical, then the range is
     * arbitrarily spread by 0.1.
     */
    public void setYRange (double min, double max) {
        _setYRange(min,max);
        _yRangeGiven = true;
    }


    /**
     * Set the starting point for an interactive zoom box.
     * @deprecated As of JDK1.1 in java.awt.component 
     * but we need to compile under 1.0.2 for netscape3.x compatibility.
     */
    public boolean mouseDown(Event evt, int x, int y) { // deprecated
        // constrain to be in range
        if (y > _lry) y=_lry;
        if (y < _uly) y=_uly;
        if (x > _lrx) x=_lrx;
        if (x < _ulx) x=_ulx;
        _zoomx = x;
        _zoomy = y;
        return true;
    }
    
    /**
     * Draw a box for an interactive zoom box.
     * Return a boolean indicating whether or not we have dealt with
     * the event.
     * @deprecated As of JDK1.1 in java.awt.component 
     * but we need to compile under 1.0.2 for netscape3.x compatibility.
     */
    public synchronized boolean mouseDrag(Event evt, int x, int y) {
        // We make this method synchronized so that we can draw the drag
        // box properly.  If this method is not synchronized, then
        // we could end up calling setXORMode, being interrupted
        // and having setPaintMode() called in another method.

        Graphics g = getGraphics();
        // Bound the rectangle so it doesn't go outside the box.
        if (y > _lry) y=_lry;
        if (y < _uly) y=_uly;
        if (x > _lrx) x=_lrx;
        if (x < _ulx) x=_ulx;
        // erase previous rectangle, if there was one.
        if ((_zoomx != -1 || _zoomy != -1)) {
            // Ability to zoom out added by William Wu.
            // If we are not already zooming, figure out whether we
            // are zooming in or out.
            if (_zoomin == false && _zoomout == false){
                if (y < _zoomy) {
                    _zoomout = true;
                    // Draw reference box.
                    g.drawRect(_zoomx-15, _zoomy-15, 30, 30);

                } else if (y > _zoomy) {
                    _zoomin = true; 
                }
            }

            if (_zoomin == true){   
                g.setXORMode(_background);
                // Erase the previous box if necessary.
                if ((_zoomxn != -1 || _zoomyn != -1) && (_drawn == true)) {
                    int minx = Math.min(_zoomx, _zoomxn);
                    int maxx = Math.max(_zoomx, _zoomxn);
                    int miny = Math.min(_zoomy, _zoomyn);
                    int maxy = Math.max(_zoomy, _zoomyn);
                    g.drawRect(minx, miny, maxx - minx, maxy - miny);
                }
                // Draw a new box if necessary.
                if (y > _zoomy) {
                    _zoomxn = x;
                      _zoomyn = y;
                    int minx = Math.min(_zoomx, _zoomxn);
                    int maxx = Math.max(_zoomx, _zoomxn);
                    int miny = Math.min(_zoomy, _zoomyn);
                    int maxy = Math.max(_zoomy, _zoomyn);
                    g.drawRect(minx, miny, maxx - minx, maxy - miny);
                    g.setPaintMode();
                    _drawn = true;
                    return true;
                } else _drawn = false;
            } else if (_zoomout == true){
                g.setXORMode(_background);
                // Erase previous box if necessary.
                if ((_zoomxn != -1 || _zoomyn != -1) && (_drawn == true)) {
                    int x_diff = Math.abs(_zoomx-_zoomxn);
                    int y_diff = Math.abs(_zoomy-_zoomyn);
                    g.drawRect(_zoomx-15-x_diff, _zoomy-15-y_diff,
                            30+x_diff*2, 30+y_diff*2);
                }
                if (y < _zoomy){
                    _zoomxn = x;
                    _zoomyn = y;     
                    int x_diff = Math.abs(_zoomx-_zoomxn);
                    int y_diff = Math.abs(_zoomy-_zoomyn);
                    g.drawRect(_zoomx-15-x_diff, _zoomy-15-y_diff,
                            30+x_diff*2, 30+y_diff*2);
                    g.setPaintMode();
                    _drawn = true;
                    return true;
                } else _drawn = false;
            }
        }
        g.setPaintMode();
        return false;
    }

    /**
     * Zoom in or out based on the box that has been drawn.
     * @deprecated As of JDK1.1 in java.awt.component 
     * but we need to compile under 1.0.2 for netscape3.x compatibility.
     */
    public synchronized boolean mouseUp(Event evt, int x, int y) { //deprecated
        // We make this method synchronized so that we can draw the drag
        // box properly.  If this method is not synchronized, then
        // we could end up calling setXORMode, being interrupted
        // and having setPaintMode() called in another method.

        Graphics g = getGraphics();
        boolean handled = false;
        if ((_zoomin == true) && (_drawn == true)){  
            if (_zoomxn != -1 || _zoomyn != -1) {
                // erase previous rectangle.
                int minx = Math.min(_zoomx, _zoomxn);
                int maxx = Math.max(_zoomx, _zoomxn);
                int miny = Math.min(_zoomy, _zoomyn);
                int maxy = Math.max(_zoomy, _zoomyn);
                g.setXORMode(_background);
                g.drawRect(minx, miny, maxx - minx, maxy - miny);
                g.setPaintMode();
                // constrain to be in range
                if (y > _lry) y=_lry;
                if (y < _uly) y=_uly;
                if (x > _lrx) x=_lrx;
                if (x < _ulx) x=_ulx;
                // NOTE: ignore if total drag less than 5 pixels.
                if ((Math.abs(_zoomx-x) > 5) && (Math.abs(_zoomy-y) > 5)) {
                    double a = _xMin + (_zoomx - _ulx)/_xscale;
                    double b = _xMin + (x - _ulx)/_xscale;
                    if (a < b) setXRange(a, b);
                    else setXRange(b, a);
                    a = _yMax - (_zoomy - _uly)/_yscale;
                    b = _yMax - (y - _uly)/_yscale;
                    if (a < b) setYRange(a, b);
                    else setYRange(b, a);
                }
                drawPlot(g, true);
                handled = true;
            }
        } else if ((_zoomout == true) && (_drawn == true)){
            // Erase previous rectangle.
            g.setXORMode(_background);
            int x_diff = Math.abs(_zoomx-_zoomxn);
            int y_diff = Math.abs(_zoomy-_zoomyn);
            g.drawRect(_zoomx-15-x_diff, _zoomy-15-y_diff,
                    30+x_diff*2, 30+y_diff*2);
            g.setPaintMode();

            // Calculate zoom factor.
            double a = (double)(Math.abs(_zoomx - x)) / 30.0;
            double b = (double)(Math.abs(_zoomy - y)) / 30.0;
            double newx1 = _xMax + (_xMax - _xMin) * a;
            double newx2 = _xMin - (_xMax - _xMin) * a;
            if (newx1 > _xTop) newx1 = _xTop; 
            if (newx2 < _xBottom) newx2 = _xBottom; 
            double newy1 = _yMax + (_yMax - _yMin) * b;
            double newy2 = _yMin - (_yMax - _yMin) * b;
            if (newy1 > _yTop) newy1 = _yTop; 
            if (newy2 < _yBottom) newy2 = _yBottom; 
            setXRange(newx2, newx1);
            setYRange(newy2, newy1);
            drawPlot(g, true);
            handled = true;
        } else if (_drawn == false){
            drawPlot(g, true);
            handled = true;
        }
        _drawn = false;
        _zoomin = _zoomout = false;
        _zoomxn = _zoomyn = _zoomx = _zoomy = -1;
        return handled;
    }


    //////////////////////////////////////////////////////////////////////////
    ////                         protected methods                        ////

    /**
     * Put a mark corresponding to the specified dataset at the
     * specified x and y position.   The mark is drawn in the
     * current color.  In this base class, a point is a
     * filled rectangle 6 pixels across.  Note that marks greater than
     * about 6 pixels in size will not look very good since they will
     * overlap axis labels and may not fit well in the legend.   The
     * <i>clip</i> argument, if <code>true</code>, states
     * that the point should not be drawn if
     * it is out of range.
     */
    protected void _drawPoint(Graphics graphics,
            int dataset, long xpos, long ypos, boolean clip) {
        boolean pointinside = ypos <= _lry && ypos >= _uly && 
            xpos <= _lrx && xpos >= _ulx;
        if (!pointinside && clip) {return;}
        graphics.fillRect((int)xpos-6, (int)ypos-6, 6, 6);
    }


    /** Set the visibility of the Fill button.
     */
    protected void _setButtonsVisibility(boolean vis) {
//       _fillButton.setVisible(vis);
       if (vis) {
          _fillButton.show();
       } else {
          _fillButton.hide();
       }
    }

    //////////////////////////////////////////////////////////////////////////
    ////                           protected variables                    ////
    
    // The range of the plot.
    protected double _yMax, _yMin, _xMax, _xMin;

    // Whether the ranges have been given.
    protected boolean _xRangeGiven = false;
    protected boolean _yRangeGiven = false;
    // The minimum and maximum values registered so far, for auto ranging.
    protected double _xBottom = Double.MAX_VALUE;
    protected double _xTop = - Double.MAX_VALUE;
    protected double _yBottom = Double.MAX_VALUE;
    protected double _yTop = - Double.MAX_VALUE;
    
    // Whether to draw a background grid.
    protected boolean _grid = true;
    
    // Color of the background, settable from HTML.
    protected Color _background = null;
    // Color of the foreground, settable from HTML.
    protected Color _foreground = null;

    // Derived classes can increment these to make space around the plot.
    protected int _topPadding = 10;
    protected int _bottomPadding = 5;
    protected int _rightPadding = 10;
    protected int _leftPadding = 10;

    // The plot rectangle in pixels.
    // The naming convention is: "_ulx" = "upper left x", where "x" is
    // the horizontal dimension.
    protected int _ulx, _uly, _lrx, _lry;

    // Scaling used in plotting points.
    protected double _yscale, _xscale;
    
    // Indicator whether to use _colors
    protected boolean _usecolor = true;

    // Default _colors, by data set.
    // There are 11 colors so that combined with the
    // 10 marks of the Plot class, we can distinguish 110
    // distinct data sets.
    static protected Color[] _colors = {
        new Color(0xff0000),   // red
        new Color(0x0000ff),   // blue
        new Color(0x14ff14),   // green-ish
        new Color(0x000000),   // black
        new Color(0xffa500),   // orange
        new Color(0x53868b),   // cadetblue4
        new Color(0xff7f50),   // coral
        new Color(0x55bb2f),   // dark green-ish
        new Color(0x90422d),   // sienna-ish
        new Color(0xa0a0a0),   // grey-ish
        new Color(0x00aaaa),   // cyan-ish
    };
        
    // Width and height of component in pixels.
    protected int _width = 400, _height = 400;

    //////////////////////////////////////////////////////////////////////////
    ////                         private methods                          ////

    /**
     * Draw the legend in the upper right corner and return the width
     * (in pixels)  used up.  The arguments give the upper right corner
     * of the region where the legend should be placed.
     */
    private int _drawLegend(Graphics graphics, int urx, int ury) {
        // FIXME: consolidate all these for efficiency
        graphics.setFont(_labelfont);
        FontMetrics _labelFontMetrics = graphics.getFontMetrics();
        int spacing = _labelFontMetrics.getHeight();

        Enumeration v = _legendStrings.elements();
        Enumeration i = _legendDatasets.elements();
        int ypos = ury + spacing;
        int maxwidth = 0;
        while (v.hasMoreElements()) {
            String legend = (String) v.nextElement();
            // NOTE: relies on _legendDatasets having the same num. of entries.
            int dataset = ((Integer) i.nextElement()).intValue();
            if (_usecolor) {
                // Points are only distinguished up to the number of colors.
                int color = dataset % _colors.length;
                graphics.setColor(_colors[color]);
            }
            _drawPoint(graphics, dataset, urx-3, ypos-3, false);

            graphics.setColor(_foreground);
            int width = _labelFontMetrics.stringWidth(legend);
            if (width > maxwidth) maxwidth = width;
            graphics.drawString(legend, urx - 15 - width, ypos);
            ypos += spacing;
        }
        return 22 + maxwidth;  // NOTE: subjective spacing parameter.
    }

    /*
     * Return a string for displaying the specified number
     * using the specified number of digits after the decimal point.
     * NOTE: This could be replaced by the NumberFormat class
     * which is available in jdk 1.1.  We don't do this now so that
     * it will run on today's browsers, which use jdk 1.0.2.
     */
    private String _formatNum (double num, int numfracdigits) {
        // First, round the number. 
        double fudge = 0.5;
        if (num < 0.0) fudge = -0.5;
        String numString = Double.toString(num +
                fudge*Math.pow(10.0, -numfracdigits));
        // Next, find the decimal point.
        int dpt = numString.lastIndexOf(".");
        StringBuffer result = new StringBuffer();
        if (dpt < 0) {
            // The number we are given is an integer.
            if (numfracdigits <= 0) {
                // The desired result is an integer.
                result.append(numString);
                return result.toString();
            }
            // Append a decimal point and some zeros.
            result.append(".");
            for (int i = 0; i < numfracdigits; i++) {
                result.append("0");
            }
            return result.toString();
        } else {
            // There are two cases.  First, there may be enough digits.
            int shortby = numfracdigits - (numString.length() - dpt -1);
            if (shortby <= 0) {
                int numtocopy = dpt + numfracdigits + 1;
                if (numfracdigits == 0) {
                    // Avoid copying over a trailing decimal point.
                    numtocopy -= 1;
                }
                result.append(numString.substring(0, numtocopy));
                return result.toString();
            } else {
                result.append(numString);
                for (int i = 0; i < shortby; i++) {
                    result.append("0");
                }
                return result.toString();                
            }
        }
    }
 
    /*
     * Measure the various fonts.  
     */
    private void _measureFonts() {
        // We only measure the fonts once, and we do it from addNotify().
        if (_labelfont == null)  
            _labelfont = new Font("Helvetica", Font.PLAIN, 12);
        if (_superscriptfont == null)  
            _superscriptfont = new Font("Helvetica", Font.PLAIN, 9);
        if (_titlefont == null)  
            _titlefont = new Font("Helvetica", Font.BOLD, 14);

        _labelFontMetrics = getFontMetrics(_labelfont);
        _superscriptFontMetrics = getFontMetrics(_superscriptfont);
        _titleFontMetrics = getFontMetrics(_titlefont);
    }

    /*
     * Return the number of fractional digits required to display the
     * given number.  No number larger than 15 is returned (if
     * more than 15 digits are required, 15 is returned).
     */
    private int _numFracDigits (double num) {
        int numdigits = 0;
        while (numdigits <= 15 && num != Math.floor(num)) {
            num *= 10.0;
            numdigits += 1;
        }
        return numdigits;
    }
 
    /*
     * Return the number of integer digits required to display the
     * given number.  No number larger than 15 is returned (if
     * more than 15 digits are required, 15 is returned).
     */
    private int _numIntDigits (double num) {
        int numdigits = 0;
        while (numdigits <= 15 && (int)num != 0.0) {
            num /= 10.0;
            numdigits += 1;
        }
        return numdigits;
    }

    /*
     * Given a number, round up to the nearest power of ten
     * times 1, 2, or 5.
     *
     * Note: The argument must be strictly positive.
     */
    private double _roundUp(double val) {
        int exponent, idx;
        exponent = (int) Math.floor(Math.log(val)*_log10scale);
        val *= Math.pow(10, -exponent);
        if (val > 5.0) val = 10.0;
        else if (val > 2.0) val = 5.0;
        else if (val > 1.0) val = 2.0;
        val *= Math.pow(10, exponent);
        return val;
    }

    /*
     * Internal implementation of setXRange, so that it can be called when
     * autoranging. 
     */
    private void _setXRange (double min, double max) {
        // If values are invalid, try for something reasonable.
        if (min > max) {
            min = -1.0;
            max = 1.0;
        } else if (min == max) {
            min -= 1.0;
            max += 1.0;
        }
        // Find the exponent.
        double largest = Math.max(Math.abs(min),Math.abs(max));
        _xExp = (int) Math.floor(Math.log(largest)*_log10scale);
        // Use the exponent only if it's larger than 1 in magnitude.
        if (_xExp > 1 || _xExp < -1) {
            double xs = 1.0/Math.pow(10.0,(double)_xExp);
            _xtickMin = min*xs;
            _xtickMax = max*xs;
        } else {
            _xtickMin = min;
            _xtickMax = max;
            _xExp = 0;
        }
        _xMin = min;
        _xMax = max;
    }

    /*
     * Internal implementation of setYRange, so that it can be called when
     * autoranging.
     */
    private void _setYRange (double min, double max) {
        // If values are invalid, try for something reasonable.
        if (min > max) {
            min = -1.0;
            max = 1.0;
        } else if (min == max) {
            min -= 0.1;
            max += 0.1;
        }
        // Find the exponent.
        double largest = Math.max(Math.abs(min),Math.abs(max));
        _yExp = (int) Math.floor(Math.log(largest)*_log10scale);
        // Use the exponent only if it's larger than 1 in magnitude.
        if (_yExp > 1 || _yExp < -1) {
            double ys = 1.0/Math.pow(10.0,(double)_yExp);
            _ytickMin = min*ys;
            _ytickMax = max*ys;
        } else {
            _ytickMin = min;
            _ytickMax = max;
            _yExp = 0;
        }
        _yMin = min;
        _yMax = max;
    }

    //////////////////////////////////////////////////////////////////////////
    ////                         private variables                        ////

    // The range of the plot as labeled (multiply by 10^exp for actual range.
    private double _ytickMax, _ytickMin, _xtickMax, _xtickMin;
    // The power of ten by which the range numbers should be multiplied.
    private int _yExp, _xExp;

    // Scaling used in making tick marks
    private double _ytickscale, _xtickscale;

    // Font information.
    private Font _labelfont = null, _superscriptfont = null,
        _titlefont = null;
    FontMetrics _labelFontMetrics = null, _superscriptFontMetrics = null,
        _titleFontMetrics = null;

    // For use in calculating log base 10.  A log times this is a log base 10.
    private static final double _log10scale = 1/Math.log(10);
    
    // The title and label strings.
    private String _xlabel, _ylabel, _title;
    
    // Legend information.
    private Vector _legendStrings = new Vector();
    private Vector _legendDatasets = new Vector();
    
    // If XTicks or YTicks are given
    private Vector _xticks, _xticklabels, _yticks, _yticklabels;

    // A button for filling the plot
    private Button _fillButton;
    
    // Variables keeping track of the interactive zoom box.
    // Initialize to impossible values.
    private int _zoomx = -1;
    private int _zoomy = -1;
    private int _zoomxn = -1;
    private int _zoomyn = -1;

    // Control whether we are zooming in or out.
    private boolean _zoomin = false;
    private boolean _zoomout = false;
    private boolean _drawn = false;
}
/* Original version copyright follows */
/* A labeled box for signal plots.

@Author: Edward A. Lee and Christopher Hylands

@Contributors:  William Wu

@Version: @(#)PlotBox.java	1.66    10/30/97

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
