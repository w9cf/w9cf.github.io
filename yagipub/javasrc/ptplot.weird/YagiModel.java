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
import java.applet.*;
import java.awt.*;
import java.io.*;

public class YagiModel extends Frame implements Runnable {
   private Pplot pplot;
   private Button bcalc,bstop,bdone,bdelete,bcancel1,bcancel2;
   private Thread calcthread;
   private Ptop ptop;
   private Panel pc;
   private Label lgain,lfb,lzin,lunit,lmat,ltime;
   private double[] elenth,elpos;
   private double elrad;
   private int nelem,nseg,ndrive,units;
   private double frequency;
   private boolean first = true;
   private Label lel,linfo;
   private TextField lelenth,lelpos,lfreq,lelrad,lnseg;
   private List ellist;
   private double sigmai = 0.28e-7; // in (ohm-meter)
   private Checkbox drivecheck;
   boolean applet = true;

   Menu filemenu,examplemenu,editmenu,unitmenu,condmenu;
   MenuItem fread,fwrite,fexit,flist;
   MenuItem s2el,nbs3el,kj13el,k2riw13el,nbs15el;
   Menu k1fo144,k1fo220,k1fo432;
   MenuItem addel;

   public YagiModel() {
      pack();
      show();
      init();
   }

   public void init() {
      Panel pbottom,pcontrol,pedit,pbright,plist;
//      setFont(new Font("Helvetica",Font.PLAIN,14));
      MenuBar mb = new MenuBar();
      filemenu = new Menu("File");
      mb.add(filemenu);
      fread = new MenuItem("Read File");
      filemenu.add(fread);
      fwrite = new MenuItem("Write File");
      filemenu.add(fwrite);
      flist= new MenuItem("List Elements");
      filemenu.add(flist);
      fexit = new MenuItem("Exit");
      filemenu.add(fexit);
      examplemenu = new Menu("Examples");
      mb.add(examplemenu);
      s2el = new MenuItem("Simple 2el");
      nbs3el =new MenuItem("NBS 3el 6m");
      kj13el = new MenuItem("Kmosko-Johnson 13el 2m");
      nbs15el = new MenuItem("NBS 15el 2m");
      k2riw13el = new MenuItem("K2RIW 13el 70cm");
      k1fo144 = new Menu("K1FO 2m");
      k1fo220 = new Menu("K1FO 125cm");
      k1fo432 = new Menu("K1FO 70cm");
      k1fo144.add(new MenuItem("10 element"));
      k1fo144.add(new MenuItem("11 element"));
      k1fo144.add(new MenuItem("12 element"));
      k1fo144.add(new MenuItem("13 element"));
      k1fo144.add(new MenuItem("14 element"));
      k1fo144.add(new MenuItem("15 element"));
      k1fo144.add(new MenuItem("16 element"));
      k1fo144.add(new MenuItem("17 element"));
      k1fo144.add(new MenuItem("18 element"));
      k1fo144.add(new MenuItem("19 element"));

      k1fo220.add(new MenuItem("12 element"));
      k1fo220.add(new MenuItem("13 element"));
      k1fo220.add(new MenuItem("14 element"));
      k1fo220.add(new MenuItem("15 element"));
      k1fo220.add(new MenuItem("16 element"));
      k1fo220.add(new MenuItem("17 element"));
      k1fo220.add(new MenuItem("18 element"));
      k1fo220.add(new MenuItem("19 element"));
      k1fo220.add(new MenuItem("20 element"));
      k1fo220.add(new MenuItem("21 element"));
      k1fo220.add(new MenuItem("22 element"));

      k1fo432.add(new MenuItem("12 element"));
      k1fo432.add(new MenuItem("13 element"));
      k1fo432.add(new MenuItem("14 element"));
      k1fo432.add(new MenuItem("15 element"));
      k1fo432.add(new MenuItem("16 element"));
      k1fo432.add(new MenuItem("17 element"));
      k1fo432.add(new MenuItem("18 element"));
      k1fo432.add(new MenuItem("19 element"));
      k1fo432.add(new MenuItem("20 element"));
      k1fo432.add(new MenuItem("21 element"));
      k1fo432.add(new MenuItem("22 element"));
      k1fo432.add(new MenuItem("23 element"));
      k1fo432.add(new MenuItem("24 element"));
      k1fo432.add(new MenuItem("25 element"));
      k1fo432.add(new MenuItem("26 element"));
      k1fo432.add(new MenuItem("27 element"));
      k1fo432.add(new MenuItem("28 element"));
      k1fo432.add(new MenuItem("29 element"));
      k1fo432.add(new MenuItem("30 element"));
      k1fo432.add(new MenuItem("31 element"));
      k1fo432.add(new MenuItem("32 element"));
      k1fo432.add(new MenuItem("33 element"));
      k1fo432.add(new MenuItem("34 element"));
      k1fo432.add(new MenuItem("35 element"));
      k1fo432.add(new MenuItem("36 element"));
      k1fo432.add(new MenuItem("37 element"));
      k1fo432.add(new MenuItem("38 element"));
      k1fo432.add(new MenuItem("39 element"));
      k1fo432.add(new MenuItem("40 element"));

      examplemenu.add(s2el);
      examplemenu.add(nbs3el);
      examplemenu.add(kj13el);
      examplemenu.add(nbs15el);
      examplemenu.add(k2riw13el);
      examplemenu.add(k1fo144);
      examplemenu.add(k1fo220);
      examplemenu.add(k1fo432);
      
      editmenu = new Menu("Edit");
      addel = new MenuItem("Add Element");
      editmenu.add(addel);
      mb.add(editmenu);

      unitmenu = new Menu("Units");
      unitmenu.add(new MenuItem("Radians"));
      unitmenu.add(new MenuItem("Wave Lengths"));
      unitmenu.add(new MenuItem("Centimeters"));
      unitmenu.add(new MenuItem("Millimeters"));
      unitmenu.add(new MenuItem("Meters"));
      unitmenu.add(new MenuItem("Inches"));
      unitmenu.add(new MenuItem("Feet"));
      mb.add(unitmenu);

      condmenu = new Menu("Conductivity");
      condmenu.add(new MenuItem("Perfect"));
      condmenu.add(new MenuItem("Aluminum"));
      condmenu.add(new MenuItem("Copper"));
      condmenu.add(new MenuItem("Gold"));
      condmenu.add(new MenuItem("Tin"));
      mb.add(condmenu);

      setMenuBar(mb); 
         
      setLayout(new GridLayout(2,1,50,50));
      add(ptop = new Ptop());
      add(pbottom = new Panel());
      pbottom.setBackground(Color.lightGray);
      pbottom.setLayout(new GridLayout(1,2,10,10));
      pbottom.add(pc = new Panel());
      pbottom.add(pbright = new Panel());
      pbright.setLayout(new GridLayout(1,1));
      pbright.add(pplot = new Pplot());
      pc.setLayout(new CardLayout());
      pc.add("Controls",pcontrol = new Panel());
      pc.add("Edit",pedit = new Panel());
      pc.add("List",plist = new Panel());

// a very simple 2 element array
      elenth = new double[] { 0.475, 0.46 };
      elpos = new double[] { 0.0, 0.125 };
      elrad = .001;
      nelem = 2;
      ndrive = 0;
      nseg = 4;
      frequency = 1.0;
      units = Units.WaveLength;

      doControl(pcontrol);
      doEdit(pedit);
      doList(plist);

      ptop.setGeometry(elpos,elenth,nelem,ndrive);
      ptop.setup(lel,lelenth,lelpos,pc,drivecheck);

      pplot.setNumSets(5);
      pplot.init();
      pplot.setTitle(new String("Pattern"));
      pplot.addLegend(1,"10dB");
      pplot.addLegend(2,"0dB");
      pplot.addLegend(3,"-10dB");
      pplot.addLegend(4,"-20dB");
      pplot.drawPlot(pplot.getGraphics(),true);

      validate();
   }

