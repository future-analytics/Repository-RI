# Repository Installation and Administration Guide

.fx:cover

@conwet
---
## System Requirements

.fx: section-title

---

### Hardware Requirements
The following table contains the minimum resource requirements for running the Repository: 

* CPU: 1-2 cores with at least 2.4 GHZ</br>
* Physical RAM: 1G-2GB</br>
* Disk Space: 25GB The actual disk space depends on the amount of data being stored within the Repositories NoSQL database System.

---
## Operating System Support
The Repository has been tested in the following Operating Systems:

* Ubuntu 12.04, 14.04
* CentOS 6.3, 6.5, 7.0

---
## Software Requirements 
In order to have the Repository running, the following software is needed. However, these dependencies are not meant to be installed manually in this step, as they will be installed throughout the documentation:

* MongoDB 2.x - mandatory
* Java 1.8.x - mandatory
* Virtuoso 7.x - mandatory
* Application Server, Apache Tomcat 8.x - mandatory
* Repository Software - mandatory
* Mongo Shell - optional (JavaScript shell that allows you to execute commands on the internal data store of the Repository from the command line)

---
## Software Requirements 
To install the required version of Virtuoso, it is needed to compile it from its sources. In this way, the installation of Virtuoso has some extra requirements, that will be also installed throughout this document.

* autoconf
* automake
* libtool
* flex
* bison
* gperf
* gawk
* m4
* make
* openssl
* openssl-devel

---
## Software Installation

.fx: section-title

---
## Getting the Repository software

