create database jboss;

create user tomcat identified by 'tomcat777';

GRANT ALL ON jboss.* TO 'tomcat'@'%';

