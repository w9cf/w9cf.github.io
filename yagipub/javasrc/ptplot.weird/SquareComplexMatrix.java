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
public class SquareComplexMatrix {
   private int n;
   private double[] areal;
   private double[] aimag;

/**
 test routine to fill a matrix with random garbage,
 solve, and verify the solution is correct. Normally this
 is not called.
*/
   public static void main(String[] args) {
      int i,j;
      int n = 100;
      Complex[] b = new Complex[n];
      Complex[] x,btest;
      SquareComplexMatrix s = new SquareComplexMatrix(n);
      for (i=0;i<n;i++) b[i] = new Complex(Math.random(),Math.random());
      for (i=0;i<n;i++) {
         for (j=0;j<n;j++) {
            s.setReal(Math.random(),i,j);
            s.setImag(Math.random(),i,j);
         }
      }
      x = s.solve(b);
      btest = s.vecMult(x);
      for (i=0;i<n;i++) {
         System.out.println(i + " " + Math.abs(b[i].real()-btest[i].real())
         + " " + Math.abs(b[i].imag()-btest[i].imag()));
      }
   }

/**
 Construct a square complex matrix of size n by n and set all
 its values to zero
*/
   public SquareComplexMatrix(int n) {
      int i;
      this.n = n;
      areal = new double[n*n];
      aimag = new double[n*n];
      for (i=0;i<n*n;i++) {
         areal[i] = 0.;
         aimag[i] = 0.;
      }
   }

/**
 set the real part of the i,j element of the matrix to value.
*/
   public void setReal( double value, int i, int j) {
      areal[i+n*j] = value;
   }

/**
 return the real part of the i,j element of the matrix.
*/
   public double getReal(int i, int j) {
      return areal[i+n*j];
   }

/**
 set the imaginary part of the i,j element of the matrix to value.
*/
   public void setImag( double value, int i, int j) {
      aimag[i+n*j] = value;
   }

/**
 return the imaginary part of the i,j element of the matrix.
*/
   public double getImag(int i, int j) {
      return aimag[i+n*j];
   }

/**
 return the complex value of the i,j element
*/
   public Complex getComplex(int i, int j) {
      return new Complex(areal[i+n*j],aimag[i+n*j]);
   }

/**
 set the complex value of the i,j element of the matrix to value.
*/
   public void setComplex(Complex value, int i, int j) {
      areal[i+n*j] = value.real();
      aimag[i+n*j] = value.imag();
   }

/**
 return an array of complex numbers that is the y values in the
 linear equations a y = b, where a is our complex matrix and b
 is the argument
*/
   public Complex[] solve(Complex[] b) {
      int i;
      double[] breal = new double[n];
      double[] bimag = new double[n];
      if (b.length != n) return null; // should throw an exception here
      for (i=0;i<n;i++) {
         breal[i] = b[i].real();
         bimag[i] = b[i].imag();
      }
      return solve(breal,bimag);
   }

/**
 return an array of complex numbers that is the y values in the
 linear equations a y = b, where a is our complex matrix and brealin
 and bimagin are the real and imaginary parts of b. This routine uses LU
 decomposition.
*/
   public Complex[] solve(double[] brealin, double[] bimagin) {
      int i,j,k,jmax;
      double tempd,amax,treal,timag;
      double[] scale = new double[n];
      double[] temp1array = new double[n];
      double[] temp2array = new double[n];
      double[] areal = new double[n*n];
      double[] aimag = new double[n*n];
      double[] breal = new double[n];
      double[] bimag = new double[n];
      double frac = 0.95/n;
      Complex[] b = new Complex[n];
      if (breal.length != n) return null; // should throw an exception here
      if (bimag.length != n) return null; // should throw an exception here
//
// get local copies
//
      for (i=0;i<n;i++) {
         breal[i] = brealin[i];
         bimag[i] = bimagin[i];
      }
      for (i=0;i<n*n;i++) {
         areal[i] = this.areal[i];
         aimag[i] = this.aimag[i];
      }
      for (i=0;i<n;i++) {
         amax = 0.0;
         for (j=0;j<n;j++) {
            tempd = Math.abs(areal[i+n*j])+Math.abs(aimag[i+n*j]);
            amax = Math.max(tempd,amax);
         }
         scale[i] = 1.0/amax;
      }
      for (i=0;i<n;i++) {
         for (j=0;j<i;j++) {
            treal = areal[j+n*i];
            timag = aimag[j+n*i];
            for (k=0;k<j;k++) {
               treal -= areal[j+k*n]*areal[k+n*i]-aimag[j+n*k]*aimag[k+n*i];
               timag -= areal[j+k*n]*aimag[k+n*i]+aimag[j+n*k]*areal[k+n*i];
            }
            areal[j+n*i] = treal;
            aimag[j+n*i] = timag;
         }
         jmax = i;
         amax = 0.0;
         for (j=i;j<n;j++) {
            treal = areal[j+n*i];
            timag = aimag[j+n*i];
            for (k=0;k<i;k++) {
               treal -= areal[j+n*k]*areal[k+n*i]-aimag[j+n*k]*aimag[k+n*i];
               timag -= areal[j+n*k]*aimag[k+n*i]+aimag[j+n*k]*areal[k+n*i];
            }
            areal[j+n*i] = treal;
            aimag[j+n*i] = timag;
            tempd = scale[j]*(Math.abs(treal)+Math.abs(timag));
            if (tempd > amax) {
               jmax = j;
               amax = tempd;
            }
         }
         if (i != jmax) {
            for (k=0;k<n;k++) {
               temp1array[k] = areal[jmax+n*k];
               temp2array[k] = aimag[jmax+n*k];
            }
            for (k=0;k<n;k++) {
               areal[jmax+n*k] = areal[i+n*k];
               aimag[jmax+n*k] = aimag[i+n*k];
            }
            for (k=0;k<n;k++) {
               areal[i+n*k] = temp1array[k];
               aimag[i+n*k] = temp2array[k];
            }
            scale[jmax] = scale[i];
            treal = breal[i];
            timag = bimag[i];
            breal[i] = breal[jmax];
            bimag[i] = bimag[jmax];
            breal[jmax] = treal;
            bimag[jmax] = timag;
         }
         tempd = areal[i+n*i]*areal[i+n*i]+aimag[i+n*i]*aimag[i+n*i];
         tempd = 1.0/tempd;
         treal = areal[i+n*i]*tempd;
         timag = -aimag[i+n*i]*tempd;
         for (j=i+1;j<n;j++) {
            tempd = areal[j+n*i]*treal-aimag[j+n*i]*timag;
            aimag[j+n*i] = areal[j+n*i]*timag+aimag[j+n*i]*treal;
            areal[j+n*i] = tempd;
         }
      }
      for (i=0;i<n;i++) {
         treal = breal[i];
         timag = bimag[i];
         for (j=0;j<i;j++) {
            treal -= areal[i+n*j]*breal[j]-aimag[i+n*j]*bimag[j];
            timag -= areal[i+n*j]*bimag[j]+aimag[i+n*j]*breal[j];
         }
         breal[i] = treal;
         bimag[i] = timag;
      }
      for (i=n-1;i>=0;i--) {
         treal = breal[i];
         timag = bimag[i];
         for (j=i+1;j<n;j++) {
            treal -= areal[i+n*j]*breal[j]-aimag[i+n*j]*bimag[j];
            timag -= areal[i+n*j]*bimag[j]+aimag[i+n*j]*breal[j];
         }
         tempd = areal[i+n*i]*areal[i+n*i]+aimag[i+n*i]*aimag[i+n*i];
         tempd = 1.0/tempd;
         treal *= tempd;
         timag *= tempd;
         breal[i] = treal*areal[i+n*i]+timag*aimag[i+n*i];
         bimag[i] = -treal*aimag[i+n*i]+timag*areal[i+n*i];
      }
      for (i=0;i<n;i++) b[i] = new Complex(breal[i],bimag[i]);
      return b;
   }

/**
 Multiply a x and return the result, where x is a complex vector
*/
   public Complex[] vecMult(Complex[] x) {
      int i,j,k;
      double[] xreal = new double[n];
      double[] ximag = new double[n];
      Complex[] result = new Complex[n];
      double rr,ri;
      for (i=0;i<n;i++) {
         xreal[i] = x[i].real();
         ximag[i] = x[i].imag();
      }
      for (i=0;i<n;i++) {
         rr = 0.0;
         ri = 0.0;
         k = i;
         for(j=0;j<n;j++) {
            rr += areal[k]*xreal[j]-aimag[k]*ximag[j];
            ri += areal[k]*ximag[j]+aimag[k]*xreal[j];
            k += n;
         }
         result[i] = new Complex(rr,ri);
      }
      return result;
   }
}
