/**
 * Database initialization
 * Prerequisite: Created Oracle Database with Characterset UTF8
 */
/**
 * create eSciDoc DB users and schemas
 */  
CREATE USER ${escidoc.oracle.database.user} 
IDENTIFIED BY ${escidoc.oracle.database.password}
DEFAULT TABLESPACE ${escidoc.oracle.default.tablespace}
TEMPORARY TABLESPACE ${escidoc.oracle.temp.tablespace}; 

GRANT CONNECT, create any table, create any index, drop any table, drop any index, select any table, insert any table, update any table, delete any table, create any procedure, UNLIMITED TABLESPACE TO ${escidoc.oracle.database.user};

CREATE USER aa IDENTIFIED BY aa
DEFAULT TABLESPACE ${escidoc.oracle.default.tablespace}
TEMPORARY TABLESPACE ${escidoc.oracle.temp.tablespace};

GRANT UNLIMITED TABLESPACE TO aa;

CREATE USER adm IDENTIFIED BY adm
DEFAULT TABLESPACE ${escidoc.oracle.default.tablespace}
TEMPORARY TABLESPACE ${escidoc.oracle.temp.tablespace}; 

GRANT UNLIMITED TABLESPACE TO adm;

CREATE USER oai IDENTIFIED BY oai
DEFAULT TABLESPACE ${escidoc.oracle.default.tablespace}
TEMPORARY TABLESPACE ${escidoc.oracle.temp.tablespace}; 

GRANT UNLIMITED TABLESPACE TO oai;

CREATE USER om IDENTIFIED BY om
DEFAULT TABLESPACE ${escidoc.oracle.default.tablespace}
TEMPORARY TABLESPACE ${escidoc.oracle.temp.tablespace}; 

GRANT UNLIMITED TABLESPACE TO om;

CREATE USER sm IDENTIFIED BY sm
DEFAULT TABLESPACE ${escidoc.oracle.default.tablespace}
TEMPORARY TABLESPACE ${escidoc.oracle.temp.tablespace}; 

GRANT UNLIMITED TABLESPACE TO sm;

CREATE USER st IDENTIFIED BY st
DEFAULT TABLESPACE ${escidoc.oracle.default.tablespace}
TEMPORARY TABLESPACE ${escidoc.oracle.temp.tablespace};

GRANT UNLIMITED TABLESPACE TO st;

    