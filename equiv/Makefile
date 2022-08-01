#linux variables
JAVAHOME=/usr/local/jdk1.1.6v4a
CLASSPATH=.:$(JAVAHOME)/lib:$(JAVAHOME)/lib/classes.zip

JAVAC=javac
JFLAGS=

.SUFFIXES: .java .class

.java.class:
	$(JAVAC) -classpath $(CLASSPATH) $(JFLAGS) $<

CLASSES=\
   EllipticIntegral.class\
   Equiv.class\
   RectangularCrossSection.class\
   MyFrame.class\
   ZPlot.class\
   ptplot/KSPlotBoxNS.class\
   ptplot/KSPlotNS.class\
   ptplot/PlotPoint.class

SRCFILES=\
   EllipticIntegral.java\
   Equiv.java\
   RectangularCrossSection.java\
   ptplot/KSPlotBoxNS.java\
   ptplot/KSPlotNS.java\
   ptplot/PlotPoint.java\

all: $(CLASSES)
	

clean:
	rm -f *.class ptplot/*.class equiv.zip equivsrc.zip

equiv.jar: $(CLASSES)
	echo "Manifest-Version: 1.1" > equiv.manifest
	echo "Main-Class: Equiv" >> equiv.manifest
	echo "Created-By: 1.2 (Schmidt)" >> equiv.manifest
	echo "Application-Name: Equiv" >> equiv.manifest
	echo "Permissions: sandbox" >> equiv.manifest
	jar -cfm equiv.jar equiv.manifest $(CLASSES) ptplot/*.class
	jarsigner -storepass dumbodumbo equiv.jar KevinThawte

srczipfile: $(SRCFILES) README copying Makefile
	zip equivsrc.zip README copying Makefile $(SRCFILES)
