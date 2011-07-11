    /** IF CHANGING THE POLICY, PLEASE ADAPT THIS SECTION!!!
      <section>
        <title>ContentRelationManager</title>

        <para>internal id: escidoc:role-content-relation-manager</para>
        <para>A ContentRelationManager is allowed to: <itemizedlist mark="opencircle"
            spacing="compact">
            <listitem>
              <para>create content-relations.</para>
            </listitem>
            <listitem>
              <para>delete, retrieve, update, submit, release, revise, lock and unlock 
              content-relations (s)he created.</para>
            </listitem>
            <listitem>
              <para>create role-grants with scope on content-relations (s)he created (see default-policy).</para>
            </listitem>
          </itemizedlist></para>

        <para>This role is unlimited (no scope-definitions).</para>
      </section>
     */
INSERT INTO aa.escidoc_role
    (id, role_name, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:role-content-relation-manager', 'ContentRelationManager', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);

  