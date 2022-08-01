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
import java.awt.*;
import java.applet.*;
import java.awt.event.*;
import java.util.Hashtable;

public class Tuner extends Applet implements Runnable {

   TNet tnet;
   Knob kc1,kc2,kl;
   Meter mswr;
   double c1max = 250.0;
   double c2max = 250.0;
   double elmax = 30.0;
   double qc1 = 2000.0;
   double qc2 = 2000.0;
   double qel = 100.0;
   double f;

   double z1r,z1i,z2r,z2i,z3r,z3i,zlr,zli;

   DBLabel lossl,dblossl,swrl;
   TextField tinfo,tzlr,tzlx,tf;
   Button bauto,bsetup;

   Panel pupperleft;
   Panel pload;
   Panel pinfo;
   Panel psetup;
   TextField tc1max,tc2max,telmax,tc1q,tc2q,telq;
   Button bok,bcancel;
   Color bcolor;
   Thread calcthread;

   public void init() {
      bcolor = new Color(100,150,100);
      setBackground(bcolor);
      setLayout(new GridLayout(2,3,10,10));
      pinfo = new Panel();
      pinfo.setLayout(new GridLayout(6,2));
      pinfo.add(new Label("SWR ="));
      pinfo.add(swrl = new DBLabel(""));
      pinfo.add(new Label("Percent Loss ="));
      pinfo.add(lossl = new DBLabel(""));
      pinfo.add(new Label("dB Loss ="));
      pinfo.add(dblossl = new DBLabel(""));
      pinfo.add(bauto = new Button("Autotune"));
      pinfo.add(tinfo = new TextField(""));
      pinfo.add(bsetup = new Button("Set Up"));
      pinfo.add(new Label(""));
      pinfo.add(new Label(""));
      pinfo.add(new Label(""));
      pupperleft = new Panel();
      pupperleft.setLayout(new CardLayout());
      add(pupperleft);
      pupperleft.add("Info",pinfo);
      psetup = new Panel();
      psetup.setLayout(new GridLayout(7,2));
      psetup.add(new Label("C1 Max (pF) ="));
      psetup.add(tc1max = new TextField(Truncate.toString(c1max,1)));
      psetup.add(new Label("C1 Q ="));
      psetup.add(tc1q = new TextField(Truncate.toString(qc1,1)));
      psetup.add(new Label("L Max (microH) ="));
      psetup.add(telmax = new TextField(Truncate.toString(elmax,1)));
      psetup.add(new Label("L Q ="));
      psetup.add(telq = new TextField(Truncate.toString(qel,1)));
      psetup.add(new Label("C2 Max (pF) ="));
      psetup.add(tc2max = new TextField(Truncate.toString(c2max,1)));
      psetup.add(new Label("C2 Q ="));
      psetup.add(tc2q = new TextField(Truncate.toString(qc2,1)));
      psetup.add(bok = new Button("OK"));
      psetup.add(bcancel = new Button("Cancel"));
      pupperleft.add("Setup",psetup);
      
      add(mswr = new Meter(0.0,1.0));
      Hashtable ht = new Hashtable(6);
      ht.put(new Double(0.0),new String("1.0"));
      ht.put(new Double(0.2),new String("1.5"));
      ht.put(new Double(1.0/3.0),new String("2.0"));
      ht.put(new Double(0.5),new String("3.0"));
      ht.put(new Double(0.8),new String("9.0"));
      ht.put(new Double(1.0),new String(""));
      mswr.setScale(ht);
      mswr.setLabel("SWR");
      pload = new Panel();
      pload.setLayout(new GridLayout(6,2));
      pload.add(new Label("R Load = "));
      pload.add(tzlr = new TextField("10",8));
      pload.add(new Label("X Load = "));
      pload.add(tzlx = new TextField("0",8));
      pload.add(new Label("F (MHz) ="));
      pload.add(tf = new TextField("1.83",8));
      pload.add(new Label(""));
      pload.add(new Label(""));
      pload.add(new Label(""));
      pload.add(new Label(""));
      pload.add(new Label(""));
      pload.add(new Label(""));
      add(pload);
      add(kc1 = new Knob("Input C (pF)",1.0e-3,c1max,10.0));
      add(kl = new Knob("Coil (microH)",1.0e-3,elmax,30.0));
      add(kc2 = new Knob("Output C (pF)",1.0e-3,c2max,10.0));
      kc1.setValue(0.5*c1max);
      kc2.setValue(0.5*c2max);
      kl.setValue(0.5*elmax);
      pupperleft.setBackground(bcolor);
      pinfo.setBackground(bcolor);
      psetup.setBackground(bcolor);
      bauto.setBackground(bcolor);
      bok.setBackground(bcolor);
      bcancel.setBackground(bcolor);
      bsetup.setBackground(bcolor);
      tinfo.setBackground(bcolor);
      tzlr.setBackground(bcolor);
      tzlx.setBackground(bcolor);
      tf.setBackground(bcolor);
      tc1max.setBackground(bcolor);
      tc2max.setBackground(bcolor);
      telmax.setBackground(bcolor);
      tc1q.setBackground(bcolor);
      tc2q.setBackground(bcolor);
      telq.setBackground(bcolor);
      calcZ();
      tnet = new TNet(z1r,z1i,z2r,z2i,z3r,z3i,zlr,zli);
      recalc();
      validate();
      calcthread = new Thread(this);
      calcthread.start();
      calcthread.suspend();
   }

