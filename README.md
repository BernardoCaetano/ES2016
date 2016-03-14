# MyDrive

Welcome to MyDrive!

This application will enable the creation, management and publishing of different types of files by the users of the system.

###Motivation 
The MyDrive application is being created within the Software Engineering course of the Information Systems and Computer Engineering degree, at Instituto Superior Técnico, Lisbon, Portugal.
###Installing and running
```
$ mysql -p -u root
Enter password: rootroot
```
```sql
mysql> GRANT ALL PRIVILEGES ON *.* TO 'mydrive'@'localhost' IDENTIFIED BY 'mydriv3' WITH GRANT OPTION;
mysql> CREATE DATABASE drivedb;
mysql> \q
```
```
$ git clone https://github.com/tecnico-softeng/es16tg_04-project
$ cd es16tg_04-project
```

If there is an XML file to import:
```
$ mvn clean package site exec:java -Dexec.args=drive.xml
```
Otherwise:
```
$ mvn clean package site exec:java 
```

=====
##### © 2016 Team #tg04, Software Engineering, Instituto Superior Técnico

