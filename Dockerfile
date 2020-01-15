FROM registry.01zhuanche.com/devops/jdk1.8_tomcat_8:v0.5

MAINTAINER lixiaolong@01zhuanche.com

# add code
ADD target/mp-manage.war /u01/tomcat_docker_8080/webapps/ROOT/
ADD run.sh  /u01/tomcat_docker_8080/

# chmod
RUN cd /u01/tomcat_docker_8080/webapps/ROOT/ && \
    unzip mp-manage.war && \
    rm -rf mp-manage.war && \
    chmod 775 /u01/tomcat_docker_8080/run.sh && \
    chmod +x  /u01/tomcat_docker_8080/run.sh

# company
ENTRYPOINT /u01/tomcat_docker_8080/run.sh