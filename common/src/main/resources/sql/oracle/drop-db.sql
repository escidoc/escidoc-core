/**
 * Database clean up
 */

/**
 * delete eSciDoc core users
 */   
begin execute immediate 'DROP USER ${escidoc.database.user} CASCADE'; exception when others then null; end;
/
begin execute immediate 'DROP USER aa CASCADE'; exception when others then null; end;
/
begin execute immediate 'DROP USER adm CASCADE'; exception when others then null; end;
/
begin execute immediate 'DROP USER oai CASCADE'; exception when others then null; end;
/
begin execute immediate 'DROP USER om CASCADE'; exception when others then null; end;
/
begin execute immediate 'DROP USER sm CASCADE'; exception when others then null; end;
/
begin execute immediate 'DROP USER st CASCADE'; exception when others then null; end;
/
