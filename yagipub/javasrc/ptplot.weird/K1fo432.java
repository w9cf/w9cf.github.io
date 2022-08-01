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
public class K1fo432 {
   private final static double elenth0[] = {
      340.0, 334.0, 315.0, 306.0, 299.0, 295.0, 291.0, 289.0, 287.0,
      285.0, 283.0, 281.0, 279.0, 278.0, 277.0, 276.0, 275.0, 274.0,
      273.0, 272.0, 271.0, 270.0, 269.0, 269.0, 268.0, 268.0, 267.0,
      267.0, 266.0, 266.0, 265.0, 265.0, 264.0, 264.0, 263.0, 263.0,
      262.0, 262.0, 261.0, 261.0
   };
   private final static double elspac0[] = {
         0.0,  104.0,  146.0,  224.0,  332.0,  466.0,  622.0,  798.0,
       990.0, 1196.0, 1414.0, 1642.0, 1879.0, 2122.0, 2373.0, 2629.0,
      2890.0, 3154.0, 3422.0, 3693.0, 3967.0, 4242.0, 4520.0, 4798.0,
      5079.0, 5360.0, 5642.0, 5925.0, 6209.0, 6494.0, 6779.0, 7064.0,
      7350.0, 7636.0, 7922.0, 8209.0, 8496.0, 8783.0, 9070.0, 9359.0
   };
   private final static double eldiam = 4.7625;
   private double[] elenth;
   private double[] elspac;
   private int nelem;

   public K1fo432(int nelem) throws ElementNumberRange {
      double correction;
      this.nelem = nelem;
      correction = -2.0;
      if (nelem < 12 || nelem > 40) throw new ElementNumberRange();
      if (nelem > 16) correction = -1.0;
      if (nelem > 19) correction = 0.0;
      if (nelem > 25) correction = 1.0;
      if (nelem > 30) correction = 2.0;
      if (nelem > 38) correction = 3.0;
      elenth = new double[nelem];
      elspac = new double[nelem];
      for (int i=0;i<nelem;i++) {
         elenth[i] = elenth0[i]+correction;
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
