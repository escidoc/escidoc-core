/**
 * create oaiprovider DB user and schema
 */  
CREATE USER ${oaiprovider.database.user} 
IDENTIFIED BY ${oaiprovider.database.password}
DEFAULT TABLESPACE ${oaiprovider.database.default.tablespace}
TEMPORARY TABLESPACE ${oaiprovider.database.temp.tablespace}; 

GRANT CONNECT, create any table, create any index, create any sequence, create any trigger, drop any table, drop any index, drop any sequence, drop any trigger, select any table, insert any table, update any table, delete any table, alter any sequence, alter any trigger, UNLIMITED TABLESPACE TO ${oaiprovider.database.user};

