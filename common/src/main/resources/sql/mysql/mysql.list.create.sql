CREATE SCHEMA IF NOT EXISTS list;

CREATE TABLE IF NOT EXISTS list.filter (
  role_id			TEXT,
  type				TEXT NOT NULL,
  scope_rule			TEXT NOT NULL,
  policy_rule			TEXT NOT NULL,
  CONSTRAINT FK_FILTER_ROLE_ID FOREIGN KEY (role_id(${escidoc.database.index.prefix.length})) REFERENCES aa.escidoc_role(id)
);
