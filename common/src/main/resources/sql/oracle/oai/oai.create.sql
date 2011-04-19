CREATE TABLE oai.set_definition (
  id VARCHAR2(255) NOT NULL,
  specification  VARCHAR2(255 CHAR) NOT NULL,
  name VARCHAR2(255 CHAR) NOT NULL,
  query CLOB NOT NULL,
  description CLOB,
  creator_id VARCHAR2(255),
  creator_title VARCHAR2(255 CHAR),
  creation_date TIMESTAMP,
  modified_by_id VARCHAR2(255),
  modified_by_title VARCHAR2(255),
  last_modification_date TIMESTAMP,
  CONSTRAINT PK_SET_DEFINITION PRIMARY KEY(id),
  CONSTRAINT SPECIFICATION_UNIQUE UNIQUE (specification)
);
