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
public final class Imped {

   private SquareComplexMatrix zmat;
   private int n,nseg;

   public Imped(int nseg, int nelem, double elrad,
      double[] elenth, double[] elpos, double r) {
      int i,ii,j,jj;
      double rho,dx;
      double frac = 2.0/(nelem*(nelem+1));
      double s,c,fac1,fac2,rr;
      n = nseg*nelem;
      this.nseg = nseg;
      Complex[][] z2el;
      zmat = new SquareComplexMatrix(n);
      for (i=0;i<nelem;i++) {
         for (j=i;j<nelem;j++) {
            if (i == j) {
               z2el = imped2(2*nseg,elrad,elenth[i],elenth[i]);
               dx = elenth[i]/(2*nseg);
               s = Math.sin(dx);
               c = Math.cos(dx);
               rr = r/(2.0*Math.PI*elrad);
               fac1 = rr*0.5*(dx-s*c)/(s*s);
               fac2 = -rr*0.5*(dx*c-s)/(s*s);
               for (ii=0;ii<2*nseg-1;ii++) {
                  z2el[ii][ii] = z2el[ii][ii].add(2.0*fac1);
                  if (ii > 0) z2el[ii][ii-1] = z2el[ii][ii-1].add(fac2);
                  if (ii < 2*nseg-2) z2el[ii][ii+1] = z2el[ii][ii+1].add(fac2);
               }
            } else {
               dx = elpos[i]-elpos[j];
               rho = pythag(elrad,dx);
               z2el = imped2(2*nseg,rho,elenth[i],elenth[j]);
            }
            for (ii=0;ii<nseg;ii++) {
               for (jj=0;jj<nseg;jj++) {
                  if (jj == 0) {
                     zmat.setComplex(z2el[nseg-1+ii][nseg-1+jj]
                        ,ii+i*nseg,jj+j*nseg);
                     zmat.setComplex(z2el[nseg-1+jj][nseg-1+ii]
                        ,ii+j*nseg,jj+i*nseg);
                  } else {
                     zmat.setComplex(z2el[nseg-1+ii][nseg-1+jj].add(
                        z2el[nseg-1+ii][nseg-1-jj]),ii+i*nseg,jj+j*nseg);
                     zmat.setComplex(z2el[nseg-1+jj][nseg-1+ii].add(
                        z2el[nseg-1+jj][nseg-1-ii]),ii+j*nseg,jj+i*nseg);
                  }
               }
            }
         }
      }
   }

   public Complex[] current(int ndrive) {
      int i;
      double[] vreal = new double[n];
      double[] vimag = new double[n];
      for (i=0;i<n;i++) {
         vreal[i] = 0.0;
         vimag[i] = 0.0;
      }
      vreal[nseg*ndrive] = 1.0;
      return zmat.solve(vreal,vimag);
   }
  
   private static Complex[][] imped2(int nseg, double rho, double el1,
      double el2) {
      int i,j;
      int ncur = nseg-1;
      double[] zzreal = new double[ncur];
      double[] zzimag = new double[ncur];
      double[] z2real = new double[ncur*ncur];
      double[] z2imag = new double[ncur*ncur];
      double dz1 = el1/nseg;
      double dz2 = el2/nseg;
      double snorm1 = 1.0/Math.sin(dz1);
      double snorm2 = 1.0/Math.sin(dz2);
      double cnorm1 = Math.cos(dz1);
      double xi,xj,zseg;
      Complex ztemp;
      Complex[][] z2 = new Complex[ncur][ncur];
      for (i=0;i<ncur*ncur;i++) {
         z2real[i] = 0.0;
         z2imag[i] = 0.0;
      }
      for (i=0;i<nseg+1;i++) {
         xi = -0.5*el1+i*dz1;
         for (j=0;j<ncur;j++) {
            xj = -0.5*el2+(j+1)*dz2;
            zseg = xj-xi;
            ztemp = znode(rho,zseg,dz2);
            zzreal[j] = ztemp.real()*snorm1*snorm2;
            zzimag[j] = ztemp.imag()*snorm1*snorm2;
         }
         if (i < ncur) {
            for (j=0;j<ncur;j++) {
               z2real[j+i*ncur] -= zzreal[j];
               z2imag[j+i*ncur] -= zzimag[j];
            }
         }
         if (i >= 2) {
            for (j=0;j<ncur;j++) {
               z2real[j+(i-2)*ncur] -= zzreal[j];
               z2imag[j+(i-2)*ncur] -= zzimag[j];
            }
         }
         if (i >=1 && i <= ncur) {
            for (j=0;j<ncur;j++) {
               z2real[j+(i-1)*ncur] += 2.0*cnorm1*zzreal[j];
               z2imag[j+(i-1)*ncur] += 2.0*cnorm1*zzimag[j];
            }
         }
      }
      for (i=0;i<ncur;i++) {
         for (j=0;j<ncur;j++) {
            z2[i][j] = new Complex(z2real[j+i*ncur],z2imag[j+i*ncur]);
         }
      }
      return z2;
   }

   private static Complex znode(double rho, double z, double d) {
      double si2p,si2m,si1pt,si1mt,si1pb,si1mb;
      double ci2p,ci2m,ci1pt,ci1mt,ci1pb,ci1mb;
      double zt,zb,r1t,r1b,r2,ct,st,cb,sb;
      double znoder,znodei;
      zt = z+d;
      zb = z-d;
      r1t = pythag(rho,zt);
      r1b = pythag(rho,zb);
      r2 = pythag(rho,z);
      ct = Math.cos(zt);
      st = Math.sin(zt);
      cb = Math.cos(zb);
      sb = Math.sin(zb);
      si2p = ExpInt.si(r2+z);
      si2m = ExpInt.si(r2-z);
      si1pt = ExpInt.si(r1t+zt);
      si1mt = ExpInt.si(r1t-zt);
      si1pb = ExpInt.si(r1b+zb);
      si1mb = ExpInt.si(r1b-zb);
      ci2p = ExpInt.ci(r2+z);
      ci2m = ExpInt.ci(r2-z);
      ci1pt = ExpInt.ci(r1t+zt);
      ci1mt = ExpInt.ci(r1t-zt);
      ci1pb = ExpInt.ci(r1b+zb);
      ci1mb = ExpInt.ci(r1b-zb);
      znoder = 0.5*(ct*(ci2p-ci1pt+ci2m-ci1mt)
         +st*(si2p-si1pt-si2m+si1mt)
         +cb*(ci2p-ci1pb+ci2m-ci1mb)
         +sb*(si2p-si1pb-si2m+si1mb));
      znodei = 0.5*(ct*(si2p-si1pt+si2m-si1mt)
         +st*(-ci2p+ci1pt+ci2m-ci1mt)
         +cb*(si2p-si1pb+si2m-si1mb)
         +sb*(-ci2p+ci1pb+ci2m-ci1mb));
      return new Complex(znoder,znodei);
   }

   private static double pythag(double a , double b) {
      double aa,ab,p,q,r,s,t;
      aa = Math.abs(a);
      ab = Math.abs(b);
      p = Math.max(aa,ab);
      q = Math.min(aa,ab);
      if (q == 0.0) return p;
      t = 0.0;
      while (t != 4.0) {
         r = q/p;
         r *= r;
         t = 4.0+r;
         s = r/t;
         p += 2.0*p*s;
         q *= s;
      }
      return p;
   }
}
