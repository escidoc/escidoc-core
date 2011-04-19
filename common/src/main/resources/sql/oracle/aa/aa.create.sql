CREATE TABLE aa.user_account ( 
  id VARCHAR2(255) NOT NULL,
  active NUMBER(1,0),
  name VARCHAR2(255 CHAR) NOT NULL,
  loginname VARCHAR2(255) NOT NULL,
  password VARCHAR2(255),
  creator_id VARCHAR2(255),
  creation_date TIMESTAMP,
  modified_by_id VARCHAR2(255),
  last_modification_date TIMESTAMP,
  CONSTRAINT PK_USER_ACCOUNT PRIMARY KEY(id),
   CONSTRAINT LOGINNAME_UNIQUE UNIQUE (loginname),
   CONSTRAINT FK_UA_CREATOR FOREIGN KEY (creator_id) REFERENCES aa.user_account (id),
   CONSTRAINT FK_UA_MODIFIED_BY FOREIGN KEY (modified_by_id) REFERENCES aa.user_account (id)
);

CREATE TABLE aa.user_group (
  id VARCHAR2(255) NOT NULL,
  label VARCHAR2(255 CHAR) NOT NULL,
  active NUMBER(1,0),
  name VARCHAR2(255 CHAR) NOT NULL,
  description CLOB,
  type VARCHAR2(255),
  email VARCHAR2(255),
  creator_id VARCHAR2(255),
  creation_date TIMESTAMP,
  modified_by_id VARCHAR2(255),
  last_modification_date TIMESTAMP,
  CONSTRAINT PK_USER_GROUP PRIMARY KEY(id),
  CONSTRAINT FK_UG_CREATOR FOREIGN KEY (creator_id) REFERENCES aa.user_account (id),
  CONSTRAINT FK_UG_MODIFIED_BY FOREIGN KEY (modified_by_id) REFERENCES aa.user_account (id),
   CONSTRAINT LABEL_UNIQUE UNIQUE (label)
);

CREATE TABLE aa.user_group_member (
  id VARCHAR2(255) NOT NULL,
  user_group_id VARCHAR2(255) NOT NULL,
  name VARCHAR2(255 CHAR) NOT NULL,
  type VARCHAR2(255) NOT NULL,
  value VARCHAR2(255) NOT NULL,
  CONSTRAINT PK_USER_GROUP_MEMBER PRIMARY KEY(id),
  CONSTRAINT FK_GROUP_ID FOREIGN KEY (user_group_id) REFERENCES aa.user_group (id)
);

CREATE INDEX group_member_name_value_idx
ON aa.user_group_member (type, name, value);

CREATE TABLE aa.escidoc_role (
  id VARCHAR2(255) NOT NULL,
  role_name VARCHAR2(255 CHAR) NOT NULL,
  description CLOB,
  creator_id VARCHAR2(255),
  creation_date TIMESTAMP,
  modified_by_id VARCHAR2(255),
  last_modification_date TIMESTAMP,
  CONSTRAINT PK_ESCIDOC_ROLE PRIMARY KEY(id),
  CONSTRAINT FK_ROLE_CREATED_BY FOREIGN KEY (creator_id) REFERENCES aa.user_account (id),
  CONSTRAINT FK_ROLE_MODIFIED_BY FOREIGN KEY (modified_by_id) REFERENCES aa.user_account (id),
   CONSTRAINT ROLENAME_UNIQUE UNIQUE (role_name)
);

CREATE TABLE aa.scope_def (
  id VARCHAR2(255) NOT NULL,
  role_id VARCHAR2(255),
  object_type VARCHAR2(255) NOT NULL,
  attribute_id VARCHAR2(255),
  attribute_object_type VARCHAR2(255),
  CONSTRAINT PK_SCOPE_DEF PRIMARY KEY(id),
  CONSTRAINT FK_SCOPE_DEF FOREIGN KEY (role_id) REFERENCES aa.escidoc_role (id) 
);

CREATE TABLE aa.escidoc_policies (
  id VARCHAR2(255) NOT NULL, 
  role_id VARCHAR2(255),
  xml CLOB, 
  CONSTRAINT PK_ESCIDOC_POLICIES PRIMARY KEY(id),
  CONSTRAINT FK_ESCIDOC_POLICIES_ROLE FOREIGN KEY (role_id) REFERENCES aa.escidoc_role (id)
);

CREATE TABLE aa.actions (
  id VARCHAR2(255) NOT NULL, 
  name VARCHAR2(255) NOT NULL,
  CONSTRAINT actions_pkey PRIMARY KEY(id)
);

