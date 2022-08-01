/*
Copyright 1998, Kevin Schmidt, all rights reserved.

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

public class Tran extends Applet implements Runnable {

   Panel paneltop,panelbottom,panelleft,panelright,panel10,panelplot;
   ZPlot plot,plotz,plotv,ploti,plotp;
   Button calc,stop;
   Choice cable,units,imped,plotchoice;
   TextField tz,tf,tl,tloadr,tloadx;
   SimpleLine rg58a,rg59,rg8x,rg8a,rg8foam;
   SimpleLine belden9913,rg11a,rg17a,hl5050ohm,hl5075ohm;
   SimpleLine hl7550ohm,hl7575ohm,tub300,win450,lad600,ideal50;
   TEMLine tline;
   Label lz0,lin,l100,latten,lswrl,lswrt,lploss,lreac,lvf,tinfo,lu,lu2;
   int nplot = 0;
   static int nlambda = 100; // plot points per wavelength
   boolean feet = true;
   boolean load = true;
   TextField ur0,uatt,uvf,uf,uexp,u2r0,u2x0,u2att,u2vf;
   Button ubcan,ubok,u2bcan,u2bok;
   Thread calcthread;

   Complex z0,gamma,zload;
   double el,f;

   public void init() {

      Panel panel1,panel2,panelin,panelout,panelu,panelu2,panelptop;
      super.init();

      setLayout(new GridLayout(2,1,50,50));
      add(paneltop = new Panel());
      add(panelbottom= new Panel());

      paneltop.setLayout(new GridLayout(1,2,10,10));
      paneltop.add(panel10 = new Panel());
      paneltop.add(panel2 = new Panel());
      panel10.setLayout(new CardLayout());
      panel10.add("Standard",panel1 = new Panel());
      panel10.add("UserLine1",panelu = new Panel());
      panel10.add("UserLine2",panelu2 = new Panel());
      panel1.setLayout(new BorderLayout());
      panel1.add("North",new Label("Input Data"));
      panel2.setLayout(new BorderLayout());
      panel2.add("North",new Label("Output"));
      panel1.add("Center", panelin = new Panel());
      panel2.add("Center", panelout = new Panel());

      in_setup(panelin);
      u_setup(panelu);
      u2_setup(panelu2);
      out_setup(panelout);

      panelbottom.setLayout(new BorderLayout());
      panelbottom.add("Center",panelplot = new Panel());
      plotchoice = new Choice();
      plotchoice.addItem("Power, Voltage, Current");
      plotchoice.addItem("Impedance");
      plotchoice.addItem("Power with 1500 Watts");
      plotchoice.addItem("RMS Voltage with 1500 Watts");
      plotchoice.addItem("RMS Current(Amps) with 1500 Watts");
      panelbottom.add("North",panelptop = new Panel());
      panelptop.add(plotchoice);
      panelplot.setLayout(new CardLayout());
      panelplot.add("PIV",plot = new ZPlot());
      panelplot.add("Z",plotz = new ZPlot());
      panelplot.add("P",plotp = new ZPlot());
      panelplot.add("V",plotv = new ZPlot());
      panelplot.add("I",ploti = new ZPlot());

      plot.setNumSets(3);
      plot.init();
      plot.setXLabel("x");
      plot.setYLabel("P V I");
      plot.setTitle("Power Voltage and Current");
      plot.addLegend(0,"|V|/R0");
      plot.addLegend(1,"|I|");
      plot.addLegend(2,"P/Pout");

      plotz.setNumSets(3);
      plotz.init();
      plotz.setXLabel("x");
      plotz.setYLabel("Z");
      plotz.setTitle("Impedance");
      plotz.addLegend(0,"R");
      plotz.addLegend(1,"X");
      plotz.addLegend(2,"|Z|");

      plotp.setNumSets(1);
      plotp.init();
      plotp.setXLabel("x");
      plotp.setYLabel("P");
      plotp.setTitle("Power");

      plotv.setNumSets(1);
      plotv.init();
      plotv.setXLabel("x");
      plotv.setYLabel("V");
      plotv.setTitle("Voltage");

      ploti.setNumSets(1);
      ploti.init();
      ploti.setXLabel("x");
      ploti.setYLabel("I");
      ploti.setTitle("Current");
      
      validate();
      plot.repaint();
      plotp.repaint();
      plotv.repaint();
      ploti.repaint();
      plotz.repaint();
      ((CardLayout)panelplot.getLayout()).show(panelplot,"P");
      ((CardLayout)panelplot.getLayout()).show(panelplot,"V");
      ((CardLayout)panelplot.getLayout()).show(panelplot,"I");
      ((CardLayout)panelplot.getLayout()).show(panelplot,"Z");
      ((CardLayout)panelplot.getLayout()).show(panelplot,"PIV");

// SimpleLine parameters chosen to agree with N6BV's DOS TL program

      rg58a = new SimpleLine(50.0,0.369419,.66,0.578018);
      rg59 = new SimpleLine(75.0,0.37952,0.66,0.50123);
      rg8x = new SimpleLine(50.,0.263788,0.78,0.527746);
      rg8a = new SimpleLine(50.,0.169,0.66,0.58343);
      rg8foam = new SimpleLine(52.,0.1496227,0.78,0.526705);
      belden9913 = new SimpleLine(50.,0.128322,0.84,0.505155);
      rg11a = new SimpleLine(75.,0.169000,0.66,0.583431);
      rg17a = new SimpleLine(50.,0.066607,0.66,0.591210);
      hl5050ohm = new SimpleLine(50.,0.053002,0.81,0.593393);
      hl5075ohm = new SimpleLine(75.,0.065865,0.81,0.555784);
      hl7550ohm = new SimpleLine(50.,0.032877,0.81,0.646439);
      hl7575ohm = new SimpleLine(75.,0.047169,0.81,0.581105);
      tub300 = new SimpleLine(300.,0.086879,0.80,0.548849);
      win450 = new SimpleLine(450.,0.0636,0.95,0.5448); // bogus in N6BV's code
      lad600 = new SimpleLine(600.,0.015671,0.97,0.559794);
      ideal50 = new SimpleLine(50.,0.0,1.0,.5);
      tline = rg8a;

   }

   private void in_setup(Panel p) {
//
// setup input panel,
//
      Panel panelbut;
      p.setLayout(new GridLayout(6,2,2,2));
      cable = new Choice();
      cable.addItem("RG8A/RG213");
      cable.addItem("RG58A");
      cable.addItem("RG59");
      cable.addItem("RG8X");
      cable.addItem("RG8 Foam");
      cable.addItem("Belden 9913/9086");
      cable.addItem("RG11A");
      cable.addItem("RG17A");
      cable.addItem("1/2 inch 50 ohm hardline");
      cable.addItem("1/2 inch 75 ohm hardline");
      cable.addItem("3/4 inch 50 ohm hardline");
      cable.addItem("3/4 inch 75 ohm hardline");
      cable.addItem("300 Tubular");
      cable.addItem("450 Window");
      cable.addItem("600 Ladder");
      cable.addItem("Ideal 50 Ohm line");
      cable.addItem("User Defined 1");
      cable.addItem("User Defined 2");
      p.add(new Label("Cable Type = ")); 
      p.add(cable);
      p.add(new Label("Frequency in MHz = "));
      p.add(tf = new TextField("3.500"));
      units = new Choice();
      units.addItem("Cable Length in Feet = ");
      units.addItem("Cable Length in Meters = ");
      p.add(units);
      p.add(tl = new TextField("0.0"));
      imped = new Choice();
      imped.addItem("Load Resistance = ");
      imped.addItem("Input Resistance = ");
      p.add(imped);
      p.add(tloadr = new TextField("50.0"));
      p.add(lreac = new Label("Load Reactance = "));
      p.add(tloadx = new TextField("0.0"));
      p.add(tinfo = new Label(""));
      p.add(panelbut = new Panel());
      panelbut.setLayout(new GridLayout(1,2,10,10));
      panelbut.add(stop = new Button("Stop"));
      panelbut.add(calc = new Button("Calculate"));
      stop.disable();
      tinfo.setBackground(Color.white);
   }

   private void u2_setup(Panel p) {
//
//User Line 2 Panel
//
      p.setLayout(new GridLayout(5,2,10,10));
      p.add(new Label("R0 = "));
      p.add(u2r0 = new TextField("50.0"));
      p.add(new Label("X0 = "));
      p.add(u2x0 = new TextField("0.0"));
      p.add(lu2 = new Label("Attenuation dB/100ft"));
      p.add(u2att = new TextField("0.0"));
      p.add(new Label("Velocity factor"));
      p.add(u2vf = new TextField("1.0"));
      p.add(u2bcan = new Button("Cancel"));
      p.add(u2bok = new Button("OK"));
   }

   private void u_setup(Panel p) {
//
//User Line Panel
//
      p.setLayout(new GridLayout(6,2,10,10));
      p.add(new Label("R0 = "));
      p.add(ur0 = new TextField("50.0"));
      p.add(lu = new Label("Attenuation dB/100ft"));
      p.add(uatt = new TextField("0.0"));
      p.add(new Label("Velocity factor"));
      p.add(uvf = new TextField("1.0"));
      p.add(new Label("f(MHz) = "));
      p.add(uf = new TextField("1.0"));
      p.add(new Label("Exponent"));
      p.add(uexp = new TextField("0.5"));
      p.add(ubcan = new Button("Cancel"));
      p.add(ubok = new Button("OK"));
   }

   private void out_setup(Panel p) {
      p.setLayout(new GridLayout(6,2,10,10));
      p.add(lz0 = new Label("Z0 = "));
      p.add(l100 = new Label("Matched Loss dB/100ft ="));
      p.add(lin = new Label("Z in = "));
      p.add(latten = new Label("Matched Loss dB = "));
      p.add(lvf = new Label("Velocity Factor = "));
      p.add(lploss = new Label("Total Loss dB = "));
      p.add(lswrl = new Label("Load SWR = "));
      p.add(new Label(" "));
      p.add(lswrt = new Label("Input SWR = "));
      p.add(new Label(" "));
      p.add(new Label(" "));
      p.add(new Label(" "));
   }
   
   public boolean action(Event e, Object o) {
      int i;

      if (e.target == calc) {
         if (calcthread == null) {
            stop.enable();
            calcthread = new Thread(this);
            calcthread.start();
         }
         return true;
      }

      if (e.target == stop) {
         calcthread.stop();
         calcthread = null;
         stop.disable();
      }

      if (e.target == units) {
         feet = (units.getSelectedIndex() == 0);
         if (feet) {
            lu.setText("Attenuation dB/100ft");
            lu2.setText("Attenuation dB/100ft");
         } else {
            lu.setText("Attenuation dB/30m");
            lu2.setText("Attenuation dB/30m");
         }
         return true;
      }

      if (e.target == imped) {
         load = (imped.getSelectedIndex() == 0);
         if (load) {
            lreac.setText("Load Reactance = ");
         } else {
            lreac.setText("Input Reactance = ");
         }
      }

      if (e.target == cable) {
         docable();
         return true;
      }
      if (e.target == plotchoice) {
         i = plotchoice.getSelectedIndex();
         switch (i) {
            case 0:
               ((CardLayout)panelplot.getLayout()).show(panelplot,"PIV");
               break;
            case 1:
               ((CardLayout)panelplot.getLayout()).show(panelplot,"Z");
               break;
            case 2:
               ((CardLayout)panelplot.getLayout()).show(panelplot,"P");
               break;
            case 3:
               ((CardLayout)panelplot.getLayout()).show(panelplot,"V");
               break;
            case 4:
               ((CardLayout)panelplot.getLayout()).show(panelplot,"I");
               break;
         }
      }
      if (e.target == u2bcan) {
         cable.select(0); // put cable to rg8a;
         tline = rg8a;
         ((CardLayout)panel10.getLayout()).show(panel10,"Standard");
         return true;
      }
      if (e.target == u2bok) {
         return u2cable();
      }

      if (e.target == ubcan) {
         cable.select(0); // put cable to rg8a;
         tline = rg8a;
         ((CardLayout)panel10.getLayout()).show(panel10,"Standard");
         return true;
      }
      if (e.target == ubok) {
         return ucable();
      }
      return false;
   }

   private boolean u2cable() {
      GenLine uline;
      double r0,x0,att,vf;
      try {
         r0 = (new Double(u2r0.getText())).doubleValue();
      } catch (java.lang.NumberFormatException e1) {
         ur0.setText("format error");
         return true;
      }
      try {
         x0 = (new Double(u2x0.getText())).doubleValue();
      } catch (java.lang.NumberFormatException e1) {
         u2x0.setText("format error");
         return true;
      }
      try {
         att = (new Double(u2att.getText())).doubleValue();
         if (!feet) att *= 30.48/30.;
      } catch (java.lang.NumberFormatException e1) {
         uatt.setText("format error");
         return true;
      }
      try {
         vf = (new Double(u2vf.getText())).doubleValue();
      } catch (java.lang.NumberFormatException e1) {
         uvf.setText("format error");
         return true;
      }
      uline = new GenLine(r0,x0,att,vf);
      tline = uline;
      ((CardLayout)panel10.getLayout()).show(panel10,"Standard");
      return true;
   }

   private boolean ucable() {
      SimpleLine uline;
      double r0,att,vf,f,ex;
      try {
         r0 = (new Double(ur0.getText())).doubleValue();
      } catch (java.lang.NumberFormatException e1) {
         ur0.setText("format error");
         return true;
      }
      try {
         att = (new Double(uatt.getText())).doubleValue();
         if (!feet) att *= 30.48/30.;
      } catch (java.lang.NumberFormatException e1) {
         uatt.setText("format error");
         return true;
      }
      try {
         vf = (new Double(uvf.getText())).doubleValue();
      } catch (java.lang.NumberFormatException e1) {
         uvf.setText("format error");
         return true;
      }
      try {
         f = (new Double(uf.getText())).doubleValue();
      } catch (java.lang.NumberFormatException e1) {
         uf.setText("format error");
         return true;
      }
      try {
         ex = (new Double(uexp.getText())).doubleValue();
      } catch (java.lang.NumberFormatException e1) {
         uexp.setText("format error");
         return true;
      }
      att = att*Math.pow(f,-ex);
      uline = new SimpleLine(r0,att,vf,ex);
      tline = uline;
      ((CardLayout)panel10.getLayout()).show(panel10,"Standard");
      return true;
   }

   private void docable() {
      int i;
      i = cable.getSelectedIndex();
      switch (i) {
         case 0: {
            tline = rg8a;
            break;
         }
         case 1: {
            tline = rg58a;
            break;
         }
         case 2: {
            tline = rg59;
            break;
         }
         case 3: {
            tline = rg8x;
            break;
         }
         case 4: {
            tline = rg8foam;
            break;
         }
         case 5: {
            tline = belden9913;
            break;
         }
         case 6: {
            tline = rg11a;
            break;
         }
         case 7: {
            tline = rg17a;
            break;
         }
         case 8: {
            tline = hl5050ohm;
            break;
         }
         case 9: {
            tline = hl5075ohm;
            break;
         }
         case 10: {
            tline = hl7550ohm;
            break;
         }
         case 11: {
            tline = hl7575ohm;
            break;
         }
         case 12: {
            tline = tub300;
            break;
         }
         case 13: {
            tline = win450;
            break;
         }
         case 14: {
            tline = lad600;
            break;
         }
         case 15: {
            tline = ideal50;
            break;
         }
         case 16: {
            ((CardLayout)panel10.getLayout()).show(panel10,"UserLine1");
            tline = null;
            break;
         }
         case 17: {
            ((CardLayout)panel10.getLayout()).show(panel10,"UserLine2");
            tline = null;
            break;
         }
      }
   }

   public void run() {
      int i;
      double att,beta,alpha,dx,y1,y2,y3,ymax,vf,ymag;
      double rr,xx,pin,pout,ploss,rhomag,swr;
      double zmin=0.,zmax=0.;
      double r,x,pnorm,vnorm,cnorm,cmax,vmax;
      Complex z,volt,current,c,s,rho,znorm,zplot;
      Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
//
// read in data
//
         tinfo.setText(" ");
         try {
            f = (new Double(tf.getText())).doubleValue();
         } catch (java.lang.NumberFormatException e1) {
            tinfo.setText("frequency format error");
            stop();
            calcthread = null;
            return ;
         }
         if (f < .001 || f > 5000.) {
            tinfo.setText("f not between .001 and 5000");
            stop();
            calcthread = null;
            return ;
         }
         try {
            el = (new Double(tl.getText())).doubleValue();
         } catch (java.lang.NumberFormatException e1) {
            tinfo.setText("length format error");
            stop();
            calcthread = null;
            return ;
         }
         try {
            rr = (new Double(tloadr.getText())).doubleValue();
         } catch (java.lang.NumberFormatException e1) {
            tinfo.setText("Load R format error");
            stop();
            calcthread = null;
            return ;
         }
         try {
            xx = (new Double(tloadx.getText())).doubleValue();
         } catch (java.lang.NumberFormatException e1) {
            tinfo.setText("Load X format error");
            stop();
            calcthread = null;
            return ;
         }
         zload = new Complex(rr,xx);
//
// do calculations
//
         tline.setF(f);
         z0 = tline.Z0();
         r = z0.real();
         x = z0.imag();
         if (x < 0) {
            x = -x;
            lz0.setText("Z0 = " + toString(r,2) +  " - j" + toString(x,2));
         } else {
            lz0.setText("Z0 = " + toString(r,2) +  " + j" + toString(x,2));
         }
         att = tline.attendB();
         if (feet) {
            l100.setText("Matched Loss dB/100ft = " + toString(att,2));
            att *= el/100.;
            latten.setText("Matched Loss dB = " + toString(att,2));
         } else {
            att *= 30./30.48;
            l100.setText("Matched Loss dB/30m = " + toString(att,2));
            att *= el/30.;
            latten.setText("Matched Loss dB = " + toString(att,2));
         }
         vf = tline.velfac();
         lvf.setText("Velocity Factor = " + toString(vf,2));
         if (feet) {
            gamma = tline.gamma().mult(el*.3048);
         } else {
            gamma = tline.gamma().mult(el);
         }
         c = gamma.cosh();
         if (load) {
            s = gamma.sinh();
         } else {
            s = gamma.sinh().mult(-1.);
         }
//
// assume 1 amp at load
//
         pout = zload.real();
         volt = (c.mult(zload)).add(s.mult(z0));
         current = (s.mult(zload).divide(z0)).add(c);
         pin = volt.real()*current.real()+volt.imag()*current.imag();
         ploss = 10.*Math.log(pin/pout)/Math.log(10.);
         if (!load) ploss = -ploss;
         lploss.setText("Total Loss dB = " + toString(ploss,2));
         z = volt.divide(current);
         r = z.real();
         x = z.imag();
         if (load) {
            if (x < 0) {
               x = -x;
               lin.setText("Z in = " + toString(r,2) +  " - j" + toString(x,2));
            } else {
               lin.setText("Z in = " + toString(r,2) +  " + j" + toString(x,2));
            }
         } else {
            if (x < 0) {
               x = -x;
               lin.setText("Z Load = " + toString(r,2) +  " - j" + toString(x,2));
            } else {
               lin.setText("Z Load = " + toString(r,2) +  " + j" + toString(x,2));
            }
         }
//
// use real part of Z0 for SWR calculations to avoid annoying negative swr
//
//         znorm = z.divide(z0.real());
         znorm = z.divide(z0);
         rho = (znorm.sub(1.)).divide(znorm.add(1.));
         rhomag = rho.abs();
         swr = (1.+rhomag)/(1.-rhomag);
         if (load) {
            lswrt.setText("Input SWR = " + toString(swr,2));
         } else {
            lswrl.setText("Load SWR = " + toString(swr,2));
         }

 //        znorm = zload.divide(z0.real());
         znorm = zload.divide(z0);
         rho = (znorm.sub(1.)).divide(znorm.add(1.));
         rhomag = rho.abs();
         swr = (1.+rhomag)/(1.-rhomag);
         if (load) {
            lswrl.setText("Load SWR = " + toString(swr,2));
         } else {
            lswrt.setText("Input SWR = " + toString(swr,2));
         }


         if (nplot != 0) { //erase old plot
            try {
               for (i=0;i<nplot;i++) {
                  plot.erasePoint(0,0);
                  plot.erasePoint(1,0);
                  plot.erasePoint(2,0);
                  plotz.erasePoint(0,0);
                  plotz.erasePoint(1,0);
                  plotz.erasePoint(2,0);
                  plotp.erasePoint(0,0);
                  plotv.erasePoint(0,0);
                  ploti.erasePoint(0,0);
               }
            } catch (java.lang.ArrayIndexOutOfBoundsException e1) {
              // if thread is killed before they all were plotted
              // we can get this exception -- just ignore it
            }
         }

         nplot = Math.max(100, (int) (nlambda*gamma.imag()/(2.*Math.PI)));
         dx = 1./(nplot-1);

//
// set zload to correct load if not already
//
         if (!load) zload = new Complex(z.real(),z.imag());
         pout = zload.real();

         pnorm = 1500./zload.real()*Math.pow(10.,-ploss*.1);
         cnorm = Math.sqrt(pnorm);
         vnorm = cnorm*z0.real();

         ymax = 0.;
         vmax = 0.;
         cmax = 0.;
         for (i=0;i<nplot;i++) {
            xx = i*dx;
            c = (gamma.mult(xx)).cosh();
            s = (gamma.mult(xx)).sinh();
            volt = (c.mult(zload)).add(s.mult(z0));
            current = (s.mult(zload).divide(z0)).add(c);
            zplot = volt.divide(current);
            pin = volt.real()*current.real()+volt.imag()*current.imag();
            y1 = volt.abs()/z0.real();
            ymax = Math.max(ymax,y1);
            y2 = current.abs();
            ymax = Math.max(ymax,y2);
            y3 = pin/pout;
            ymax = Math.max(ymax,y3);
            xx = (1.-xx)*el;
            plot.addPoint(0,xx,y1,(i>0));
            plot.addPoint(1,xx,y2,(i>0));
            plot.addPoint(2,xx,y3,(i>0));
            plotv.addPoint(0,xx,y1*vnorm,(i>0));
            ploti.addPoint(0,xx,y2*cnorm,(i>0));
            plotp.addPoint(0,xx,pnorm*pin,(i>0));
            vmax = Math.max(vmax,y1*vnorm);
            cmax = Math.max(cmax,y2*cnorm);
            y1 = zplot.real();
            y2 = zplot.imag();
            ymag = Math.sqrt(y1*y1+y2*y2);
            zmax = Math.max(zmax,ymag);
            zmin = Math.min(zmin,y1);
            zmin = Math.min(zmin,y2);
            plotz.addPoint(0,xx,y1,(i>0));
            plotz.addPoint(1,xx,y2,(i>0));
            plotz.addPoint(2,xx,ymag,(i>0));
         }
         plot.setXRange(0.,el);
         plot.setYRange(0.,ymax*1.1);
         plotz.setYRange(Math.min(0.,zmin)*1.1,Math.max(0.,zmax)*1.1);
         plotz.setXRange(0.,el);
         plotp.setXRange(0.,el);
         plotv.setXRange(0.,el);
         ploti.setXRange(0.,el);
         plotp.setYRange(0.,1600.);
         plotv.setYRange(0.,vmax*1.1);
         ploti.setYRange(0.,cmax*1.1);

         plot.repaint();
         plotz.repaint();
         plotp.repaint();
         plotv.repaint();
         ploti.repaint();
         stop.disable();
         stop();
         calcthread = null;
         return ;
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
      return "Transmission Line Calculator, version 0.03" +
        " Copyright 1998, Kevin Schmidt";
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
