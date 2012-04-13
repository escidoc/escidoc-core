/**
 * delete proai user
 */   
begin execute immediate 'DROP USER ${oaiprovider.database.user} CASCADE'; exception when others then null; end;
/
