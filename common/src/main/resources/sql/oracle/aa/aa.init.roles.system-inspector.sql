    /** IF CHANGING THE POLICY, PLEASE ADAPT THIS SECTION!!!
      <section>
        <title>System Inspector</title>
        
        <para>internal id: escidoc:role-system-inspector</para>
        <para>The System Inspector is the eSciDoc read-only super user that is
        allowed to retrieve any resource object, but he/she is not allowed to
        create or update objects.</para>
      </section>
     */
INSERT INTO aa.escidoc_role
    (id, role_name, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:role-system-inspector', 'System-Inspector', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
