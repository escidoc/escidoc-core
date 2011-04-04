    /** IF CHANGING THE POLICY, PLEASE ADAPT THIS SECTION!!!
      <section>
        <title>Collaborator-Modifier</title>

        <para>internal id: escidoc:role-collaborator-modifier</para>
        <para>A Collaborator-Modifier is allowed to: <itemizedlist mark="opencircle"
            spacing="compact">
            <listitem>
              <para>retrieve, update, lock, unlock containers or items.</para>
            </listitem>
            <listitem>
              <para>retrieve content of components.</para>
            </listitem>
          </itemizedlist></para>

        <para>This role is a limited role. 
        It is restricted to a context, container or item.
        Scope definitions to point from container to member-items are not realized.</para>
      </section>
     */
INSERT INTO aa.escidoc_role
    (id, role_name, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:role-collaborator-modifier', 'Collaborator-Modifier', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-collaborator-modifier', 'escidoc:role-collaborator-modifier', 'component', 
     'info:escidoc/names:aa:1.0:resource:component:item',
     'item');

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-collaborator-modifier-3', 'escidoc:role-collaborator-modifier', 'component', 
     'info:escidoc/names:aa:1.0:resource:component:item:context',
     'context');

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-collaborator-modifier-4', 'escidoc:role-collaborator-modifier', 'item', 
     'info:escidoc/names:aa:1.0:resource:item-id',
     'item');

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-collaborator-modifier-6', 'escidoc:role-collaborator-modifier', 'item', 
     'info:escidoc/names:aa:1.0:resource:item:context',
     'context');

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-collaborator-modifier-7', 'escidoc:role-collaborator-modifier', 'container', 
     'info:escidoc/names:aa:1.0:resource:container-id',
     'container');

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-collaborator-modifier-9', 'escidoc:role-collaborator-modifier', 'container', 
     'info:escidoc/names:aa:1.0:resource:container:context',
     'context');

