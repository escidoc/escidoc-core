/**
 * Database initialization
 * Prerequisite: Created Oracle Database with Characterset UTF8
 */
/**
 * create triplestore DB users
 */  
CREATE USER ${triplestore.database.user} 
IDENTIFIED BY ${triplestore.database.password}
DEFAULT TABLESPACE ${triplestore.default.tablespace}
TEMPORARY TABLESPACE ${triplestore.temp.tablespace}; 

GRANT CONNECT, create any table, create any index, drop any table, drop any index, select any table, insert any table, update any table, delete any table, UNLIMITED TABLESPACE TO ${triplestore.database.user};
