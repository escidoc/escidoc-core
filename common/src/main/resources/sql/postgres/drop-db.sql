/**
 * Database clean up
 */

/**
 * delete eSciDoc core database
 */   
DROP DATABASE IF EXISTS "${escidoc.database.name}";

/**
 * delete eSciDoc DB user role
 */  
DROP ROLE IF EXISTS "${escidoc.database.user}";
