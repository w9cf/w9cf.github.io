tran.jar:
	echo "Manifest-Version: 1.0" > tran.manifest
	echo "Main-Class: TranWindow" >> tran.manifest
	echo "Created-By: W9CF" >> tran.manifest
	jar -cfm tran.jar tran.manifest *class ptplot/*class

sign: tran.jar
	rm -rf tmpkeystore
	keytool -genkey -keystore tmpkeystore -alias w9cf -validity 3650
	keytool -selfcert -keystore tmpkeystore -alias w9cf -validity 3650
	jarsigner -keystore tmpkeystore tran.jar w9cf
