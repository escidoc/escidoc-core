    /** IF CHANGING THE POLICY, PLEASE ADAPT THIS SECTION!!!
      <section>
        <title>Collaborator</title>

        <para>internal id: escidoc:role-collaborator</para>
        <para>A Collaborator is allowed to: <itemizedlist mark="opencircle"
            spacing="compact">
            <listitem>
              <para>retrieve items or containers.</para>
            </listitem>
            <listitem>
              <para>retrieve content of components.</para>
            </listitem>
          </itemizedlist></para>

        <para>This role is a limited role. 
        It is restricted to a context, container, item or component.
        If restricted to a component, user may also see the item.</para>
      </section>
     */
INSERT INTO aa.escidoc_role
    (id, role_name, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:role-collaborator', 'Collaborator', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
        /** 
         * Role Collaborator Scope
         */  
INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-collaborator', 'escidoc:role-collaborator', 'component', 
     'info:escidoc/names:aa:1.0:resource:component-id',
     'component');

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-collaborator-2', 'escidoc:role-collaborator', 'component', 
     'info:escidoc/names:aa:1.0:resource:component:item',
     'item');

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-collaborator-4', 'escidoc:role-collaborator', 'component', 
     'info:escidoc/names:aa:1.0:resource:component:item:context',
     'context');

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-collaborator-5', 'escidoc:role-collaborator', 'item', 
     'info:escidoc/names:aa:1.0:resource:item:component',
     'component');

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-collaborator-6', 'escidoc:role-collaborator', 'item', 
     'info:escidoc/names:aa:1.0:resource:item-id',
     'item');

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-collaborator-8', 'escidoc:role-collaborator', 'item', 
     'info:escidoc/names:aa:1.0:resource:item:context',
     'context');

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-collaborator-9', 'escidoc:role-collaborator', 'container', 
     'info:escidoc/names:aa:1.0:resource:container-id',
     'container');

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-collaborator-11', 'escidoc:role-collaborator', 'container', 
     'info:escidoc/names:aa:1.0:resource:container:context',
     'context');

