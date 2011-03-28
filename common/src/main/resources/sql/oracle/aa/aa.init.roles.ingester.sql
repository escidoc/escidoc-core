    /** IF CHANGING THE POLICY, PLEASE ADAPT THIS SECTION!!!
      <section>
        <title>Ingester</title>
        <para>internal id: escidoc:role-ingester</para>
        <para>An Ingester is allowed to: 
          <itemizedlist mark="opencircle"
            spacing="compact">
            <listitem>
              <para>ingest object via ingest-interfcae.</para>
            </listitem>
          </itemizedlist>
        </para>
        <para>This role is unlimited (no scope-definitions).</para>
      </section>
     */
INSERT INTO aa.escidoc_role
    (id, role_name, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:role-ingester', 'Ingester', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
