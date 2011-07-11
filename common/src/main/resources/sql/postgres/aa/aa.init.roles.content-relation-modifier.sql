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
        
INSERT INTO aa.escidoc_policies (id, role_id, xml) VALUES ('escidoc:content-relation-modifier-policy-1', 'escidoc:role-content-relation-modifier', '
<Policy PolicyId="ContentRelationModifier-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
        <Target>
            <Subjects>
                <AnySubject/>
            </Subjects>
            <Resources>
                <AnyResource/>
            </Resources>
            <Actions>
                <Action>
                    <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                        info:escidoc/names:aa:1.0:action:delete-content-relation 
                        info:escidoc/names:aa:1.0:action:retrieve-content-relation 
                        info:escidoc/names:aa:1.0:action:update-content-relation 
                        info:escidoc/names:aa:1.0:action:submit-content-relation 
                        info:escidoc/names:aa:1.0:action:release-content-relation 
                        info:escidoc/names:aa:1.0:action:revise-content-relation 
                        info:escidoc/names:aa:1.0:action:withdraw-content-relation 
                        info:escidoc/names:aa:1.0:action:lock-content-relation 
                        info:escidoc/names:aa:1.0:action:unlock-content-relation 
                        </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
        <Rule RuleId="ContentRelationModifier-policy-rule-1" Effect="Permit">
            <Target>
                <Subjects>
                    <AnySubject/>
                </Subjects>
                <Resources>
                    <AnyResource/>
                </Resources>
                <Actions>
                    <AnyAction/>
                </Actions>
            </Target>
        </Rule>
    </Policy>');
  