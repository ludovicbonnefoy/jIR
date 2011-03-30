#!/bin/sh

BASEDIR=`dirname $0`
BASEDIR=`cd $BASEDIR ; pwd`

CLASSPATH=$BASEDIR/lib/jIR.jar:$BASEDIR/lib/jgrapht-jdk1.6.jar:$BASEDIR/lib/nekohtml-1.9.14.jar:$BASEDIR/lib/stanford-ner/stanford-ner-2010-02-24.jar:$BASEDIR/lib/weka-3.6.2.jar:$BASEDIR/lib/xercesImpl-2.9.1.jar:$BASEDIR
MAINCLASS=token.probabilitydistribution.NGramsProbabilityDistributionDirichletSmoothed

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
