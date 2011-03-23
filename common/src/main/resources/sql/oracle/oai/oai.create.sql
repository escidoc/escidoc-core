CREATE SCHEMA oai;

CREATE TABLE oai.set_definition (
  id VARCHAR(255) NOT NULL,
  specification  VARCHAR(255) NOT NULL UNIQUE,
  name VARCHAR(255) NOT NULL,
  query TEXT NOT NULL,
  description TEXT,
  creator_id VARCHAR(255),
  creator_title VARCHAR(255),
  creation_date TIMESTAMP,
  modified_by_id VARCHAR(255),
  modified_by_title VARCHAR(255),
  last_modification_date TIMESTAMP,
  primary key (id)
);
