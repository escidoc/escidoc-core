    /** IF CHANGING THE POLICY, PLEASE ADAPT THIS SECTION!!!
      <section>
        <title>ContextModifier</title>

        <para>internal id: escidoc:role-context-modifier</para>
        <para>A ContextModifier is allowed to: <itemizedlist mark="opencircle"
            spacing="compact">
            <listitem>
              <para>retrieve, update, delete, close and open contexts.</para>
            </listitem>
            <listitem>
              <para>retrieve certain roles.</para>
            </listitem>
            <listitem>
              <para>create user-account and user-group grants with scope same than this role is assigned to user.</para>
            </listitem>
          </itemizedlist></para>

        <para>This role is a limited role. 
        It is restricted to a context.</para>
      </section>
     */
INSERT INTO escidoc_role 
    (id, role_name, description, creator_id, creation_date, modified_by_id, last_modification_date) 
    VALUES ('escidoc:role-context-modifier', 'Context-Modifier', NULL, '${escidoc.creator.user}', 
    CURRENT_TIMESTAMP, '${escidoc.creator.user}', CURRENT_TIMESTAMP);

INSERT INTO scope_def 
    (id, role_id, object_type, attribute_id, attribute_object_type) 
    VALUES 
    ('escidoc:scope-def-role-context-modifier', 'escidoc:role-context-modifier', 'context', 
    'info:escidoc/names:aa:1.0:resource:context-id', 'context');
INSERT INTO scope_def 
    (id, role_id, object_type, attribute_id) 
    VALUES 
    ('escidoc:scope-def-role-context-modifier-6', 'escidoc:role-context-modifier', 'role', NULL);
INSERT INTO scope_def 
    (id, role_id, object_type, attribute_id) 
    VALUES 
    ('escidoc:scope-def-role-context-modifier-5', 'escidoc:role-context-modifier', 'user-account', NULL);

        
INSERT INTO escidoc_policies (id, role_id, xml) 
    VALUES 
    ('escidoc:policy-context-modifier', 'escidoc:role-context-modifier', 
    '<Policy PolicyId="Context-Modifier-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
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
            info:escidoc/names:aa:1.0:action:close-context 
            info:escidoc/names:aa:1.0:action:open-context 
            info:escidoc/names:aa:1.0:action:retrieve-role
            info:escidoc/names:aa:1.0:action:create-grant
            info:escidoc/names:aa:1.0:action:create-user-group-grant
          </AttributeValue>
                    <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </ActionMatch>
            </Action>
        </Actions>
    </Target>
    <Rule RuleId="Context-Modifier-policy-rule-1" Effect="Permit">
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
    </Rule>
    <Rule RuleId="Context-Modifier-policy-rule-3" Effect="Permit">
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
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">                  info:escidoc/names:aa:1.0:action:retrieve-role                  </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
        <Condition FunctionId="info:escidoc/names:aa:1.0:function:string-contains">
            <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">escidoc:role-audience escidoc:role-collaborator-modifier-container-add-remove-any-members escidoc:role-collaborator-modifier-container-add-remove-members escidoc:role-collaborator-modifier-container-update-any-members escidoc:role-collaborator-modifier-container-update-direct-members escidoc:role-collaborator-modifier escidoc:role-collaborator escidoc:role-content-relation-manager escidoc:role-content-relation-modifier escidoc:role-cone-closed-vocabulary-editor escidoc:role-cone-open-vocabulary-editor escidoc:role-moderator escidoc:role-privileged-viewer escidoc:role-depositor escidoc:role-context-modifier</AttributeValue>
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                <ResourceAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:resource:resource-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
            </Apply>
        </Condition>
    </Rule>
    <Rule RuleId="Context-modifier-policy-rule-grant-create" Effect="Permit">
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
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">                            info:escidoc/names:aa:1.0:action:create-grant  </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
        <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-is-in">
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:user-account:grant:assigned-on" DataType="http://www.w3.org/2001/XMLSchema#string"/>
            </Apply>
            <SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="info:escidoc/names:aa:1.0:subject:role-grant:escidoc:role-context-modifier:assigned-on" DataType="http://www.w3.org/2001/XMLSchema#string"/>        </Condition>
    </Rule>
    
    <Rule RuleId="Context-modifier-policy-rule-grant-create" Effect="Permit">
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
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">                            info:escidoc/names:aa:1.0:action:create-user-group-grant  </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
        <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-is-in">
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:user-grant:grant:assigned-on" DataType="http://www.w3.org/2001/XMLSchema#string"/>
            </Apply>
            <SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="info:escidoc/names:aa:1.0:subject:role-grant:escidoc:role-context-modifier:assigned-on" DataType="http://www.w3.org/2001/XMLSchema#string"/>
        </Condition>
    </Rule>
</Policy>
');

  