<xsl:stylesheet version="1.0" extension-element-prefixes="exsl" exclude-result-prefixes="data" xmlns:data="urn:some.urn"
                                                                                               xmlns:exsl="http://exslt.org/common"
                                                                                               xmlns="http://xml.apache.org/axis/wsdd/"
                                                                                               xmlns:wsdd="http://xml.apache.org/axis/wsdd/"
                                                                                               xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output encoding="utf-8" method="xml"/>

  <!-- upper-/lowercase translation -->
  <xsl:variable name="lower">abcdefghijklmnopqrstuvwxyz</xsl:variable>
  <xsl:variable name="upper">ABCDEFGHIJKLMNOPQRSTUVWXYZ</xsl:variable>

  <!-- mapping from handler name to package -->
  <xsl:variable name="preferences">
    <data:preference name="ActionHandler"                    package="aa"/>
    <data:preference name="AdminHandler"                     package="adm"/>
    <data:preference name="AggregationDefinitionHandler"     package="sm"/>
    <data:preference name="ContainerHandler"                 package="om"/>
    <data:preference name="ContentModelHandler"              package="cmm"/>
    <data:preference name="ContentRelationHandler"           package="om"/>
    <data:preference name="ContextHandler"                   package="om"/>
    <data:preference name="FedoraAccessDeviationHandler"     package="om"/>
    <data:preference name="FedoraManagementDeviationHandler" package="om"/>
    <data:preference name="IngestHandler"                    package="om"/>
    <data:preference name="ItemHandler"                      package="om"/>
    <data:preference name="JhoveHandlerService"              package="tme"/>
    <data:preference name="OrganizationalUnitHandler"        package="oum"/>
    <data:preference name="PolicyDecisionPoint"              package="aa"/>
    <data:preference name="PreprocessingHandler"             package="sm"/>
    <data:preference name="ReportDefinitionHandler"          package="sm"/>
    <data:preference name="ReportHandler"                    package="sm"/>
    <data:preference name="RoleHandler"                      package="aa"/>
    <data:preference name="ScopeHandler"                     package="sm"/>
    <data:preference name="SemanticStoreHandler"             package="om"/>
    <data:preference name="SetDefinitionHandler"             package="oai"/>
    <data:preference name="SoapExceptionGeneration"          package="common"/>
    <data:preference name="StatisticDataHandler"             package="sm"/>
    <data:preference name="UserAccountHandler"               package="aa"/>
    <data:preference name="UserGroupHandler"                 package="aa"/>
    <data:preference name="UserManagementWrapper"            package="aa"/>
  </xsl:variable>

  <!-- skip the following elements -->
  <xsl:template match="wsdd:parameter[@name='wsdlPortType']"/>
  <xsl:template match="wsdd:parameter[@name='wsdlServiceElement']"/>
  <xsl:template match="wsdd:parameter[@name='wsdlServicePort']"/>

  <xsl:template match="@* | node()">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()"/>
    </xsl:copy>
  </xsl:template>

  <!-- don't touch webservices from Axis itself -->
  <xsl:template match="wsdd:service[(@name='AdminService' or @name='Version')]">
    <xsl:copy-of select="."/>
  </xsl:template>

  <xsl:template match="wsdd:service[(@name='FedoraAccessDeviationHandlerService' or @name='FedoraManagementDeviationHandlerService')]">
    <xsl:variable name="handlerName" select="substring-before(wsdd:parameter[@name='wsdlServiceElement']/@value, 'Service')"/>
    <xsl:variable name="serviceName" select="translate(substring-before(substring-after(@name, 'Fedora'), 'DeviationHandlerService'), $upper, $lower)"/>

    <xsl:copy>
      <xsl:apply-templates select="@*"/>

      <xsl:attribute name="name"><xsl:value-of select="$serviceName"/></xsl:attribute>
      <xsl:attribute name="provider">java:EscidocEJB</xsl:attribute>

      <xsl:element name="requestFlow">
        <xsl:element name="handler">
          <xsl:attribute name="type">java:org.apache.axis.handlers.http.HTTPAuthHandler</xsl:attribute>
        </xsl:element>
      </xsl:element>

      <xsl:apply-templates select="node()"/>

      <xsl:call-template name="addElements">
        <xsl:with-param name="handlerName" select="$handlerName"/>
        <xsl:with-param name="serviceName" select="$serviceName"/>
      </xsl:call-template>

    </xsl:copy>
  </xsl:template>

  <xsl:template match="wsdd:service">
    <xsl:variable name="serviceName" select="wsdd:parameter[@name='wsdlServiceElement']/@value"/>
    <xsl:variable name="handlerName" select="substring-before($serviceName, 'Service')"/>

    <xsl:copy>
      <xsl:apply-templates select="@*"/>

      <xsl:attribute name="provider">java:EscidocEJB</xsl:attribute>

      <xsl:element name="requestFlow">
        <xsl:element name="handler">
          <xsl:attribute name="type">java:org.apache.ws.axis.security.WSDoAllReceiver</xsl:attribute>
          <xsl:element name="parameter">
            <xsl:attribute name="name">passwordCallbackClass</xsl:attribute>
            <xsl:attribute name="value">de.escidoc.core.aa.security.server.PWCallback</xsl:attribute>
          </xsl:element>
          <xsl:element name="parameter">
            <xsl:attribute name="name">action</xsl:attribute>
            <xsl:attribute name="value">UsernameToken</xsl:attribute>
          </xsl:element>
          <xsl:element name="parameter">
            <xsl:attribute name="name">passwordType</xsl:attribute>
            <xsl:attribute name="value">PasswordText</xsl:attribute>
          </xsl:element>
          <xsl:element name="parameter">
            <xsl:attribute name="name">addUTElement</xsl:attribute>
            <xsl:attribute name="value">Nonce Created</xsl:attribute>
          </xsl:element>
        </xsl:element>
      </xsl:element>

      <xsl:apply-templates select="node()"/>

      <xsl:call-template name="addElements">
        <xsl:with-param name="handlerName" select="$handlerName"/>
        <xsl:with-param name="serviceName" select="$serviceName"/>
      </xsl:call-template>

    </xsl:copy>
  </xsl:template>

  <xsl:template name="addElements">
    <xsl:param name="handlerName"/>
    <xsl:param name="serviceName"/>
    
    <xsl:element name="parameter">
      <xsl:attribute name="name">beanJndiName</xsl:attribute>
      <xsl:attribute name="value">ejb/<xsl:value-of select="$handlerName"/></xsl:attribute>
    </xsl:element>
    <xsl:element name="parameter">
      <xsl:attribute name="name">springBean</xsl:attribute>
      <xsl:attribute name="value">service.<xsl:value-of select="$handlerName"/>Bean</xsl:attribute>
    </xsl:element>
    <xsl:element name="parameter">
      <xsl:attribute name="name">homeInterfaceName</xsl:attribute>
      <xsl:attribute name="value">de.escidoc.core.<xsl:value-of select="exsl:node-set($preferences)/data:preference[@name=$handlerName]/@package"/>.ejb.interfaces.<xsl:value-of select="$handlerName"/>RemoteHome</xsl:attribute>
    </xsl:element>
    <xsl:element name="parameter">
      <xsl:attribute name="name">jndiURL</xsl:attribute>
      <xsl:attribute name="value">${de.escidoc.core.<xsl:value-of select="exsl:node-set($preferences)/data:preference[@name=$handlerName]/@package"/>.service.provider.url}</xsl:attribute>
    </xsl:element>
    <xsl:element name="parameter">
      <xsl:attribute name="name">remoteInterfaceName</xsl:attribute>
      <xsl:attribute name="value">de.escidoc.core.<xsl:value-of select="exsl:node-set($preferences)/data:preference[@name=$handlerName]/@package"/>.ejb.interfaces.<xsl:value-of select="$handlerName"/>Remote</xsl:attribute>
    </xsl:element>
    <xsl:element name="parameter">
      <xsl:attribute name="name">wsdlPortType</xsl:attribute>
      <xsl:attribute name="value"><xsl:value-of select="$handlerName"/></xsl:attribute>
    </xsl:element>
    <xsl:element name="parameter">
      <xsl:attribute name="name">wsdlServiceElement</xsl:attribute>
      <xsl:attribute name="value"><xsl:value-of select="$serviceName"/></xsl:attribute>
    </xsl:element>
    <xsl:element name="parameter">
      <xsl:attribute name="name">wsdlServicePort</xsl:attribute>
      <xsl:attribute name="value"><xsl:value-of select="$serviceName"/></xsl:attribute>
    </xsl:element>
  </xsl:template>

</xsl:stylesheet>
