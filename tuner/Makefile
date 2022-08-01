JAVAC=javac
JFLAGS=-target 1.1

.SUFFIXES: .java .class

.java.class:
	$(JAVAC) $(JFLAGS) $<


CLASSES=\
   Knob.class\
   Meter.class\
   TNet.class\
   Truncate.class\
   AutoTune.class\
   Tuner.class

SRCFILES=\
   Knob.java\
   Meter.java\
   TNet.java\
   Truncate.java\
   Tuner.java\
   AutoTune.java\
   Makefile\
   copying

all: $(CLASSES) tuner.jar

clean:
	rm -f *.class *.manifest tunersrc.zip
	rm -f display.jar

tuner.jar: $(CLASSES)
	echo "Manifest-Version: 1.1" > tuner.manifest
	echo "Main-Class: Tuner" >> tuner.manifest
	echo "Created-By: 1.2 (Schmidt)" >> tuner.manifest
	echo "Permissions: sandbox" >> tuner.manifest
	rm -f tuner.jar
	jar -cfm tuner.jar tuner.manifest *class
	jarsigner -storepass dumbodumbo tuner.jar KevinThawte

tunersrc.zip: $(SRCFILES) Makefile copying tuner.html
	zip tunersrc.zip $(SRCFILES) Makefile copying tuner.html
