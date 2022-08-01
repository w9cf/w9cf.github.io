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

public class Knob extends Panel {

   private double angle = 0.0;
   private double anglemax;
   private double anglem = 0.0;;
   private double valuemin;
   private double valuemax;
   private double value;
   private double turns;
   private Panel p;
   private Graphics gimage;
   private Image image,imageback;
   private Dimension dimage;
   private String label;
   private Color knobcolor;
   private int places;

   public Knob() {
      this("");
   }

   public Knob(String label) {
      this(label,0.0,1.0,1.0);
   }

   public Knob(String label, double valuemin, double valuemax, double turns) {
      super();
      this.valuemin = valuemin;
      this.valuemax = valuemax;
      this.label = label;
      this.turns = turns;
      value = valuemin;
      anglemax = Math.PI*2.0*turns;
      knobcolor = new Color(80,20,20);
      places = 1;
   }

   public void setPlaces(int i) {
      places = i;
   }

   public void paint(Graphics g) {
      update(g);
   }

   public void update(Graphics g) {
      Dimension d = size();
      if (gimage == null || d.width != dimage.width
         || d.height != dimage.height) {
         dimage = d;
         image = createImage(dimage.width,dimage.height);
         gimage = image.getGraphics();
      }
      gimage.setColor(getBackground());
      gimage.fillRect(0,0,d.width,d.height);
//      d.height -= p.size().height;
      gimage.setColor(knobcolor);
      int ir = Math.min(d.width,d.height)/2;
      int ix0 = d.width/2;
      int iy0 = d.height/2;
//      int ix = ix0+(int) (Math.round(Math.cos(angle)*ir));
//      int iy = iy0+(int) (Math.round(Math.sin(angle)*ir));
      int ix = ix0+(int) (-Math.round(Math.cos(angle)*ir));
      int iy = iy0+(int) (-Math.round(Math.sin(angle)*ir));
      ir *= .8;
      int iangle = (int) (-Math.round(180.*angle/Math.PI));
//      int iangle = (int) (Math.round(180.*angle/Math.PI));
      int irarc = (int) (ir*1.05);
      for (int i=0;i<10;i++) {
         gimage.fillArc(ix0-irarc,iy0-irarc,irarc*2,irarc*2,iangle-36*i-28,18);
//         gimage.fillArc(ix0-irarc,iy0-irarc,irarc*2,irarc*2,iangle+36*i+28,18);
      }
      gimage.fillOval(ix0-ir,iy0-ir,ir*2,ir*2);
      gimage.setColor(Color.red);
      int ir2 = (int)(.3*ir);
      int ix1 = (int)((ix-ix0)*.65+ix0);
      int iy1 = (int)((iy-iy0)*.65+iy0);
      gimage.fillOval(ix1-ir2/2,iy1-ir2/2,ir2,ir2);

      FontMetrics fm = g.getFontMetrics();
      gimage.setColor(knobcolor);
      gimage.drawString(label + " =   " + Truncate.toString(value,places)
         ,0,d.height-5);
      

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

   public boolean mouseDown(Event e, int x, int y) {
      Dimension d = size();
      int mx = x-d.width/2;
      int my = y-d.height/2;
      anglem = Math.atan2((double) my, (double) mx);
      return false;
   }
      
   public boolean mouseDrag(Event e, int x, int y) {
      Dimension d = size();
      int mx = x-d.width/2;
      int my = y-d.height/2;
      double t = Math.atan2((double) my, (double) mx);
      double dangle = t-anglem;
      if (dangle > Math.PI) dangle -= 2.*Math.PI;
      if (dangle < -Math.PI) dangle += 2.*Math.PI;
      angle += dangle;
      angle = Math.max(0.0,Math.min(angle,anglemax));
      value = (angle/(turns*2.0*Math.PI))*(valuemax-valuemin)+valuemin;
//      value = (1.0-angle/(turns*2.0*Math.PI))*(valuemax-valuemin)+valuemin;
      anglem = t;
      repaint();
      return false;
   }

   public double getValue() {
      return value;
   }

   public void setValue(double value) {
      value = Math.max(valuemin,Math.min(valuemax,value));
      angle = turns*2.0*Math.PI*((value-valuemin)/(valuemax-valuemin));
      this.value = value;
      repaint();
   }

   public void setLimits(double valuemin, double valuemax, double turns) {
      this.valuemin = valuemin;
      this.valuemax = valuemax;
      this.turns = turns;
      value = Math.max(valuemin,Math.min(valuemax,value));
      anglemax = Math.PI*2.0*turns;
   }
}
