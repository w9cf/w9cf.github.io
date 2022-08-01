/*
Copyright 1999-2003, Kevin Schmidt, all rights reserved.

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

public class AutoTune {
   
   private boolean tuned;
   private double x1,x2,x3,t1,t2,t3,rl,xl,x1min,x3min,x2max,ploss,f;

   public AutoTune() {
      super();
   }

   public void tune(double cin, double el, double cout,
      double cinmax, double elmax, double coutmax,
      double qcin, double qel, double qcout, double f, double rl, double xl) {
      this.rl = rl;
      this.xl = xl;
      this.f = f;
      t1 = 1.0/qcin;
      t2 = 1.0/qel;
      t3 = 1.0/qcout;
      x1min = 0.02/(2.0*Math.PI*f*1.e-6*cinmax*(1.0+t1*t1));
      x3min = 0.02/(2.0*Math.PI*f*1.e-6*coutmax*(1.0+t3*t3));
      x2max = 0.02*2.0*Math.PI*f*elmax;
      double x1in = 0.02/(2.0*Math.PI*f*1.e-6*cin*(1.0+t1*t1));
      double x3in = 0.02/(2.0*Math.PI*f*1.e-6*cout*(1.0+t3*t3));
      double x2in = 0.02*2.0*Math.PI*f*el;
      x1in = Math.max(x1min,x1in);
      x3in = Math.max(x3min,x3in);
      x2in = Math.min(x2max,x2in);
//
// set one of the capacitors at maximum and see if we can match --
// if not try the current input values -- if still no match give up.
//
      x3 = x3min; // output c to max
      calx12(false);
      if (!tuned) {
         x1 = x1min; // input c to max
         calx23(false);
      }
      if (!tuned) {
         x3 = x3in;
         calx12(false);
      }
      if (!tuned) {
         x1 = x1in;
         calx23(false);
      }
      if (!tuned) {
         x2 = x2in;
         calx13(false);
      }
      if (!tuned) return; // give up
//
// now do binary chop to find minimum inductance
//
      double x2up = x2;
      double x2dn = 0.0;
      do {
         x2 = 0.5*(x2up+x2dn);
         calx13(false);
         if (tuned) {
            x2up = x2;
         } else {
            x2dn = x2;
         }
      } while (1.0-x2dn/x2up > 1.0e-12);
      tuned = true;
   }
   
// for fixed x3 calculate values of x1 and x2
   private void calx12(boolean print) {
      double a1 = (x3*(t1*t3-1.0)+rl*t1+xl)*(t2-t1)
         -(x3*(t1+t3)+rl-xl*t1)*(t1*t2+1.0);
      double b1 =(-x3*t3-rl)*(t2-t1)
         +(x3*(t1*t3-1.0)+rl*t1+xl)*(1.0-x3*(t3-t2)-rl-xl*t2)
         +(x3-xl)*(t1*t2+1.0)
         +(x3*(t1+t3)+rl-xl*t1)*(t2-x3*(t2*t3+1.0)-rl*t2+xl);
      double c1 = (-x3*t3-rl)*(1.0-x3*(t3-t2)-rl-xl*t2)
         +(-x3+xl)*(t2-x3*(t2*t3+1.0)-rl*t2+xl);
      double rt = b1*b1-4.0*a1*c1;
      if (rt < 0.0) {
         tuned = false;
      } else {
         rt = Math.sqrt(rt);
         double x1p = (-b1+rt)/(2.0*a1);
         double x2p = (x1p*xl-x3*t3-rl+x1p*x3*(t1*t3-1.0)+x1p*rl*t1)
            /(t2-x1p*(t1*t2+1.0)-x3*(t2*t3+1.0)-rl*t2+xl);
         double plossp = 1.0-(x2p*x2p*(1.0+t2*t2)*rl)
           /((x2p-x3+xl)*(x2p-x3+xl)+(t2*x2p+t3*x3+rl)*(t2*x2p+t3*x3+rl));
         boolean pgood = (x1p > x1min && x2p > 0.0 && x2p < x2max);
         double x1m = (-b1-rt)/(2.0*a1);
         double x2m = (x1m*xl-x3*t3-rl+x1m*x3*(t1*t3-1.0)+x1m*rl*t1)
            /(t2-x1m*(t1*t2+1.0)-x3*(t2*t3+1.0)-rl*t2+xl);
         double plossm = 1.0-(x2m*x2m*(1.0+t2*t2)*rl)
           /((x2m-x3+xl)*(x2m-x3+xl)+(t2*x2m+t3*x3+rl)*(t2*x2m+t3*x3+rl));
         boolean mgood = (x1m > x1min && x2m > 0.0 && x2m < x2max);
         if (!mgood && !pgood) { // both solutions fail
            tuned = false;
            return;
         }
         tuned = true;
if (print) System.out.println(" m p " + mgood + " " + pgood);
         if (mgood && pgood) { // both work -- find lowest loss
            if (plossp < plossm) {
               x1 = x1p;
               x2 = x2p;
               ploss = plossp;
            } else {
               x1 = x1m;
               x2 = x2m;
               ploss = plossm;
            }
            return;
         }
         if (mgood) {
            x1 = x1m;
            x2 = x2m;
            ploss = plossm;
         } else {
            x1 = x1p;
            x2 = x2p;
            ploss = plossp;
         } 
      }
   }

// for fixed x1 calculate values of x2 and x3
   private void calx23(boolean print) {
      double a1 = (x1*(t1*t3-1.0)-t3)*(t2-t3)-(x1*(t1+t3)-1.0)*(t2*t3+1.0);
      double b1 = (x1*xl-rl+x1*rl*t1)*(t2-t3)
         +(x1*(t1*t3-1.0)-t3)*(1-x1*(t1-t2)-rl-xl*t2)
         -(-x1*xl*t1+xl+x1*rl)*(t2*t3+1.0)
         +(x1*(t1+t3)-1.0)*(t2-x1*(t1*t2+1.0)-rl*t2+xl);
      double c1 = (x1*xl-rl+x1*rl*t1)*(1.0-x1*(t1-t2)-rl-xl*t2)
         +(-x1*xl*t1+xl+x1*rl)*(t2-x1*(t1*t2+1.0)-rl*t2+xl);
      double rt = b1*b1-4.0*a1*c1;
      if (rt < 0.0) {
         tuned = false;
      } else {
         rt = Math.sqrt(rt);
         double x3p = (-b1+rt)/(2.0*a1);
         double x2p = (x1*xl-x3p*t3-rl+x1*x3p*(t1*t3-1.0)+x1*rl*t1)
            /(t2-x1*(t1*t2+1.0)-x3p*(t2*t3+1.0)-rl*t2+xl);
         double plossp = 1.0-(x2p*x2p*(1.0+t2*t2)*rl)
           /((x2p-x3p+xl)*(x2p-x3p+xl)+(t2*x2p+t3*x3p+rl)*(t2*x2p+t3*x3p+rl));
         boolean pgood = (x3p > x3min && x2p > 0.0 && x2p < x2max);
         double x3m = (-b1-rt)/(2.0*a1);
         double x2m = (x1*xl-x3m*t3-rl+x1*x3m*(t1*t3-1.0)+x1*rl*t1)
            /(t2-x1*(t1*t2+1.0)-x3m*(t2*t3+1.0)-rl*t2+xl);
         double plossm = 1.0-(x2m*x2m*(1.0+t2*t2)*rl)
           /((x2m-x3m+xl)*(x2m-x3m+xl)+(t2*x2m+t3*x3m+rl)*(t2*x2m+t3*x3m+rl));
         boolean mgood = (x3m > x3min && x2m > 0.0 && x2m < x2max);
         if (!mgood && !pgood) { // both solutions fail
            tuned = false;
            return;
         }
         tuned = true;
if (print) System.out.println(" m p " + mgood + " " + pgood);
         if (mgood && pgood) { // both work -- find lowest loss
            if (plossp < plossm) {
               x3 = x3p;
               x2 = x2p;
               ploss = plossp;
            } else {
               x3 = x3m;
               x2 = x2m;
               ploss = plossm;
            }
            return;
         }
         if (mgood) {
            x3 = x3m;
            x2 = x2m;
            ploss = plossm;
         } else {
            x3 = x3p;
            x2 = x2p;
            ploss = plossp;
         } 
      }
   }

// for fixed x2 calculate values of x1 and x3
   private void calx13(boolean print) {
      double a1 = ((x2*(t3-t2)+1.0)*(-t1*t3+1.0)+(-x2*(t2*t3+1.0)+t3)*(t1+t3));
      double b1 = (-x2+x2*xl*t2-xl+x2*rl)*(-t1*t3+1.0)
         +(x2*(t3-t2)+1.0)*(-x2*(t1*t2+1.0)-rl*t1-xl)
         +(x2*t2+x2*xl+rl-x2*rl*t2)*(t1+t3)
         +(-x2*(t2*t3+1.0)+t3)*(x2*(t2-t1)+rl-xl*t1);
      double c1 =(-x2+x2*xl*t2-xl+x2*rl)*(-x2*(t1*t2+1.0)-rl*t1-xl)
         +(x2*t2+x2*xl+rl-x2*rl*t2)*(x2*(t2-t1)+rl-xl*t1);
      double rt = b1*b1-4.0*a1*c1;
      if (rt < 0.0) {
         tuned = false;
         if (print) System.out.println("rt < 0");
      } else {
         rt = Math.sqrt(rt);
         double x3p = (-b1+rt)/(2.0*a1);
         double x1p = (-x2+x3p-xl+x2*x3p*(t3-t2)+x2*rl+x2*xl*t2)
            /(x2*(t2-t1)+x3p*(t1+t3)+rl-xl*t1);
         double plossp = 1.0-(x2*x2*(1.0+t2*t2)*rl)
           /((x2-x3p+xl)*(x2-x3p+xl)+(t2*x2+t3*x3p+rl)*(t2*x2+t3*x3p+rl));
         boolean pgood = (x3p > x3min && x1p > x1min);
         double x3m = (-b1-rt)/(2.0*a1);
         double x1m = (-x2+x3m-xl+x2*x3m*(t3-t2)+x2*rl+x2*xl*t2)
            /(x2*(t2-t1)+x3m*(t1+t3)+rl-xl*t1);
         double plossm = 1.0-(x2*x2*(1.0+t2*t2)*rl)
           /((x2-x3m+xl)*(x2-x3m+xl)+(t2*x2+t3*x3m+rl)*(t2*x2+t3*x3m+rl));
         boolean mgood = (x3m > x3min && x1m > x1min);
         if (!mgood && !pgood) { // both solutions fail
            tuned = false;
            return;
         }
         tuned = true;
if (print) System.out.println(" m p " + mgood + " " + pgood);
         if (mgood && pgood) { // both work -- find lowest loss
            if (plossp < plossm) {
               x3 = x3p;
               x1 = x1p;
               ploss = plossp;
            } else {
               x3 = x3m;
               x1 = x1m;
               ploss = plossm;
            }
            return;
         }
         if (mgood) {
            x3 = x3m;
            x1 = x1m;
            ploss = plossm;
         } else {
            x3 = x3p;
            x1 = x1p;
            ploss = plossp;
         } 
      }
   }

   public double getCin() {
      return 1.0/(x1*50.0*2.0*Math.PI*f*1.e-6*(1.+t1*t1));
   }

   public double getCout() {
      return 1.0/(x3*50.0*2.0*Math.PI*f*1.e-6*(1.+t3*t3));
   }

   public double getL() {
      return 50.0*x2/(2.0*Math.PI*f);
   }

   public boolean isTuned() {
      return tuned;
   }
}
