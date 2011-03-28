    /** IF CHANGING THE POLICY, PLEASE ADAPT THIS SECTION!!!
      <section>
        <title>Statistics-Reader</title>

        <para>internal id: escidoc:role-statistics-reader</para>
        <para>A Statistics-Reader is allowed to: <itemizedlist mark="opencircle"
            spacing="compact">
            <listitem>
              <para>retrieve reports.</para>
            </listitem>
          </itemizedlist></para>

        <para>This role is a limited role. It is restricted to a statistic-scope. 
        report-definition user for report-creation has to belong to that statistic-scope</para>
      </section>
     */
INSERT INTO aa.escidoc_role
    (id, role_name, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:role-statistics-reader', 'Statistics-Reader', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-statistics-reader', 'escidoc:role-statistics-reader', 'report', 
     'info:escidoc/names:aa:1.0:resource:report:scope',
     'scope');