   private void doControl(Panel p) {
      p.setLayout(new GridLayout(7,2,10,10));
      p.add(bcalc = new Button("Calculate"));
      p.add(bstop = new Button("Stop"));

      p.add(new Label("Frequency ="));
      p.add(lfreq = new TextField(Trunc.toString(frequency,5)));

      p.add(new Label("Element Diameter = "));
      p.add(lelrad = new TextField(Trunc.toString(elrad*2.0,5)));

      p.add(new Label("Segments in 1/2 ="));
      p.add(lnseg = new TextField(nseg + ""));

      p.add(lzin = new Label("Zin = "));
      p.add(ltime = new Label(" "));

      p.add(lgain = new Label("Gain = "));
      p.add(lfb = new Label("F/B =  "));
      
      lmat = new Label("Aluminum");
      p.add(lmat);
      lunit = new Label(Units.ustring[units-1]);
      p.add(lunit);
      bstop.disable();

   }

   private void doEdit(Panel p) {
      p.setLayout(new GridLayout(6,2,10,10));
      p.add(new Label("Element ="));
      p.add(lel = new Label(" "));
      lel.setBackground(Color.white);
      p.add(new Label("Element Length ="));
      p.add(lelenth = new TextField(" "));
      p.add(new Label("Element Postion ="));
      p.add(lelpos = new TextField(" "));
      p.add(new Label("Driven Element"));
      drivecheck = new Checkbox("  ");
      p.add(drivecheck);
      p.add(linfo = new Label(" "));
      linfo.setBackground(Color.white);
      p.add(bcancel1 = new Button("Cancel"));
      p.add(bdelete = new Button("Delete"));
      p.add(bdone = new Button("OK"));
   }

   private void doList(Panel p) {
      p.setLayout(new BorderLayout());
      p.add("Center",ellist = new List(10,false));
      p.add("South", bcancel2 = new Button("Cancel"));
      ellist.addItem(" ");
   }

