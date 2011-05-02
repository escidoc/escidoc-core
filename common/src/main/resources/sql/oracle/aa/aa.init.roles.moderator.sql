    /** IF CHANGING THE POLICY, PLEASE ADAPT THIS SECTION!!!
      <section>
        <title>Moderator</title>
        <para>internal id: escidoc:role-moderator</para>
        <para>A Moderator is allowed to: <itemizedlist mark="opencircle"
            spacing="compact">
            <listitem>
              <para>retrieve containers and items 
              in public-status submitted, released, in-revision and withdrawn
              and version-status pending, submitted, released or in-revision.</para>
            </listitem>
            <listitem>
              <para>release containers and items in 
              public-status submitted or released and latest-version-status submitted.</para>
            </listitem>
            <listitem>
              <para>submit containers and items in public-status 
              submitted or released and latest-version-status pending or submitted.</para>
            </listitem>
            <listitem>
              <para>revise items and containers in status submitted (public-status and latest-version-status).</para>
            </listitem>
            <listitem>
              <para>update and lock items or containers in public-status submitted or released.
              version-status has to be submitted or released
              or item was last-modified by moderator.</para>
            </listitem>
            <listitem>
              <para>retrieve content of items NOT in status pending.</para>
            </listitem>
            <listitem>
              <para>add and remove members of containers in public-status submitted or released.
              version-status has to be submitted or released
              or container was last-modified by moderator.</para>
            </listitem>
            <listitem>
              <para>create group-grants for all objects in his context.</para>
            </listitem>
          </itemizedlist></para>
        <para>This role is a limited role. It is restricted to a context.</para>
      </section>
     */
INSERT INTO aa.escidoc_role
    (id, role_name, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:role-moderator', 'Moderator', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-moderator', 'escidoc:role-moderator', 'context', 
     'info:escidoc/names:aa:1.0:resource:context-id',
     'context');
        
INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:rlc-scope-def-moderator-2', 'escidoc:role-moderator', 'item', 
     'info:escidoc/names:aa:1.0:resource:item:context',
     'context');
     
INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:rlc-scope-def-moderator-3', 'escidoc:role-moderator', 'component', 
     'info:escidoc/names:aa:1.0:resource:component:item:context',
     'context');
     
INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:rlc-scope-def-moderator-4', 'escidoc:role-moderator', 'container', 
     'info:escidoc/names:aa:1.0:resource:container:context',
     'context');
     
INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id) 
    VALUES 
    ('escidoc:scope-def-role-moderator-5', 'escidoc:role-moderator', 'grant', NULL);
    
INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id) 
    VALUES 
    ('escidoc:scope-def-role-moderator-8', 'escidoc:role-moderator', 'user-group', NULL);

