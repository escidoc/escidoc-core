CREATE SCHEMA adm;

CREATE TABLE adm.version (
  major_number INTEGER NOT NULL,
  minor_number INTEGER NOT NULL,
  revision_number INTEGER NOT NULL,
  date TIMESTAMP NOT NULL
);

INSERT INTO adm.version VALUES (1, 3, 0, CURRENT_TIMESTAMP);
