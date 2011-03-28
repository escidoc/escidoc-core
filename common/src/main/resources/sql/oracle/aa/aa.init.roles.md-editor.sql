    /** IF CHANGING THE POLICY, PLEASE ADAPT THIS SECTION!!!
      <section>
        <title>MD-Editor</title>

        <para>internal id: escidoc:role-md-editor</para>
        <para>An MD-Editor is allowed to: <itemizedlist mark="opencircle"
            spacing="compact">
            <listitem>
              <para>retrieve containers and items if version-status is
              <emphasis>pending</emphasis>,<emphasis>in-revision</emphasis>,
              <emphasis>submitted</emphasis> or <emphasis>released</emphasis> 
              and public-status is 
              <emphasis>submitted</emphasis>,<emphasis>released</emphasis>, 
              <emphasis>in-revision</emphasis> or <emphasis>withdrawn</emphasis>
              </para>
            </listitem>

            <listitem>
              <para>update and lock containers and items in version-status
              <emphasis>submitted</emphasis> and 
              public-status <emphasis>submitted</emphasis> or <emphasis>released</emphasis>.</para>
            </listitem>
            
            <listitem>
              <para>submit items and containers that are 
              in public-status <emphasis>submitted</emphasis> or <emphasis>released</emphasis> 
              and latest-version-status <emphasis>pending</emphasis>.</para>
            </listitem>

            <listitem>
              <para>revise items and containers that are in public-status
              <emphasis>submitted</emphasis> and latest-version-status <emphasis>submitted</emphasis>.</para>
            </listitem>
            <listitem>
              <para>retrieve content of items in public-status NOT <emphasis>pending</emphasis>.</para>
            </listitem>
            <listitem>
              <para>add and remove members from containers in version-status
              <emphasis>submitted</emphasis> and 
              public-status <emphasis>submitted</emphasis> or <emphasis>released</emphasis>.</para>
            </listitem>
          </itemizedlist></para>

        <para>This role is a limited role. It is restricted to a context.</para>
      </section>
     */
INSERT INTO aa.escidoc_role
    (id, role_name, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:role-md-editor', 'MD-Editor', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-md-editor', 'escidoc:role-md-editor', 'context', 
     'info:escidoc/names:aa:1.0:resource:context-id',
     'context');
        
INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-md-editor-2', 'escidoc:role-md-editor', 'item', 
     'info:escidoc/names:aa:1.0:resource:item:context',
     'context');
     
INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-md-editor-3', 'escidoc:role-md-editor', 'component', 
     'info:escidoc/names:aa:1.0:resource:component:item:context',
     'context');
     
INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-md-editor-4', 'escidoc:role-md-editor', 'container', 
     'info:escidoc/names:aa:1.0:resource:container:context',
     'context');