CREATE TABLE aa.method_mappings (
  id VARCHAR2(255) NOT NULL,
  class_name VARCHAR2(255) NOT NULL,
  method_name VARCHAR2(255) NOT NULL,
  action_name VARCHAR2(255) NOT NULL,
  exec_before NUMBER(1,0) NOT NULL,
  single_resource NUMBER(1,0) NOT NULL,
  resource_not_found_exception CLOB,
  CONSTRAINT method_mappings_pkey PRIMARY KEY(id)
);

CREATE TABLE aa.invocation_mappings (
  id VARCHAR2(255) NOT NULL,
  attribute_id CLOB NOT NULL, 
  path VARCHAR2(255),
  position NUMERIC(2,0) NOT NULL,
  attribute_type VARCHAR2(255) NOT NULL,
  mapping_type NUMERIC(2,0) NOT NULL,
  multi_value NUMBER(1,0) NOT NULL,
  value VARCHAR2(100) NULL,
  method_mapping VARCHAR2(255) NULL, 
  CONSTRAINT invocation_mappings_pkey PRIMARY KEY(id),
  CONSTRAINT invocation_mappings_fkey FOREIGN KEY (method_mapping) REFERENCES aa.method_mappings (id)
  );

CREATE TABLE aa.role_grant (
  id VARCHAR2(255) NOT NULL,
  user_id VARCHAR2(255),
  group_id VARCHAR2(255),
  role_id VARCHAR2(255) NOT NULL,
  object_id VARCHAR2(255), 
  object_title VARCHAR2(255 CHAR),
  object_href VARCHAR2(255),
  grant_remark VARCHAR2(255 CHAR) NULL,
  creator_id VARCHAR2(255),
  creation_date TIMESTAMP,
  revoker_id VARCHAR2(255),
  revocation_date TIMESTAMP DEFAULT NULL,
  revocation_remark VARCHAR2(255),
  granted_from TIMESTAMP DEFAULT NULL,
  granted_to TIMESTAMP DEFAULT NULL,
  CONSTRAINT PK_ROLE_GRANT PRIMARY KEY(id),
  CONSTRAINT FK_GRANT_USER FOREIGN KEY (user_id) REFERENCES aa.user_account (id),
  CONSTRAINT FK_GRANT_GROUP FOREIGN KEY (group_id) REFERENCES aa.user_group (id),
  CONSTRAINT FK_GRANTOR_GRANT FOREIGN KEY (creator_id) REFERENCES aa.user_account (id),
  CONSTRAINT FK_REVOKER_GRANT FOREIGN KEY (revoker_id) REFERENCES aa.user_account (id),
  CONSTRAINT FK_ROLE_GRANT FOREIGN KEY (role_id) REFERENCES aa.escidoc_role (id)
);

CREATE INDEX group_role_grant_idx
ON aa.role_grant (group_id, revocation_date);

CREATE INDEX user_role_role_grant_idx
ON aa.role_grant (user_id, role_id);

CREATE INDEX group_role_role_grant_idx
ON aa.role_grant (group_id, role_id);

CREATE TABLE aa.user_preference (
  id VARCHAR2(255) NOT NULL,
  user_id VARCHAR2(255),
  name VARCHAR2(255 CHAR),
  value VARCHAR2(255 CHAR),
  CONSTRAINT PK_USER_PREFERENCE PRIMARY KEY(id),
  CONSTRAINT FK_PREFERENCE_USER FOREIGN KEY (user_id) REFERENCES aa.user_account (id)
);

CREATE TABLE aa.user_attribute (
  id VARCHAR2(255) NOT NULL,
  user_id VARCHAR2(255),
  name VARCHAR2(255 CHAR),
  value VARCHAR2(255 CHAR),
  internal NUMBER(1,0),
  CONSTRAINT PK_USER_ATTRIBUTE PRIMARY KEY(id),
  CONSTRAINT FK_ATTRIBUTE_USER FOREIGN KEY (user_id) REFERENCES aa.user_account (id)
);

CREATE TABLE aa.user_login_data (
  id VARCHAR2(255) NOT NULL,
  user_id  VARCHAR2(255) NOT NULL,
  handle VARCHAR2(255) NOT NULL,
  expiryts NUMBER(19) NOT NULL,
  CONSTRAINT PK_USER_LOGIN_DATA PRIMARY KEY(id),
  CONSTRAINT FK_USER_LOGIN_DATA FOREIGN KEY (user_id) REFERENCES aa.user_account (id)
);

CREATE INDEX user_login_data_idx
ON aa.user_login_data (handle);

CREATE TABLE aa.unsecured_action_list (
  id VARCHAR2(255) NOT NULL,
  context_id VARCHAR2(255) NOT NULL,
  action_ids CLOB NOT NULL,
  CONSTRAINT PK_UNSECURED_ACTIONS PRIMARY KEY(id)
);




