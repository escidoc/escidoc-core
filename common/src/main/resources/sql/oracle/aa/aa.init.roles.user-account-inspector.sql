    /** IF CHANGING THE POLICY, PLEASE ADAPT THIS SECTION!!!
      <section>
        <title>User-Account-Inspector</title>
        <para>internal id: escidoc:role-user-account-inspector</para>
        <para>A User-Account-Inspector is allowed to: <itemizedlist mark="opencircle"
            spacing="compact">
            <listitem>
              <para>retrieve user-accounts.</para>
            </listitem>
          </itemizedlist></para>
        <para>This role is a limited role. It is restricted to a user-account.</para>
      </section>
     */
INSERT INTO aa.escidoc_role
    (id, role_name, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:role-user-account-inspector', 'User-Account-Inspector', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-user-account-inspector', 'escidoc:role-user-account-inspector', 'user-account', 
     'urn:oasis:names:tc:xacml:1.0:resource:resource-id',
     'user-account');
        
