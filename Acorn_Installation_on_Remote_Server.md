# Introduction #
This description assumes that the remote server is running Linux. Although Acorn can use any relational database with JDBC drivers, here we provide instructions for MySQL DBMS.

# Installation and configuration process #
For deploying Acorn follow these steps:
  * Download the NetBeans IDE Java 6.7.1 from http://netbeans.org/downloads/6.7.1/index.html and install it on Your computer.
  * Run NetBeans IDE, select ''Team'' from the upper menu, then ''Subversion'' and ''Checkout''. Fill "Repository URL" field with url: http://a-c-o-r-n.googlecode.com/svn/trunk/. Leave the "User" and "Password" fields empty. Click "Next" and fill "Local Folder" field with path to the place where source of project will be downloaded. In "Repository Folder(s)" select "Browse", then expand "Trunk" node, hold **Ctrl** key and select all projects situated under "Trunk" node. Select "OK" and "Finish". Open all projects after download.
  * Install MySQL DBMS on the remote server by writing in the console window:
```
 $ yum install mysql-server-5.3
```
> or
```
 $ apt-get install mysql-server-5.3
```
> Run MySQL after installation:
```
 $ mysqld_safe --user=mysql --log-warnings &
```
> Next rum MySQL client:
```
 $ mysql -u root -p
```
> Then create user "acorn" with password "acorn" using client session:
```
 $ CREATE USER 'acorn'@'localhost' IDENTIFIED BY 'acorn';
 $ GRANT ALL ON *.* TO 'acorn'@'localhost' IDENTIFIED BY 'acorn';
```
> Change value of global variables "wait\_timeout" and "interactive\_timeout" to 691200 sec.:
```
 $ SET GLOBAL wait_timeout = 691200;
 $ SET GLOBAL interactive_timeout = 691200;
```
> Close client session as root. Run client session as "acorn". Create "acorn-db" database:
```
 $ CREATE DATABASE acorn-db;
```
> If something is not clear, You can find help on MySQL website http://dev.mysql.com/
  * Install Sun GlassFish Enterprise Server v2.1.1 (GlassFish V2.1.1) on the remote server. Recommended directory is _/usr/local_. Download proper MySQL connector for JDBC from http://dev.mysql.com/downloads/connector/j/. Name of driver should be as follows:
```
 mysql-connector-java-5.1.12-bin.jar
```
> Put it in _lib_ and _domains/domain1/lib_ subdirectories of installation folder.
> Run:
```
 $ sudo apt-get install libmysql-java
```
> in console window. It will install more libraries needed by the GlassFish server to communicate with MySQL.
  * Create following directory structure:
```
  /usr/local
        |_ acorn
              |_ acron-0.3
              |_ amkfba
              |_ acorn-worker
                     |_lib
```
> Copy libraries used by acorn-worker and acorn-db library to _acorn-worker/lib_ directory.  Copy sources of Amkfba project to _amkfba_ directory.
  * Depending on your servers architecture You may have to recompile the part of the system that does linear programming optimisation (amkfba project).  Recompilation may increase speed of program, which is written in C/C++. If this is necessary change directory to _/usr/local/acorn/amkfba_ and run:
```
 $ make clean
 $ make
```
> You may need to install GLPK and libSBML libraries:
```
 $ sudo yum install glpk
 $ sudo yum install libxml2
 $ sudo yum install libxml2-devel
```
  * Download sources of GraphViz for Linux from http://www.graphviz.org and install on the remote server. Add path to _bin_ directory of GraphViz to PATH variable.
  * Open "Files" window (**Ctrl+2**) in NetBeans IDE. Open and edit "local-individuals.properties" file. Write proper values for variables:
    * local-root-name" - name of folder where projects were downloaded
    * server-name" - name of remote server
  * Expand "AcornWSClient" node in "Projects" window in NetBeans IDE, then "Web Service References" node:
    * right mouse button on "AcornWSService", then "Edit Web Service Attributes". Fill "wsdlLocation" field in "Wsimport Options", click "OK".
    * right mouse button on "AcornWSService", then "Refresh Client". Check "Also replace WSDL file" box and write adress of WSDL file, click "OK".
  * Expand "acorn-ant" node in "Projects" window in NetBeans IDE. Click right mouse button on "build.xml" and choose "Run Target", "Other Targets", "server-create-resources".
  * Expand "acorn-ant" node, click right mouse button on "build.xml", next choose "Run Target", "Other Targets", "server-copy-acorn-config".
  * Expand "acorn-ant" node, click right mouse button on "build.xml", next choose "Run Target", "server-web-deploy-all". Your web browser should open logging page of Acorn Project.
  * Expand "acorn-ant" node, click right mouse button on "build.xml", next choose "Run Target", "server-worker-run-all".
  * Expand "acorn-ant" node, click right mouse button on "build.xml", next choose "Run Target", next "Other Targets" and "vaapp-run-all-on-sysbio-server".
  * Copy files "db\_backup.sh", "run\_acorn\_worker.sh", "acorn\_meintenance.sh" from acorn-ant project to some folder on the remote server. Change values of variables used in these scripts:
    * _gf\_dir_ -- directory to GlassFish instalation folder
    * _where_ -- directory where weekly backup of database will be stored
    * _root\_user_ -- MySql root username
    * _root\_pass_ -- password for MySql root
    * _db\_user_ -- MySql username
    * _db\_pass_ -- MySql password
    * _iiop\_port_ -- number of iiop port
    * _logFile_ -- directory where Acorn Worker will store log files
    * _amkfbaPath_ -- path to Amkfba directory with compiled sources
    * _jarfile_ -- path to worker jarfile
    * _path\_to\_jdk_ -- path to installation folder of jdk
> Run in console window:
```
 $ crontab -e
```
> which allows to edit cron table. Cron is demon program which runs commands periodically. Add this line to table:
```
 0 5 * * 1 /path_to_folder_where_scripts_were_copied/acorn_meintenance.sh
```