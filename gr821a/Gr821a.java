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
/**
Correct for residual parameters of the General Radio GR821A Twin-T
admittance measuring circuit.

@author Kevin Schmidt
@version @(#)Gr821a.java   0.01 February 8, 1998
*/

public class Gr821a {

   double lp,lc,lpp,rc; // residual parameters

/**
Create Gr821a class with measured values of L',Lc,L'',Rc
*/
   public Gr821a(double lp, double lc, double lpp, double rc) {
      this.lp = lp;
      this.lc = lc;
      this.lpp = lpp;
      this.rc = rc;
   }

/**
Create Gr821a class with default values of residual parameters
from manual. L' = 6.8nH, Lc = 6.1nH, L'' = 3.15nH, Rc = .026 Ohm
*/
   public Gr821a() {
      this(6.8e-9,6.1e-9,3.15e-9,0.026); // Use default values from manual
   }

/**
Calculate the corrected admittance in microSiemen at frequency f(MHz),
with conductance read from fread(MHz) range, cinitial is the initial
capacitance reading in pF, and cfinal is the final capacitance reading
in pF. Conductance is the value of conductance read from the dial of
the GR821A in micromho (microSieman).
*/
   public Complex admittance(double f, double fread, double cinitial,
      double cfinal, double conductance) {
      double rcf,omega,ce1,ce2,bm,bx,gx;

      conductance *= (f/fread)*(f/fread); //calculate conductance from dial
      rcf = Math.sqrt(f/30.)*rc; //scale resistance for skin effect
//
// calculate effective capacitance from lc
//
      omega = 2.*Math.PI*f;
      ce1 = cinitial/(1.0-omega*omega*lc*cinitial);
      ce2 = cfinal/(1.0-omega*omega*lc*cfinal);
      bm = omega*(ce1-ce2);
//
// correct susceptance and conductance for lp
//
      bx = bm/(1.+omega*lp*bm);
/*
      gx = conductance*(1.-omega*omega*lpp*ce1)*(1.-omega*omega*lpp*ce1)
             *(1.-omega*omega*lp*bx)*(1.-omega*omega*lp*bx)
             -1.e-6*rcf*omega*omega*(ce2*ce2-ce1*ce1);
*/
      gx = conductance*(1.-omega*omega*lpp*ce1)*(1.-omega*omega*lpp*ce1)
         +1.e-6*rcf*omega*bm*(ce1+ce2);
      gx /= (1.0+omega*lp*bm)*(1.0+omega*lp*bm);
      return new Complex(gx,bx);
   }
}
