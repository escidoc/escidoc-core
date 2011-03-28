    /** IF CHANGING THE POLICY, PLEASE ADAPT THIS SECTION!!!
      <section>
        <title>Administrator</title>
        <para>internal id: escidoc:role-administrator</para>
        <para>An Administrator is allowed to: <itemizedlist mark="opencircle"
            spacing="compact">
            <listitem>
              <para>retrieve the context,</para>
            </listitem>
            <listitem>
              <para>update the context,</para>
            </listitem>
            <listitem>
              <para>open the context,</para>
            </listitem>
            <listitem>
              <para>close the context,</para>
            </listitem>
            <listitem>
              <para>retrieve containers and items,</para>
            </listitem>
            <listitem>
              <para>withdraw containers and items,</para>
            </listitem>
            <listitem>
              <para>revise containers and items,</para>
            </listitem>
            <listitem>
              <para>create a grant (user + group),</para>
            </listitem>
            <listitem>
              <para>revoke a grant (user + group),</para>
            </listitem>
            <listitem>
              <para>retrieve a grant (user + group),</para>
            </listitem>
            <listitem>
              <para>add a selector to a user-group,</para>
            </listitem>
            <listitem>
              <para>remove a selector from a user-group,</para>
            </listitem>
            <listitem>
              <para>retrieve user-accounts,</para>
            </listitem>
            <listitem>
              <para>retrieve user-groups.</para>
            </listitem>
          </itemizedlist></para>

        <para>This role is a limited role. It is restricted to the context on
        that the role is granted to an user.</para>
      </section>
     */
INSERT INTO aa.escidoc_role
    (id, role_name, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:role-administrator', 'Administrator', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
        /** 
         * Role Administrator Scope
         */
INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-administrator', 'escidoc:role-administrator', 'context', 
     'info:escidoc/names:aa:1.0:resource:context-id',
     'context');
       
INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-administrator-2', 'escidoc:role-administrator', 'item', 
     'info:escidoc/names:aa:1.0:resource:item:context',
     'context');
     
INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-administrator-3', 'escidoc:role-administrator', 'container', 
     'info:escidoc/names:aa:1.0:resource:container:context',
     'context');
     
INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id)
     VALUES
    ('escidoc:scope-def-role-administrator-5', 'escidoc:role-administrator', 'grant', 
     null);
    
INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id)
     VALUES
    ('escidoc:rlc-scope-def-administrator-user-account', 'escidoc:role-administrator', 'user-account', 
     null);

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id)
     VALUES
    ('escidoc:rlc-scope-def-administrator-user-group', 'escidoc:role-administrator', 'user-group', 
     null);

