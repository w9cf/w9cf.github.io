tran.jar:
	echo "Manifest-Version: 1.1" > tran.manifest
	echo "Main-Class: TranWindow" >> tran.manifest
	echo "Created-By: 1.2 (Schmidt)" >> tran.manifest
	echo "Permissions: sandbox" >> tran.manifest
	rm -f tran.jar
	jar -cfm tran.jar tran.manifest *class ptplot/*class
	jarsigner -storepass dumbodumbo tran.jar KevinThawte