   public boolean action(Event e, Object o) {
      int i;
      if (e.target == bcalc) {
         if (calcthread == null) {
            repaint();
            bstop.enable();
            calcthread = new Thread(this);
            calcthread.start();
         }
         return true;
      }
      if (e.target == bstop) {
         calcthread.stop();
         calcthread = null;
         bstop.disable();
         return true;
      }
      if (e.target == fexit) {
         if (applet) {
            dispose();
         } else {
            System.exit(0);
         }
      }

      if (e.target == addel) {
         nelem++;
         double[] ell = new double[nelem];
         double[] elp = new double[nelem];
         i = nelem;
         lel.setText(i + "");
         lelenth.setText("999.0");
         lelpos.setText("999.0");
         drivecheck.setState(i == (ndrive+1));
         for (int j=0;j<nelem-1;j++) {
            ell[j] = elenth[j];
            elp[j] = elpos[j];
         }
         ell[nelem-1] = 999.0;
         elp[nelem-1] = 999.0;
         elenth = ell;
         elpos = elp;
         ((CardLayout)pc.getLayout()).show(pc,"Edit");
         return true;
      }

      if (e.target == ellist) {
         i = ellist.getSelectedIndex();
         lel.setText(i + "");
         lelenth.setText(Trunc.toString(elenth[i-1],5));
         lelpos.setText(Trunc.toString(elpos[i-1],5));
         drivecheck.setState(i == (ndrive+1));
         ((CardLayout)pc.getLayout()).show(pc,"Edit");
         return true;
      }

      if (e.target == flist) {
         StringBuffer sb;
         ellist.clear();
         ellist.addItem("Element    Length        Position");
         
         for (int k=0;k<nelem;k++) {
            sb = new StringBuffer();
            sb.append(k+1);
            while (sb.length() < 10) sb.append(" ");
            sb.append(Trunc.toString(elenth[k],5));
            while (sb.length() < 25) sb.append(" ");
            sb.append(Trunc.toString(elpos[k],5));
            ellist.addItem(sb.toString());
         }
         ((CardLayout)pc.getLayout()).show(pc,"List");
         return true;
      }

      if (e.target == bcancel2 || e.target == bcancel1) {
         ((CardLayout)pc.getLayout()).show(pc,"Controls");
         return true;
      }

      if (e.target == fwrite) {
         byte[] bytes;
         String s,s1;
         String newline = System.getProperty("line.separator");
         ((CardLayout)pc.getLayout()).show(pc,"Controls");
         FileDialog fd =
            new FileDialog((Frame) this,"Output File",FileDialog.SAVE);
         fd.show();
         try {
            netscape.security.PrivilegeManager.enablePrivilege(
               "UniversalFileWrite");
         } catch (Throwable ex) {}
         try {
            File f = new File(fd.getDirectory(),fd.getFile());
            FileOutputStream fos = new FileOutputStream(f);
            BufferedOutputStream bos = new BufferedOutputStream(fos);

            s = new String("Change this to a useful Title # Title" + newline);
            bytes = s.getBytes();
            bos.write(bytes,0,bytes.length);

            s = new String(Units.ustring[units-1] + " # length units "
               + newline);
            bytes = s.getBytes();
            bos.write(bytes,0,bytes.length);

            s = new String("absolute" + " #spacing " + newline);
            bytes = s.getBytes();
            bos.write(bytes,0,bytes.length);

            s = new String(nseg + " #segments for 1/2 element" + newline);
            bytes = s.getBytes();
            bos.write(bytes,0,bytes.length);
            
            s = new String(nelem + " #number of elements" + newline);
            bytes = s.getBytes();
            bos.write(bytes,0,bytes.length);
         
            s = new String( (ndrive+1) + " #driven element" + newline);
            bytes = s.getBytes();
            bos.write(bytes,0,bytes.length);

            s = new String( 2.0*elrad + " #element diameter" + newline);
            bytes = s.getBytes();
            bos.write(bytes,0,bytes.length);
          
            for (int j=0;j<nelem;j++) {
               s = new String( elenth[j] + " #length " + (j+1) + newline);
               bytes = s.getBytes();
               bos.write(bytes,0,bytes.length);
            }

            for (int j=0;j<nelem;j++) {
               s = new String( elpos[j] + " #position " + (j+1) + newline);
               bytes = s.getBytes();
               bos.write(bytes,0,bytes.length);
            }
            s = new String(frequency + " #frequency" + newline);
            bytes = s.getBytes();
            bos.write(bytes,0,bytes.length);
            bos.close();
         } catch (IOException io) {
         } catch (NullPointerException nn) {}
      }

      if (e.target == fread ) {
         ((CardLayout)pc.getLayout()).show(pc,"Controls");
         FileDialog fd =
            new FileDialog((Frame) this,"Input File",FileDialog.LOAD);
         fd.show();
         try {
            netscape.security.PrivilegeManager.enablePrivilege(
               "UniversalFileRead");
         } catch (Throwable ex) {}

         try {
            File f = new File(fd.getDirectory(),fd.getFile());
            FileInputStream fis = new FileInputStream(f);
            DataInputStream dis = new DataInputStream(fis);
            units = Units.Radian;
            String u = Myread.reads(dis); //discard title for now
            u = Myread.reads(dis);
//
// mess to figure out units of input file
//
            if (u.charAt(0) == 'r' || u.charAt(0) == 'R') units = Units.Radian;
            if (u.charAt(0) == 'w' || u.charAt(0) == 'W')
                units = Units.WaveLength;
            if (u.charAt(0) == 'c' || u.charAt(0) == 'C') units = Units.Cm;
            if (u.charAt(0) == 'm' || u.charAt(0) == 'M') {
               if (u.charAt(1) == 'm' || u.charAt(1) == 'M' ||
                   u.charAt(1) == 'i' || u.charAt(1) == 'I') {
                   units = Units.Mm;
               } else {
                   units = Units.Meter;
               }
            }
            if (u.charAt(0) == 'i' || u.charAt(0) == 'I') units = Units.Inch;
            if (u.charAt(0) == 'f' || u.charAt(0) == 'F') units = Units.Foot;
            lunit.setText(Units.ustring[units-1]);
            u = Myread.reads(dis);
            nseg = Myread.readi(dis);
            lnseg.setText(nseg + "");
            nelem = Myread.readi(dis);
            ndrive = Myread.readi(dis)-1;
            elrad = 0.5*Myread.readd(dis);
            lelrad.setText(Trunc.toString(2.*elrad,5));
            elenth = new double[nelem];
            elpos = new double[nelem];
            for (i=0;i<nelem;i++) elenth[i] = Myread.readd(dis);
            for (i=0;i<nelem;i++) elpos[i] = Myread.readd(dis);
            if (units != Units.Radian && units != Units.WaveLength) {
               frequency = Myread.readd(dis);
               lfreq.setText(Trunc.toString(frequency,5));
            }
            if (u.charAt(0) == 'r' || u.charAt(0) == 'R') {
               for (i=1;i<nelem;i++) elpos[i] = elpos[i-1]+elpos[i];
            } // convert to absolute position
            dis.close();
            ptop.setGeometry(elpos,elenth,nelem,ndrive);
            ptop.repaint();
            ptop.repaint();
         } catch (IOException io) { }
         return true;
      }
      if (e.target == bdelete) {
         try {
            i =  (new Integer(lel.getText())).intValue()-1;
         } catch (java.lang.NumberFormatException e1) {
            linfo.setText("el number error");
            return true;
         }
         nelem--;
         if (nelem <= 0) {
            nelem = 0;
            return true;
         }
         double[] ell = new double[nelem];
         double[] elp = new double[nelem];
         for (int j=0, k=0;j<nelem+1;j++) {
            if (i != j) {
              ell[k] = elenth[j];
              elp[k] = elpos[j];
              k++;
            }
         }
         elenth = ell;
         elpos = elp;
         ndrive = Math.min(nelem-1,ndrive);
         ((CardLayout)pc.getLayout()).show(pc,"Controls");
         ptop.setGeometry(elpos,elenth,nelem,ndrive);
         repaint();
         return true;
      }

      if (e.target == bdone) {
         double eln,elpn;
         try {
            i =  (new Integer(lel.getText())).intValue()-1;
         } catch (java.lang.NumberFormatException e1) {
            linfo.setText("el number error");
            return true;
         }
         try {
            eln =  (new Double(lelenth.getText())).doubleValue();
         } catch (java.lang.NumberFormatException e1) {
            linfo.setText("length format error");
            return true;
         }
         try {
            elpn =  (new Double(lelpos.getText())).doubleValue();
         } catch (java.lang.NumberFormatException e1) {
            linfo.setText("position format error");
            return true;
         }
         elenth[i] = eln;
         elpos[i] = elpn;
         if (drivecheck.getState()) ndrive = i;
         ((CardLayout)pc.getLayout()).show(pc,"Controls");
         ptop.setGeometry(elpos,elenth,nelem,ndrive);
         repaint();
         return true;
      }

      if (e.target instanceof MenuItem
         && ((MenuItem) e.target).getParent() == condmenu) {
         sigmai = 0.0;
         lmat.setText("Perfect");
         if (((MenuItem) e.target).getLabel().equals("Perfect")) {
            return true;
         }
         if (((MenuItem) e.target).getLabel().equals("Aluminum")) {
            sigmai = 0.28e-7;
            lmat.setText("Aluminum");
         }
         if (((MenuItem) e.target).getLabel().equals("Copper")) {
            sigmai = .17e-7;
            lmat.setText("Copper");
         }
         if (((MenuItem) e.target).getLabel().equals("Gold")) {
            sigmai = .24e-7;
            lmat.setText("Gold");
         }
         if (((MenuItem) e.target).getLabel().equals("Tin")) {
            sigmai = 1.2e-7;
            lmat.setText("Tin");
         }
         return true;
      }
      if (e.target instanceof MenuItem
         && ((MenuItem) e.target).getParent() == unitmenu) {
         double conv1,conv2;
         i = 1;
         if (((MenuItem) e.target).getLabel().equals("Radians")) i = 1;
         if (((MenuItem) e.target).getLabel().equals("Wave Lengths")) i = 2;
         if (((MenuItem) e.target).getLabel().equals("Centimeters")) i = 3;
         if (((MenuItem) e.target).getLabel().equals("Millimeters")) i = 4;
         if (((MenuItem) e.target).getLabel().equals("Meters")) i = 5;
         if (((MenuItem) e.target).getLabel().equals("Inches")) i = 6;
         if (((MenuItem) e.target).getLabel().equals("Feet")) i = 7;
         frequency = (new Double(lfreq.getText())).doubleValue();
         if (frequency == 0.0) frequency = 1.0;
         switch (units) {
            case Units.Radian:
               conv1 = 1.0;
               break;
            case Units.WaveLength:
               conv1 = Units.WaveToRadian;
               break;
            case Units.Cm:
               conv1 = Units.CmToRadian*frequency;
               break;
            case Units.Mm:
               conv1 = Units.MmToRadian*frequency;
               break;
            case Units.Meter:
               conv1 = Units.MToRadian*frequency;
               break;
            case Units.Inch:
               conv1 = Units.InchToRadian*frequency;
               break;
            case Units.Foot:
               conv1 = Units.FootToRadian*frequency;
               break;
            default:
               conv1 = 1.0;
         }
         switch (i) {
            case Units.Radian:
               conv2 = 1.0;
               break;
            case Units.WaveLength:
               conv2 = Units.WaveToRadian;
               break;
            case Units.Cm:
               conv2 = Units.CmToRadian*frequency;
               break;
            case Units.Mm:
               conv2 = Units.MmToRadian*frequency;
               break;
            case Units.Meter:
               conv2 = Units.MToRadian*frequency;
               break;
            case Units.Inch:
               conv2 = Units.InchToRadian*frequency;
               break;
            case Units.Foot:
               conv2 = Units.FootToRadian*frequency;
               break;
            default:
               conv2 = 1.0;
         }
//         System.out.println(" conv1 " + conv1 + " conv2 " + conv2 );
         units = i;
         lunit.setText(Units.ustring[units-1]);
         for (i=0;i<nelem;i++) {
            elpos[i] *= conv1/conv2;
            elenth[i] *= conv1/conv2;
         }
         elrad = (new Double(lelrad.getText())).doubleValue();
         elrad *= conv1/conv2;
         lelrad.setText(Trunc.toString(elrad,5));
         ((CardLayout)pc.getLayout()).show(pc,"Controls");
         return true;
      }

      if (e.target == s2el) {
         ((CardLayout)pc.getLayout()).show(pc,"Controls");
         units = Units.WaveLength;
         lunit.setText(Units.ustring[units-1]);
         nseg = 4;
         elenth[0] = 0.475;
         elenth[1] = 0.460;
         elpos[0] = 0.0;
         elpos[1] = 0.125;
         elrad = .001;
         nelem = 2;
         ndrive = 0;
         frequency = 1.0;
         lelrad.setText(Trunc.toString(2.*elrad,5));
         lnseg.setText(nseg + "");
         lfreq.setText(Trunc.toString(frequency,5));
         ptop.setGeometry(elpos,elenth,nelem,ndrive);
         ptop.repaint();
         return true;
      }
      if (e.target == nbs3el) {
         ((CardLayout)pc.getLayout()).show(pc,"Controls");
         units = Units.Inch;
         lunit.setText(Units.ustring[units-1]);
         nseg = 4;
         nelem = 3;
         ndrive = 1;
         elrad = 0.25;
         elenth = new double[nelem];
         elpos = new double[nelem];
         elenth[0] = 115.000;
         elenth[1] = 109.750;
         elenth[2] = 108.625;
         elpos[0] = 0.000;
         elpos[1] = 47.125;
         elpos[2] = 94.250;
         frequency = 50.100;
         lelrad.setText(Trunc.toString(2.*elrad,5));
         lnseg.setText(nseg + "");
         lfreq.setText(Trunc.toString(frequency,5));
         ptop.setGeometry(elpos,elenth,nelem,ndrive);
         ptop.repaint();
         return true;
      }
      if (e.target == kj13el) {
         ((CardLayout)pc.getLayout()).show(pc,"Controls");
         units = Units.Inch;
         lunit.setText(Units.ustring[units-1]);
         nseg = 4;
         nelem = 13;
         ndrive = 1;
         elrad = .5*.09375;
         elenth = new double[nelem];
         elpos = new double[nelem];
         elenth[0] = 41.5;
         elenth[1] = 39.5;
         elenth[2] = 37.75;
         elenth[3] = 37.625;
         elenth[4] = 37.5;
         elenth[5] = 37.25;
         elenth[6] = 37.0;
         elenth[7] = 36.75;
         elenth[8] = 36.5;
         elenth[9] = 36.25;
         elenth[10] = 36.0;
         elenth[11] = 35.75;
         elenth[12] = 35.5;
         elpos[0] = 0.000;
         elpos[1] = 20.000;
         elpos[2] = 27.0;
         elpos[3] = 34.5;
         elpos[4] = 42.0;
         elpos[5] = 58.0;
         elpos[6] = 90.0;
         elpos[7] = 122.0;
         elpos[8] = 154.0;
         elpos[9] = 186.0;
         elpos[10] = 218.0;
         elpos[11] = 250.0;
         elpos[12] = 282.0;
         frequency = 144.5;
         lelrad.setText(Trunc.toString(2.*elrad,5));
         lnseg.setText(nseg + "");
         lfreq.setText(Trunc.toString(frequency,5));
         ptop.setGeometry(elpos,elenth,nelem,ndrive);
         ptop.repaint();
         return true;
      }
      if (e.target == nbs15el) {
         ((CardLayout)pc.getLayout()).show(pc,"Controls");
         units = Units.Inch;
         lunit.setText(Units.ustring[units-1]);
         nseg = 4;
         nelem = 15;
         ndrive = 1;
         elrad = .5*.1875;
         elenth = new double[nelem];
         elpos = new double[nelem];
         elenth[0] = 39.375;
         elenth[1] = 36.7;
         elenth[2] = 36.5625;
         elenth[3] = 36.5625;
         elenth[4] = 36.3750;
         elenth[5] = 35.625;
         elenth[6] = 35.500;
         elenth[7] = 35.125;
         elenth[8] = 34.8125;
         elenth[9] = 34.5625;
         elenth[10] = 34.5625;
         elenth[11] = 34.5625;
         elenth[12] = 34.5625;
         elenth[13] = 34.5625;
         elenth[14] = 34.5625;
         elpos[0] = 0.000;
         elpos[1] = 16.375;
         elpos[2] = 41.5851;
         elpos[3] = 66.8132;
         elpos[4] = 92.0413;
         elpos[5] = 117.2694;
         elpos[6] = 142.4975;
         elpos[7] = 167.7256;
         elpos[8] = 192.9537;
         elpos[9] = 218.1818;
         elpos[10] = 243.4099;
         elpos[11] = 268.6380;
         elpos[12] = 293.8661;
         elpos[13] = 319.0942;
         elpos[14] = 344.3223;
         frequency = 144.1;
         lelrad.setText(Trunc.toString(2.*elrad,5));
         lnseg.setText(nseg + "");
         lfreq.setText(Trunc.toString(frequency,5));
         ptop.setGeometry(elpos,elenth,nelem,ndrive);
         ptop.repaint();
         return true;
      }
      if (e.target == k2riw13el) {
         ((CardLayout)pc.getLayout()).show(pc,"Controls");
         units = Units.Inch;
         lunit.setText(Units.ustring[units-1]);
         nseg = 4;
         nelem = 13;
         ndrive = 1;
         elrad = .5*.125;
         elenth = new double[nelem];
         elpos = new double[nelem];
         elenth[0] = 13.3125;
         elenth[1] = 12.3750;
         elenth[2] = 11.7500;
         elenth[3] = 11.7500;
         elenth[4] = 11.7500;
         elenth[5] = 11.7500;
         elenth[6] = 11.7500;
         elenth[7] = 11.7500;
         elenth[8] = 11.7500;
         elenth[9] = 11.7500;
         elenth[10] = 11.7500;
         elenth[11] = 11.7500;
         elenth[12] = 11.7500;
         elpos[0] = -6.2500;
         elpos[1] = 0.000;
         elpos[2] = 3.000;
         elpos[3] = 5.6875;
         elpos[4] = 9.0625;
         elpos[5] = 14.5625;
         elpos[6] = 22.0625;
         elpos[7] = 33.8750;
         elpos[8] = 44.5000;
         elpos[9] = 55.1250;
         elpos[10] = 65.7500;
         elpos[11] = 76.3750;
         elpos[12] = 87.0000;
         frequency = 432.1;
         lelrad.setText(Trunc.toString(2.*elrad,5));
         lnseg.setText(nseg + "");
         lfreq.setText(Trunc.toString(frequency,5));
         ptop.setGeometry(elpos,elenth,nelem,ndrive);
         ptop.repaint();
         return true;
      }
      if (e.target instanceof MenuItem
         && ((MenuItem) e.target).getParent() == k1fo144) {
         ((CardLayout)pc.getLayout()).show(pc,"Controls");
         nelem = 10;
         if (((MenuItem) e.target).getLabel().equals("10 element")) nelem = 10;
         if (((MenuItem) e.target).getLabel().equals("11 element")) nelem = 11;
         if (((MenuItem) e.target).getLabel().equals("12 element")) nelem = 12;
         if (((MenuItem) e.target).getLabel().equals("13 element")) nelem = 13;
         if (((MenuItem) e.target).getLabel().equals("14 element")) nelem = 14;
         if (((MenuItem) e.target).getLabel().equals("15 element")) nelem = 15;
         if (((MenuItem) e.target).getLabel().equals("16 element")) nelem = 16;
         if (((MenuItem) e.target).getLabel().equals("17 element")) nelem = 17;
         if (((MenuItem) e.target).getLabel().equals("18 element")) nelem = 18;
         if (((MenuItem) e.target).getLabel().equals("19 element")) nelem = 19;
         units = Units.Mm;
         lunit.setText(Units.ustring[units-1]);
         frequency = 144.1;
         nseg = 4;
         ndrive = 1;
         try {
            K1fo144 yagmod = new K1fo144(nelem);
            nelem = yagmod.getNelem();
            elenth = yagmod.getElenth();
            elpos = yagmod.getElspac();
            elrad = .5*yagmod.getEldiam();
         } catch (ElementNumberRange exxx) {
//            System.out.println(" oh oh " + exxx);
            return true;
         }
         lelrad.setText(Trunc.toString(2.*elrad,5));
         lnseg.setText(nseg + "");
         lfreq.setText(Trunc.toString(frequency,5));
         ptop.setGeometry(elpos,elenth,nelem,ndrive);
         ptop.repaint();
         return true;
      }
      if (e.target instanceof MenuItem
         && ((MenuItem) e.target).getParent() == k1fo220) {
         ((CardLayout)pc.getLayout()).show(pc,"Controls");
         nelem = 12;
         if (((MenuItem) e.target).getLabel().equals("12 element")) nelem = 12;
         if (((MenuItem) e.target).getLabel().equals("13 element")) nelem = 13;
         if (((MenuItem) e.target).getLabel().equals("14 element")) nelem = 14;
         if (((MenuItem) e.target).getLabel().equals("15 element")) nelem = 15;
         if (((MenuItem) e.target).getLabel().equals("16 element")) nelem = 16;
         if (((MenuItem) e.target).getLabel().equals("17 element")) nelem = 17;
         if (((MenuItem) e.target).getLabel().equals("18 element")) nelem = 18;
         if (((MenuItem) e.target).getLabel().equals("19 element")) nelem = 19;
         if (((MenuItem) e.target).getLabel().equals("20 element")) nelem = 20;
         if (((MenuItem) e.target).getLabel().equals("21 element")) nelem = 21;
         if (((MenuItem) e.target).getLabel().equals("22 element")) nelem = 22;
//         System.out.println(" nelem " + nelem);
         units = Units.Mm;
         lunit.setText(Units.ustring[units-1]);
         frequency = 222.1;
         nseg = 4;
         ndrive = 1;
         try {
            K1fo220 yagmod = new K1fo220(nelem);
            nelem = yagmod.getNelem();
            elenth = yagmod.getElenth();
            elpos = yagmod.getElspac();
            elrad = .5*yagmod.getEldiam();
         } catch (ElementNumberRange exxx) {
//            System.out.println(" oh oh " + exxx);
            return true;
         }
         lelrad.setText(Trunc.toString(2.*elrad,5));
         lnseg.setText(nseg + "");
         lfreq.setText(Trunc.toString(frequency,5));
         ptop.setGeometry(elpos,elenth,nelem,ndrive);
         ptop.repaint();
         return true;
      }
      if (e.target instanceof MenuItem
         && ((MenuItem) e.target).getParent() == k1fo432) {
         ((CardLayout)pc.getLayout()).show(pc,"Controls");
         nelem = 12;
         if (((MenuItem) e.target).getLabel().equals("12 element")) nelem = 12;
         if (((MenuItem) e.target).getLabel().equals("13 element")) nelem = 13;
         if (((MenuItem) e.target).getLabel().equals("14 element")) nelem = 14;
         if (((MenuItem) e.target).getLabel().equals("15 element")) nelem = 15;
         if (((MenuItem) e.target).getLabel().equals("16 element")) nelem = 16;
         if (((MenuItem) e.target).getLabel().equals("17 element")) nelem = 17;
         if (((MenuItem) e.target).getLabel().equals("18 element")) nelem = 18;
         if (((MenuItem) e.target).getLabel().equals("19 element")) nelem = 19;
         if (((MenuItem) e.target).getLabel().equals("20 element")) nelem = 20;
         if (((MenuItem) e.target).getLabel().equals("21 element")) nelem = 21;
         if (((MenuItem) e.target).getLabel().equals("22 element")) nelem = 22;
         if (((MenuItem) e.target).getLabel().equals("23 element")) nelem = 23;
         if (((MenuItem) e.target).getLabel().equals("24 element")) nelem = 24;
         if (((MenuItem) e.target).getLabel().equals("25 element")) nelem = 25;
         if (((MenuItem) e.target).getLabel().equals("26 element")) nelem = 26;
         if (((MenuItem) e.target).getLabel().equals("27 element")) nelem = 27;
         if (((MenuItem) e.target).getLabel().equals("28 element")) nelem = 28;
         if (((MenuItem) e.target).getLabel().equals("29 element")) nelem = 29;
         if (((MenuItem) e.target).getLabel().equals("30 element")) nelem = 30;
         if (((MenuItem) e.target).getLabel().equals("31 element")) nelem = 31;
         if (((MenuItem) e.target).getLabel().equals("32 element")) nelem = 32;
         if (((MenuItem) e.target).getLabel().equals("33 element")) nelem = 33;
         if (((MenuItem) e.target).getLabel().equals("34 element")) nelem = 34;
         if (((MenuItem) e.target).getLabel().equals("35 element")) nelem = 35;
         if (((MenuItem) e.target).getLabel().equals("36 element")) nelem = 36;
         if (((MenuItem) e.target).getLabel().equals("37 element")) nelem = 37;
         if (((MenuItem) e.target).getLabel().equals("38 element")) nelem = 38;
         if (((MenuItem) e.target).getLabel().equals("39 element")) nelem = 39;
         if (((MenuItem) e.target).getLabel().equals("40 element")) nelem = 40;
         units = Units.Mm;
         lunit.setText(Units.ustring[units-1]);
         frequency = 432.1;
         nseg = 4;
         ndrive = 1;
         try {
            K1fo432 yagmod = new K1fo432(nelem);
            nelem = yagmod.getNelem();
            elenth = yagmod.getElenth();
            elpos = yagmod.getElspac();
            elrad = .5*yagmod.getEldiam();
         } catch (ElementNumberRange exxx) {
//            System.out.println(" oh oh " + exxx);
            return true;
         }
         lelrad.setText(Trunc.toString(2.*elrad,5));
         lnseg.setText(nseg + "");
         lfreq.setText(Trunc.toString(frequency,5));
         ptop.setGeometry(elpos,elenth,nelem,ndrive);
         ptop.repaint();
         return true;
      }
      return false;
   }

