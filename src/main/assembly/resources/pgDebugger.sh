#!/bin/sh

# **************************************************************************
#
# you should not touch the lines below 
# unless you know what you are doing ;-)
#
# **************************************************************************

if [ -z "$JAVA_HOME" ]; then
	echo the JAVA_HOME environment variable is not set!
	echo please set this as the JMS Load Tester needs it
	exit 1
fi

if [ ! -r "$JAVA_HOME"/bin/java ]; then
	echo the JAVA_HOME environment variable is not set!
	echo please set this as the JMS Load Tester needs it
	exit 1
fi

LIBS="./lib/*"
CLASS=de.marcelsauer.xxx
JAVA_EXE="$JAVA_HOME"/bin/java

#CONFIG_FILE=./conf/app.properties
#CP="$CLASSPATH":"$YOUR_CP":"$LIBS":"./conf/"
#CMD="$JAVA_EXE -Dapp.properties.file=$CONFIG_FILE -classpath $CP $CLASS"

echo "using JAVA_HOME:   $JAVA_HOME"
echo "using YOUR_CP:     $YOUR_CP"
echo "using CLASSPATH:   $CLASSPATH"
echo "using CMD:         $CMD"

exec $CMD
