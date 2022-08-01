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

public class SimpleLine implements TEMLine {

   static double c = 299792458.0; //exact speed of light in meters/second
   private double att1MHz,ex; // atten dB/100ft at 1 MHz and exponent to scale
   private double r0; // characteristic resistance
   private double f,vf; //frequency MHz, velocity factor

   public SimpleLine() { // defaults to ideal 50 ohm line
      f = 3.5; 
      r0 = 50.0;
      att1MHz = 0.0;
      vf = 1.0;
      ex = 0.0;
   }

   public SimpleLine(double r0, double att1MHz, double vf, double ex) {
      f = 3.5;
      this.r0 = r0;
      this.att1MHz = att1MHz;
      this.vf = vf;
      this.ex = ex;
   }

   public void setR0(double r0) {
      this.r0 = r0;
   }

   public void setAtt1MHz(double att1MHz) {
      this.att1MHz = att1MHz;
   }

   public void setVf(double vf) {
      this.vf = vf;
   }

   public void setF(double f) {
      this.f = f;
   }

   public double getR0() {
      return r0;
   }

   public double getAtt1MHz() {
      return att1MHz;
   }

   public double getVf() {
      return vf;
   }

   public double getF() {
      return f;
   }

   public Complex Z0() { //Complex Z0 assuming only small conductor losses
      double alpha,beta; //atten nepers/100ft, propagation in radians/100ft
      alpha = (Math.log(10)/20.)*attendB();
      return new Complex(r0,-r0*alpha/beta());
   }

   public Complex gamma() { //Propagation constant in m^-1
      double alpha,beta;
      alpha = (Math.log(10)/20.)*attendB()/30.48;
      beta = 2.*Math.PI*f*1.e6/(c*vf);
      return new Complex(alpha,beta);
   }

   public double attendB() {
      return att1MHz*Math.pow(f,ex); //a fit to true attenuation
   }
 
   public double beta() {
      return 2.*Math.PI*f*1.e6/(c*vf)*30.48;
   }

   public double velfac() {
      return vf;
   }

}
