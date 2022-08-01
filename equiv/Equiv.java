/*
Copyright 1999, Kevin Schmidt, all rights reserved.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

You can contact me at W9CF callbook address, or w9cf@ptolemy.la.asu.edu
*/

import java.applet.*;
import java.awt.*;

public class Equiv extends Applet implements Runnable {

   Panel panelleft,panelright;
   TextField tw,tt;
   Label lself,lres,lerror;
   Button calc;
   Font font;
   RectangularCrossSection r;
   ZPlot plot;
   int nplot = 200;
   double[] x;
   double[] charge;

   public void init() {

      super.init();
      setBackground(Color.lightGray);
      setFont(font = new Font("Helvetica",Font.PLAIN,14));
      setLayout(new GridLayout(1,2,50,50));
      add(panelleft = new Panel());
      add(panelright = new Panel());
      panelleft.setBackground(Color.lightGray);
      panelright.setBackground(Color.lightGray);

      panelleft.setLayout(new GridLayout(5,2,10,10));
      panelleft.add(new Label("Width = "));
      panelleft.add(tw = new TextField("1.0"));
      panelleft.add(new Label("Thickness = "));
      panelleft.add(tt = new TextField("1.0"));
      panelleft.add(new Label("Equivalent Diameter ="));
      panelleft.add(lself = new Label(""));
      panelleft.add(new Label("Resistance Diameter ="));
      panelleft.add(lres = new Label(""));
      panelleft.add(calc = new Button("Calculate"));
      panelleft.add(lerror = new Label(""));
      lself.setBackground(Color.white);
      lres.setBackground(Color.white);
      lerror.setBackground(Color.white);
      panelright.setLayout(new GridLayout(1,1,10,10));
      panelright.add(plot = new ZPlot());
      plot.setNumSets(2);
      plot.init();
      plot.setXLabel("x");
      plot.setYLabel("I");
      plot.setTitle("Current Density");
      x = new double[nplot];
      charge = new double[nplot];
      validate();
      action(new Event(calc,Event.ACTION_EVENT,calc),calc);
   }


   public boolean action(Event e, Object o) {

      if (e.target == calc) {
         lerror.setText("");
         double w = 1.0;
         double t = 1.0;
         try {
            w = (new Double(tw.getText())).doubleValue();
            if (w <= 0.0) {
               lerror.setText("width <= 0");
               return true;
            } 
         } catch (NumberFormatException ee) {
            lerror.setText("width format error");
            return true;
         }
         try {
            t = (new Double(tt.getText())).doubleValue();
            if (t <= 0.0) {
               lerror.setText("thickness <= 0");
               return true;
            } 
         } catch (NumberFormatException ee) {
            lerror.setText("thickness format error");
            return true;
         }
         r = new RectangularCrossSection(w,t);
         lself.setText(""+(float)r.getSelfDiameter());
         lres.setText(""+(float)r.getResistanceDiameter());

         try {
            for (int i=0;i<2*nplot;i++) plot.erasePoint(0,0);
            plot.erasePoint(1,0);
            plot.erasePoint(1,0);
         } catch (ArrayIndexOutOfBoundsException ee) { }
         r.widthCharge(charge,x);
         for (int i=0;i<nplot;i++) plot.addPoint(0,x[i],charge[i],(i>0));
         r.thicknessCharge(charge,x);
         double uniform = 1.0/(2.*(w+t));
         double cmin = charge[0];
         for (int i=nplot-1;i>=0;i--) {
            plot.addPoint(0,0.5*(w+t)-x[i],charge[i],true);
            cmin = Math.min(cmin,charge[i]);
         }
         
         plot.setXRange(0.,0.5*(w+Math.max(.1*w,t)));
         plot.setYRange(0.,Math.max(5.*uniform,2.*cmin));
         plot.addPoint(1,0.,uniform,false);
         plot.addPoint(1,0.5*(w+t),uniform,true);
         plot.repaint();
         return true;
      }
      return false;
   }

   public void run() {
   }

   public void start() {

   }

   public void stop() { // keep running when we look at another page

   }

   static String truncString(double d, int places) {
//
// This mess is a stopgap until we can use version 1.1 NumberFormat etc.
//
      int i,j,n,idecimal;
      Double dd = new Double(d);
      StringBuffer b = new StringBuffer(dd.toString());
      n = b.length();
      j = 0;
      idecimal = n;
      for (i=0;i<n;i++) {
         switch(b.charAt(i)) {
            case '.':
               idecimal = i;
               b.setCharAt(j++,b.charAt(i));
               break;
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
               if (i <= idecimal+places) b.setCharAt(j++,b.charAt(i));
               break;
            default:
               idecimal = n;
               b.setCharAt(j++,b.charAt(i));
         }
      }
      b.setLength(j);
      return b.toString();
   }

   static String toString(double d, int places) {
      String s = truncString(d,places);
      Double xx;
      try {
         xx = new Double(s);
      } catch (NumberFormatException e) {
         return s;
      }
      double x = xx.doubleValue();
      return truncString(x+2.*(d-x),places);
   }

   public String getAppletInfo() {
      return "Equivalent Diameter Calculator, version 0.01" +
        " Copyright 1999, Kevin Schmidt";
   }


   public Dimension minimumSize() {
      return new Dimension(700,350);
   }
   public Dimension preferredSize() {
      return minimumSize();
   }

   public static void main(String[] arg) {
      MyFrame f = new MyFrame();
      Equiv eq = new Equiv();
      f.setLayout(new BorderLayout());
      f.add("Center",eq);
      f.pack();
      f.show();
      eq.init();
   }

}

class ZPlot extends ptplot.KSPlotNS {

   Graphics back;
   Image backimg;
   Dimension backdim;

   public void drawPlot (Graphics g, boolean b) {
      _setButtonsVisibility(false);
      super.drawPlot(g,b);
   }

//This double buffers the plots -- delete this update routine to
//watch the plotting

   public void update(Graphics g) {
      Dimension d = size();
      if ((back == null)
         || (d.width != backdim.width)
         || (d.height != backdim.height)) {
         backdim = d;
         backimg = createImage(d.width,d.height);
         back = backimg.getGraphics();
      }
      super.update(back);
      g.drawImage(backimg,0,0,null);
   }

}

class MyFrame extends Frame {

   public boolean handleEvent(Event e) {
      if (e.id == e.WINDOW_DESTROY) {
         System.exit(0);
      }
      return super.handleEvent(e);
   }
}
