    /** IF CHANGING THE POLICY, PLEASE ADAPT THIS SECTION!!!
      <section>
        <title>Collaborator-Modifier-Container-Add-Remove-Members</title>

        <para>internal id: escidoc:role-collaborator-modifier-container-add-remove-members</para>
        <para>An Collaborator-Modifier-Container-Add-Remove-Members is allowed to: <itemizedlist mark="opencircle"
            spacing="compact">
            <listitem>
              <para>retrieve, update, lock, unlock containers</para>
            </listitem>
            <listitem>
              <para>add members to a container</para>
            </listitem>
          </itemizedlist></para>

        <para>This role is a limited role. 
        It is restricted to a container. scope is not resolved hierarchically.</para>
      </section>
     */
INSERT INTO aa.escidoc_role
    (id, role_name, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:role-collaborator-modifier-container-add-remove-members', 'Collaborator-Modifier-Container-Add-Remove-Members', 
    '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-collaborator-modifier-container-add-remove-members-1', 
    'escidoc:role-collaborator-modifier-container-add-remove-members', 'container', 
     'info:escidoc/names:aa:1.0:resource:container-id',
     'container');

