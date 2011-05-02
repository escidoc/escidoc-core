Unpack the zip archive containing the foxml files to a directory of your choice. This directory will be named ${FOXML} in the following, ${PATH_TO_FOXML} will be the path to the parent directory of ${FOXML}.
Make sure Fedora repository is up and running. 
Open a command shell, change into the directory in which this file is located, and execute one of the following commands:
- (Windows) 
%FEDORA_HOME%\client\bin\fedora-ingest.bat d .\foxml info:fedora/fedora-system:FOXML-1.1 o <FEDORA_HOST>:<FEDORA_PORT> <FEDORA_USER> <FEDORA_PASSWORD> http 
- (Linux) 
$FEDORA_HOME/client/bin/fedora-ingest.sh  d ./foxml info:fedora/fedora-system:FOXML-1.1 o <FEDORA_HOST>:<FEDORA_PORT> <FEDORA_USER> <FEDORA_PASSWORD> http 

Note: The default value for both <FEDORA_USER> and <FEDORA_PASSWORD> is fedoraAdmin, if you changed any of it, you have to insert the changed value(s).
Default for <FEDORA_HOST> is localhost. Default for <FEDORA_PORT> is 8082.  

Example:
$FEDORA_HOME/client/bin/fedora-ingest.sh  d ./foxml \
	info:fedora/fedora-system:FOXML-1.1 o localhost:8082 fedoraAdmin fedoraAdmin http
	
