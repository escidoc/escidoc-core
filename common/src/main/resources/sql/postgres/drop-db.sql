/**
 * Database clean up
 */

/**
 * delete eSciDoc core database
 */   
DROP DATABASE IF EXISTS "${escidoc.database.name}";

/**
 * delete Fedora database
 */   
DROP DATABASE IF EXISTS "${fedora.database.name}";

/**
 * delete triple-store database
 */   
DROP DATABASE IF EXISTS "riTriples";

/**
 * delete eSciDoc DB user role
 */  
DROP ROLE IF EXISTS "${escidoc.database.user}";

/**
 * delete Fedora DB user role
 */  
DROP ROLE IF EXISTS "${fedora.database.user}";
