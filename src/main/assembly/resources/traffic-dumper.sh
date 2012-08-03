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

BASE_DIR=$(dirname $0)
LIBS="$BASE_DIR/lib/*"
CLASS=de.marcelsauer.traffic_dumper.TrafficDumper
JAVA_EXE="$JAVA_HOME/bin/java"

echo "using JAVA_HOME:   $JAVA_HOME"
echo "using CLASSPATH:   $LIBS"

$JAVA_EXE -classpath "$LIBS" "$CLASS" "$@"
