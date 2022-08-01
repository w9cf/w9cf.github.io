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

public interface TEMLine {

/** This method returns the Complex characteristic impedance */
   public Complex Z0();

/** This method returns the attenuation in dB/100ft */
   public double attendB();

/** This method returns the velocity factor */
   public double velfac();
 
/** This method returns the propagation constant */
   public Complex gamma();

/** This method sets the frequency */
   public void setF(double frequency);

}
