/**
 * delete proai database
 */   
DROP DATABASE IF EXISTS "${oaiprovider.database.name}";

/**
 * delete proai user role
 */  
DROP ROLE IF EXISTS "${oaiprovider.database.user}";

