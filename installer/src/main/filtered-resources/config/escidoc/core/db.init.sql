/**
 * Database initialization
 */
    /**
     * drop roles and databases, if they already exist
     */
    DROP DATABASE IF EXISTS "${escidoc.database.name}";
    DROP DATABASE IF EXISTS "${fedora.database.name}";
    DROP DATABASE IF EXISTS "riTriples";
    DROP ROLE IF EXISTS "${escidoc.database.userid}";
    DROP ROLE IF EXISTS "${fedora.database.userid}";
    /**
     * create eSciDoc DB user role
     */  
    CREATE ROLE "${escidoc.database.userid}" LOGIN PASSWORD '${escidoc.database.password}' NOINHERIT CREATEDB VALID UNTIL 'infinity'; 
    /**
     * create escidoc-core database
     */   
    CREATE DATABASE "${escidoc.database.name}" WITH ENCODING='UTF8' OWNER="${escidoc.database.userid}";
    /**
     * create Fedora DB user role
     */  
    CREATE ROLE "${fedora.database.userid}" LOGIN PASSWORD '${fedora.database.password}' NOINHERIT CREATEDB VALID UNTIL 'infinity'; 
    /**
     * create fedora31 database
     */
    CREATE DATABASE "${fedora.database.name}" WITH ENCODING='UTF8' OWNER="${fedora.database.userid}";   
    /**
     * create riTriples database
     */ 
    CREATE DATABASE "riTriples" WITH ENCODING='SQL_ASCII' OWNER="${fedora.database.userid}" TEMPLATE=template0;
