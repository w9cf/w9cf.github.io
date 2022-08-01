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
public class Truncate {

   public static String truncString(double d, int places) {
//
// This mess is a stopgap until we can use version 1.1 NumberFormat etc.
//
      int i,j,n,idecimal;
      Double dd = new Double(d);
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

   public static String toString(double d, int places) {
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
}
