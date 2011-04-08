/**
 * Database clean up
 */

/**
 * delete eSciDoc core users
 */   
begin execute immediate 'DROP USER ${fedora.database.user} CASCADE'; exception when others then null; end;
/

/**
 * delete triplestore user
 */   
begin execute immediate 'DROP USER ${triplestore.database.user} CASCADE'; exception when others then null; end;
/
