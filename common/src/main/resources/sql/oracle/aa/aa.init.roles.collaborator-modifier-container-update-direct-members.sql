    /** IF CHANGING THE POLICY, PLEASE ADAPT THIS SECTION!!!
      <section>
        <title>Role Collaborator-Modifier-Container-update-direct-members</title>

        <para>internal id: escidoc:role-collaborator-modifier-container-update-direct-members</para>
        <para>An Role Collaborator-Modifier-Container-update-direct-members is allowed to: <itemizedlist mark="opencircle"
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
        It is restricted to a container. scope is not resolved hierarchically.</para>
      </section>
     */
INSERT INTO aa.escidoc_role
    (id, role_name, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:role-collaborator-modifier-container-update-direct-members', 
    'Collaborator-Modifier-Container-update-direct-members', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
        /** 
         * Role Collaborator-Modifier-Container-update-direct-members Scope
         */  
INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-collaborator-modifier-container-update-direct-members', 
    'escidoc:role-collaborator-modifier-container-update-direct-members', 
    'component', 
     'info:escidoc/names:aa:1.0:resource:component:item:container',
     'container');

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-collaborator-modifier-container-update-direct-members-4', 
    'escidoc:role-collaborator-modifier-container-update-direct-members', 
    'item', 
     'info:escidoc/names:aa:1.0:resource:item:container',
     'container');

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-collaborator-modifier-container-update-direct-members-6', 
    'escidoc:role-collaborator-modifier-container-update-direct-members', 
    'container', 
     'info:escidoc/names:aa:1.0:resource:container-id',
     'container');

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-collaborator-modifier-container-update-direct-members-7', 
    'escidoc:role-collaborator-modifier-container-update-direct-members', 
    'container', 
     'info:escidoc/names:aa:1.0:resource:container:container',
     'container');

