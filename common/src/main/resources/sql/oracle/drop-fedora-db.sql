/**
 * Database clean up
 */

/**
 * delete eSciDoc core users
 */   
begin execute immediate 'DROP USER ${fedora.oracle.database.user} CASCADE'; exception when others then null; end;
/
