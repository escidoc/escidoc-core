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

INSERT INTO aa.escidoc_policies (id, role_id, xml) VALUES ('escidoc:depositor-policy-1', 'escidoc:role-depositor', '<PolicySet PolicySetId="Depositor-policies" PolicyCombiningAlgId="urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:ordered-permit-overrides">
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
    <Policy PolicyId="Depositor-policy-staging-file" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
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
                        info:escidoc/names:aa:1.0:action:create-staging-file
                        </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
        <Rule RuleId="Depositor-policy-staging-file-Rule" Effect="Permit">
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
    </Policy>
    <Policy PolicyId="Depositor-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
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
                        info:escidoc/names:aa:1.0:action:create-container 
                        info:escidoc/names:aa:1.0:action:create-item 
                        info:escidoc/names:aa:1.0:action:retrieve-container 
                        info:escidoc/names:aa:1.0:action:retrieve-item 
                        info:escidoc/names:aa:1.0:action:update-container 
                        info:escidoc/names:aa:1.0:action:delete-container 
                        info:escidoc/names:aa:1.0:action:add-members-to-container 
                        info:escidoc/names:aa:1.0:action:remove-members-from-container 
                        info:escidoc/names:aa:1.0:action:lock-container 
                        info:escidoc/names:aa:1.0:action:update-item 
                        info:escidoc/names:aa:1.0:action:delete-item 
                        info:escidoc/names:aa:1.0:action:lock-item 
                        info:escidoc/names:aa:1.0:action:retrieve-content 
                        info:escidoc/names:aa:1.0:action:submit-container 
                        info:escidoc/names:aa:1.0:action:withdraw-container 
                        info:escidoc/names:aa:1.0:action:submit-item 
                        info:escidoc/names:aa:1.0:action:withdraw-item 
                        info:escidoc/names:aa:1.0:action:release-item 
                        info:escidoc/names:aa:1.0:action:release-container
                        </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
        <Rule RuleId="Depositor-policy-rule-0" Effect="Permit">
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
                            info:escidoc/names:aa:1.0:action:create-container 
                            info:escidoc/names:aa:1.0:action:create-item
                            </AttributeValue>
                            <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                        </ActionMatch>
                    </Action>
                </Actions>
            </Target>
        </Rule>
        <Rule RuleId="Depositor-policy-rule-1a" Effect="Permit">
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
            <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:created-by" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
            </Condition>
        </Rule>
        <Rule RuleId="Depositor-policy-rule-1b" Effect="Permit">
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
            <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:created-by" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
            </Condition>
        </Rule>
        <Rule RuleId="Depositor-policy-rule-2" Effect="Permit">
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
                            info:escidoc/names:aa:1.0:action:delete-container
                            </AttributeValue>
                            <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                        </ActionMatch>
                    </Action>
                </Actions>
            </Target>
            <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:not">
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                            <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                        </Apply>
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">released</AttributeValue>
                    </Apply>
                </Apply>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:not">
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                            <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                        </Apply>
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">withdrawn</AttributeValue>
                    </Apply>
                </Apply>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:or">
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                            <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:latest-version-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                        </Apply>
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">pending</AttributeValue>
                    </Apply>
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                            <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:latest-version-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                        </Apply>
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">in-revision</AttributeValue>
                    </Apply>
                </Apply>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                        <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:created-by" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </Apply>
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                        <SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </Apply>
                </Apply>
            </Condition>
        </Rule>
        <Rule RuleId="Depositor-policy-rule-2b" Effect="Permit">
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
                            info:escidoc/names:aa:1.0:action:add-members-to-container 
                            info:escidoc/names:aa:1.0:action:remove-members-from-container 
                            info:escidoc/names:aa:1.0:action:lock-container
                            </AttributeValue>
                            <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                        </ActionMatch>
                    </Action>
                </Actions>
            </Target>
              <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
                <Apply FunctionId="info:escidoc/names:aa:1.0:function:string-contains">
                    <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">pending released in-revision submitted</AttributeValue>
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                        <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </Apply>
                </Apply>
                <Apply FunctionId="info:escidoc/names:aa:1.0:function:string-contains">
                    <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">pending released in-revision submitted</AttributeValue>
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                        <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:latest-version-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </Apply>
                </Apply>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                        <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:created-by" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </Apply>
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                        <SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </Apply>
                </Apply>
            </Condition>
        </Rule>
<Rule RuleId="Depositor-policy-rule-3" Effect="Permit">
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
                            info:escidoc/names:aa:1.0:action:delete-item
                            </AttributeValue>
                            <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                        </ActionMatch>
                    </Action>
                </Actions>
            </Target>
            <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
                            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:not">
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                            <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                        </Apply>
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">released</AttributeValue>
                    </Apply>
                </Apply>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:not">
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                            <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                        </Apply>
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">withdrawn</AttributeValue>
                    </Apply>
                </Apply>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:or">
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                            <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:latest-version-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                        </Apply>
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">pending</AttributeValue>
                    </Apply>
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                            <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:latest-version-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                        </Apply>
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">in-revision</AttributeValue>
                    </Apply>
                </Apply>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                        <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:created-by" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </Apply>
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                        <SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </Apply>
                </Apply>
            </Condition>
        </Rule>
                <Rule RuleId="Depositor-policy-rule-3b" Effect="Permit">
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
                            info:escidoc/names:aa:1.0:action:delete-item 
                            info:escidoc/names:aa:1.0:action:lock-item
                            </AttributeValue>
                            <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                        </ActionMatch>
                    </Action>
                </Actions>
            </Target>
        <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
                    <Apply FunctionId="info:escidoc/names:aa:1.0:function:string-contains">
                    <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">pending released in-revision submitted</AttributeValue>
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                        <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </Apply>
                </Apply>
                <Apply FunctionId="info:escidoc/names:aa:1.0:function:string-contains">
                    <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">pending released in-revision submitted</AttributeValue>
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                        <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:latest-version-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </Apply>
                </Apply>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                        <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:created-by" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </Apply>
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                        <SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </Apply>
                </Apply>
            </Condition>
        </Rule>
        <Rule RuleId="Depositor-policy-rule-4" Effect="Permit">
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
            <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:component:item:created-by" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
            </Condition>
        </Rule>
            <Rule RuleId="Depositor-policy-rule-5" Effect="Permit">
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
                            info:escidoc/names:aa:1.0:action:withdraw-container 
                            info:escidoc/names:aa:1.0:action:release-container
                            </AttributeValue>
                            <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                        </ActionMatch>
                    </Action>
                </Actions>
            </Target>
            <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:created-by" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
            </Condition>
        </Rule>
                <Rule RuleId="Depositor-policy-rule-6" Effect="Permit">
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
                            info:escidoc/names:aa:1.0:action:withdraw-item 
                            info:escidoc/names:aa:1.0:action:release-item
                            </AttributeValue>
                            <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                        </ActionMatch>
                    </Action>
                </Actions>
            </Target>
            <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:created-by" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
            </Condition>
        </Rule>
    </Policy>
</PolicySet>');
  