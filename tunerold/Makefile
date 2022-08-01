JAVAC=javac
JFLAGS=

.SUFFIXES: .java .class

.java.class:
	$(JAVAC) $(JFLAGS) $<


CLASSES=\
   Knob.class\
   Meter.class\
   TNet.class\
   Truncate.class\
   Tuner.class

SRCFILES=\
   Knob.class\
   Meter.class\
   TNet.class\
   Truncate.class\
   Tuner.class\
   Makefile\
   copying

all: $(CLASSES) tuner.jar

clean:
	rm -f *.class
	rm -f display.jar

tuner.jar: $(CLASSES)
	echo "Manifest-Version: 1.0" > tuner.manifest
	echo "Main-Class: Tuner" >> tuner.manifest
	echo "Created-By: 1.2 (Schmidt)" >> tuner.manifest
	rm -f tuner.jar
	jar -cfm tuner.jar tuner.manifest *class

tunersrc.zip: $(SRCFILES) Makefile copying tuner.html
	zip tunersrc.zip *.java Makefile copying tuner.html
