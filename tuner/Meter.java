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
import java.awt.*;
import java.awt.event.*;
import java.util.Hashtable;
import java.util.Enumeration;

public class Meter extends Panel {

   private double value;
   private double valuemax;
   private double valuemin;
   private Graphics gimage;
   private Image image,image0;
   private Dimension dimage;
   private Hashtable meterScale;
   private String label="";
   private int ifont = -1;
   private Font f1,f2;

   public Meter(double valuemin, double valuemax) {
      super();
      this.valuemax = valuemax;
      this.valuemin = valuemin;
      value = valuemin;
   }

   public Meter() {
      this(0.0,100.0);
   }


   public void paint(Graphics g) {
      update(g);
   }

   public void update(Graphics g) {
      Dimension d = size();
      int id = Math.min(d.width,d.height);
      if (ifont == -1 || ifont !=  (12*id)/190) {
         ifont = (12*id)/190;
         f1 = new Font("Helvetica",Font.BOLD,ifont);
         f2 = new Font("Helvetica",Font.PLAIN,(ifont*3)/4);
      }
      if (gimage == null || d.width != dimage.width
         || d.height != dimage.height) {
         dimage = d;
         image = createImage(dimage.width,dimage.height);
         image0 = createImage(dimage.width,dimage.height);
         gimage = image.getGraphics();
         Graphics gimage0 = image0.getGraphics();

         gimage0.setColor(getBackground());
//
// clear area
//
         gimage0.fillRect(0,0,d.width,d.height);
//
// Meter case
//
         gimage0.setColor(new Color(80,20,20));
         int ix0 = d.width/16;
         int iy0 = d.height/16;
         int iw = d.width/8;
         int ih = d.height/8;
//         gimage0.fillRoundRect(ix0,iy0,(d.width*7)/8,(d.height*7)/8,60,60);
         gimage0.fillRoundRect(ix0,iy0,(d.width*7)/8,(d.height*7)/8,iw,ih);
//
// Meter Interior
//
         int ix1 = d.width/8;
         int iy1 = d.height/8;
         gimage0.setColor(Color.lightGray);
//         gimage0.fillRoundRect(ix1,iy1,(d.width*3)/4,(d.height*15)/32,60,60);
         gimage0.fillRoundRect(ix1,iy1,(d.width*3)/4,(d.height*15)/32,iw,ih);
//
// Meter Scale
//
         double f = .16;
         ix0 = (int)(Math.round(d.width*f));
         ix1 = (int)(Math.round(d.width*(1.0-f)));
         iy0 = (int)(Math.round(d.height*.26));
         gimage0.setColor(Color.black);
         gimage0.drawLine(ix0,iy0,ix1,iy0);

         gimage0.setFont(f1);
         FontMetrics fm = gimage0.getFontMetrics();
         gimage0.setColor(Color.black);
         gimage0.drawString(label,d.width/2-fm.stringWidth(label)/2,d.height/2);

         gimage0.setFont(f2);
         fm = gimage0.getFontMetrics();
         int maxh = fm.getMaxAscent();

         Enumeration en = meterScale.keys();
         Double rhokey;
         String s;
         double rho;
         double x = d.width*(.5-f);
         double y0 = x/Math.tan(Math.PI*35.0/180.0);
         while (en.hasMoreElements()) {
            rhokey = (Double) en.nextElement();
            s = (String) meterScale.get(rhokey);
            rho = rhokey.doubleValue();
            double angle = 70.0*(rho-valuemin)/(valuemax-valuemin)-35.0;
            double tn = Math.tan(Math.PI*angle/180.0);
            double xx = d.width/2+y0*tn;
            int ix3 = (int) Math.round(xx);
            xx = d.width/2+(y0-d.height*.05)*tn;
            int ix4 = (int) Math.round(xx);
            gimage0.drawLine(ix3,iy0,ix4,iy0+d.height/20);
            gimage0.drawString(s,ix4-fm.stringWidth(s)/2,iy0+d.height/20+maxh);
         }

      }
//
// Needle
//
      gimage.drawImage(image0,0,0,null);
      double angle = 70.0*(value-valuemin)/(valuemax-valuemin)-35.0;
      angle *= Math.PI/180.0;
      int ix2 = d.width/2;
      int iy2 = (d.height*3)/4;
      int ix3 = (int)(Math.sin(angle)*d.width*.6)+ix2;
      int iy3 = (int)(-Math.cos(angle)*d.height*.6)+iy2;
      int iy4 = (d.height*15)/32+d.height/8;
      int ix4 = ix2+((iy4-iy2)*(ix3-ix2))/(iy3-iy2);
      gimage.setColor(Color.black);
      gimage.drawLine(ix4,iy4,ix3,iy3);
      g.drawImage(image,0,0,null);
   }

/*
   public Dimension getMinimumSize() {
      return new Dimension(300,300);
   }

   public Dimension getPreferredSize() {
      return getMinimumSize();
   }
*/

   public void setValue(double value) {
      this.value = Math.min(valuemax,Math.max(value,valuemin));
   }

   public void setScale(Hashtable meterScale) {
      this.meterScale = meterScale;
   }

   public void setLabel(String label) {
      this.label = label;
   }
}
