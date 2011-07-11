/**
 * Database initialization
 */

/**
 * create eSciDoc DB user role
 */  
CREATE ROLE "${escidoc.database.user}" LOGIN 
	PASSWORD '${escidoc.database.password}' 
	NOINHERIT CREATEDB VALID UNTIL 'infinity'; 

/**
 * create eSciDoc core database
 */   
CREATE DATABASE "${escidoc.database.name}" WITH ENCODING='UTF8' 
	OWNER="${escidoc.database.user}" TEMPLATE=template0;