   public void run() {

      Yagi y;
      int i;
      double[] elpos = new double[nelem];
      double[] elenth = new double[nelem];
      double elrad,frequency;
      double conversion;
      double gain,fb,c,s,r;
      int nseg;
      try {
         Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
//
// convert local copies to radians
//
         System.arraycopy(this.elenth,0,elenth,0,nelem);
         System.arraycopy(this.elpos,0,elpos,0,nelem);
         elrad = .5*(new Double(lelrad.getText())).doubleValue();
         frequency = (new Double(lfreq.getText())).doubleValue();
         nseg = (new Integer(lnseg.getText())).intValue();
         switch (units) {
            case Units.Radian:
               conversion = 1.0;
               break;
            case Units.WaveLength:
               conversion = Units.WaveToRadian;
               break;
            case Units.Cm:
               conversion = Units.CmToRadian*frequency;
               break;
            case Units.Mm:
               conversion = Units.MmToRadian*frequency;
               break;
            case Units.Meter:
               conversion = Units.MToRadian*frequency;
               break;
            case Units.Inch:
               conversion = Units.InchToRadian*frequency;
               break;
            case Units.Foot:
               conversion = Units.FootToRadian*frequency;
               break;
            default:
               conversion = 1.0;
         }
         elrad *= conversion;
         for (i=0;i<nelem;i++) {
            elpos[i] *= conversion;
            elenth[i] *= conversion;
         }
//         System.out.println("units = " + units);
//         System.out.println("frequency = " + frequency);
//         System.out.println("first element = " + elenth[0]);
         if (units == Units.WaveLength || units == Units.Radian) {
            r = 0.0;
         } else {
//            skin = 1./(Math.sqrt(0.4*frequency*sigma)*Math.PI);
//            r = 1.0/(sigma*skin);
              r = Math.sqrt(0.4*frequency*sigmai)*Math.PI;
//            System.out.println(" skin " + skin);
         }
//         System.out.println(" r " + r);
         long t0 = System.currentTimeMillis();
         y = new Yagi(elenth,elpos,elrad,nelem,nseg,ndrive,r);
         long t1 = System.currentTimeMillis();
         ltime.setText(Trunc.toString(.001*(t1-t0),2) + " seconds");
//         System.out.println(" Setup+Solve = " + (.001*(t1-t0)));
        
         Complex zin = y.zin();
//         System.out.println("Impedance = " + zin.real() + " " + zin.imag());
         gain = y.gaindB();
         fb = y.ftobdB();
//         System.out.println(" loss = " + y.fracloss());
         lgain.setText("Gain = " + Trunc.toString(gain,2) + " dBi");
         lfb.setText("f/b = " + Trunc.toString(fb,2) + " dB");
         if (zin.imag() < 0) {
            lzin.setText("Zin = " + Trunc.toString(zin.real(),2) + " -j " +
               Trunc.toString(-zin.imag(),2));
         } else {
            lzin.setText("Zin = " + Trunc.toString(zin.real(),2) + " +j " +
               Trunc.toString(zin.imag(),2));
         }
//         System.out.println(" Gain = " + gain);
//         System.out.println(" Front to Back = " + fb);
      
/*
         for (int i=0;i<nseg;i++) {
//         System.out.println(i+ " "+ ycurrent[i].real() + " " + current[i].imag());
      }
*/

         int ngrid = 200;
         if (!first) {
            for (i=0;i<2*ngrid;i++) {
               try {
                  pplot.erasePoint(0,0);
                  pplot.erasePoint(1,0);
                  pplot.erasePoint(2,0);
                  pplot.erasePoint(3,0);
                  pplot.erasePoint(4,0);
               } catch (java.lang.ArrayIndexOutOfBoundsException e1) {}
            }
         }
         first = false;
         double[] pat = y.pattern(ngrid);
         double dphi = Math.PI/ngrid;
         double phi,x,yy,xmin;
         xmin = 0.0;
         for (i=0;i<2*ngrid;i++) xmin = Math.min(xmin,pat[i]);
         pplot.setXRange(xmin*1.1,-xmin*1.1);
         pplot.setYRange(xmin*1.1,-xmin*1.1);
         for (i=0;i<2*ngrid;i++) {
            phi = i*dphi;
            c = Math.cos(phi);
            s = Math.sin(phi);
            x = (-gain-xmin+10.0)*c;
            yy = (-gain-xmin+10.0)*s;
            pplot.addPoint(1,x,yy,i>1);
            x = (-gain-xmin)*c;
            yy = (-gain-xmin)*s;
            pplot.addPoint(2,x,yy,i>1);
            x = (-gain-xmin-10.0)*c;
            yy = (-gain-xmin-10.0)*s;
            pplot.addPoint(3,x,yy,i>1);
            x = (-gain-xmin-20.0)*c;
            yy = (-gain-xmin-20.0)*s;
            pplot.addPoint(4,x,yy,i>1);
            x = (pat[i]-xmin)*c;
            yy = (pat[i]-xmin)*s;
            pplot.addPoint(0,x,yy,i>0);
         }
         pplot.setGraphics(pplot.getGraphics());
         pplot.drawPlot(pplot.getGraphics(),true);
         bstop.disable();
         calcthread = null;
      } catch (Exception ee) {
         bstop.disable();
         calcthread = null;
      }
   }

