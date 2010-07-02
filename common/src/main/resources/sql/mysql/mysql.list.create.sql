CREATE SCHEMA IF NOT EXISTS list;

CREATE TABLE IF NOT EXISTS list.container (
  id				TEXT NOT NULL,
  rest_content			TEXT NOT NULL,
  soap_content			TEXT NOT NULL,
  primary key (id(${escidoc.database.index.prefix.length}))
);

CREATE TABLE IF NOT EXISTS list.context (
  id				TEXT NOT NULL,
  rest_content			TEXT NOT NULL,
  soap_content			TEXT NOT NULL,
  primary key (id(${escidoc.database.index.prefix.length}))
);

CREATE TABLE IF NOT EXISTS list.item (
  id				TEXT NOT NULL,
  rest_content			TEXT NOT NULL,
  soap_content			TEXT NOT NULL,
  primary key (id(${escidoc.database.index.prefix.length}))
);

CREATE TABLE IF NOT EXISTS list.ou (
  id				TEXT NOT NULL,
  rest_content			TEXT NOT NULL,
  soap_content			TEXT NOT NULL,
  primary key (id(${escidoc.database.index.prefix.length}))
);

CREATE TABLE IF NOT EXISTS list.filter (
  role_id			TEXT,
  type				TEXT NOT NULL,
  rule				TEXT NOT NULL,
  CONSTRAINT FK_FILTER_ROLE_ID FOREIGN KEY (role_id(${escidoc.database.index.prefix.length})) REFERENCES aa.escidoc_role(id)
);

CREATE TABLE IF NOT EXISTS list.property (
  resource_id                   TEXT NOT NULL,
  local_path                    TEXT NOT NULL,
  value                         VARCHAR(${escidoc.database.index.prefix.length}),
  position			INTEGER NOT NULL
);

CREATE INDEX id_local_path_position
  USING btree
  ON list.property
  (resource_id(150), local_path(150), position);

CREATE INDEX id_local_path_value
  USING btree
  ON list.property
  (resource_id(100), local_path(100), value(100));

CREATE INDEX local_path_value_position
  USING btree
  ON list.property
  (local_path(150), value(150), position);

CREATE INDEX local_path_value_id
  USING btree
  ON list.property
  (local_path(100), value(100), resource_id(100));
