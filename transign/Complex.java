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
public class Complex {

   private double r,i;

   Complex() {
      r = 0.0;
      i = 0.0;
   }

   Complex(double r, double i) {
      this.r = r;
      this.i = i;
   }

   Complex(Complex a) {
      r = a.r;
      i = a.i;
   }

   public Complex add(Complex a) {
      return new Complex(r+a.r,i+a.i);
   }

   public Complex add(double a) {
      return new Complex(r+a,i);
   }

   public Complex sub(Complex a) {
      return new Complex(r-a.r,i-a.i);
   }

   public Complex sub(double a) {
      return new Complex(r-a,i);
   }

   public Complex mult(Complex a) {
      return new Complex(a.r*r-a.i*i,a.r*i+a.i*r);
   }

   public Complex mult(double a) {
      return new Complex(a*r,a*i);
   }

   public Complex divide(Complex a) {
      double cmag2i;
      cmag2i = 1./(a.r*a.r+a.i*a.i);
      return new Complex(cmag2i*(r*a.r+i*a.i),cmag2i*(i*a.r-r*a.i));
   }

   public Complex divide(double a) {
      return new Complex(r/a,i/a);
   }

   public Complex exp() {
      double ex;
      ex = Math.exp(r);
      return new Complex(ex*Math.cos(i),ex*Math.sin(i));
   }

   public Complex log() {
      double cmag2;
      cmag2 = r*r+i*i;
      return new Complex(.5*Math.log(cmag2),Math.atan2(i,r));
   }

   public Complex cosh() {
      double ex,exi,c,s;
      ex = Math.exp(r);
      exi = 1./ex;
      c = Math.cos(i);
      s = Math.sin(i);
      return new Complex(0.5*(ex+exi)*c,0.5*(ex-exi)*s);
   }

   public Complex sinh() {
      double ex,exi,c,s;
      ex = Math.exp(r);
      exi = 1./ex;
      c = Math.cos(i);
      s = Math.sin(i);
      return new Complex(0.5*(ex-exi)*c,0.5*(ex+exi)*s);
   }

   public double abs() {
      return Math.sqrt(r*r+i*i);
   }

   public double real() {
      return r;
   }

   public double imag() {
      return i;
   }
   public Complex conjg() {
      return new Complex(r,-i);
   }
}