   public String getAppletInfo() {
      return "Yagi, version 0.01" +
        " Copyright 1998, Kevin Schmidt";
   }

   public Dimension minimumSize() {
      return new Dimension(700,700);
   }

   public Dimension preferredSize() {
      return new Dimension(700,700);
   }

   public static void main(String[] args) {
      YagiModel a = new YagiModel();
      a.applet = false;
//      a.pack();
//      a.show();
//      a.init();
   }

}


class Pplot extends ptplot.Plot {

//
// extend ptplot to allow double buffering it
// We need to be able to set the graphics context to the image buffer
// That's done here.
//
   private Graphics gsave;

   public void setGraphics(Graphics g) {
      gsave = getGraphics();
      _graphics = g;
   }

   public void drawPlot (Graphics g, boolean b) { // turn off fill button
      _setButtonsVisibility(false);
      super.drawPlot(g,b);
   }

//
// For the fill stuff to work, the graphics context has to be set before
// any events will work in the plot panel. Reset graphics context here
//
   public boolean handleEvent(Event e) {
      if (gsave != null) _graphics = gsave;
      return super.handleEvent(e);
   }
}

class Ptop extends Panel {
   private Graphics back;
   private Dimension backdim;
   private Image backim;
   private Frame f = null;
   private int cursor;
   private double[] elpos,elenth;
   private int nelem,ndrive;
   private Rectangle[] elrec;
   private boolean newrect = false;
   private int rectnow = -1;
   private Label lel;
   private TextField lelpos,lelenth;
   private Panel pc;
   private Checkbox drivecheck;

