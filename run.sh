#!/bin/sh

export JAVA_HOME=/usr/local/java
export CLASSPATH=.:$JAVA_HOME/lib:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
export PATH=$JAVA_HOME/bin:$PATH


#1,start supervisord
/usr/bin/supervisord &

#2,start tomcat


#if [ "${START_ENV}" = "dev" ]; then
#   cd /u01/tomcat_docker_8080/webapps/ROOT
#   cp -rfp ./WEB-INF/classes/dev/*  ./WEB-INF/classes/
#fi

#if [ "${START_ENV}" = "test" ]; then
#   cd /u01/tomcat_docker_8080/webapps/ROOT
#   cp -rfp ./WEB-INF/classes/test/*  ./WEB-INF/classes/
#fi

#if [ "${START_ENV}" = "pre" ]; then
  # cd /u01/tomcat_docker_8080/webapps/ROOT
  # cp -rfp ./WEB-INF/classes/pre/*  ./WEB-INF/classes/
  # echo 'export CATALINA_OPTS="$CATALINA_OPTS -javaagent:/opt/jacocoagent.jar=includes=*,output=tcpserver,port=8044,address=${dIP}"' >>
   # /u01/tomcat_docker_8080/bin/config.pre
#fi

#if [ "${START_ENV}" = "online" ]; then
#   cd /u01/tomcat_docker_8080/webapps/ROOT
#   cp -rfp ./WEB-INF/classes/online/*  ./WEB-INF/classes/
#fi


/u01/tomcat_docker_8080/bin/run.sh

#3,process