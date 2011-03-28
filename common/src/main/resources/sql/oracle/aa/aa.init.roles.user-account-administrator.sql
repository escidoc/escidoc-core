    /** IF CHANGING THE POLICY, PLEASE ADAPT THIS SECTION!!!
      <section>
        <title>User-Account-Administrator</title>
        <para>internal id: escidoc:role-user-account-administrator</para>
        <para>A User-Account-Administrator is allowed to: <itemizedlist mark="opencircle"
            spacing="compact">
            <listitem>
              <para>create user-accounts.</para>
            </listitem>
            <listitem>
              <para>retrieve, update, activate, deactivate user-accounts (s)he created.</para>
            </listitem>
        	<listitem>
          		<para>create, retrieve and revoke grants for user-accounts (s)he created.</para>
        	</listitem>
        	<listitem>
          		<para>create user-account-inspector role for users + groups with scope on user-accounts (s)he created.</para>
        	</listitem>
          </itemizedlist></para>
        <para>This role is a unlimited role. (Has no scope-definitions).</para>
      </section>
     */
INSERT INTO aa.escidoc_role
    (id, role_name, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:role-user-account-administrator', 'User-Account-Administrator', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