   public void setGeometry(double[] elpos, double[] elenth, int nelem,
      int ndrive) {
      this.elpos = elpos;
      this.elenth = elenth;
      this.nelem = nelem;
      this.ndrive = ndrive;
      elrec = new Rectangle[nelem];
      newrect = true;
      repaint();
   }

   public void setup(Label lel, TextField lelenth, TextField lelpos, Panel pc,
      Checkbox drivecheck) {
      this.lel = lel;
      this.lelenth = lelenth;
      this.lelpos = lelpos;
      this.pc = pc;
      this.drivecheck = drivecheck;
   }

   public void paint(Graphics g) {
      update(g);
   }

   public void update(Graphics g) {
      int i,ix,iy;
      double hmax,wmin,wmax,scale;
      Dimension d = size();
      if ((back == null)
         || (d.width != backdim.width)
         || (d.height != backdim.height)) {
         backdim = d;
         backim = createImage(backdim.width,backdim.height);
         back = backim.getGraphics();
         newrect = true;
      }
      back.setColor(Color.white);
      back.fillRect(0,0,d.width,d.height);
      back.setColor(Color.blue);
      hmax = elenth[0];
      wmin = elpos[0];
      wmax = elpos[0];
      for (i=0;i<nelem;i++) {
         hmax = Math.max(hmax,elenth[i]);
         wmin = Math.min(wmin,elpos[i]);
         wmax = Math.max(wmax,elpos[i]);
      }
      hmax *=1.05;

//      System.out.println(hmax + " " + wmin + " " + wmax);

      scale = d.height/hmax;
      scale = Math.min(scale,.9*d.width/(wmax-wmin));
      ix = (int)((wmax-wmin)*scale+0.5);
//      System.out.println(" ix "+ix);
      back.fillRect(d.width/20,d.height/2-3,ix,6);
      for (i=0;i<nelem;i++) {
         ix = d.width/20+(int)(scale*(elpos[i]-wmin)+0.5)-1;
         iy = (int) (scale*elenth[i]+.5);
         if (i == ndrive) {
            back.setColor(Color.green);
         } else {
            back.setColor(Color.red);
         }
         back.fillRect(ix,(d.height-iy)/2,2,iy);
         if (newrect) {
            elrec[i] = new Rectangle(ix,(d.height-iy)/2,2,iy);
         }
      }
      g.drawImage(backim,0,0,null);
      newrect = false;
   }

