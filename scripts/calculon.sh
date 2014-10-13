#!/bin/bash

if [ -z "$JAVA_HOME" ]; then
	JAVA_HOME=/opt/java
fi

if [ -z "$1" ]; then
	echo "No target specified. Try -fics or -icc"
	exit -1
fi

if [ -z "$CPASS" ]; then
	echo -n "Password: "
	read CPASS
fi

case $1 in
-fics)
	class="nl.zoidberg.calculon.fics.FICSInterface"
	;;
-icc)
	class="nl.zoidberg.calculon.icc.ICCInterface"
	;;
*)
	echo "Unknown target. Try -fics or -icc"
	exit -1
	;;
esac

mv Calculon.log.4 Calculon.log.5
mv Calculon.log.3 Calculon.log.4
mv Calculon.log.2 Calculon.log.3
mv Calculon.log.1 Calculon.log.2
mv Calculon.log Calculon.log.1

nohup java -cp target/calculon-0.2-SNAPSHOT-jar-with-dependencies.jar -Dcalculon.password=${CPASS} $class > Calculon.log 2>&1 &
sleep 1
tail -f Calculon.log
