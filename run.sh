#!/bin/sh

export JAVA_HOME=/usr/local/java
export CLASSPATH=.:$JAVA_HOME/lib:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
export PATH=$JAVA_HOME/bin:$PATH


#1,start supervisord
/usr/bin/supervisord &

#2,start tomcat
cd /u01/mp-manage/

java  ${JAVA_OPTS} -jar mp-manage.jar --spring.profiles.active=${START_ENV}

#3,process