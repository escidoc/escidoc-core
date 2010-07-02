/**
 * Database initialization
 */

/**
 * create eSciDoc DB user role
 */  
CREATE ROLE "${escidoc.database.userid}" LOGIN 
	PASSWORD '${escidoc.database.password}' 
	NOINHERIT CREATEDB VALID UNTIL 'infinity'; 

/**
 * create eSciDoc core database
 */   
CREATE DATABASE "${escidoc.database.name}" WITH ENCODING='UTF8' 
	OWNER="${escidoc.database.userid}" TEMPLATE=template0;

/**
 * create Fedora DB user role
 */  
CREATE ROLE "${fedora.database.userid}" LOGIN 
	PASSWORD '${fedora.database.password}' 
	NOINHERIT CREATEDB VALID UNTIL 'infinity';    

/**
 * create Fedora database
 */
CREATE DATABASE "${fedora.database.name}" WITH ENCODING='UTF8' 
	OWNER="${fedora.database.userid}";   

/**
 * create triple-store database
 */ 
CREATE DATABASE "riTriples" WITH ENCODING='SQL_ASCII' 
	OWNER="${fedora.database.userid}" TEMPLATE=template0;

/**
 * create database scripting language
 */ 
/* The following command is actually wrong, as it creates the PLPGSQL language 
   in the wrong database. Instead of the root database, it should be created in
   the ${escidoc.database.name} database.
   The installer takes care of this issue by directly executing the necessary
   commands in database-init.xml */
CREATE LANGUAGE plpgsql;
    