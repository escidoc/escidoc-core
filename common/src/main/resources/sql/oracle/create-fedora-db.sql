/**
 * Database initialization
 * Prerequisite: Created Oracle Database with Characterset UTF8
 */
/**
 * create fedora DB users
 */  
CREATE USER ${fedora.oracle.database.user} 
IDENTIFIED BY ${fedora.oracle.database.password}
DEFAULT TABLESPACE ${fedora.oracle.default.tablespace}
TEMPORARY TABLESPACE ${fedora.oracle.temp.tablespace}; 

GRANT CONNECT, create any table, create any index, drop any table, drop any index, select any table, insert any table, update any table, delete any table, UNLIMITED TABLESPACE TO ${fedora.oracle.database.user};

    