CREATE SCHEMA aa;

CREATE TABLE aa.user_account ( 
  id VARCHAR(255) NOT NULL,
  active BOOLEAN,
  name VARCHAR(255) NOT NULL,
  loginname VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255),
  creator_id VARCHAR(255),
  creation_date TIMESTAMP,
  modified_by_id VARCHAR(255),
  last_modification_date TIMESTAMP,
   primary key (id),
   CONSTRAINT FK_CREATOR FOREIGN KEY (creator_id) REFERENCES aa.user_account (id),
   CONSTRAINT FK_MODIFIED_BY FOREIGN KEY (modified_by_id) REFERENCES aa.user_account (id)
);

CREATE TABLE aa.user_group (
  id VARCHAR(255) NOT NULL,
  label VARCHAR(255) NOT NULL UNIQUE,
  active BOOLEAN,
  name VARCHAR(255) NOT NULL,
  description TEXT,
  type VARCHAR(255),
  email VARCHAR(255),
  creator_id VARCHAR(255),
  creation_date TIMESTAMP,
  modified_by_id VARCHAR(255),
  last_modification_date TIMESTAMP,
  primary key (id),
  CONSTRAINT FK_CREATOR FOREIGN KEY (creator_id) REFERENCES aa.user_account (id),
  CONSTRAINT FK_MODIFIED_BY FOREIGN KEY (modified_by_id) REFERENCES aa.user_account (id)
);

CREATE TABLE aa.user_group_member (
  id VARCHAR(255) NOT NULL,
  user_group_id VARCHAR(255) NOT NULL,
  name VARCHAR(255) NOT NULL,
  type VARCHAR(255) NOT NULL,
  value VARCHAR(255) NOT NULL,
  primary key (id),
  CONSTRAINT FK_GROUP_ID FOREIGN KEY (user_group_id) REFERENCES aa.user_group (id)
);

CREATE INDEX group_member_name_value_idx
ON aa.user_group_member (type(100), name(100), value(100));

CREATE TABLE aa.escidoc_role (
  id VARCHAR(255) NOT NULL,
  role_name VARCHAR(255) NOT NULL UNIQUE,
  description TEXT,
  creator_id VARCHAR(255),
  creation_date TIMESTAMP,
  modified_by_id VARCHAR(255),
  last_modification_date TIMESTAMP,
  primary key (id),
  CONSTRAINT FK_ROLE_CREATED_BY FOREIGN KEY (creator_id) REFERENCES aa.user_account (id),
  CONSTRAINT FK_ROLE_MODIFIED_BY FOREIGN KEY (modified_by_id) REFERENCES aa.user_account (id)
);

CREATE TABLE aa.scope_def (
  id VARCHAR(255) NOT NULL,
  role_id VARCHAR(255),
  object_type VARCHAR(255) NOT NULL,
  attribute_id VARCHAR(255),
  primary key (id),
  CONSTRAINT FK_SCOPE_DEF FOREIGN KEY (role_id) REFERENCES aa.escidoc_role (id) 
);

CREATE TABLE aa.escidoc_policies (
  id VARCHAR(255) NOT NULL, 
  role_id VARCHAR(255),
  xml TEXT, 
  CONSTRAINT PK_ESCIDOC_POLICIES PRIMARY KEY(id),
  CONSTRAINT FK_ESCIDOC_POLICIES_ROLE FOREIGN KEY (role_id) REFERENCES aa.escidoc_role (id)
);

CREATE TABLE aa.actions (
  id VARCHAR(255) NOT NULL, 
  name VARCHAR(255) NOT NULL,
  CONSTRAINT actions_pkey PRIMARY KEY(id)
);

CREATE TABLE aa.method_mappings (
  id VARCHAR(255) NOT NULL,
  class_name VARCHAR(255) NOT NULL,
  method_name VARCHAR(255) NOT NULL,
  action_name VARCHAR(255) NOT NULL,
  exec_before BOOLEAN NOT NULL,
  single_resource BOOLEAN NOT NULL,
  resource_not_found_exception TEXT,
  CONSTRAINT method_mappings_pkey PRIMARY KEY(id)
);

CREATE TABLE aa.invocation_mappings (
  id VARCHAR(255) NOT NULL,
  attribute_id TEXT NOT NULL, 
  path VARCHAR(100) NOT NULL,
  position NUMERIC(2,0) NOT NULL,
  attribute_type VARCHAR(255) NOT NULL,
  mapping_type NUMERIC(2,0) NOT NULL,
  multi_value boolean NOT NULL,
  value VARCHAR(100) NULL,
  method_mapping VARCHAR(255) NULL, 
  CONSTRAINT invocation_mappings_pkey PRIMARY KEY(id),
  CONSTRAINT invocation_mappings_fkey FOREIGN KEY (method_mapping) REFERENCES aa.method_mappings (id)
  );

CREATE TABLE aa.role_grant (
  id VARCHAR(255) NOT NULL,
  user_id VARCHAR(255),
  group_id VARCHAR(255),
  role_id VARCHAR(255) NOT NULL,
  object_id VARCHAR(255), 
  object_title VARCHAR(255),
  object_href VARCHAR(255),
  grant_remark VARCHAR(255) NULL,
  creator_id VARCHAR(255),
  creation_date TIMESTAMP,
  revoker_id VARCHAR(255),
  revocation_date TIMESTAMP NULL DEFAULT NULL,
  revocation_remark VARCHAR(255),
  granted_from TIMESTAMP NULL DEFAULT NULL,
  granted_to TIMESTAMP NULL DEFAULT NULL,
  primary key (id),
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
  id VARCHAR(255) NOT NULL,
  user_id VARCHAR(255),
  name VARCHAR(255),
  value VARCHAR(255),
  primary key (id),
  CONSTRAINT FK_PREFERENCE_USER FOREIGN KEY (user_id) REFERENCES aa.user_account (id)
);

CREATE TABLE aa.user_attribute (
  id VARCHAR(255) NOT NULL,
  user_id VARCHAR(255),
  name VARCHAR(255),
  value VARCHAR(255),
  internal BOOLEAN,
  primary key (id),
  CONSTRAINT FK_ATTRIBUTE_USER FOREIGN KEY (user_id) REFERENCES aa.user_account (id)
);

CREATE TABLE aa.user_login_data (
  id VARCHAR(255) NOT NULL,
  user_id  VARCHAR(255) NOT NULL,
  handle VARCHAR(255) NOT NULL,
  expiryts int8 NOT NULL,
  primary key (id),
  CONSTRAINT FK_USER_LOGIN_DATA FOREIGN KEY (user_id) REFERENCES aa.user_account (id)
);

CREATE INDEX user_login_data_idx
ON aa.user_login_data (handle);

CREATE TABLE aa.unsecured_action_list (
  id VARCHAR(255) NOT NULL,
  context_id VARCHAR(255) NOT NULL,
  action_ids TEXT NOT NULL,
  primary key (id)
);




