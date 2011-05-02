/**
 * Database initialization
 */

/**
 * create Fedora DB user role
 */  
CREATE ROLE "${fedora.database.user}" LOGIN 
	PASSWORD '${fedora.database.password}' 
	NOINHERIT CREATEDB VALID UNTIL 'infinity';    

/**
 * create Fedora database
 */
CREATE DATABASE "${fedora.database.name}" WITH ENCODING='UTF8' 
	OWNER="${fedora.database.user}";   

/**
 * create triple-store database
 */ 
CREATE DATABASE "riTriples" WITH ENCODING='SQL_ASCII' 
	OWNER="${fedora.database.user}" TEMPLATE=template0;
