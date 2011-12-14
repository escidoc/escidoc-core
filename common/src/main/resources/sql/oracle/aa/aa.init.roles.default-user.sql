/** IF CHANGING THE POLICY, PLEASE ADAPT THIS SECTION!!!
  <section>
    <title>Default privileges</title>

    <para>Besides the defined roles, some default privileges exists for every
    user, even for the anonymous user, that tries access to the services
    without providing an eSciDoc user handle.</para>

    <para>Every user is allowed to: <itemizedlist mark="opencircle"
        spacing="compact">
        <listitem>
          <para>retrieve container versions in the version-status 
          <emphasis>released</emphasis>,</para>
        </listitem>

        <listitem>
          <para>retrieve item versions in the version-status
          <emphasis>released</emphasis>,</para>
        </listitem>

        <listitem>
          <para>retrieve contexts in the public-status <emphasis>open</emphasis> and <emphasis>closed</emphasis>
          ,</para>
        </listitem>

        <listitem>
          <para>retrieve content models,</para>
        </listitem>

        <listitem>
          <para>retrieve content relations in the public-status <emphasis>released</emphasis>,</para>
        </listitem>

        <listitem>
          <para>retrieve binary content of an item if the item is not in the 
          public-status <emphasis>withdrawn</emphasis>, the item version is in 
          the version-status <emphasis>released</emphasis>, and the component visibility is
          <emphasis>public</emphasis> ,</para>
        </listitem>

        <listitem>
          <para>retrieve organizational-units in the public-status
          <emphasis>open</emphasis> and <emphasis>closed</emphasis> 
          and also the children and parents of these organizational-units.</para>
        </listitem>

        <listitem>
          <para>retrieve his/her own user-account (if the user has been
          authenticated) and grants of their user-accounts.</para>
        </listitem>

        <listitem>
          <para>query the semantic store, if the user has been
          authenticated.</para>
        </listitem>

        <listitem>
          <para>logout.</para>
        </listitem>
        <listitem>
          <para>unlock items that she/he has locked.</para>
        </listitem>
        <listitem>
          <para>unlock containers that she/he has locked.</para>
        </listitem>
        <listitem>
          <para>retrieve list of object-refs and 
           to retrieve list of objects, as the latter is filtered during 
           the method call.</para>
        </listitem>
        <listitem>
          <para>download a staging file. 
           This is needed for anonymous access by fedora</para>
        </listitem>
        <listitem>
          <para>retrieve OAI-PMH set definitions</para>
        </listitem>
        <listitem>
          <para>retrieve repository info</para>
        </listitem>
        <listitem>
          <para>retrieve users permission-filter query</para>
        </listitem>
        <listitem>
          <para>retrieve statistic-reports if she/he is 
           in the role permitted by the record-definition</para>
        </listitem>
        <listitem>
          <para>create grants that have scope on 
          context/item/container/component/content-relation she/he created</para>
        </listitem>
        <listitem>
          <para>revoke grants she/he created</para>
        </listitem>
        <listitem>
          <para>retrieve grants she/he created</para>
        </listitem>
        <listitem>
          <para>retrieve grants of groups she/he belongs to</para>
        </listitem>
        <listitem>
          <para>create grant for role User Inspector for his/her own account</para>
        </listitem>
        <listitem>
          <para>retrieve certain roles</para>
        </listitem>
        <listitem>
          <para>retrieve user-group if user created that group</para>
        </listitem>
        <listitem>
          <para>evaluate actions against pdp for own user</para>
        </listitem>
      </itemizedlist></para>

    <para>Additionally, every user is allowed to perform a search request, as
    the search service is not protected by the eSciDoc authentication and
    authorization component.</para>
  </section>
 */
INSERT INTO aa.escidoc_role
    (id, role_name, creator_id, creation_date, modified_by_id, last_modification_date)
     VALUES
    ('escidoc:role-default-user', 'Default-User', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}',
    CURRENT_TIMESTAMP);
    
