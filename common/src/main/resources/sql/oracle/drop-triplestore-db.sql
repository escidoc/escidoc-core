/**
 * Database clean up
 */

/**
 * delete triplestore user
 */   
begin execute immediate 'DROP USER ${triplestore.oracle.database.user} CASCADE'; CASCADE'; exception when others then null; end;
/
