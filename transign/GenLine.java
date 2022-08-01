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

public class GenLine implements TEMLine {

   static double c = 299792458.0; //exact speed of light in meters/second
   private double att,vf,f;
   private double r0,x0; // characteristic impedance

   public GenLine(double r0, double x0, double att, double vf) {
      this.r0 = r0;
      this.x0 = x0;
      this.att = att*Math.log(10.)/20./30.48;  //convert to nepers/m
      this.vf = vf;
   }

   public Complex Z0() {
      return new Complex(r0,x0);
   }

   public void setF(double f) {
      this.f = f;
   }

   public double attendB() {
      return att*20./Math.log(10.)*30.48;
   }

   public Complex gamma() { //Propagation constant in m^-1
      double alpha,beta;
      alpha = att;
      beta = 2.*Math.PI*f*1.e6/(c*vf);
      return new Complex(alpha,beta);
   }

   public double velfac() {
      return vf;
   }
}
