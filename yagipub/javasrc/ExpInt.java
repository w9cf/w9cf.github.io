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
public final class ExpInt {

   private static int nf1;
   private static int nf2;
   private static int ng1;
   private static int ng2;
   private static double xbig,xmaxf,xmaxg,xbnd;
   private static int nci;
   private static int nsi;
   private static double xsml;
   private final static double pi2 = Math.PI*.5;

/*
 december 1980 edition.  w. fullerton, bell labs

 series for f1   on the interval  2.00000e-02 to  6.25000e-02
                 with weighted error   2.82e-17
                 log weighted error  16.55
                 significant figures required  15.36
                 decimal places required  17.20
*/
   private static double f1cs[] = {
      -0.1191081969051363610 ,
      -0.0247823144996236248 ,
       0.0011910281453357821 ,
      -0.0000927027714388562 ,
       0.0000093373141568271 ,
      -0.0000011058287820557 ,
       0.0000001464772071460 ,
      -0.0000000210694496288 ,
       0.0000000032293492367 ,
      -0.0000000005206529618 ,
       0.0000000000874878885 ,
      -0.0000000000152176187 ,
       0.0000000000027257192 ,
      -0.0000000000005007053 ,
       0.0000000000000940241 ,
      -0.0000000000000180014 ,
       0.0000000000000035063 ,
      -0.0000000000000006935 ,
       0.0000000000000001391 ,
      -0.0000000000000000282 };
/*
 series for f2   on the interval  0.00000e+00 to  2.00000e-02
                                        with weighted error   4.32e-17
                                         log weighted error  16.36
                               significant figures required  14.75
                                    decimal places required  17.10
*/
   private static double f2cs[] = {
      -0.0348409253897013234,
      -0.0166842205677959686,
       0.0006752901241237738,
      -0.0000535066622544701,
       0.0000062693421779007,
      -0.0000009526638801991,
       0.0000001745629224251,
      -0.0000000368795403065,
       0.0000000087202677705,
      -0.0000000022601970392,
       0.0000000006324624977,
      -0.0000000001888911889,
       0.0000000000596774674,
      -0.0000000000198044313,
       0.0000000000068641396,
      -0.0000000000024731020,
       0.0000000000009226360,
      -0.0000000000003552364,
       0.0000000000001407606,
      -0.0000000000000572623,
       0.0000000000000238654,
      -0.0000000000000101714,
       0.0000000000000044259,
      -0.0000000000000019634,
       0.0000000000000008868,
      -0.0000000000000004074,
       0.0000000000000001901,
      -0.0000000000000000900,
       0.0000000000000000432 };
/*
 series for g1   on the interval  2.00000e-02 to  6.25000e-02
                                        with weighted error   5.48e-17
                                         log weighted error  16.26
                               significant figures required  15.47
                                    decimal places required  16.92
*/
   private static double g1cs[] = {
      -0.3040578798253495954,
      -0.0566890984597120588,
       0.0039046158173275644,
      -0.0003746075959202261,
       0.0000435431556559844,
      -0.0000057417294453025,
       0.0000008282552104503,
      -0.0000001278245892595,
       0.0000000207978352949,
      -0.0000000035313205922,
       0.0000000006210824236,
      -0.0000000001125215474,
       0.0000000000209088918,
      -0.0000000000039715832,
       0.0000000000007690431,
      -0.0000000000001514697,
       0.0000000000000302892,
      -0.0000000000000061400,
       0.0000000000000012601,
      -0.0000000000000002615,
       0.0000000000000000548 };
/*
 series for g2   on the interval  0.00000e+00 to  2.00000e-02
                                        with weighted error   5.01e-17
                                         log weighted error  16.30
                               significant figures required  15.12
                                    decimal places required  17.07
*/
   private static double g2cs[] = {
      -0.0967329367532432218,
      -0.0452077907957459871,
       0.0028190005352706523,
      -0.0002899167740759160,
       0.0000407444664601121,
      -0.0000071056382192354,
       0.0000014534723163019,
      -0.0000003364116512503,
       0.0000000859774367886,
      -0.0000000238437656302,
       0.0000000070831906340,
      -0.0000000022318068154,
       0.0000000007401087359,
      -0.0000000002567171162,
       0.0000000000926707021,
      -0.0000000000346693311,
       0.0000000000133950573,
      -0.0000000000053290754,
       0.0000000000021775312,
      -0.0000000000009118621,
       0.0000000000003905864,
      -0.0000000000001708459,
       0.0000000000000762015,
      -0.0000000000000346151,
       0.0000000000000159996,
      -0.0000000000000075213,
       0.0000000000000035970,
      -0.0000000000000017530,
       0.0000000000000008738,
      -0.0000000000000004487,
       0.0000000000000002397,
      -0.0000000000000001347,
       0.0000000000000000801,
      -0.0000000000000000501 };

/*
series for ci   on the interval  0.00000e+00 to  1.60000e+01
                with weighted error   1.94e-18
                log weighted error  17.71
                significant figures required  17.74
                decimal places required  18.27
*/
   static double cics[] = {
      -.34004281856055363156 ,
     -1.03302166401177456807 ,
       .19388222659917082877 ,
      -.01918260436019865894 ,
       .00110789252584784967 ,
      -.00004157234558247209 ,
       .00000109278524300229 ,
      -.00000002123285954183 ,
       .00000000031733482164 ,
      -.00000000000376141548 ,
       .00000000000003622653 ,
      -.00000000000000028912 ,
       .00000000000000000194  };
/*
series for si   on the interval  0.00000e+00 to  1.60000e+01
                with weighted error   1.22e-17
                log weighted error  16.91
                significant figures required  16.37
                decimal places required  17.45
*/
   static double sics[] = {
      -0.1315646598184841929 ,
      -0.2776578526973601892 ,
       0.0354414054866659180 ,
      -0.0025631631447933978 ,
       0.0001162365390497009 ,
      -0.0000035904327241606 ,
       0.0000000802342123706 ,
      -0.0000000013562997693 ,
       0.0000000000179440722 ,
      -0.0000000000001908387 ,
       0.0000000000000016670 ,
      -0.0000000000000000122  };

