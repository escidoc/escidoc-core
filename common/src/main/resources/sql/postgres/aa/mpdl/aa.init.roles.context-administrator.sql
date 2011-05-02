    /** IF CHANGING THE POLICY, PLEASE ADAPT THIS SECTION!!!
      <section>
        <title>ContextAdministrator</title>

        <para>internal id: escidoc:role-context-administrator</para>
        <para>A ContextAdministrator is allowed to: <itemizedlist mark="opencircle"
            spacing="compact">
            <listitem>
              <para>create contexts.</para>
            </listitem>
            <listitem>
              <para>retrieve certain roles.</para>
            </listitem>
            <listitem>
              <para>retrieve, update, delete, open, close contextes (s)he created.</para>
            </listitem>
          </itemizedlist></para>

        <para>This role is unlimited (no scope-definitions).</para>
      </section>
     */
INSERT INTO escidoc_role 
    (id, role_name, description, creator_id, creation_date, modified_by_id, last_modification_date) 
    VALUES 
    ('escidoc:role-context-administrator', 'Context-Administrator', NULL, '${escidoc.creator.user}', 
    CURRENT_TIMESTAMP, '${escidoc.creator.user}', CURRENT_TIMESTAMP);

INSERT INTO escidoc_policies 
    (id, role_id, xml) 
    VALUES 
    ('escidoc:policy-context-administrator', 'escidoc:role-context-administrator', 
    '<Policy PolicyId="Context-Administrator-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
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
                        info:escidoc/names:aa:1.0:action:create-context 
                        info:escidoc/names:aa:1.0:action:retrieve-context 
                        info:escidoc/names:aa:1.0:action:update-context 
                        info:escidoc/names:aa:1.0:action:delete-context 
                        info:escidoc/names:aa:1.0:action:close-context 
                        info:escidoc/names:aa:1.0:action:open-context 
                        info:escidoc/names:aa:1.0:action:retrieve-role 
                    </AttributeValue>
                    <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </ActionMatch>
            </Action>
        </Actions>
    </Target>
    <Rule RuleId="Context-Administrator-policy-rule-1" Effect="Permit">
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
                            info:escidoc/names:aa:1.0:action:create-context 
                        </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
    </Rule>
    <Rule RuleId="Context-Administrator-policy-rule-2" Effect="Permit">
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
                            info:escidoc/names:aa:1.0:action:retrieve-context 
                            info:escidoc/names:aa:1.0:action:update-context 
                            info:escidoc/names:aa:1.0:action:delete-context 
                            info:escidoc/names:aa:1.0:action:open-context 
                            info:escidoc/names:aa:1.0:action:close-context 
                        </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
        <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:context:created-by" DataType="http://www.w3.org/2001/XMLSchema#string"/>
            </Apply>
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                <SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
            </Apply>
        </Condition>
    </Rule>
    <Rule RuleId="Context-Administrator-policy-rule-3" Effect="Permit">
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
                    info:escidoc/names:aa:1.0:action:retrieve-role
                  </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
        <Condition FunctionId="info:escidoc/names:aa:1.0:function:string-contains">
            <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">escidoc:role-audience 
escidoc:role-collaborator-modifier-container-add-remove-any-members escidoc:role-collaborator-modifier-container-add-remove-members escidoc:role-collaborator-modifier-container-update-any-members escidoc:role-collaborator-modifier-container-update-direct-members escidoc:role-collaborator-modifier escidoc:role-collaborator escidoc:role-content-relation-manager escidoc:role-content-relation-modifier escidoc:role-cone-closed-vocabulary-editor escidoc:role-cone-open-vocabulary-editor escidoc:role-moderator escidoc:role-privileged-viewer escidoc:role-depositor</AttributeValue>
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                <ResourceAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:resource:resource-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
            </Apply>
        </Condition>
    </Rule>
</Policy>');

  