   private void recalc() {
      double rho = tnet.getRho();
      double s = (1.0+rho)/(1.0-rho);
      mswr.setValue(rho);
      mswr.repaint();
      swrl.setText(Truncate.toString(s,1));
      double ploss = tnet.getLoss();
      lossl.setText(Truncate.toString(ploss*100.0,1));
      double dbloss = -10.0*Math.log(1.0-ploss)/Math.log(10.0);
      dblossl.setText(Truncate.toString(dbloss,1));
      swrl.repaint();
      dblossl.repaint();
      lossl.repaint();
   }

   public boolean mouseDrag(Event e, int ix, int iy) {
      if (e.target == kc1 ||
          e.target == kc2 ||
          e.target == kl) {
          calcthread.resume();
          Thread.currentThread().yield();
/*
          calcZ();
          tnet.setZl(zlr,zli);
          tnet.setZ1(z1r,z1i);
          tnet.setZ2(z2r,z2i);
          tnet.setZ3(z3r,z3i);
          recalc();
*/
      }
      return true;
   }

   public void run() {
      while (Thread.currentThread() == calcthread) {
         calcZ();
         tnet.setZl(zlr,zli);
         tnet.setZ1(z1r,z1i);
         tnet.setZ2(z2r,z2i);
         tnet.setZ3(z3r,z3i);
         recalc();
         calcthread.yield();
         calcthread.suspend();
         try {
            calcthread.sleep(30);
         } catch (InterruptedException e) {}
      }
   }

   private boolean calcZLoad() {
      zlr = 0.0;
      zli = 0.0;
      tinfo.setText("");
      try {
         zlr = (new Double(tzlr.getText())).doubleValue();
         if (zlr <= 0.0) {
            tinfo.setText("R Load < 0");
            pinfo.repaint();
            return false;
         }
      } catch (NumberFormatException e1) {
         tinfo.setText("R Load Error");
         pinfo.repaint();
         return false;
      }
      try {
         zli = (new Double(tzlx.getText())).doubleValue();
      } catch (NumberFormatException e1) {
         tinfo.setText("X Load Error");
         pinfo.repaint();
         return false;
      }
      try {
         f = (new Double(tf.getText())).doubleValue();
         if (f <= 0.0) {
            tinfo.setText("F < 0");
            pinfo.repaint();
            return false;
         }
      } catch (NumberFormatException e1) {
         tinfo.setText("F error");
         pinfo.repaint();
         return false;
      }
      zlr *= .02; //normalize to 50 ohms
      zli *= .02;
      return true;
   }

   private boolean calcZ() {
      if (!calcZLoad()) return false;
      double c1 = kc1.getValue();
      double c2 = kc2.getValue();
      double el = kl.getValue();
      double yr,yi,y;
      yi = 2.0*Math.PI*f*c1*1.e-6;
      yr = yi/qc1;
      y = 1.0/(yr*yr+yi*yi);
      z1r = yr*y;
      z1i = -yi*y;

      yi = 2.0*Math.PI*f*c2*1.e-6;
      yr = yi/qc2;
      y = 1.0/(yr*yr+yi*yi);
      z3r = yr*y;
      z3i = -yi*y;

      z2i = 2.0*Math.PI*f*el;
      z2r = z2i/qel;

      z1r *= .02; // normalize to 50 ohms
      z1i *= .02;
      z2r *= .02;
      z2i *= .02;
      z3r *= .02;
      z3i *= .02;
      return true;
   }

   public boolean handleEvent(Event e) {
      if (e.target == tzlr || e.target == tzlx || e.target == tf) {
         calcZ();
         tnet.setZl(zlr,zli);
         tnet.setZ1(z1r,z1i);
         tnet.setZ2(z2r,z2i);
         tnet.setZ3(z3r,z3i);
         recalc();
      }
      return super.handleEvent(e);
   }
      
