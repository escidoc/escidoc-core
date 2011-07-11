    /** IF CHANGING THE POLICY, PLEASE ADAPT THIS SECTION!!!
      <section>
        <title>ContentRelationModifier</title>

        <para>internal id: escidoc:role-content-relation-modifier</para>
        <para>A ContentRelationModifier is allowed to: <itemizedlist mark="opencircle"
            spacing="compact">
            <listitem>
              <para>delete, retrieve, update, submit, release, revise, lock and unlock 
              content-relations.</para>
            </listitem>
          </itemizedlist></para>

        <para>This role is a limited role. 
        It is restricted to a content-relation.</para>
      </section>
     */
INSERT INTO aa.escidoc_role
    (id, role_name, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:role-content-relation-modifier', 'ContentRelationModifier', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);

INSERT INTO aa.scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type)
     VALUES
    ('escidoc:scope-def-role-content-relation-modifier', 'escidoc:role-content-relation-modifier', 'content-relation', 
     'info:escidoc/names:aa:1.0:resource:content-relation-id',
     'content-relation');
        
  