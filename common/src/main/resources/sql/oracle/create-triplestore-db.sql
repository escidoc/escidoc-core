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

GRANT CONNECT, create table, create index, drop table, drop index, select table, insert table, update table, delete table, UNLIMITED TABLESPACE TO ${triplestore.oracle.database.user};