   public boolean action(Event e, Object o) {
      if (e.target == bauto) {
         autoTune();
         return true;
      }
      if (e.target == bsetup) {
         ((CardLayout) pupperleft.getLayout()).show(pupperleft,"Setup");
         return true;
      }
      if (e.target == bcancel) {
         ((CardLayout) pupperleft.getLayout()).show(pupperleft,"Info");
         return true;
      }
      if (e.target == bok) {
         double c1m,c2m,elm,c1q,c2q,elq;
         c1m = c1max;
         c2m = c2max;
         elm = elmax;
         c1q = qc1;
         c2q = qc2;
         elq = qel;
         boolean gooddata = true;
         try {
            c1m = (new Double(tc1max.getText())).doubleValue();
            if (c1m <= 0.0) {
               tc1max.setText("Error");
               gooddata = false;
            }
         } catch (NumberFormatException e1) {
            tc1max.setText("Error");
            gooddata = false;
         }
         try {
            c1q = (new Double(tc1q.getText())).doubleValue();
            if (c1q <= 0.0) {
               tc1q.setText("Error");
               gooddata = false;
            }
         } catch (NumberFormatException e1) {
            tc1q.setText("Error");
            gooddata = false;
         }
         try {
            c2m = (new Double(tc2max.getText())).doubleValue();
            if (c2m <= 0.0) {
               tc2max.setText("Error");
               gooddata = false;
            }
         } catch (NumberFormatException e1) {
            tc2max.setText("Error");
            gooddata = false;
         }
         try {
            c2q = (new Double(tc2q.getText())).doubleValue();
            if (c2q <= 0.0) {
               tc2q.setText("Error");
               gooddata = false;
            }
         } catch (NumberFormatException e1) {
            tc2q.setText("Error");
            gooddata = false;
         }
         try {
            elm = (new Double(telmax.getText())).doubleValue();
            if (elm <= 0.0) {
               telmax.setText("Error");
               gooddata = false;
            }
         } catch (NumberFormatException e1) {
            telmax.setText("Error");
            gooddata = false;
         }
         try {
            elq = (new Double(telq.getText())).doubleValue();
            if (elq <= 0.0) {
               telq.setText("Error");
               gooddata = false;
            }
         } catch (NumberFormatException e1) {
            telq.setText("Error");
            gooddata = false;
         }
         if (gooddata) {
            c1max = c1m;
            c2max = c2m;
            elmax = elm;
            qc1 = c1q;
            qc2 = c2q;
            qel = elq;
            ((CardLayout) pupperleft.getLayout()).show(pupperleft,"Info");
            kc1.setLimits(1.0e-3,c1max,10.0);
            kl.setLimits(1.0e-3,elmax,30.0);
            kc2.setLimits(1.0e-3,c2max,10.0);
            kc1.repaint();
            kc2.repaint();
            kl.repaint();
         } 
         return true;
      }
      return false;
   }
   
