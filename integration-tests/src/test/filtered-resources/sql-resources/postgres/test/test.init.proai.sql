/**
 * delete proai database
 */   
DROP DATABASE IF EXISTS "proai";

/**
 * delete proai user role
 */  
DROP ROLE IF EXISTS "proai";

/**
 * create proai user role
 */  
CREATE ROLE "proai" LOGIN PASSWORD 'proai' VALID UNTIL 'infinity';

/**
 * create proai database
 */   
CREATE DATABASE "proai" WITH ENCODING='UTF8' OWNER="proai";

