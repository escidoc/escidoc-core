    /** IF CHANGING THE POLICY, PLEASE ADAPT THIS SECTION!!!
      <section>
        <title>Cone Open Vocabulary Editor</title>

        <para>internal id: escidoc:cone-open-vocabulary-editor</para>
        <para>A Cone Open Vocabulary Editor is allowed to: <itemizedlist mark="opencircle"
            spacing="compact">
          </itemizedlist></para>

        <para>This role is unlimited (no scope-definitions).</para>
      </section>
     */
INSERT INTO escidoc_role 
    (id, role_name, description, creator_id, creation_date, modified_by_id, last_modification_date) 
    VALUES 
    ('escidoc:role-cone-open-vocabulary-editor', 'CoNE-Open-Vocabulary-Editor', NULL, '${escidoc.creator.user}', 
    CURRENT_TIMESTAMP, '${escidoc.creator.user}', CURRENT_TIMESTAMP);

INSERT INTO escidoc_policies 
    (id, role_id, xml) 
    VALUES 
    ('escidoc:role-cone-open-vocabulary-editor-policy-1', 'escidoc:role-cone-open-vocabulary-editor', 
    '<Policy PolicyId="Cone-User-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
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
                        info:escidoc/names:aa:1.0:action:retrieve-content-model 
                        info:escidoc/names:aa:1.0:action:logout 
                        info:escidoc/names:aa:1.0:action:retrieve-objects-filtered 
                        info:escidoc/names:aa:1.0:action:retrieve-staging-file 
                        info:escidoc/names:aa:1.0:action:retrieve-report 
                        info:escidoc/names:aa:1.0:action:retrieve-set-definition 
                        info:escidoc/names:aa:1.0:action:get-repository-info 
                        info:escidoc/names:aa:1.0:action:retrieve-registered-predicates 
                        info:escidoc/names:aa:1.0:action:retrieve-current-user-account 
                </AttributeValue>
                    <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </ActionMatch>
            </Action>
        </Actions>
    </Target>
    <Rule RuleId="Default-User-policy-rule-0" Effect="Permit">
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
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">                         info:escidoc/names:aa:1.0:action:retrieve-content-model                         info:escidoc/names:aa:1.0:action:logout                         info:escidoc/names:aa:1.0:action:retrieve-objects-filtered                         info:escidoc/names:aa:1.0:action:retrieve-staging-file                         info:escidoc/names:aa:1.0:action:retrieve-report                         info:escidoc/names:aa:1.0:action:retrieve-set-definition                         info:escidoc/names:aa:1.0:action:get-repository-info                        info:escidoc/names:aa:1.0:action:retrieve-registered-predicates                         info:escidoc/names:aa:1.0:action:retrieve-current-user-account                         </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
    </Rule>
</Policy>');


  