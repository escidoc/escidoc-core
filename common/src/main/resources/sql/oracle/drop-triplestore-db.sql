/**
 * Database clean up
 */

/**
 * delete triplestore user
 */   
begin execute immediate 'DROP USER ${triplestore.database.user} CASCADE'; exception when others then null; end;
/
