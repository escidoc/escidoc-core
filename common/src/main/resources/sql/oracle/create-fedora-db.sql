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

GRANT CONNECT, create table, create index, drop table, drop index, select table, insert table, update table, delete table, UNLIMITED TABLESPACE TO ${fedora.oracle.database.user};

    