    /** IF CHANGING THE POLICY, PLEASE ADAPT THIS SECTION!!!
      <section>
        <title>User-Group-Administrator</title>
        <para>internal id: escidoc:role-user-group-administrator</para>
        <para>A User-Group-Administrator is allowed to: <itemizedlist mark="opencircle"
            spacing="compact">
            <listitem>
              <para>create user-groups.</para>
            </listitem>
            <listitem>
              <para>retrieve, update, delete, activate, deactivate user-groups (s)he created.</para>
            </listitem>
            <listitem>
              <para>add and remove user-group-selectors to user-groups (s)he created.</para>
            </listitem>
            <listitem>
              <para>create, retrieve and revoke user-group-grants for user groups (s)he created.</para>
            </listitem>
        	<listitem>
          		<para>create user-group-inspector role for users + groups with scope on user-groups (s)he created.</para>
        	</listitem>
          </itemizedlist></para>
        <para>This role is a unlimited role. (Has no scope-definitions).</para>
      </section>
     */
INSERT INTO aa.escidoc_role
    (id, role_name, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:role-user-group-administrator', 'User-Group-Administrator', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.escidoc_policies
  (id, role_id, xml)
     VALUES
  ('escidoc:policy-user-group-administrator', 'escidoc:role-user-group-administrator',
'<Policy PolicyId="User-Group-Administrator-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
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
            info:escidoc/names:aa:1.0:action:create-user-group 
            info:escidoc/names:aa:1.0:action:retrieve-user-group 
            info:escidoc/names:aa:1.0:action:update-user-group 
            info:escidoc/names:aa:1.0:action:delete-user-group 
            info:escidoc/names:aa:1.0:action:activate-user-group 
            info:escidoc/names:aa:1.0:action:deactivate-user-group 
            info:escidoc/names:aa:1.0:action:create-user-group-grant 
            info:escidoc/names:aa:1.0:action:retrieve-user-group-grant 
            info:escidoc/names:aa:1.0:action:revoke-user-group-grant 
            info:escidoc/names:aa:1.0:action:create-grant 
            info:escidoc/names:aa:1.0:action:retrieve-grant 
            info:escidoc/names:aa:1.0:action:revoke-grant 
            info:escidoc/names:aa:1.0:action:add-user-group-selectors 
            info:escidoc/names:aa:1.0:action:remove-user-group-selectors 
          </AttributeValue>
          <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
        </ActionMatch>
      </Action>
    </Actions>
  </Target>
  <Rule RuleId="User-Group-Administrator-policy-rule-1" Effect="Permit">
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
                    info:escidoc/names:aa:1.0:action:create-user-group 
                  </AttributeValue>
                  <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
              </ActionMatch>
          </Action>
      </Actions>
    </Target>
  </Rule>
  <Rule RuleId="User-Group-Administrator-policy-rule-2" Effect="Permit">
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
                    info:escidoc/names:aa:1.0:action:retrieve-user-group 
                    info:escidoc/names:aa:1.0:action:update-user-group 
                    info:escidoc/names:aa:1.0:action:delete-user-group 
                    info:escidoc/names:aa:1.0:action:activate-user-group 
                    info:escidoc/names:aa:1.0:action:deactivate-user-group 
                    info:escidoc/names:aa:1.0:action:add-user-group-selectors 
                    info:escidoc/names:aa:1.0:action:remove-user-group-selectors 
                  </AttributeValue>
                  <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
              </ActionMatch>
          </Action>
      </Actions>
    </Target>
    <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
            <SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
        </Apply>
        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
            <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:user-group:created-by" DataType="http://www.w3.org/2001/XMLSchema#string"/>
        </Apply>
    </Condition>
  </Rule>
  <Rule RuleId="User-Group-Administrator-policy-rule-3" Effect="Permit">
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
                    info:escidoc/names:aa:1.0:action:create-user-group-grant 
                    info:escidoc/names:aa:1.0:action:revoke-user-group-grant 
                    info:escidoc/names:aa:1.0:action:retrieve-user-group-grant 
                  </AttributeValue>
                  <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
              </ActionMatch>
          </Action>
      </Actions>
    </Target>
    <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:or">
    	<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
        	<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
            	<SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
        	</Apply>
        	<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
            	<ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:user-group:created-by" DataType="http://www.w3.org/2001/XMLSchema#string"/>
        	</Apply>
    	</Apply>
        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
        		<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
            		<SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
        		</Apply>
        		<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
            		<ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:user-group:grant:assigned-on:created-by" DataType="http://www.w3.org/2001/XMLSchema#string"/>
        		</Apply>
            </Apply>
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">User-Group-Inspector</AttributeValue>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:user-group:grant:role:name" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
            </Apply>
        </Apply>
	</Condition>
  </Rule>
  <Rule RuleId="User-Group-Administrator-policy-rule-4" Effect="Permit">
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
                    info:escidoc/names:aa:1.0:action:create-grant 
                    info:escidoc/names:aa:1.0:action:revoke-grant 
                    info:escidoc/names:aa:1.0:action:retrieve-grant 
                  </AttributeValue>
                  <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
              </ActionMatch>
          </Action>
      </Actions>
    </Target>
    <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
      		<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
          		<SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
      		</Apply>
        	<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
            	<ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:user-account:grant:assigned-on:created-by" DataType="http://www.w3.org/2001/XMLSchema#string"/>
        	</Apply>
        </Apply>
        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
            <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">User-Group-Inspector</AttributeValue>
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:user-account:grant:role:name" DataType="http://www.w3.org/2001/XMLSchema#string"/>
            </Apply>
        </Apply>
    </Condition>
  </Rule>
</Policy>');
