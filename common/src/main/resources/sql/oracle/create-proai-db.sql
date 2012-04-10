/**
 * create proai DB user and schema
 */  
CREATE USER ${proai.database.user} 
IDENTIFIED BY ${proai.database.password}
DEFAULT TABLESPACE ${escidoc.database.default.tablespace}
TEMPORARY TABLESPACE ${escidoc.database.temp.tablespace}; 

GRANT CONNECT, create any table, create any index, create any sequence, create any trigger, drop any table, drop any index, drop any sequence, drop any trigger, select any table, insert any table, update any table, delete any table, alter any sequence, alter any trigger, UNLIMITED TABLESPACE TO ${proai.database.user};

