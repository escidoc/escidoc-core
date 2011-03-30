/**
 * Database initialization
 */
    /**
     * delete eSciDoc core database
     */   
    DROP DATABASE IF EXISTS "${escidoc.database.name}";
    /**
     * delete Fedora database
     */   
    DROP DATABASE IF EXISTS "${fedora.database.name}";
    /**
     * delete triple-store database
     */   
    DROP DATABASE IF EXISTS "riTriples";
    /**
     * delete eSciDoc DB user role
     */  
    DROP ROLE IF EXISTS "${escidoc.database.userid}";
    /**
     * delete Fedora DB user role
     */  
    DROP ROLE IF EXISTS "${fedora.database.userid}";
    /**
     * delete database scripting language
     */
    /* The following command is actually wrong, as it deletes the PLPGSQL language 
       in the wrong database. Instead of the root database, it should be deleted in
       the ${escidoc.database.name} database.
       The installer takes care of this issue by directly executing the necessary
       commands in database-init.xml */
    DROP LANGUAGE IF EXISTS plpgsql;