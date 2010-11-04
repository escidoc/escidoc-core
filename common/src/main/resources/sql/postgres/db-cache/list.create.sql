CREATE SCHEMA list;

CREATE TABLE list.filter (
  role_id			TEXT,
  type				TEXT NOT NULL,
  scope_rule			TEXT NOT NULL,
  policy_rule			TEXT NOT NULL,
  CONSTRAINT FK_FILTER_ROLE_ID FOREIGN KEY (role_id) REFERENCES aa.escidoc_role(id)
);