   public static double si(double x) {
//
// Returns Si the Sine integral
//
      double y,f,g,sinx,cosx,ereal,eimag;
      if ( x < 0.0) {
         System.out.println("x is < 0 in si - stopping ");
         System.exit(1); // should throw exception
      }
      if ( x <= 4.0) {
         y = -1.0;
         eimag = x;
         if ( x > xsml ) {
            y = (x*x-8.0)*0.125;
            eimag = x*(0.75+csevl(y,sics,nsi));
         }
      } else {
         f = r9sif(x);
         g = r9sig(x);
         sinx = Math.sin(x);
         cosx = Math.cos(x);
         eimag = pi2-f*cosx-g*sinx;
      }
      return eimag;
   }

   public static double ci(double x) {
//
// Returns Ci the Cosine integral
//
      double y,f,g,sinx,cosx,ereal;
      if ( x < 0.0) {
         System.out.println("x is < 0 in ci - stopping ");
         System.exit(1); // should throw exception
      }
      if ( x <= 4.0) {
         y = -1.0;
         if ( x > xsml ) {
            y = (x*x-8.0)*0.125;
         }
         ereal = Math.log(x)-0.5+csevl(y,cics,nci);
      } else {
         f = r9sif(x);
         g = r9sig(x);
         sinx = Math.sin(x);
         cosx = Math.cos(x);
         ereal = f*sinx-g*cosx;
      }
      return ereal;
   }

   public static Complex ei(double x) {
//
// Returns Ci + i Si where Ci and Si are Cosine and Sine integrals
//
      double y,f,g,sinx,cosx,ereal,eimag;
      if ( x < 0.0) {
         System.out.println("x is < 0 in ei - stopping ");
         System.exit(1); // should throw exception
      }
      if ( x <= 4.0) {
         y = -1.0;
         eimag = x;
         if ( x > xsml ) {
            y = (x*x-8.0)*0.125;
            eimag = x*(0.75+csevl(y,sics,nsi));
         }
         ereal = Math.log(x)-0.5+csevl(y,cics,nci);
      } else {
         f = r9sif(x);
         g = r9sig(x);
         sinx = Math.sin(x);
         cosx = Math.cos(x);
         ereal = f*sinx-g*cosx;
         eimag = pi2-f*cosx-g*sinx;
      }
      return new Complex(ereal,eimag);
   }

