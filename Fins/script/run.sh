#!/bin/sh

APP_HOME=..
LIB=$APP_HOME/lib
BIN=$APP_HOME/build
CONF=$APP_HOME/conf
JAVA_HOME=/usr/java/jdk1.6.0_21
JAVA=$JAVA_HOME/jre/bin/java
CLASSPATH=.:$BIN:$CONF:$LIB/commons-codec-1.3.jar:$LIB/commons-httpclient-3.0.1.jar:$LIB/derby.jar:$LIB/httpmime-4.0.1.jar:$LIB/commons-csv-20060924.jar:$LIB/commons-logging-1.1.1.jar:$LIB/httpclient-4.0.1.jar:$LIB/log4j-1.2.15.jar


echo $CLASSPATH
$JAVA -cp $CLASSPATH com.basus.portfolio.Main
