/*
Copyright 1998, Kevin Schmidt

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

public class Dstring {

   static public String truncString(double d, int places) {
//
// This mess is a stopgap until we can use version 1.1 NumberFormat etc.
//
      int i,j,n,idecimal;
      Double dd = new Double(d);
      char decimal = '.';
      char[] digits  = {'0','1','2','3','4','5','6','7','8','9'};
      StringBuffer b = new StringBuffer(dd.toString());
      n = b.length();
      j = 0;
      idecimal = n;
      for (i=0;i<n;i++) {
         switch(b.charAt(i)) {
            case '.':
               idecimal = i;
               b.setCharAt(j++,b.charAt(i));
               break;
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
               if (i <= idecimal+places) b.setCharAt(j++,b.charAt(i));
               break;
            default:
               idecimal = n;
               b.setCharAt(j++,b.charAt(i));
         }
      }
      b.setLength(j);
      return b.toString();
   }

   static public String toString(double d, int places) {
      String s = truncString(d,places);
      Double xx;
      try {
         xx = new Double(s);
      } catch (NumberFormatException e) {
         return s;
      }
      double x = xx.doubleValue();
      return truncString(x+2.*(d-x),places);
   }

/*
   public static void main(String[] args) {
      System.out.println(toString(-Math.PI,3));
      System.out.println(toString(1.0,3));
      System.out.println(toString(Math.PI*1.e200,3));
      System.out.println(toString(1.e-200,3));
      System.out.println(toString(1./0.,3));
      System.out.println(toString(4.4999,3));
      System.out.println(toString(4.49949,3));

      System.out.println(-Math.PI);
      System.out.println(1.0);
      System.out.println(Math.PI*1.e200);
      System.out.println(1.e-200);
      System.out.println(1./0.);
   }
*/
}
