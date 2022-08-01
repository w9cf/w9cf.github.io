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
public class K1fo144 {
   private final static double elenth0[] = {
      1038.0, 955.0, 956.0, 932.0, 916.0, 906.0, 897.0, 891.0,
       887.0, 883.0, 879.0, 875.0, 870.0, 865.0, 861.0, 857.0,
       853.0, 849.0, 845.0
   };
   private final static double elspac0[] = {
         0.0,  312.0,  447.0,  699.0, 1050.0, 1482.0, 1986.0, 2553.0,
      3168.0, 3831.0, 4527.0, 5259.0, 6015.0, 6786.0, 7566.0, 8352.0,
      9144.0, 9942.0, 10744.0
   };
   private final static double eldiam = 6.35;
   private double[] elenth;
   private double[] elspac;
   private int nelem;

   public K1fo144(int nelem) throws ElementNumberRange {
      double correction;
      this.nelem = nelem;
      if (nelem < 10 || nelem > 19) throw new ElementNumberRange();
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
