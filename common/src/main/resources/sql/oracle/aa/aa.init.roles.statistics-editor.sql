    /** IF CHANGING THE POLICY, PLEASE ADAPT THIS SECTION!!!
      <section>
        <title>Statistics-Editor</title>

        <para>internal id: escidoc:role-statistics-editor</para>
        <para>A Statistics-Editor is allowed to: <itemizedlist mark="opencircle"
            spacing="compact">
            <listitem>
              <para>create statistic-data-records.</para>
            </listitem>
            <listitem>
              <para>create, retrieve and delete aggregation-definitions.</para>
            </listitem>
            <listitem>
              <para>create, retrieve, update and delete report-definitions.</para>
            </listitem>
            <listitem>
              <para>retrieve and update scopes.</para>
            </listitem>
            <listitem>
              <para>preprocess statistic-data.</para>
            </listitem>
          </itemizedlist></para>

        <para>This role is a limited role. It is restricted to a statistic-scope.</para>
      </section>
     */
INSERT INTO aa.escidoc_role
    (id, role_name, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:role-statistics-editor', 'Statistics-Editor', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-statistics-editor', 'escidoc:role-statistics-editor', 'aggregation-definition', 
     'info:escidoc/names:aa:1.0:resource:aggregation-definition:scope',
     'scope');

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-statistics-editor-2', 'escidoc:role-statistics-editor', 'report-definition', 
     'info:escidoc/names:aa:1.0:resource:report-definition:scope',
     'scope');

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-statistics-editor-3', 'escidoc:role-statistics-editor', 'statistic-data', 
     'info:escidoc/names:aa:1.0:resource:statistic-data:scope',
     'scope');

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-statistics-editor-4', 'escidoc:role-statistics-editor', 'scope', 
     'urn:oasis:names:tc:xacml:1.0:resource:resource-id',
     'scope');

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-statistics-editor-5', 'escidoc:role-statistics-editor', 'preprocessing', 
     'info:escidoc/names:aa:1.0:resource:aggregation-definition:scope',
     'scope');