   public boolean mouseMove(Event evt, int x, int y) {
      int i;
      if (newrect) {
         repaint();
         return true;
      }
      rectnow = -1;
      for (i=0;i<nelem;i++) {
try {
         if (elrec[i].inside(x,y)) {
            f.setCursor(Frame.CROSSHAIR_CURSOR);
            rectnow = i;
            break;
         }
} catch (NullPointerException ee) {
        newrect = true;
//        System.out.println(i + " " + elrec[i]);
}
      }
      if (rectnow == -1) f.setCursor(Frame.HAND_CURSOR);
      return true;
   }

   public boolean mouseExit(Event evt, int x, int y) {
      if (f == null) {
         Component c = getParent();
         while (c !=null && !(c instanceof Frame)) c=c.getParent();
         f = (Frame)c;
      }
      f.setCursor(cursor);
      return true;
   }

   public boolean mouseEnter(Event evt, int x, int y) {
//
// find the frame we live in, so the cursor can be changed
//
      if (f == null) {
         Component c = getParent();
         while (c !=null && !(c instanceof Frame)) c=c.getParent();
         f = (Frame)c;
      }
      cursor = f.getCursorType();
      f.setCursor(Frame.HAND_CURSOR);
      return true;
   }

   public boolean mouseDown(Event evt, int x, int y) {
      int i;
      if (newrect) {
         repaint();
         return true;
      }
      rectnow = -1;
      for (i=0;i<nelem;i++) {
         if (elrec[i].inside(x,y)) {
            rectnow = i;
            break;
         }
      }
      lel.setText((i+1)+"");
      lelenth.setText(Trunc.toString(elenth[i],5));
      lelpos.setText(Trunc.toString(elpos[i],5));
      drivecheck.setState(i == ndrive);
      ((CardLayout)pc.getLayout()).show(pc,"Edit");
//      System.out.println(" element " + rectnow);
      return true;
   }
}

