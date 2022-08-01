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
public class K1fo220 {
   private final static double elenth0[] = {
       676.0, 647.0, 623.0, 608.0, 594.0, 587.0, 581.0, 576.0,
       573.0, 569.0, 565.0, 562.0, 558.0, 556.0, 554.0, 553.0,
       552.0, 551.0, 550.0, 549.0, 548.0, 547.0
   };
   private final static double elspac0[] = {
         0.0,  204.0,  292.0,  450.0,  668.0,  938.0, 1251.0, 1602.0,
      1985.0, 2395.0, 2829.0, 3283.0, 3755.0, 4243.0, 4745.0, 5259.0,
      5783.0, 6315.0, 6853.0, 7395.0, 7939.0, 8483.0
   };
   private final static double eldiam = 4.7625;
   private double[] elenth;
   private double[] elspac;
   private int nelem;

   public K1fo220(int nelem) throws ElementNumberRange {
      double correction;
      this.nelem = nelem;
      if (nelem < 12 || nelem > 22) throw new ElementNumberRange();
      elenth = new double[nelem];
      elspac = new double[nelem];
      for (int i=0;i<nelem;i++) {
         elenth[i] = elenth0[i];
         elspac[i] = elspac0[i];
      }
   }

   public int getNelem() {
      return nelem;
   }
   
   public double[] getElspac() {
      return elspac;
   }

   public double[] getElenth() {
      return elenth;
   }

   public double getEldiam() {
      return eldiam;
   }
}
