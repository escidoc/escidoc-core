/**
 * delete proai user
 */   
begin execute immediate 'DROP USER proai CASCADE'; exception when others then null; end;
/