   private static double csevl(double x, double[] cs, int n) {
/*
      april 1977 version.  w. fullerton, c3, los alamos scientific lab.
      evaluate the n-term chebyshev series cs at x.  adapted from
      r. broucke, algorithm 446, c.a.c.m., 16, 254 (1973).  also see fox
      and parker, chebyshev polys in numerical analysis, oxford press, p.56.
             input arguments --
      x      value at which the series is to be evaluated.
      cs     array of n terms of a chebyshev series.  in eval-
             uating cs, only half the first coef is summed.
      n      number of terms in array cs.

      Converted from fortran by Kevin Schmidt
*/
      double b0,b1,b2,twox;
      int i;
      b1 =  0.0;
      b0 = 0.0;
      b2 = 0.0;
      twox = 2.0*x;
      for (i=n-1;i>=0;i--) {
         b2 = b1;
         b1 = b0;
         b0 = twox*b1-b2+cs[i];
      }
      return 0.5*(b0-b2);
   }

   private static int inits(double[] os, int nos, double eta) {
/*
      april 1977 version.  w. fullerton, c3, los alamos scientific lab.
      initialize the orthogonal series so that inits is the number of terms
      needed to insure the error is no larger than eta.  ordinarily, eta
      will be chosen to be one-tenth machine precision.
             input arguments --
      os     array of nos coefficients in an orthogonal series.
      nos    number of coefficients in os.
      eta    requested accuracy of series.
*/
      int i;
      double err;
      err = 0.0;
      i = nos;
      while ( ((--i) >= 0) && (err < eta)) {
         err = err+Math.abs(os[i]);
      }
      i++;
      return i;
   }

   private static double r9sif(double x) {
      double arg;
      if ( x <= xbnd ) {
         arg = (1.0/(x*x)-0.04125)/0.02125;
          return (1.0+csevl(arg,f1cs,nf1))/x;
      } else {
         if ( x <= xbig ) {
            arg = 100./(x*x)-1.0;
            return (1.0+csevl(arg,f2cs,nf2))/x;
         } else {
            return (x<xmaxf) ? 1./x:0.0;
         }
      }
   }

   private static double r9sig(double x) {
      double arg;
      if ( x <= xbnd ) {
         arg = (1.0/(x*x)-0.04125)/0.02125;
         return (1.0+csevl(arg,g1cs,ng1))/(x*x);
      } else {
         if ( x <= xbig ) {
            arg = 100./(x*x)-1.0;
            return (1.0+csevl(arg,g2cs,ng2))/(x*x);
         } else {
            return (x<xmaxg) ? 1.0/(x*x):0.0;
         }
      }
   }

   static {
      double tol;
      tol = d1mach(3);
      tol = 1.e-6; // Gives a factor of 2 or so speed up
      nf1 = inits(f1cs,20,tol);
      nf2 = inits(f2cs,29,tol);
      ng1 = inits(g1cs,21,tol);
      ng2 = inits(g2cs,34,tol);
      xbig = Math.sqrt(1.0/tol);
      xmaxf = Math.exp(Math.min(-Math.log(d1mach(1)),Math.log(d1mach(2)))-0.01);
      xmaxg = 1.0/Math.sqrt(d1mach(1));
      xbnd = Math.sqrt(50.0);
      nci = inits(cics,13,d1mach(3));
      nsi = inits(sics,12,d1mach(3));
      xsml = Math.sqrt(d1mach(3));
   }

   private static double d1mach(int i) {
      switch (i) {
      case 1:
         return 2.22507e-308;
      case 2:
         return 1.79769e+308;
      case 3:
         return 1.11022e-16;
      }
      return 0;
   }
}

