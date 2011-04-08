/**
 * Database clean up
 */

/**
 * delete Fedora database
 */   
DROP DATABASE IF EXISTS "${fedora.database.name}";

/**
 * delete triple-store database
 */   
DROP DATABASE IF EXISTS "riTriples";

/**
 * delete Fedora DB user role
 */  
DROP ROLE IF EXISTS "${fedora.database.user}";
