/**
 * create proai user role
 */  
CREATE ROLE "${oaiprovider.database.user}" LOGIN PASSWORD '${oaiprovider.database.password}' VALID UNTIL 'infinity';

/**
 * create proai database
 */   
CREATE DATABASE "${oaiprovider.database.name}" WITH ENCODING='UTF8' OWNER="${oaiprovider.database.user}" TEMPLATE=template0;

