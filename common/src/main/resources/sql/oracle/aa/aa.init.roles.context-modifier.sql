    /** IF CHANGING THE POLICY, PLEASE ADAPT THIS SECTION!!!
      <section>
        <title>Context-Modifier</title>
        <para>internal id: escidoc:role-context-modifier</para>
        <para>A Context-Modifier is allowed to: <itemizedlist mark="opencircle"
            spacing="compact">
            <listitem>
              <para>retrieve, modify, delete, open, close context granted for.</para>
            </listitem>
            <listitem>
              <para>grant other context-scoped roles to the context (s)he has privileges for (except Context Modifier role).</para>
            </listitem>
          </itemizedlist></para>
        <para>This role is a limited role. 
        It is restricted to a context.</para>
      </section>
     */
INSERT INTO aa.escidoc_role
    (id, role_name, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:role-context-modifier', 'Context-Modifier', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
        /** 
         * Role Context-Modifier Scope
         */  
INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-context-modifier', 'escidoc:role-context-modifier', 'context', 
     'info:escidoc/names:aa:1.0:resource:context-id',
     'context');

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-context-modifier-2', 'escidoc:role-context-modifier', 'user-account', 
     null,
     null);

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-context-modifier-3', 'escidoc:role-context-modifier', 'user-group', 
     null,
     null);


