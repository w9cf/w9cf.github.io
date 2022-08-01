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

import java.awt.*;

public class TranWindow extends Frame {

   boolean applet = true;
   Tran s;

   public TranWindow() {
      setLayout(new BorderLayout());
      add("Center",s = new Tran());
      pack();
      show();
      s.init();
   }

   public boolean handleEvent(Event e) {
      if (e.id == e.WINDOW_DESTROY) {
         if (applet) {
            dispose();
         } else {
            System.exit(0);
         }
      }
      return super.handleEvent(e);
   }

   public static void main(String args[]) {
      TranWindow window = new TranWindow();
      window.applet = false;
      window.setTitle("Transmission Line Solver");
   }

   public Dimension minimumSize() {
      return new Dimension(800,600);
   }

   public Dimension preferredSize() {
      return minimumSize();
   }
}

