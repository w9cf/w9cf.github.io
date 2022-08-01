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

public class EllipticIntegral {

   public static final double LEMNISCATEA;
   public static final double LEMNISCATEB;

   static {
      LEMNISCATEA = RF(0.0,1.0,2.0);
      LEMNISCATEB = RD(0.0,2.0,1.0)/3.0;
   }
 
   public static double E(double ak) {
      double a,b,c,fact,temp,sum;
      double error = 1.e-12;
      int i;
      a = 1.0;
      fact = 1.0;
      b = Math.sqrt(1.0-ak*ak);
      temp = ak*ak;
      sum = 0.0;
      for (i=0;i<10000;i++) {
         sum += temp;
         c = 0.5*(a-b);
         temp = 0.5*(a+b);
         fact *= 2.0;
         b = Math.sqrt(a*b);
         a = temp;
         temp = fact*c*c;
         if (Math.abs(c) < error*a && temp < error*sum) break;
      }
      return Math.PI*(1.0-0.5*sum)/(a+b);
   }

   public static double EL1(double x, double akc) {
      return x*RF(1.0,1.0+akc*x,1.0+x);
   }

   public static double ZETA(double b, double ak) {
      double s = Math.sin(b);
      double c = Math.cos(b);
      return (ak*ak/3.0)*s*(RF(c*c,1.0-ak*ak*s*s,1.0)*RD(0.0,1.0-ak*ak,1.0)/
         RF(0.0,1.0-ak*ak,1.0)-s*RD(c*c,1.0-ak*ak*s*s,1.0));
   }

   public static double K(double ak) {
      int i;
      double a,b,c,fact,temp;
      double error = 1.e-12;
      a = 1.0;
      fact = 1.0;
      b = Math.sqrt(1.0-ak*ak);
      temp = ak*ak;
      for (i=0;i<10000;i++) {
         c = 0.5*(a-b);
         temp = 0.5*(a+b);
         fact *= 2.0;
         b = Math.sqrt(a*b);
         a = temp;
         temp = fact*c*c;
         if (Math.abs(c) < error*a) break;
      }
      return Math.PI/(a+b);
   }


   public static double F(double phi, double ak) {
      double c = Math.cos(phi);
      double s = Math.sin(phi);
      double c2 = c*c;
      double ss = 1.0-ak*ak*s*s;
      double f = EllipticIntegral.RF(c2,ss,1.0);
      return s*f;
   }

   public static double E(double phi, double ak) {
      double c = Math.cos(phi);
      double s = Math.sin(phi);
      double c2 = c*c;
      double ss = 1.0-ak*ak*s*s;
      double f = EllipticIntegral.RF(c2,ss,1.0);
      double d = EllipticIntegral.RD(c2,ss,1.0);
      return s*f-ak*ak*s*s*s*d/3.0;
   }

   public static double RD(double x, double y, double z) {

      double errtol = 0.00182549856;
      double lolim = 6.27893936E-206;
      double tuplim = 2.0168892E+101;
      double uplim = 4.06784205E+202;
      double c1 = 3.0/14.0;
      double c2 = 1.0/6.0;
      double c3 = 9.0/22.0;
      double c4 = 3.0/26.0;
      double epslon,ea,eb,ec,ed,ef,lamda;
      double mu,power4,sigma,s1,s2,xn,xndev;
      double xnroot,yn,yndev,ynroot,zn,zndev,znroot;
      if (Math.min(x,y) < 0.0) {
         throw new
         IllegalArgumentException(" x = " + x + " y = " + y + " min(x,y) < 0");
      }
      if (Math.max(x,Math.max(y,z)) > uplim) {
         throw new
         IllegalArgumentException(" x = " + x + " y = " + y + " z = " + z
         + " max(x,y,z) > uplim");
      }
      if (Math.min(x+y,z) < lolim) {
         throw new
         IllegalArgumentException(" x = " + x + " y = " + y + " z = " + z
         + " min(x+y,z) < lolim");
      }
      xn = x;
      yn = y;
      zn = z;
      sigma = 0.0;
      power4 = 1.0;

      while (true) {
         mu = (xn+yn+3.0*zn)*0.20;
         xndev = (mu-xn)/mu;
         yndev = (mu-yn)/mu;
         zndev = (mu-zn)/mu;
         epslon = Math.max(Math.abs(xndev),
            Math.max(Math.abs(yndev),Math.abs(zndev)));
         if (epslon < errtol) break;
         xnroot = Math.sqrt(xn);
         ynroot = Math.sqrt(yn);
         znroot = Math.sqrt(zn);
         lamda = xnroot*(ynroot+znroot)+ynroot*znroot;
         sigma += power4/(znroot*(zn+lamda));
         power4 *= 0.250;
         xn = (xn+lamda)*0.250;
         yn = (yn+lamda)*0.250;
         zn = (zn+lamda)*0.250;
      }
      ea = xndev*yndev;
      eb = zndev*zndev;
      ec = ea-eb;
      ed = ea-6.0*eb;
      ef = ed+ec+ec;
      s1 = ed*(-c1+0.250*c3*ed-1.50*c4*zndev*ef);
      s2 = zndev*(c2*ef+zndev*(-c3*ec+zndev*c4*ea));
      return 3.0*sigma+power4*(1.0+s1+s2)/(mu*Math.sqrt(mu));
   }

