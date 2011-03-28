    /** IF CHANGING THE POLICY, PLEASE ADAPT THIS SECTION!!!
      <section>
        <title>Collaborator-Modifier-Container-Update-any-Members</title>

        <para>internal id: escidoc:role-collaborator-modifier-container-update-any-members</para>
        <para>An Collaborator-Modifier-Container-Update-any-Members is allowed to: <itemizedlist mark="opencircle"
            spacing="compact">
            <listitem>
              <para>retrieve a container, item, component-content</para>
            </listitem>
            <listitem>
              <para>update, lock, unlock a container or item</para>
            </listitem>
            <listitem>
              <para>add members to a container</para>
            </listitem>
          </itemizedlist></para>

        <para>This role is a limited role. 
        It is restricted to a container. scope is resolved hierarchically.</para>
      </section>
     */
INSERT INTO aa.escidoc_role
    (id, role_name, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:role-collaborator-modifier-container-update-any-members', 'Collaborator-Modifier-Container-Update-any-Members', 
    '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-collaborator-modifier-container-update-any-members',
     'escidoc:role-collaborator-modifier-container-update-any-members',
      'item', 
     'info:escidoc/names:aa:1.0:resource:item:hierarchical-containers',
     'container');

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-collaborator-modifier-container-update-any-members-2',
     'escidoc:role-collaborator-modifier-container-update-any-members',
      'container', 
     'info:escidoc/names:aa:1.0:resource:container-id',
     'container');

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-collaborator-modifier-container-update-any-members-3',
     'escidoc:role-collaborator-modifier-container-update-any-members',
      'container', 
     'info:escidoc/names:aa:1.0:resource:container:hierarchical-containers',
     'container');

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-collaborator-modifier-container-update-any-members-4',
     'escidoc:role-collaborator-modifier-container-update-any-members',
      'component', 
     'info:escidoc/names:aa:1.0:resource:component:item:hierarchical-containers',
     'container');

