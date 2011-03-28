    /** IF CHANGING THE POLICY, PLEASE ADAPT THIS SECTION!!!
      <section>
        <title>System Administrator</title>
        
        <para>internal id: escidoc:role-system-administrator</para>
        <para>The System Administrator is the eSciDoc super user that is
        allowed to execute any method on any resource.</para>
      </section>
     */
INSERT INTO aa.escidoc_role
    (id, role_name, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:role-system-administrator', 'System-Administrator', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
