    /** IF CHANGING THE POLICY, PLEASE ADAPT THIS SECTION!!!
      <section>
        <title>Depositor</title>

        <para>internal id: escidoc:role-depositor</para>
        <para>A Depositor is allowed to: <itemizedlist mark="opencircle"
            spacing="compact">
            <listitem>
              <para>create containers and items,</para>
            </listitem>

            <listitem>
              <para>retrieve containers and items and binary content of an
              item if the user has created them,</para>
            </listitem>

            <listitem>
              <para>update containers and items in the status
              <emphasis>pending</emphasis>, <emphasis>released</emphasis>, 
              <emphasis>in-revision</emphasis> or <emphasis>submitted</emphasis> if the user has created
              them,</para>
            </listitem>

            <listitem>
              <para>lock containers and items in the status
              <emphasis>pending</emphasis>, <emphasis>released</emphasis>, 
              <emphasis>in-revision</emphasis> or <emphasis>submitted</emphasis> if the user has created
              them,</para>
            </listitem>

            <listitem>
              <para>add/remove members (containers and items) to a container that is
              in status <emphasis>pending</emphasis>, <emphasis>released</emphasis>, 
              <emphasis>in-revision</emphasis> or <emphasis>submitted</emphasis> and has been created by
              the user,</para>
            </listitem>

            <listitem>
              <para>delete containers and items not in public-status
              <emphasis>released</emphasis> or <emphasis>withdrawn</emphasis> 
              and in latest-version-status <emphasis>pending</emphasis> 
              or <emphasis>in-revision</emphasis> if the user has created
              them,</para>
            </listitem>

            <listitem>
              <para>submit, withdraw, release containers and items 
                if the user has created them,</para>
            </listitem>
            
            <listitem>
              <para>upload files to the staging area (i.e. create staging
              files).</para>
            </listitem>
          </itemizedlist></para>

        <para>This role is a limited role. It is restricted to a context.</para>
      </section>
     */
INSERT INTO aa.escidoc_role
    (id, role_name, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:role-depositor', 'Depositor', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-depositor', 'escidoc:role-depositor', 'context', 
     'info:escidoc/names:aa:1.0:resource:context-id',
     'context');
        
INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-depositior-2', 'escidoc:role-depositor', 'item', 
     'info:escidoc/names:aa:1.0:resource:item:context',
     'context');
     
INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-depositior-3', 'escidoc:role-depositor', 'component', 
     'info:escidoc/names:aa:1.0:resource:component:item:context',
     'context');
     
INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-depositior-4', 'escidoc:role-depositor', 'container', 
     'info:escidoc/names:aa:1.0:resource:container:context',
     'context');

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id)
     VALUES
    ('escidoc:rlc33', 'escidoc:role-depositor', 'staging-file', 
     null);

