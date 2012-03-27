/**
 * delete proai user
 */   
begin execute immediate 'DROP USER proai CASCADE'; exception when others then null; end;
/

/**
 * create proai DB user and schema
 */  
CREATE USER proai 
IDENTIFIED BY proai
DEFAULT TABLESPACE proai
TEMPORARY TABLESPACE proai; 

GRANT CONNECT, create any table, create any index, drop any table, drop any index, select any table, insert any table, update any table, delete any table, create any procedure, analyze any, UNLIMITED TABLESPACE TO proai;

