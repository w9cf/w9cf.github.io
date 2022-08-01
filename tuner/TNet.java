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
public class TNet {

   private double z1r,z2r,z3r,z1i,z2i,z3i;
   private double zlr,zli;

   public TNet(double z1r, double z1i, double z2r, double z2i,
      double z3r, double z3i, double zlr, double zli) {
      super();
      this.z1r = z1r;
      this.z2r = z2r;
      this.z3r = z3r;
      this.z1i = z1i;
      this.z2i = z2i;
      this.z3i = z3i;
      this.zlr = zlr;
      this.zli = zli;
   }

   public TNet() {
      super();
   }

   public void setZ1(double zr, double zi) {
      z1r = zr;
      z1i = zi;
   }

   public void setZ2(double zr, double zi) {
      z2r = zr;
      z2i = zi;
   }

   public void setZ3(double zr, double zi) {
      z3r = zr;
      z3i = zi;
   }

   public void setZl(double zr, double zi) {
      zlr = zr;
      zli = zi;
   }

   public double getRho() {
      double zinr,zini,zl2r,zl2i,tr,ti,t,r;
      tr = zlr+z3r+z2r;
      ti = zli+z3i+z2i;
      t = 1.0/(tr*tr+ti*ti);
      zl2r = z2r*z2r-z2i*z2i;
      zl2i = 2.0*z2r*z2i;
      zinr = z1r+z2r-(zl2r*tr+zl2i*ti)*t;
      zini = z1i+z2i-(zl2i*tr-zl2r*ti)*t;
      r = (zinr-1.0)*(zinr-1.0)+zini*zini;
      t = (zinr+1.0)*(zinr+1.0)+zini*zini;
      return Math.sqrt(r/t);
   }

   public double getLoss() {
      double zinr,zl2r,zl2i,tr,ti,t,r,z;
      tr = zlr+z3r+z2r;
      ti = zli+z3i+z2i;
      t = 1.0/(tr*tr+ti*ti);
      zl2r = z2r*z2r-z2i*z2i;
      zl2i = 2.0*z2r*z2i;
      zinr = z1r+z2r-(zl2r*tr+zl2i*ti)*t;
      z = z2r*z2r+z2i*z2i;
      t = (z3r+z2r+zlr)*(z3r+z2r+zlr)+(z3i+z2i+zli)*(z3i+z2i+zli);
      return 1.0-zlr*z/(t*zinr);
   }
}