   public static double RF(double x, double y, double z) {
      double lolim = 1.11253693e-307;
      double uplim = 3.59538627e+307;
      double errtol = 0.00276213586;
      double c5 = 1.0/24.0;
      double c6 = 3.0/44.0;
      double c7 = 1.0/14.0;
      double epslon,e2,e3,lamda,mu,s,xn,xndev,xnroot,yn,yndev;
      double ynroot,zn,zndev,znroot;
      if (Math.min(x,Math.min(y,z)) < 0.0) {
         throw new
         IllegalArgumentException(" x = " + x + " y = " + y +
         " min(x,y,z) < 0");
      }
      if (Math.max(x,Math.max(y,z)) > uplim) {
         throw new
         IllegalArgumentException(" x = " + x + " y = " + y +
         " min(x,y,z) > uplim");
      }
      if (Math.min(x+y,Math.min(x+z,y+z)) < lolim) {
         throw new
         IllegalArgumentException(" x = " + x + " y = " + y +
         " min(x+y,x+z,y+z) < lolim");
      }
      xn = x;
      yn = y;
      zn = z;
      while (true) {
         mu = (xn+yn+zn)/3.0;
         xndev = 2.0-(mu+xn)/mu;
         yndev = 2.0-(mu+yn)/mu;
         zndev = 2.0-(mu+zn)/mu;
         epslon = Math.max(Math.abs(xndev)
            ,Math.max(Math.abs(yndev),Math.abs(zndev)));
         if (epslon < errtol) break;
         xnroot = Math.sqrt(xn);
         ynroot = Math.sqrt(yn);
         znroot = Math.sqrt(zn);
         lamda = xnroot*(ynroot+znroot)+ynroot*znroot;
         xn = (xn+lamda)*0.25;
         yn = (yn+lamda)*0.25;
         zn = (zn+lamda)*0.25;
      }
      e2 = xndev*yndev-zndev*zndev;
      e3 = xndev*yndev*zndev;
      s  = 1.0+(c5*e2-0.10-c6*e3)*e2+c7*e3;
      return s/Math.sqrt(mu);
   }
/*
comments from the fortran codes
C***BEGIN PROLOGUE  DRD
C***PURPOSE  Compute the incomplete or complete elliptic integral of
C            the 2nd kind. For X and Y nonnegative, X+Y and Z positive,
C            DRD(X,Y,Z) = Integral from zero to infinity of
C                                -1/2     -1/2     -3/2
C                      (3/2)(t+X)    (t+Y)    (t+Z)    dt.
C            If X or Y is zero, the integral is complete.
C***LIBRARY   SLATEC
C***CATEGORY  C14
C***TYPE      DOUBLE PRECISION (RD-S, DRD-D)
C***KEYWORDS  COMPLETE ELLIPTIC INTEGRAL, DUPLICATION THEOREM,
C             INCOMPLETE ELLIPTIC INTEGRAL, INTEGRAL OF THE SECOND KIND,
C             TAYLOR SERIES
C***AUTHOR  Carlson, B. C.
C             Ames Laboratory-DOE
C             Iowa State University
C             Ames, IA  50011
C           Notis, E. M.
C             Ames Laboratory-DOE
C             Iowa State University
C             Ames, IA  50011
C           Pexton, R. L.
C             Lawrence Livermore National Laboratory
C             Livermore, CA  94550
C***DESCRIPTION
C
C   1.     DRD
C          Evaluate an INCOMPLETE (or COMPLETE) ELLIPTIC INTEGRAL
C          of the second kind
C          Standard FORTRAN function routine
C          Double precision version
C          The routine calculates an approximation result to
C          DRD(X,Y,Z) = Integral from zero to infinity of
C                              -1/2     -1/2     -3/2
C                    (3/2)(t+X)    (t+Y)    (t+Z)    dt,
C          where X and Y are nonnegative, X + Y is positive, and Z is
C          positive.  If X or Y is zero, the integral is COMPLETE.
C          The duplication theorem is iterated until the variables are
C          nearly equal, and the function is then expanded in Taylor
C          series to fifth order.
C
C   2.     Calling Sequence
C
C          DRD( X, Y, Z, IER )
C
C          Parameters On Entry
C          Values assigned by the calling routine
C
C          X      - Double precision, nonnegative variable
C
C          Y      - Double precision, nonnegative variable
C
C                   X + Y is positive
C
C          Z      - Double precision, positive variable
C
C
C
C          On Return    (values assigned by the DRD routine)
C
C          DRD     - Double precision approximation to the integral
C
C
C          IER    - Integer
C
C                   IER = 0 Normal and reliable termination of the
C                           routine. It is assumed that the requested
C                           accuracy has been achieved.
C
C                   IER >  0 Abnormal termination of the routine
C
C
C          X, Y, Z are unaltered.
C
C   3.    Error Messages
C
C         Value of IER assigned by the DRD routine
C
C                  Value assigned         Error message printed
C                  IER = 1                MIN(X,Y) .LT. 0.0D0
C                      = 2                MIN(X + Y, Z ) .LT. LOLIM
C                      = 3                MAX(X,Y,Z) .GT. UPLIM
C
C
C   4.     Control Parameters
C
C                  Values of LOLIM, UPLIM, and ERRTOL are set by the
C                  routine.
C
C          LOLIM and UPLIM determine the valid range of X, Y, and Z
C
C          LOLIM  - Lower limit of valid arguments
C
C                    Not less  than 2 / (machine maximum) ** (2/3).
C
C          UPLIM  - Upper limit of valid arguments
C
C                 Not greater than (0.1D0 * ERRTOL / machine
C                 minimum) ** (2/3), where ERRTOL is described below.
C                 In the following table it is assumed that ERRTOL will
C                 never be chosen smaller than 1.0D-5.
C
C
C                    Acceptable values for:   LOLIM      UPLIM
C                    IBM 360/370 SERIES   :   6.0D-51     1.0D+48
C                    CDC 6000/7000 SERIES :   5.0D-215    2.0D+191
C                    UNIVAC 1100 SERIES   :   1.0D-205    2.0D+201
C                    CRAY                 :   3.0D-1644   1.69D+1640
C                    VAX 11 SERIES        :   1.0D-25     4.5D+21
C
C
C          ERRTOL determines the accuracy of the answer
C
C                 The value assigned by the routine will result
C                 in solution precision within 1-2 decimals of
C                 "machine precision".
C
C          ERRTOL    Relative error due to truncation is less than
C                    3 * ERRTOL ** 6 / (1-ERRTOL) ** 3/2.
C
C
C
C        The accuracy of the computed approximation to the integral
C        can be controlled by choosing the value of ERRTOL.
C        Truncation of a Taylor series after terms of fifth order
C        introduces an error less than the amount shown in the
C        second column of the following table for each value of
C        ERRTOL in the first column.  In addition to the truncation
C        error there will be round-off error, but in practice the
C        total error from both sources is usually less than the
C        amount given in the table.
C
C
C
C
C          Sample choices:  ERRTOL   Relative truncation
C                                    error less than
C                           1.0D-3    4.0D-18
C                           3.0D-3    3.0D-15
C                           1.0D-2    4.0D-12
C                           3.0D-2    3.0D-9
C                           1.0D-1    4.0D-6
C
C
C                    Decreasing ERRTOL by a factor of 10 yields six more
C                    decimal digits of accuracy at the expense of one or
C                    two more iterations of the duplication theorem.
C
C *Long Description:
C
C   DRD Special Comments
C
C
C
C          Check: DRD(X,Y,Z) + DRD(Y,Z,X) + DRD(Z,X,Y)
C          = 3 / SQRT(X * Y * Z), where X, Y, and Z are positive.
C
C
C          On Input:
C
C          X, Y, and Z are the variables in the integral DRD(X,Y,Z).
C
C
C          On Output:
C
C
C          X, Y, Z are unaltered.
C
C
C
C          ********************************************************
C
C          WARNING: Changes in the program may improve speed at the
C                   expense of robustness.
C
C
C
C    -------------------------------------------------------------------
C
C
C   Special double precision functions via DRD and DRF
C
C
C                  Legendre form of ELLIPTIC INTEGRAL of 2nd kind
C
C                  -----------------------------------------
C
C
C                                             2         2   2
C                  E(PHI,K) = SIN(PHI) DRF(COS (PHI),1-K SIN (PHI),1) -
C
C                     2      3             2         2   2
C                  -(K/3) SIN (PHI) DRD(COS (PHI),1-K SIN (PHI),1)
C
C
C                                  2        2            2
C                  E(K) = DRF(0,1-K ,1) - (K/3) DRD(0,1-K ,1)
C
C                         PI/2     2   2      1/2
C                       = INT  (1-K SIN (PHI) )  D PHI
C                          0
C
C                  Bulirsch form of ELLIPTIC INTEGRAL of 2nd kind
C
C                  -----------------------------------------
C
C                                               2 2    2
C                  EL2(X,KC,A,B) = AX DRF(1,1+KC X ,1+X ) +
C
C                                              3          2 2    2
C                                 +(1/3)(B-A) X DRD(1,1+KC X ,1+X )
C
C
C
C
C                  Legendre form of alternative ELLIPTIC INTEGRAL
C                  of 2nd kind
C
C                  -----------------------------------------
C
C
C
C                            Q     2       2   2  -1/2
C                  D(Q,K) = INT SIN P  (1-K SIN P)     DP
C                            0
C
C
C
C                                     3          2     2   2
C                  D(Q,K) = (1/3) (SIN Q) DRD(COS Q,1-K SIN Q,1)
C
C
C
C
C                  Lemniscate constant  B
C
C                  -----------------------------------------
C
C
C
C
C                       1    2    4 -1/2
C                  B = INT  S (1-S )    DS
C                       0
C
C
C                  B = (1/3) DRD (0,2,1)
C
C
C                  Heuman's LAMBDA function
C
C                  -----------------------------------------
C
C
C
C                  (PI/2) LAMBDA0(A,B) =
C
C                                    2                2
C                 = SIN(B) (DRF(0,COS (A),1)-(1/3) SIN (A) *
C
C                            2               2         2       2
C                  *DRD(0,COS (A),1)) DRF(COS (B),1-COS (A) SIN (B),1)
C
C                            2       3             2
C                  -(1/3) COS (A) SIN (B) DRF(0,COS (A),1) *
C
C                           2         2       2
C                   *DRD(COS (B),1-COS (A) SIN (B),1)
C
C
C
C                  Jacobi ZETA function
C
C                  -----------------------------------------
C
C                             2                 2       2   2
C                  Z(B,K) = (K/3) SIN(B) DRF(COS (B),1-K SIN (B),1)
C
C
C                                       2             2
C                             *DRD(0,1-K ,1)/DRF(0,1-K ,1)
C
C                               2       3           2       2   2
C                            -(K /3) SIN (B) DRD(COS (B),1-K SIN (B),1)
C
C
C ---------------------------------------------------------------------
C
C***REFERENCES  B. C. Carlson and E. M. Notis, Algorithms for incomplete
C                 elliptic integrals, ACM Transactions on Mathematical
C                 Software 7, 3 (September 1981), pp. 398-403.
C               B. C. Carlson, Computing elliptic integrals by
C                 duplication, Numerische Mathematik 33, (1979),
C                 pp. 1-16.
C               B. C. Carlson, Elliptic integrals of the first kind,
C                 SIAM Journal of Mathematical Analysis 8, (1977),
C                 pp. 231-242.



C***BEGIN PROLOGUE  DRF
C***PURPOSE  Compute the incomplete or complete elliptic integral of the
C            1st kind.  For X, Y, and Z non-negative and at most one of
C            them zero, RF(X,Y,Z) = Integral from zero to infinity of
C                                -1/2     -1/2     -1/2
C                      (1/2)(t+X)    (t+Y)    (t+Z)    dt.
C            If X, Y or Z is zero, the integral is complete.
C***LIBRARY   SLATEC
C***CATEGORY  C14
C***TYPE      DOUBLE PRECISION (RF-S, DRF-D)
C***KEYWORDS  COMPLETE ELLIPTIC INTEGRAL, DUPLICATION THEOREM,
C             INCOMPLETE ELLIPTIC INTEGRAL, INTEGRAL OF THE FIRST KIND,
C             TAYLOR SERIES
C***AUTHOR  Carlson, B. C.
C             Ames Laboratory-DOE
C             Iowa State University
C             Ames, IA  50011
C           Notis, E. M.
C             Ames Laboratory-DOE
C             Iowa State University
C             Ames, IA  50011
C           Pexton, R. L.
C             Lawrence Livermore National Laboratory
C             Livermore, CA  94550
C***DESCRIPTION
C
C   1.     DRF
C          Evaluate an INCOMPLETE (or COMPLETE) ELLIPTIC INTEGRAL
C          of the first kind
C          Standard FORTRAN function routine
C          Double precision version
C          The routine calculates an approximation result to
C          DRF(X,Y,Z) = Integral from zero to infinity of
C
C                               -1/2     -1/2     -1/2
C                     (1/2)(t+X)    (t+Y)    (t+Z)    dt,
C
C          where X, Y, and Z are nonnegative and at most one of them
C          is zero.  If one of them  is zero, the integral is COMPLETE.
C          The duplication theorem is iterated until the variables are
C          nearly equal, and the function is then expanded in Taylor
C          series to fifth order.
C
C   2.     Calling sequence
C          DRF( X, Y, Z, IER )
C
C          Parameters On entry
C          Values assigned by the calling routine
C
C          X      - Double precision, nonnegative variable
C
C          Y      - Double precision, nonnegative variable
C
C          Z      - Double precision, nonnegative variable
C
C
C
C          On Return    (values assigned by the DRF routine)
C
C          DRF     - Double precision approximation to the integral
C
C          IER    - Integer
C
C                   IER = 0 Normal and reliable termination of the
C                           routine. It is assumed that the requested
C                           accuracy has been achieved.
C
C                   IER >  0 Abnormal termination of the routine
C
C          X, Y, Z are unaltered.
C
C
C   3.    Error Messages
C
C
C         Value of IER assigned by the DRF routine
C
C                  Value assigned         Error Message Printed
C                  IER = 1                MIN(X,Y,Z) .LT. 0.0D0
C                      = 2                MIN(X+Y,X+Z,Y+Z) .LT. LOLIM
C                      = 3                MAX(X,Y,Z) .GT. UPLIM
C
C
C
C   4.     Control Parameters
C
C                  Values of LOLIM, UPLIM, and ERRTOL are set by the
C                  routine.
C
C          LOLIM and UPLIM determine the valid range of X, Y and Z
C
C          LOLIM  - Lower limit of valid arguments
C
C                   Not less than 5 * (machine minimum).
C
C          UPLIM  - Upper limit of valid arguments
C
C                   Not greater than (machine maximum) / 5.
C
C
C                     Acceptable values for:   LOLIM      UPLIM
C                     IBM 360/370 SERIES   :   3.0D-78     1.0D+75
C                     CDC 6000/7000 SERIES :   1.0D-292    1.0D+321
C                     UNIVAC 1100 SERIES   :   1.0D-307    1.0D+307
C                     CRAY                 :   2.3D-2466   1.09D+2465
C                     VAX 11 SERIES        :   1.5D-38     3.0D+37
C
C
C
C          ERRTOL determines the accuracy of the answer
C
C                 The value assigned by the routine will result
C                 in solution precision within 1-2 decimals of
C                 "machine precision".
C
C
C
C          ERRTOL - Relative error due to truncation is less than
C                   ERRTOL ** 6 / (4 * (1-ERRTOL)  .
C
C
C
C        The accuracy of the computed approximation to the integral
C        can be controlled by choosing the value of ERRTOL.
C        Truncation of a Taylor series after terms of fifth order
C        introduces an error less than the amount shown in the
C        second column of the following table for each value of
C        ERRTOL in the first column.  In addition to the truncation
C        error there will be round-off error, but in practice the
C        total error from both sources is usually less than the
C        amount given in the table.
C
C
C
C
C
C          Sample choices:  ERRTOL   Relative Truncation
C                                    error less than
C                           1.0D-3    3.0D-19
C                           3.0D-3    2.0D-16
C                           1.0D-2    3.0D-13
C                           3.0D-2    2.0D-10
C                           1.0D-1    3.0D-7
C
C
C                    Decreasing ERRTOL by a factor of 10 yields six more
C                    decimal digits of accuracy at the expense of one or
C                    two more iterations of the duplication theorem.
C
C *Long Description:
C
C   DRF Special Comments
C
C
C
C          Check by addition theorem: DRF(X,X+Z,X+W) + DRF(Y,Y+Z,Y+W)
C          = DRF(0,Z,W), where X,Y,Z,W are positive and X * Y = Z * W.
C
C
C          On Input:
C
C          X, Y, and Z are the variables in the integral DRF(X,Y,Z).
C
C
C          On Output:
C
C
C          X, Y, Z are unaltered.
C
C
C
C          ********************************************************
C
C          WARNING: Changes in the program may improve speed at the
C                   expense of robustness.
C
C
C
C   Special double precision functions via DRF
C
C
C
C
C                  Legendre form of ELLIPTIC INTEGRAL of 1st kind
C
C                  -----------------------------------------
C
C
C
C                                             2         2   2
C                  F(PHI,K) = SIN(PHI) DRF(COS (PHI),1-K SIN (PHI),1)
C
C
C                                  2
C                  K(K) = DRF(0,1-K ,1)
C
C
C                         PI/2     2   2      -1/2
C                       = INT  (1-K SIN (PHI) )   D PHI
C                          0
C
C
C
C                  Bulirsch form of ELLIPTIC INTEGRAL of 1st kind
C
C                  -----------------------------------------
C
C
C                                          2 2    2
C                  EL1(X,KC) = X DRF(1,1+KC X ,1+X )
C
C
C                  Lemniscate constant A
C
C                  -----------------------------------------
C
C
C                       1      4 -1/2
C                  A = INT (1-S )    DS = DRF(0,1,2) = DRF(0,2,1)
C                       0
C
C
C
C    -------------------------------------------------------------------
C
C***REFERENCES  B. C. Carlson and E. M. Notis, Algorithms for incomplete
C                 elliptic integrals, ACM Transactions on Mathematical
C                 Software 7, 3 (September 1981), pp. 398-403.
C               B. C. Carlson, Computing elliptic integrals by
C                 duplication, Numerische Mathematik 33, (1979),
C                 pp. 1-16.
C               B. C. Carlson, Elliptic integrals of the first kind,
C                 SIAM Journal of Mathematical Analysis 8, (1977),
C                 pp. 231-242.
*/

/*
   public static void main(String[] arg) {
      for (int i=0;i<10;i++) {
         double x = i*0.1+.05;
         System.out.println("E " + x + " " + EllipticIntegral.E(x));
         System.out.println("E(p " + x + " " + EllipticIntegral.E(Math.PI/2.,x));
         System.out.println("K " + x + " " + EllipticIntegral.K(x));
         System.out.println("F " + x + " " + EllipticIntegral.F(Math.PI/2.,x));
      }
   }
*/
}
