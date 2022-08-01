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
import java.io.*;

public class RectangularCrossSection {

   private double selfDiameter;
   private double resistanceDiameter;
   private double width;
   private double thickness;
   private double ak;
   private static final double small = 1.e-12;

   public RectangularCrossSection(double side1, double side2) {
      width = Math.max(side1,side2);
      thickness = Math.min(side1,side2);
      int i;
      double akp,aspectnow;
      double aspect = width/thickness;
      double ellipe = 1.0;
      double ellipep = 1.0;
      double ellipk = 1.0;
      double ellipkp = 1.0;
      ak = 0.5;
      double akl = 0.0;
      double aku = 1.0/Math.sqrt(2.0);
      for (i=0;i<1000;i++) {
         ak = 0.5*(aku+akl);
         akp=Math.sqrt(1.0-ak*ak);
         ellipe = EllipticIntegral.E(ak);
         ellipep = EllipticIntegral.E(akp);
         ellipk = EllipticIntegral.K(ak);
         ellipkp = EllipticIntegral.K(akp);
         aspectnow = (ellipep-ak*ak*ellipkp)/(ellipe-akp*akp*ellipk);
         if (aspectnow > aspect) {
            akl = ak;
         } else {
            aku = ak;
         }
         if (Math.abs(akl-aku) < small*ak) break;
      }
//      System.out.println(ak);
      selfDiameter = 0.5*width/(ellipep-ak*ak*ellipkp);
      resistanceDiameter =
         Math.PI*width/(2.0*(ellipk+ellipkp)*(ellipep-ak*ak*ellipkp));
   }

   public double getWidth() {
      return width;
   }

   public double getThickness() {
      return thickness;
   }
   
   public double getResistanceDiameter() {
      return resistanceDiameter;
   }

   public double getSelfDiameter() {
      return selfDiameter;
   }

   public double charge(double x) {
      if (x < 0.0 || x > width+thickness) return 0.0;
      double phi = 0.0;
      if (x <= width) {
         double akp = Math.sqrt(1.0-ak*ak);
         double side = 2.0*(EllipticIntegral.E(akp)
            -ak*ak*EllipticIntegral.K(akp));
         x /= width;
         x -= 0.5;
         x = Math.abs(x);
         x *= 2.0*side;
         double phiu = Math.PI*0.5;
         double phil = 0.0;
         for (int i=0;i<100;i++) {
            phi = 0.5*(phiu+phil);
            double xt = 2.0*(EllipticIntegral.E(phi,akp)
               -ak*ak*EllipticIntegral.F(phi,akp));
            if (xt < x) {
            phil = phi;
            } else {
               phiu = phi;
            }
            if (Math.abs(phiu-phil) < small*phi) break;
         }
         return side/(2.0*Math.PI*akp*Math.cos(phi));
      } else {
         double akp = Math.sqrt(1.0-ak*ak);
         double side = 2.0*(EllipticIntegral.E(ak)
            -akp*akp*EllipticIntegral.K(ak));
         x -= width;
         x /= thickness;
         x -= 0.5;
         x = Math.abs(x);
         x *= 2.0*side;
         double phiu = Math.PI*0.5;
         double phil = 0.0;
         for (int i=0;i<100;i++) {
            phi = 0.5*(phiu+phil);
            double xt = 2.0*(EllipticIntegral.E(phi,ak)
               -akp*akp*EllipticIntegral.F(phi,ak));
            if (xt < x) {
               phil = phi;
            } else {
               phiu = phi;
            }
            if (Math.abs(phiu-phil) < small*phi) break;
         }
         side = 2.0*(EllipticIntegral.E(akp)
            -ak*ak*EllipticIntegral.K(akp));
         return side/(2.0*Math.PI*ak*Math.cos(phi));
      }
   }

   public void widthCharge(double[] charge, double[] x) {
      if (charge.length != x.length) {
         throw new
         IllegalArgumentException(" x.length " + x.length + " charge.length "
            + charge.length + "not equal");
      }
      int n = charge.length;
      double dphi = 0.5*Math.PI/n;
      double akp = Math.sqrt(1.0-ak*ak);
      double side = 2.0*(EllipticIntegral.E(akp)
         -ak*ak*EllipticIntegral.K(akp));
      double phi;
      for (int i=0;i<n;i++) {
         phi = (i+0.5)*dphi;
         x[i] = (EllipticIntegral.E(phi,akp)
               -ak*ak*EllipticIntegral.F(phi,akp))*width/side;
         charge[i] = side/(2.0*Math.PI*akp*Math.cos(phi))/width;
      }
   }

   public void thicknessCharge(double[] charge, double[] x) {
      if (charge.length != x.length) {
         throw new
         IllegalArgumentException(" x.length " + x.length + " charge.length "
            + charge.length + "not equal");
      }
      int n = charge.length;
      double dphi = 0.5*Math.PI/n;
      double akp = Math.sqrt(1.0-ak*ak);
      double s2 = 2.0*(EllipticIntegral.E(ak)
         -akp*akp*EllipticIntegral.K(ak));
      double phi;
      for (int i=0;i<n;i++) {
         phi = (i+0.5)*dphi;
         x[i] = (EllipticIntegral.E(phi,ak)
               -akp*akp*EllipticIntegral.F(phi,ak))*thickness/s2;
         charge[i] = s2/(2.0*thickness*Math.PI*ak*Math.cos(phi));
      }
   }
/*
   public static void main(String[] arg) {
      double s1 = 1.0;
      double s2 = 0.01;
      RectangularCrossSection r = new RectangularCrossSection(s1,s2);
      System.out.println("#"+r.getResistanceDiameter());
      System.out.println("#"+r.getSelfDiameter());
      int nplot = 1000;
      double dx = 0.5*s1/nplot;
      for (int i=0;i<nplot;i++) {
         double x = 0.5*s1+dx*i;
         System.out.println((x-0.5*s1) + " " + r.charge(x));
      }
      dx = 0.5*s2/nplot;
      for (int i=0;i<nplot;i++) {
         double x = s1+dx*(i+1);
         System.out.println((x-0.5*s1) + " " + r.charge(x));
      }
      System.out.println(" ");
      double[] charge = new double[nplot];
      double[] x = new double[nplot];
      r.widthCharge(charge,x);
      for (int i=0;i<nplot;i++) {
         System.out.println(x[i] + " " + charge[i]);
      }
      r.thicknessCharge(charge,x);
      for (int i=0;i<nplot;i++) {
         System.out.println((s1*.5+0.5*s2-x[nplot-1-i]) + " " + charge[nplot-1-i]);
      }
   }
*/
      
}

