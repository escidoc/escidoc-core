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

INSERT INTO aa.escidoc_policies (id, role_id, xml) VALUES ('escidoc:md-editor-policy-container', 'escidoc:role-md-editor', '<Policy PolicyId="MD-Editor-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
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
                    info:escidoc/names:aa:1.0:action:retrieve-item 
                    info:escidoc/names:aa:1.0:action:update-item 
                    info:escidoc/names:aa:1.0:action:lock-item 
                    info:escidoc/names:aa:1.0:action:submit-item  
                    info:escidoc/names:aa:1.0:action:retrieve-content 
                    info:escidoc/names:aa:1.0:action:retrieve-container 
                    info:escidoc/names:aa:1.0:action:update-container 
                    info:escidoc/names:aa:1.0:action:lock-container 
                    info:escidoc/names:aa:1.0:action:submit-container 
                    info:escidoc/names:aa:1.0:action:revise-item 
                    info:escidoc/names:aa:1.0:action:revise-container 
                    info:escidoc/names:aa:1.0:action:add-members-to-container 
                    info:escidoc/names:aa:1.0:action:remove-members-from-container
                    </AttributeValue>
                    <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </ActionMatch>
            </Action>
        </Actions>
    </Target>
    <Rule RuleId="MD-Editor-policy-rule-si" Effect="Permit">
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
                        info:escidoc/names:aa:1.0:action:submit-item 
                        </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
        <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:or">
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                        <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </Apply>
                    <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">submitted</AttributeValue>
                </Apply>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                        <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </Apply>
                    <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">released</AttributeValue>
                </Apply>
            </Apply>
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:latest-version-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">pending</AttributeValue>
            </Apply>
        </Condition>
    </Rule>
    <Rule RuleId="MD-Editor-policy-rule-retrievei" Effect="Permit">
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
                        info:escidoc/names:aa:1.0:action:retrieve-item
                        </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
        <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
            <Apply FunctionId="info:escidoc/names:aa:1.0:function:string-contains">
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">submitted released in-revision withdrawn</AttributeValue>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
            </Apply>
            <Apply FunctionId="info:escidoc/names:aa:1.0:function:string-contains">
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">pending submitted released in-revision</AttributeValue>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:version-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
            </Apply>
        </Condition>
    </Rule>
    <Rule RuleId="MD-Editor-policy-rule-mi" Effect="Permit">
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
                        info:escidoc/names:aa:1.0:action:update-item 
                        info:escidoc/names:aa:1.0:action:lock-item
                        </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
        <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
            <Apply FunctionId="info:escidoc/names:aa:1.0:function:string-contains">
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">submitted released</AttributeValue>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
            </Apply>
            <Apply FunctionId="info:escidoc/names:aa:1.0:function:string-contains">
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">submitted</AttributeValue>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:version-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
            </Apply>
        </Condition>
    </Rule>
    <Rule RuleId="Moderator-policy-rule-revisei" Effect="Permit">
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
                        info:escidoc/names:aa:1.0:action:revise-item
                        </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
        <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">submitted</AttributeValue>
            </Apply>
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:latest-version-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">submitted</AttributeValue>
            </Apply>
        </Condition>
    </Rule>
    <Rule RuleId="MD-Editor-policy-rule-rcontent" Effect="Permit">
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
                        info:escidoc/names:aa:1.0:action:retrieve-content
                        </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
        <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:not">
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:component:item:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">pending</AttributeValue>
            </Apply>
        </Condition>
    </Rule>
    <Rule RuleId="MD-Editor-policy-rule-sc" Effect="Permit">
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
                        info:escidoc/names:aa:1.0:action:submit-container
                        </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
        <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:or">
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                        <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </Apply>
                    <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">submitted</AttributeValue>
                </Apply>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                        <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </Apply>
                    <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">released</AttributeValue>
                </Apply>
            </Apply>
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:latest-version-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">pending</AttributeValue>
            </Apply>
        </Condition>
    </Rule>
    <Rule RuleId="MD-Editor-policy-rule-retrievec" Effect="Permit">
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
                        info:escidoc/names:aa:1.0:action:retrieve-container
                        </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
        <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
            <Apply FunctionId="info:escidoc/names:aa:1.0:function:string-contains">
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">submitted released in-revision withdrawn</AttributeValue>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
            </Apply>
            <Apply FunctionId="info:escidoc/names:aa:1.0:function:string-contains">
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">pending submitted released in-revision</AttributeValue>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:version-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
            </Apply>
        </Condition>
    </Rule>
    <Rule RuleId="MD-Editor-policy-rule-mc" Effect="Permit">
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
                        info:escidoc/names:aa:1.0:action:update-container 
                        info:escidoc/names:aa:1.0:action:lock-container 
                        info:escidoc/names:aa:1.0:action:add-members-to-container 
                        info:escidoc/names:aa:1.0:action:remove-members-from-container
                        </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
        <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
            <Apply FunctionId="info:escidoc/names:aa:1.0:function:string-contains">
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">submitted released</AttributeValue>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
            </Apply>
            <Apply FunctionId="info:escidoc/names:aa:1.0:function:string-contains">
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">submitted</AttributeValue>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:version-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
            </Apply>
        </Condition>
    </Rule>
    <Rule RuleId="Moderator-policy-rule-revisec" Effect="Permit">
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
                        info:escidoc/names:aa:1.0:action:revise-container
                        </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
        <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">submitted</AttributeValue>
            </Apply>
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:latest-version-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">submitted</AttributeValue>
            </Apply>
        </Condition>
    </Rule>
</Policy>');
