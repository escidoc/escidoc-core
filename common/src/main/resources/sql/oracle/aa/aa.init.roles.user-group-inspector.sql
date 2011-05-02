    /** IF CHANGING THE POLICY, PLEASE ADAPT THIS SECTION!!!
      <section>
        <title>User-Group-Inspector</title>
        <para>internal id: escidoc:role-user-group-inspector</para>
        <para>A User-Group-Inspector is allowed to: <itemizedlist mark="opencircle"
            spacing="compact">
            <listitem>
              <para>retrieve user-groups.</para>
            </listitem>
          </itemizedlist></para>
        <para>This role is a limited role. It is restricted to a user-group.</para>
      </section>
     */
INSERT INTO aa.escidoc_role
    (id, role_name, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:role-user-group-inspector', 'User-Group-Inspector', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-user-group-inspector', 'escidoc:role-user-group-inspector', 'user-group', 
     'urn:oasis:names:tc:xacml:1.0:resource:resource-id',
     'user-group');
        
