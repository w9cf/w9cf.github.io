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
public class Yagi {
   private int nelem; // number of yagi elements
   private int ndrive; // driven element
   private int nseg; // number of segments per element
   private double[] elenth; // element lengths in radians
   private double[] elpos; // element positions in radians
   private double elrad; // element radius in radians -- assumed all same
   private double r; // resistance in ohms = 1/(conductivity*skin depth)
   private Complex[] current; // element segment currents
   private Imped yagi;
//
// speed of light to convert from internal units to Ohms
//
   private final static double conimp = 29.9792458;

   public Yagi(double[] elenth, double[] elpos, double elrad, int nelem,
      int nseg, int ndrive, double r) {
//
// make local copies
//
      this.ndrive = ndrive;
      this.nseg = nseg;
      this.nelem = nelem;
      this.elrad = elrad;
      this.elenth = new double[nelem];
      this.elpos = new double[nelem];
      System.arraycopy(elenth,0,this.elenth,0,nelem);
      System.arraycopy(elpos,0,this.elpos,0,nelem);
//
// set up impedance matrix and solve for currents
//
      r /= conimp;  // to statohms
      this.r = r;
      yagi = new Imped(nseg,nelem,elrad,elenth,elpos,r);
      current = yagi.current(ndrive);
   }

   public Complex zin() { // input impedance
      // take complex conjugate to convert from physicist's time
      // convention to engineer's convention
      return current[ndrive*nseg].inv().conjg().mult(conimp);
   }

   public double ftobdB() {
      int i,j;
      double dx,fr,fi,sr,si,er,ei,br,bi,fg,factor;
//
// calculate the electric field in the foward/reverse direction
// from the current
//
      er = 0.0;
      ei = 0.0;
      br = 0.0;
      bi = 0.0;
      for (i=0;i<nelem;i++) {
         dx = elenth[i]/(2*nseg);
         factor = 2.0*Math.tan(0.5*dx);
         fr = factor*Math.cos(elpos[i]); //phase factor
         fi = factor*Math.sin(elpos[i]);
         sr = current[i*nseg].real(); // center segment 
         si = current[i*nseg].imag();
         for (j=1;j<nseg;j++) {
            sr += 2.*current[i*nseg+j].real(); //count outer segments twice
            si += 2.*current[i*nseg+j].imag();
         }
         er += sr*fr+si*fi;
         ei += -sr*fi+si*fr;
         br += sr*fr-si*fi; // flip sign of phase for reverse direction
         bi += sr*fi+si*fr;
      }
      fg = (er*er+ei*ei)/(br*br+bi*bi); // square field for power density
      return 10.*Math.log(fg)/Math.log(10.);
   }

   public double gaindB() {
      int i,j;
      double dx,fr,fi,sr,si,er,ei,fg,factor;
//
// calculate the electric field in the foward direction
// from the current
//
      er = 0.0;
      ei = 0.0;
      for (i=0;i<nelem;i++) {
         dx = elenth[i]/(2*nseg);
         factor = 2.0*Math.tan(0.5*dx);
         fr = factor*Math.cos(elpos[i]); //phase factor
         fi = factor*Math.sin(elpos[i]);
         sr = current[i*nseg].real(); // center segment 
         si = current[i*nseg].imag();
         for (j=1;j<nseg;j++) {
            sr += 2.*current[i*nseg+j].real(); //count outer segments twice
            si += 2.*current[i*nseg+j].imag();
         }
         er += sr*fr+si*fi;
         ei += -sr*fi+si*fr;
      }
      fg = er*er+ei*ei; // square field for power density
      fg /= current[ndrive*nseg].real();
      return 10.*Math.log(fg)/Math.log(10.);
   }

