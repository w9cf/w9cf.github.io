JAVAC=javac
JFLAGS=-source 1.4 -target 1.4

.SUFFIXES: .java .class

.java.class:
	$(JAVAC) $(JFLAGS) $<


CLASSES=\
   Complex.class\
   Gr821a.class\
   Gr821applet.class

SRCFILES=\
   Complex.java\
   Gr821a.java\
   Gr821applet.java

all: $(CLASSES) gr821.jar

clean:
	rm -f *.class *.manifest
	rm -f gr821.jar

gr821.jar: $(CLASSES)
	echo "Manifest-Version: 1.1" > gr821.manifest
	echo "Main-Class: Gr821applet" >> gr821.manifest
	echo "Created-By: 1.2 (Schmidt)" >> gr821.manifest
	echo "Permissions: sandbox" >> gr821.manifest
	rm -f gr821.jar
	jar -cfm gr821.jar gr821.manifest *class
	jarsigner -storepass dumbodumbo gr821.jar KevinThawte

gr821src.zip: $(SRCFILES)
	zip gr821src.zip $(SRCFILES)
