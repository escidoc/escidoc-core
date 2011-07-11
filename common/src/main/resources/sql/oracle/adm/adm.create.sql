CREATE TABLE adm.version (
  major_number NUMBER(10) NOT NULL,
  minor_number NUMBER(10) NOT NULL,
  revision_number NUMBER(10) NOT NULL,
  "date" TIMESTAMP NOT NULL
);

INSERT INTO adm.version VALUES (1, 3, 0, CURRENT_TIMESTAMP);