The packaged version of the Repository software can be downloaded from: 
* The FIWARE Files page (https://forge.fiware.org/frs/?group_id=7)
* The FIWARE catalgue (http://catalogue.fiware.org/enablers/downloads-10).

This package contains the war file of the Repository as well as the intallation scripts used in this document.

---
## Getting the Repository software

Alternatively, it is possible to install the Repository from the sources published in GitHub. To compile the source code will be necesary the following packages:

<pre>
# Ubuntu/Debian
# apt-get install git
# apt-get install maven

# CentOS
# yum -y install git
# yum -y install maven
</pre>
---
## Getting the Repository software
To download the source code usig git, execute the following command:

<pre>
# git clone https://github.com/conwetlab/Repository-RI.git
</pre>

---
## Getting the Repository software
Before compiling the code, is necesary to install Java 8. This can be achieved using the script provided with the source code as follows:

<pre>
# Ubuntu/Debian
# export INSPWD=$PWD
# ./scripts/installJavaDebian.sh

# CentOS
# export INSPWD=$PWD
# # wget --no-cookies --no-check-certificate --header "Cookie: gpw_e24=http%3A%2F%2Fwww.oracle.com%2F; oraclelicense=accept-securebackup-cookie" "http://download.oracle.com/otn-pub/java/jdk/8u25-b17/jdk-8u25-linux-x64.rpm"
# sudo rpm -ivh jdk-8u25-linux-x64.rpm
</pre>

Once the Repository sources have been downloaded, go to <code>Repository-RI</code> folder, and execute:

<pre>
# mvn clean install
</pre>

This command will compile the source code and generate the war file <code>FiwareRepository.war</code> in the <code>taget</code> folder.

---
## Installing the Repository using scripts

At this  step it is assumed that you aready have the war file of the Repository.

In order to facilitate the installation of the Repository, the script *install.sh* has been provided. This script installs all needed dependencies, configures the repository and deploys it. To use this script execute the following command:

    $ ./install.sh

---
## Installing the Repository using scripts

The installation script also optionally resolves the extra dependencies that are needed for the installation of Virtuoso.

<pre>
Some packages are needed for installing Virtuoso: autoconf, automake, libtoo, flex, bison, gperf, gawk, m4, make, openssl, openssl-devel
Do you want to install them? Y/N
</pre>

---
## Installing the Repository using scripts
Finally, the installation script allows to configure the OAuth2 authentication of users.

<pre>
Do you want to activate OAuth2 authentication in the Repository? Y/N
y

The default OAuth2 enpoint is http://account.lab.fiware.org
Do you want to provide a different idm enpoint? Y/N
n

What is your FIWARE Client id?
[client id]

What is your FIWARE Client Secret?
[client secret]
</pre>

---
## Manually installing the Repository

#### Ubuntu/Debian

All the mandatory dependencies can be easily installed on a debian based Linux distribution using diferent scripts:

<pre>
 # export INSPWD=$PWD
 # ./installTomcat8.sh
 # ./installJavaDebian.sh
 # ./installVirtuoso.sh
 # apt-get install mongodb
</pre>

The variable <code>INSPWD</code> contains the path where the repository (Virtuoso, and Tomcat) has been installed.

---
## Manually installing the Repository

#### CentOS/RedHat

The different dependencies can be installed in CentOS/RedHat using *yum*.

<pre>
# export INSPWD=$PWD
# ./installTomcat8.sh
# wget --no-cookies --no-check-certificate --header "Cookie: gpw_e24=http%3A%2F%2Fwww.oracle.com%2F; oraclelicense=accept-securebackup-cookie" "http://download.oracle.com/otn-pub/java/jdk/8u25-b17/jdk-8u25-linux-x64.rpm"
# sudo rpm -ivh jdk-8u25-linux-x64.rpm
# ./installVirtuoso.sh
# ./installMongoCentos.sh
</pre>

---
## Configuration

.fx: section-title

---
## Configuration
This configuration section assumes that the enviroment variable INSPWD exists, this variable is created during the installation process. If it does not exists execute the following command from the directory where the repository have been installed:

    $ export INSPWD=$PWD

---  
## Virtuoso 7 Configuration

The first step is to create and configure the Virtuoso database to store RDF content. You may need to have root rights to do that.

<pre>
 $ cd $INSPWD/virtuoso7/var/lib/virtuoso/db/
 $ $INSPWD/virtuoso7/bin/virtuoso-t -f &
 $ cd $INSPWD
</pre>

This allows you to start the Virtuoso database. To make avanced configuration you can edit the file <code>$INSPWD/virtuoso7/var/lib/virtuoso/db/virtuoso.ini</code> by your own.

---
## MongoDB Configuration

The next step is to create the Repository internal database, e.g. "test". You may need to have root rights to do that. 

<pre>
 $ mongo
 $ use test
</pre>

---
## MongoDB Configuration

By default the Database saves its data in <code>/var/lib/mongodb</code>. Since all the Resources you upload to the Repository are stored there, the size of this folder can grow rapidly.
If you want to relocate that folder, you have to edit <code>/etc/mongodb.conf</code>

<pre>
# mongodb.conf

# Where to store the data.
dbpath=/var/lib/mongodb
</pre>

---
## Tomcat 8 Configuration

To continue, the next step is to start and to configurate Tomcat 8. You may need to have root rights to do that.

<pre>
 $ cd $INSPWD/apache-tomcat-8.0.22/bin/
 $ ./shutdown.sh
 $ ./startup.sh
 $ cd
</pre>

---
## Tomcat 8 Configuration
To start Apache Tomcat 8 is necesary to have some variables well configurated like <code>CATALINA_HOME, JAVA_HOME</code>. Maybe you will need configure them if you make a manual installation. 

It is possible to use the Apache Tomcat Application server as is, that is, without any further configuration. However, it is recommended to allow incoming connections to the Repository only through HTTPS. 
This can be achieved by using a front-end HTTPS server that will proxy all requests to Repository, or by configuring the Application Server in order to accept only HTTPS/SSL connection, please refer to http://tomcat.apache.org/tomcat-8.0-doc/ssl-howto.html for more information.

--- 
## Repository Configuration

If you have installed the Repository manually, you have to deploy the Repository software to your Application Server. For that you have to copy the Repository WAR package into the "webapp" folder of Apache Tomcat. 
To install it on other Java Application Servers (e.g. JBoss), please refer to the specific application server guidelines.

The repository can use OAuth2 authentication with FIWARE Lab accounts. If you have used the automatic installation script you have been already asked to choose whether you want to use this authentication mechanism and to provide OAuth2 credentials in that case. 

--- 
## Repository Configuration
The OAuth2 authentication can be enabled and disabled modifiying the file <code>web.xml</code> located at <code>WEB-INF/web.xml</code>.

To enable OAuth2 include <code>securityOAuth2.xml</code>


    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>
            /WEB-INF/securityOAuth2.xml
        </param-value>
    </context-param>

--- 
## Repository Configuration

To disable OAuth2 <code>noSecurity.xml</code>
 
    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>
            /WEB-INF/noSecurity.xml
        </param-value>
    </context-param>

--- 
## Repository Configuration
You can modify OAuth2 credentials in the <code>repository.properties</code> file located at <code>resources/properties/repository.properties</code>

<pre>
oauth2.server=https://account.lab.fiware.org
oauth2.key=[Client id]
oauth2.secret=[Client secret]
oauth2.callbackURL=http://[host]/FiwareRepository/v2/callback
</pre>

Note that if you have decided to use OAuth2 authentication you will need to modify <code>oauth2.callbackURL</code> property to include the host where the Repository is going to run. 

--- 
## Repository Configuration
Finally, you can configure the MongoDB and Virtuoso instances the Repository is going to use in <code>repository.properties</code>, which contains the following values by default.

<pre>
#MongoDb Database
mongodb.host=127.0.0.1
mongodb.db=test
mongodb.port=27017

#Virtuoso Database
virtuoso.host=jdbc:virtuoso://localhost:
virtuoso.port=1111
virtuoso.user=dba
virtuoso.password=dba
</pre>

---
## Sanity check procedures

.fx: section-title

---
## Sanity check procedures
The Sanity Check Procedures are those activities that a System Administrator has to perform to verify that an installation is ready to be tested. 
Therefore there is a preliminary set of tests to ensure that obvious or basic malfunctioning is fixed before proceeding to unit tests, integration tests and user validation.

---
## End to End testing

Although one End to End testing must be associated to the Integration Test, we can show here a quick testing to check that everything is up and running.
The first test step involves creating a new resource as well as the implicit creation of a collection. The second test step checks if meta information in different file formats can be obtained.

---
## End to End testing
**Step 1 - Create the Resource**
Create a file named resource.xml with resource content like this.
<pre>
```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<resource id="collectionA/collectionB/ResourceExample">
	<creator>Yo</creator>
	<creationDate></creationDate>
	<modificationDate></modificationDate>
	<name>Resource Example</name>
	<contentUrl>http://localhost:8080/FiwareRepository/v2/collec/collectionA/collectionB/ResourceExample</contentUrl>
	<contentFileName>http://whereistheresource.com/ResourceExample</contentFileName>
</resource>
```
</pre>

<pre>
curl -v -H "Content-Type: application/xml" -X POST --data "@resource.xml" http://[SERVER_URL]:8080/FiwareRepository/v2/collec/
</pre>

You should receive a HTTP/1.1 201 as status code

---
## End to End testing
Create a file named resourceContent.txt with arbitrary content.

<pre>
curl -v -H "Content-Type: text/plain" -X PUT --data "@resourceContent.txt" http://localhost:8080/FiwareRepository/v2/collec/collectionA/collectionB/ResourceExample
</pre>

You should receive a HTTP/1.1 200 as status code

---
## End to End testing
**Step 2 - Retrieve meta information**

Test HTML Response:

Open <code>
http://[SERVER_URL]:8080/FiwareRepository/v2/collec/collectionA/</code> in your web browser. You should receive meta information about the implicit created collection in HTML format.

Test Text Response:
<pre>
curl -v -H "Content-Type: text/plain" -X GET http://[SERVER_URL]:8080/FiwareRepository/v2/collectionA/collectionB/ResourceExample
</pre>


You should receive meta information about the implicit created collection in text format. 
You may use curl to also test the other supported content types (''application/json,application/rdf+xml,text/turtle,text/n3,text/html,text/plain,application/xml'')

---

.fx: back-cover

Thanks!

FIWARE                                FIWARE Lab
OPEN APIs FOR OPEN MINDS              Spark your imagination

         www.fiware.org               FIWARE Ops
twitter: @Fiware                      Easing your operations