   public double[] pattern(int ngrid) {
//
// compute the E and H plane patterns,
// E plane is stored in points 0 to ngrid-1,
// where phi = i*dphi, dphi = pi/ngrid;
// Similarly for H plane except it has phi = (i+1)*dphi
//
      int i,j,k,nseg2,nseg2p,nseg2m,jj;
      double c,s,dz,phi,dphi,ezr,ezi,cphi,sphi,x,z,cr,ci;
      double rdot,sdot,cdot,p,p0,factor,facr,faci;
      double dlog10 = 1.0/Math.log(10.0);
      double[] cpnoder = new double[(2*nseg+1)*nelem];
      double[] cpnodei = new double[(2*nseg+1)*nelem];
      double[] ssr = new double[nelem];
      double[] ssi = new double[nelem];
      double[] pat = new double[2*ngrid];
      p0 = 0.0;
      p = 0.0;
      nseg2 = 2*nseg;
      nseg2p = nseg2+1;
      nseg2m = nseg2-1;
      for (i=0;i<nelem;i++) {
         dz = elenth[i]/(2*nseg);
         c = Math.cos(dz);
         s = 1.0/Math.sin(dz);
         ssr[i] = current[i*nseg].real(); // center segment 
         ssi[i] = current[i*nseg].imag();
         for (j=1;j<nseg;j++) {
            ssr[i] += 2.*current[i*nseg+j].real(); //count outer segments twice
            ssi[i] += 2.*current[i*nseg+j].imag();
         }
         for (j=0;j<nseg2p;j++) {
            cr = 0.0;
            ci = 0.0;
            if (j<nseg2m) {
               jj = Math.abs(j-nseg+1);
               cr += current[i*nseg+jj].real();
               ci += current[i*nseg+jj].imag();
            }
            if (j>1) {
               jj = Math.abs(j-2-nseg+1);
               cr += current[i*nseg+jj].real();
               ci += current[i*nseg+jj].imag();
            }
            if (j>0 && j<nseg2) {
               jj = Math.abs(j-1-nseg+1);
               cr -= 2.0*c*current[i*nseg+jj].real();
               ci -= 2.0*c*current[i*nseg+jj].imag();
            }
            cpnoder[nseg2p*i+j] = s*cr;
            cpnodei[nseg2p*i+j] = s*ci;
         }
      }
      dphi = Math.PI/ngrid;
      for (k=0;k<ngrid;k++) {
         ezr = 0.0;
         ezi = 0.0;
         phi = k*dphi;
         cphi = Math.cos(phi);
         sphi = Math.sin(phi);
         for (i=0;i<nelem;i++) {
            x = elpos[i];
            dz = elenth[i]/nseg2;
            for (j=0;j<nseg2p;j++) {
               z = -0.5*elenth[i]+j*dz;
               rdot = z*sphi+x*cphi;
               cdot = Math.cos(rdot);
               sdot = -Math.sin(rdot);
               ezr += cdot*cpnoder[i*nseg2p+j]-sdot*cpnodei[i*nseg2p+j];
               ezi += sdot*cpnoder[i*nseg2p+j]+cdot*cpnodei[i*nseg2p+j];
            }
         }
         if (Math.abs(cphi) > 1.e-4) {
            p = (ezr*ezr+ezi*ezi)/(cphi*cphi);
            p = 10.0*dlog10*Math.log(p);
            if (k == 0) p0 = p;
            p -= p0;
         }
         pat[k] = p;
      }
      for (k=0;k<ngrid;k++) {
         ezr = 0.0;
         ezi = 0.0;
         phi = Math.PI+dphi*k;
         cphi = Math.cos(phi);
         for (i=0;i<nelem;i++) {
            dz = elenth[i]/(2*nseg);
            factor = 2.0*Math.tan(0.5*dz);
            facr = factor*Math.cos(cphi*elpos[i]);
            faci = -factor*Math.sin(cphi*elpos[i]);
            ezr += ssr[i]*facr-ssi[i]*faci;
            ezi += ssr[i]*faci+ssi[i]*facr;
         }
         p = ezr*ezr+ezi*ezi;
         p = 10.0*dlog10*Math.log(p)-p0;
         pat[ngrid+k] = p;
      }
      return pat;
   }

   public double fracloss() {
      int i,ii;
      double dx,s,c,ploss,cr1,ci1,cr2,ci2,p,rr,fac1,fac2;
      ploss = 0.0;
      for (i=0;i<nelem;i++) {
         dx = elenth[i]/(2*nseg);
         s = Math.sin(dx);
         c = Math.cos(dx);
         rr = r/(2.0*Math.PI*elrad);
         fac1 = rr*0.5*(dx-s*c)/(s*s);
         fac2 = -rr*0.5*(dx*c-s)/(s*s);
         for (ii=0;ii<nseg;ii++) {
            cr1 = current[i*nseg+ii].real();
            ci1 = current[i*nseg+ii].imag();
            p = (cr1*cr1+ci1*ci1)*2.0*fac1;
            if (ii != 0) p *= 2.0;
            ploss += p;
            if (ii < nseg-1) {
               cr2 = current[i*nseg+ii+1].real();
               ci2 = current[i*nseg+ii+1].imag();
               ploss += (cr1*cr2+ci1*ci2)*4.0*fac2;
            }
         }
      }
      ploss /= current[ndrive*nseg].real();
      return ploss;
   }
}
