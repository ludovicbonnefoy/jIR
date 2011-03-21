#!/bin/sh

BASEDIR=`dirname $0`
BASEDIR=`cd $BASEDIR ; pwd`

CLASSPATH=@CLASSPATH@
MAINCLASS=@MAINCLASS@

if [ -z "$JAVA_HOME" ] ; then
	JAVACMD=java
else
	JAVACMD=$JAVA_HOME/bin/java
fi

JAVA_OPTS=""
PROGRAM_OPTS=""
while [ ! -z "$1" ] ; do
	if [ "$1" = "--" ] ; then
		shift
		while [ ! -z "$1" ] ; do
			PROGRAM_OPTS="$PROGRAM_OPTS $1"
			shift
		done
	else
		JAVA_OPTS="$JAVA_OPTS $1"
		shift
	fi
done

$JAVACMD $JAVA_OPTS -cp $CLASSPATH $MAINCLASS $PROGRAM_OPTS 
