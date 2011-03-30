/**
 * Database clean up
 */

/**
 * delete triplestore user
 */   
begin execute immediate riTriples CASCADE'; exception when others then null; end;
/
