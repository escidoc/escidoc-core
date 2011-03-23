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

    /**
         * Role ContentRelationManager Policies
         */
            /**
             * A ContentRelationManager is allowed to create content-relations.
             */
            /**
             * A ContentRelationManager is allowed to update, delete, assignObjectPid, 
             * lock, unlock, submit, release, withdraw content-relations
             * that she/he has created.
             */
INSERT INTO aa.escidoc_policies (id, role_id, xml) VALUES ('escidoc:content-relation-manager-policy-1', 'escidoc:role-content-relation-manager', '
<Policy PolicyId="ContentRelationManager-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
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
                        info:escidoc/names:aa:1.0:action:create-content-relation 
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
        <Rule RuleId="ContentRelationManager-policy-rule-0" Effect="Permit">
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
                            info:escidoc/names:aa:1.0:action:create-content-relation 
                            </AttributeValue>
                            <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                        </ActionMatch>
                    </Action>
                </Actions>
            </Target>
        </Rule>
        <Rule RuleId="ContentRelationManager-policy-rule-1" Effect="Permit">
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
            <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:content-relation:created-by" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
            </Condition>
        </Rule>
    </Policy>');
  