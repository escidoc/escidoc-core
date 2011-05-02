    /** IF CHANGING THE POLICY, PLEASE ADAPT THIS SECTION!!!
      <section>
        <title>OU-Administrator</title>
        <para>internal id: escidoc:role-ou-administrator</para>
        <para>A OU-Administrator is allowed to: <itemizedlist mark="opencircle"
            spacing="compact">
            <listitem>
              <para>create organizational-units, if parent is in scope.</para>
            </listitem>
            <listitem>
              <para>retrieve, modify, open, close organizational-units if parent is in scope.</para>
            </listitem>
            <listitem>
              <para>grant Organizational Unit Administrator privileges for organizational-units in scope.</para>
            </listitem>
          </itemizedlist></para>
        <para>This role is a limited role. 
        It is restricted to an organizational-unit and implicitely to all its children.</para>
      </section>
     */
INSERT INTO aa.escidoc_role
    (id, role_name, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:role-ou-administrator', 'OU-Administrator', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
        /** 
         * Role OU-Administrator Scope
         */  
INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-ou-administrator', 'escidoc:role-ou-administrator', 'organizational-unit', 
     'info:escidoc/names:aa:1.0:resource:organizational-unit:hierarchical-parents',
     'organizational-unit');

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-ou-administrator-2', 'escidoc:role-ou-administrator', 'user-account', 
     null,
     null);

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-ou-administrator-3', 'escidoc:role-ou-administrator', 'user-group', 
     null,
     null);

     