final class Myread { // This really stinks -- There must be a better way

   public static int readi( java.io.DataInputStream d )
      throws java.io.IOException {
      int i;
      String s = d.readLine().trim(); // read and trim whitespace from ends
      i = s.indexOf((int) ' '); // find last space
      i = (i == -1) ? s.length():i; //set to length if no other spaces
      s = s.substring(0,i); // extract number
      return Integer.parseInt(s); //read it
   }

   public static long readl( java.io.DataInputStream d ) 
      throws java.io.IOException {
      int i;
      String s = d.readLine().trim(); // read and trim whitespace from ends
      i = s.indexOf((int) ' '); // find last space
      i = (i == -1) ? s.length():i; //set to length if no other spaces
      s = s.substring(0,i); // extract number
      return Long.parseLong(s); //read it
   }

   public static float readf( java.io.DataInputStream d ) 
      throws java.io.IOException {
      int i;
      String s = d.readLine().trim(); // read and trim whitespace from ends
      i = s.indexOf((int) ' '); // find last space
      i = (i == -1) ? s.length():i; //set to length if no other spaces
      s = s.substring(0,i); // extract number
      return new Float(s).floatValue(); //read it
   }

   public static double readd( java.io.DataInputStream d ) 
      throws java.io.IOException {
      int i;
      String s = d.readLine().trim(); // read and trim whitespace from ends
      i = s.indexOf((int) ' '); // find last space
      i = (i == -1) ? s.length():i; //set to length if no other spaces
      s = s.substring(0,i); // extract number
      return new Double(s).doubleValue(); //read it
   }

   public static String reads( java.io.DataInputStream d )
      throws java.io.IOException {
      int i;
      String s = d.readLine().trim(); // read and trim whitespace from ends
      i = s.indexOf((int) ' '); // find last space
      i = (i == -1) ? s.length():i; //set to length if no other spaces
      s = s.substring(0,i); // extract string
      return s; //read it
   }
}

final class Units {
   static final int Radian = 1;
   static final int WaveLength = 2;
   static final int Cm = 3;
   static final int Mm = 4;
   static final int Meter = 5;
   static final int Inch = 6;
   static final int Foot = 7;
   static double ToOhm = 29.9792458;
   static final double WaveToRadian = Math.PI*2.0;
   static final double CmToRadian = 1.e-3*2.0*Math.PI/ToOhm;
   static final double MmToRadian = 0.1*CmToRadian;
   static final double MToRadian = 100.0*CmToRadian;
   static final double InchToRadian = 2.54*CmToRadian;
   static final double FootToRadian = 12.0*InchToRadian;
   static final String ustring[] = {
      "Radians", "Wave Lengths",
      "Centimeters", "Millimeters", "Meters", "Inches", "Feet"
   };
}

final class Trunc {
   static String truncString(double d, int places) {
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

   static String toString(double d, int places) {
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