   private void autoTune() {
      tinfo.setText("");
      double yr,yi,y;
      yi = 2.0*Math.PI*f*c2max*1.e-6;
      yr = yi/qc2;
      y = 1.0/(yr*yr+yi*yi);
      double rx = yr*y+zlr*50.0;
      double xx = -yi*y+zli*50.0;

      double a = 1.0/(1.0+1.0/(qc1*qc1))/(2.0*Math.PI*f*1.e-6);
      double b = a/qc1;
      double d = 2.*Math.PI*f;
      double e = d/qel;
   
      double e1 = -a*rx+b*xx;
      double e0 = -50.0*xx;
      double f1 = -e*b-d*a;
      double f0 = e*50.0-e*rx+d*xx;
      double g1 = -d*b+e*a;
      double g0 = d*50.0-d*rx-e*xx;
      double h1 = b*rx+a*xx;
      double h0 = -50.0*rx;

      double c2 = e1*f1-g1*h1;
      double c1 = e0*f1+f0*e1-g0*h1-g1*h0;
      double c0 = e0*f0-g0*h0;
      double rt = c1*c1-4.*c0*c2;
      double rt1,rt2,el;
      if (rt > 0) {
         rt = Math.sqrt(rt);
         rt1 = (-c1+rt)/(2.*c2);
         rt2 = (-c1-rt)/(2.*c2);
         c2 = c2max;
         if (rt2 > 0.0 && 1.0/rt2 <= c1max) {
            c1 = 1.0/rt2;
            el = -(rx*a*rt2+(50.0-b*rt2)*xx)
               /(d*50.0-d*b*rt2+e*a*rt2-d*rx-e*xx);
            if (el > 0.0 && el < elmax) {
               setComponents(c1,el,c2);
               return;
            }
         }
         if (rt1 > 0.0 && 1.0/rt1 <= c1max) {
            c1 = 1.0/rt1;
            el = -(rx*a*rt1+(50.0-b*rt1)*xx)
               /(d*50.0-d*b*rt1+e*a*rt1-d*rx-e*xx);
            if (el > 0.0 && el < elmax) {
               setComponents(c1,el,c2);
               return;
            }
         }
      }

      yi = 2.0*Math.PI*f*c1max*1.e-6;
      yr = yi/qc1;
      y = 1.0/(yr*yr+yi*yi);
      rx = 50.0-yr*y;
      xx = y*yi;
      a = 1.0/(1.0+1.0/(qc2*qc2))/(2.0*Math.PI*f*1.e-6);
      b = a/qc2;
      d = 2.*Math.PI*f;
      e = d/qel;
      double rl = zlr*50.0;
      double xl = zli*50.0;
   
      e1 = b*xx-a*rx;
      e0 = rl*xx+rx*xl;
      f1 = -e*b-a*d;
      f0 = e*rx-d*xx-rl*e+xl*d;
      g1 = a*e-b*d;
      g0 = e*xx+d*rx-rl*d-xl*e;
      h1 = b*rx+a*xx;
      h0 = rl*rx-xx*xl;

      c2 = e1*f1-g1*h1;
      c1 = e0*f1+f0*e1-g0*h1-g1*h0;
      c0 = e0*f0-g0*h0;
      rt = c1*c1-4.*c0*c2;
      if (rt > 0) {
         rt = Math.sqrt(rt);
         rt1 = (-c1+rt)/(2.*c2);
         rt2 = (-c1-rt)/(2.*c2);
      
         c1 = c1max;
         if (rt2 > 0.0 && 1.0/rt2 <= c2max) {
            c2 = 1.0/rt2;
            el = -(rt2*e1+e0)/(rt2*g1+g0);
            if (el > 0.0 && el < elmax) {
               setComponents(c1,el,c2);
               return;
            }
         }
         if (rt1 > 0.0 && 1.0/rt1 <= c2max) {
            c2 = 1.0/rt1;
            el = -(rt1*e1+e0)/(rt1*g1+g0);
            if (el > 0.0 && el < elmax) {
               setComponents(c1,el,c2);
               return;
            }
         }
      }
      tinfo.setText("Tune Failed");
   }

   private void setComponents(double c1, double el, double c2) {
      kc2.setValue(c2);
      kc1.setValue(c1);
      kl.setValue(el);
      calcZ();
      tnet.setZ1(z1r,z1i);
      tnet.setZ2(z2r,z2i);
      tnet.setZ3(z3r,z3i);
      recalc();
   }

   public Dimension minimumSize() {
      return new Dimension(600,400);
   }

   public Dimension preferredSize() {
      return minimumSize();
   }

   public static void main(String[] arg) {
      Frame f = new MyFrame();
      Tuner t = new Tuner();
      f.setLayout(new BorderLayout());
      f.add(t,BorderLayout.CENTER);
      f.setTitle("T-Network Tuner Simulator");
      t.init();
      f.pack();
      f.show();
   }

   public String getAppletInfo() {
      return "T-Network Tuner Simulator, version 0.02" +
        " Copyright 1999, Kevin E. Schmidt";
   }
}

class DBLabel extends Panel {

   Dimension dimage;
   Image image;
   Graphics gimage;
   String s;

   public DBLabel(String s) {
      this.s = s;
   }

   public void paint(Graphics g) {
      update(g);
   }

   public void update(Graphics g) {
      Dimension d = size();
      if (gimage == null || d.height != dimage.height ||
         d.width != dimage.width) {
         if (gimage != null) gimage.dispose();
         dimage = d;
         image = createImage(d.width,d.height);
         gimage = image.getGraphics();
      }
      FontMetrics fm = gimage.getFontMetrics();
      int ih = fm.getMaxAscent();
      gimage.setColor(getBackground());
      gimage.fillRect(0,0,d.width,d.height);
      gimage.setColor(getForeground());
      gimage.drawString(s,0,d.height/2+ih/2);
      g.drawImage(image,0,0,null);
   }

   public void setText(String s) {
      this.s = s;
   }
}
class MyFrame extends Frame {
   public boolean handleEvent(Event e) {
      if (e.id == Event.WINDOW_DESTROY) {
         System.exit(0);
      }
      return super.handleEvent(e);
   }
}
