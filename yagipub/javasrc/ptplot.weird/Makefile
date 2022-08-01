#Copyright (C) 1998, Kevin Schmidt
#
#    This program is free software; you can redistribute it and/or modify
#    it under the terms of the GNU General Public License as published by
#    the Free Software Foundation; either version 2 of the License, or
#    (at your option) any later version.
#
#    This program is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU General Public License for more details.
#
#    You should have received a copy of the GNU General Public License
#    along with this program; if not, write to the Free Software
#    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
#
#You can contact me at W9CF callbook address, or w9cf@ptolemy.la.asu.edu
#

#sgi variables
#PTPLOT=../ptplot/ptplot.zip
#JAVAHOME=/usr/java/classes
#NETSCAPE=/usr/local/netscape/java/classes/java40.jar
#CLASSPATH=.:$(PTPLOT):$(JAVAHOME):$(NETSCAPE)

#linux variables
PTPLOT=../ptplot/ptplot.zip
JAVAHOME=/usr/local/jdk1.1.5
NETSCAPE=/usr/local/netscape/java/classes/java40.jar
CLASSPATH=.:$(PTPLOT):$(JAVAHOME)/lib/classes.zip:$(NETSCAPE)


JAVAC=javac
JFLAGS=
JARFILE=YagiModel.jar

.SUFFIXES: .java .class

.java.class:
	$(JAVAC) -classpath $(CLASSPATH) $(JFLAGS) $<

CLASSES=\
   AppletButton.class\
   Complex.class\
   ElementNumberRange.class\
   ExpInt.class\
   Imped.class\
   K1fo144.class\
   K1fo220.class\
   K1fo432.class\
   SiCi.class\
   SquareComplexMatrix.class\
   Yagi.class\
   YagiModel.class
   
all: $(CLASSES)

$(JARFILE):$(CLASSES)
	rm -f YagiModel.jar
	jar -cf YagiModel.jar *.class ptplot/*.class

clean:
	rm -f *.class YagiModel.jar

classes:
	cp *.class ../classes
	(cd ../classes; unzip -o ../ptplot/ptplot.zip)
