    /** IF CHANGING THE POLICY, PLEASE ADAPT THIS SECTION!!!
      <section>
        <title>User-Group-Administrator</title>
        <para>internal id: escidoc:role-user-group-administrator</para>
        <para>A User-Group-Administrator is allowed to: <itemizedlist mark="opencircle"
            spacing="compact">
            <listitem>
              <para>create user-groups.</para>
            </listitem>
            <listitem>
              <para>retrieve, update, delete, activate, deactivate user-groups (s)he created.</para>
            </listitem>
            <listitem>
              <para>add and remove user-group-selectors to user-groups (s)he created.</para>
            </listitem>
            <listitem>
              <para>create, retrieve and revoke user-group-grants for user groups (s)he created.</para>
            </listitem>
        	<listitem>
          		<para>create user-group-inspector role for users + groups with scope on user-groups (s)he created.</para>
        	</listitem>
          </itemizedlist></para>
        <para>This role is a unlimited role. (Has no scope-definitions).</para>
      </section>
     */
INSERT INTO aa.escidoc_role
    (id, role_name, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:role-user-group-administrator', 'User-Group-Administrator', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
