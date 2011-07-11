    /** IF CHANGING THE POLICY, PLEASE ADAPT THIS SECTION!!!
      <section>
        <title>Privileged-Viewer</title>

        <para>internal id: escidoc:role-privileged-viewer</para>
        <para>A Privileged-Viewer is allowed to: <itemizedlist mark="opencircle"
            spacing="compact">
            <listitem>
              <para>retrieve content of items, no matter in what visibility.</para>
            </listitem>
          </itemizedlist></para>

        <para>This role is a limited role. It is restricted to a context.</para>
      </section>
     */
INSERT INTO aa.escidoc_role
    (id, role_name, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:role-privileged-viewer', 'Privileged-Viewer', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-privileged-viewer', 'escidoc:role-privileged-viewer', 'component', 
     'info:escidoc/names:aa:1.0:resource:component:item:context',
     'context');
     
  