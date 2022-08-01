/*
Copyright (C) 1998, Kevin Schmidt

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

public class Gr821applet extends Applet {

   TextField tf,tfread,tcin,tcf,tcond;
   TextField tlp,tlpp,trc,tlc;
   Label lad,limp,lcap,lind,lq,tinfo;
   Button setup,bok,bcancel;
   double f,fread,cin,cfin,g;
   Gr821a gr821a;
   Panel pleft;

   public void init() {
      Panel pleft1,pleft2,pright;
      setFont(new Font("Helvetica",Font.PLAIN,14));
      setLayout(new GridLayout(1,2));
      add(pleft = new Panel());
      add(pright = new Panel());
      pleft.setLayout(new CardLayout());
      pleft.add("Standard",pleft1 = new Panel());
      pleft.add("Setup",pleft2 = new Panel());

      pleft1.setLayout(new GridLayout(5,2));
      pleft1.add(new Label("f(MHz) = "));
      pleft1.add(tf = new TextField("3.5"));
      pleft1.add(new Label("f_read(MHz) = "));
      pleft1.add(tfread = new TextField("3.0"));
      pleft1.add(new Label("Initial C(pF) = "));
      pleft1.add(tcin = new TextField("500.0"));
      pleft1.add(new Label("Final C(pF) = "));
      pleft1.add(tcf = new TextField("600.0"));
      pleft1.add(new Label("Conductivity (\u00b5S) = "));
      pleft1.add(tcond = new TextField("0.0"));

      pleft2.setLayout(new GridLayout(6,2));
      pleft2.add(new Label("Residual Parameters"));
      pleft2.add(new Label("See GR821A manual"));
      pleft2.add(new Label("L'(nH) = "));
      pleft2.add(tlp = new TextField("6.8"));
      pleft2.add(new Label("Lc(nH) = "));
      pleft2.add(tlc = new TextField("6.1"));
      pleft2.add(new Label("L''(nH) = "));
      pleft2.add(tlpp = new TextField("3.15"));
      pleft2.add(new Label("Rc(Ohm) = "));
      pleft2.add(trc = new TextField("0.026"));
      pleft2.add(bcancel = new Button("Cancel"));
      pleft2.add(bok = new Button("OK"));

      f = 3.5;
      fread = 3.0;
      cin = 500.0;
      cfin = 600.0;
      g = 0.0;
      pright.setLayout(new GridLayout(5,2));
      pright.add(lad = new Label("Y = "));
      pright.add(limp = new Label("Impedance = "));
      pright.add(lcap = new Label("Capacitance = "));
      pright.add(lind = new Label("Inductance = "));
      pright.add(lq = new Label("Q ="));
      pright.add(new Label(" "));
      pright.add(setup = new Button("Setup"));
      pright.add(tinfo = new Label());
      gr821a = new Gr821a();
      calculate();
   }

   public void calculate() {
      double c,l,q;
      Complex a,z;
      double gg,b,r,x;
      a = gr821a.admittance(f,fread,cin,cfin,g);
      z = a.inv().mult(1.e6);
      gg = a.real();
      b = a.imag();
      r = z.real();
      x = z.imag();
      if (b < 0) {
         b = -b;
         lad.setText("Y = " + toString(gg,2) +  " - j" + toString(b,2) +
            " \u00b5S");
      } else {
         lad.setText("Y = " + toString(gg,2) +  " + j" + toString(b,2) +
            " \u00b5S");
      }
      if (x < 0) {
         x = -x;
         limp.setText("Z = " + toString(r,2) +  " - j" + toString(x,2) +
            " Ohm");
      } else {
         limp.setText("Z = " + toString(r,2) +  " + j" + toString(x,2) +
            " Ohm");
      }
      c = b/(2.*Math.PI*f);
      lcap.setText("Capacitance = " + toString(c,2) + " pF");
      l = x/(2.*Math.PI*f);
      lind.setText("Inductance = " + toString(l,2) + " \u00b5H");
      q = Math.abs(x/r);
      lq.setText("Q = " + toString(q,1));
   }

   public boolean handleEvent(Event e) {
      if (e.id == Event.KEY_RELEASE) {
         tinfo.setText(" ");
         if (e.target == tf) {
            try {
               f = (new Double(tf.getText())).doubleValue();
               calculate();
            } catch (java.lang.NumberFormatException e1) {
               tinfo.setText("frequency format error");
               return false;
            }
         }
         if (e.target == tfread) {
            try {
               fread = (new Double(tfread.getText())).doubleValue();
               calculate();
            } catch (java.lang.NumberFormatException e1) {
               tinfo.setText("read frequency read format error");
               return false;
            }
         }
         if (e.target == tcin) {
            try {
               cin = (new Double(tcin.getText())).doubleValue();
               calculate();
            } catch (java.lang.NumberFormatException e1) {
               tinfo.setText("initial C format error");
               return false;
            }
         }
         if (e.target == tcf) {
            try {
               cfin = (new Double(tcf.getText())).doubleValue();
               calculate();
            } catch (java.lang.NumberFormatException e1) {
               tinfo.setText("final C format error");
               return false;
            }
         }
         if (e.target == tcond) {
            try {
               g = (new Double(tcond.getText())).doubleValue();
               calculate();
            } catch (java.lang.NumberFormatException e1) {
               tinfo.setText("Conductivity format error");
               return false;
            }
         }
      }

      if (e.target == setup && e.id == Event.ACTION_EVENT) {
         ((CardLayout)pleft.getLayout()).show(pleft,"Setup");
         return true;
      }

      if (e.target == bcancel) {
         ((CardLayout)pleft.getLayout()).show(pleft,"Standard");
         return true;
      }

      if (e.target == bok) {
         double elp,elpp,elc,rc;
         try {
               elp = 1.e-9*(new Double(tlp.getText())).doubleValue();
               elc = 1.e-9*(new Double(tlc.getText())).doubleValue();
               elpp = 1.e-9*(new Double(tlpp.getText())).doubleValue();
               rc = (new Double(trc.getText())).doubleValue();
               gr821a = new Gr821a(elp,elc,elpp,rc);
               ((CardLayout)pleft.getLayout()).show(pleft,"Standard");
               calculate();
            } catch (java.lang.NumberFormatException e1) {
            }
         return true;
      }
      return false;

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
      return "GR821 Applet, version 0.01" +
        " Copyright 1998, Kevin Schmidt";
   }

   public Dimension getMinimumSize() {
      return new Dimension(800,200);
   }

   public Dimension getPreferredSize() {
      return getMinimumSize();
   }


   public static void main(String[] args) {
      Gr821applet a;
      Frame f = new Frame();
      f.setLayout(new BorderLayout());
      f.add("Center",a = new Gr821applet());
      f.setTitle("GR821A");
      f.pack();
      f.show();
      a.init();
      f.show();
   }

}
