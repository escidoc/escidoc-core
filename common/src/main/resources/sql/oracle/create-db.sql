/**
 * Database initialization
 * Prerequisite: Created Oracle Database with Characterset UTF8
 */
/**
 * create eSciDoc DB users and schemas
 */  
CREATE USER ${escidoc.database.user} 
IDENTIFIED BY ${escidoc.database.password}
DEFAULT TABLESPACE ${escidoc.database.default.tablespace}
TEMPORARY TABLESPACE ${escidoc.database.temp.tablespace}; 

GRANT CONNECT, create any table, create any index, drop any table, drop any index, select any table, insert any table, update any table, delete any table, create any procedure, analyze any, UNLIMITED TABLESPACE TO ${escidoc.database.user};

CREATE USER aa IDENTIFIED BY aa
DEFAULT TABLESPACE ${escidoc.database.default.tablespace}
TEMPORARY TABLESPACE ${escidoc.database.temp.tablespace};

GRANT UNLIMITED TABLESPACE TO aa;

CREATE USER adm IDENTIFIED BY adm
DEFAULT TABLESPACE ${escidoc.database.default.tablespace}
TEMPORARY TABLESPACE ${escidoc.database.temp.tablespace}; 

GRANT UNLIMITED TABLESPACE TO adm;

CREATE USER oai IDENTIFIED BY oai
DEFAULT TABLESPACE ${escidoc.database.default.tablespace}
TEMPORARY TABLESPACE ${escidoc.database.temp.tablespace}; 

GRANT UNLIMITED TABLESPACE TO oai;

CREATE USER om IDENTIFIED BY om
DEFAULT TABLESPACE ${escidoc.database.default.tablespace}
TEMPORARY TABLESPACE ${escidoc.database.temp.tablespace}; 

GRANT UNLIMITED TABLESPACE TO om;

CREATE USER sm IDENTIFIED BY sm
DEFAULT TABLESPACE ${escidoc.database.default.tablespace}
TEMPORARY TABLESPACE ${escidoc.database.temp.tablespace}; 

GRANT UNLIMITED TABLESPACE TO sm;

CREATE USER st IDENTIFIED BY st
DEFAULT TABLESPACE ${escidoc.database.default.tablespace}
TEMPORARY TABLESPACE ${escidoc.database.temp.tablespace};

GRANT UNLIMITED TABLESPACE TO st;

    
