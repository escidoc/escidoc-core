/**
 * Database initialization
 * Prerequisite: Created Oracle Database with Characterset UTF8
 */
/**
 * create triplestore DB users
 */  
CREATE USER ${triplestore.oracle.database.user} 
IDENTIFIED BY ${triplestore.oracle.database.password}
DEFAULT TABLESPACE ${triplestore.oracle.default.tablespace}
TEMPORARY TABLESPACE ${triplestore.oracle.temp.tablespace}; 

GRANT CONNECT, create any table, create any index, drop any table, drop any index, select any table, insert any table, update any table, delete any table, UNLIMITED TABLESPACE TO ${triplestore.oracle.database.user};
