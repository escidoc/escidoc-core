Unpack the zip archive containing the foxml files to a directory of your choice. This directory will be named ${FOXML} in the following.
Make shure fedora repository is up and running and has access to the internet (otherwise the ingestion of component-escidoc_ex6.fo.xml will fail). 
Open a command shell, change into the directory ${FOXML}, and execute one of the following commands:
- (Windows) 
${FEDORA_HOME}\client\bin\fedora-ingest.bat d foxml info:fedora/fedora-system:FOXML-1.1 o localhost:8082 ${FEDORA_USER} ${FEDORA_PASSWORD} http 
- (Linux) 
${FEDORA_HOME}/client/bin/fedora-ingest.sh  d foxml info:fedora/fedora-system:FOXML-1.1 o localhost:8082 ${FEDORA_USER} ${FEDORA_PASSWORD} http 

Note: The default value for both FEDORA_USER and  FEDORA_PASSWORD is fedoraAdmin, if you changed any of it, you have to insert the changed value(s).  