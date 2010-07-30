CREATE SCHEMA list;

-- create tables

CREATE TABLE list.container (
  id				TEXT NOT NULL,
  rest_content			TEXT NOT NULL,
  soap_content			TEXT NOT NULL,
  primary key (id)
);

CREATE TABLE list.content_model (
  id                            TEXT NOT NULL,
  rest_content                  TEXT NOT NULL,
  soap_content                  TEXT NOT NULL,
  primary key (id)
);

CREATE TABLE list.content_relation (
  id                            TEXT NOT NULL,
  rest_content                  TEXT NOT NULL,
  soap_content                  TEXT NOT NULL,
  primary key (id)
);

CREATE TABLE list.context (
  id				TEXT NOT NULL,
  rest_content			TEXT NOT NULL,
  soap_content			TEXT NOT NULL,
  primary key (id)
);

CREATE TABLE list.item (
  id				TEXT NOT NULL,
  rest_content			TEXT NOT NULL,
  soap_content			TEXT NOT NULL,
  primary key (id)
);

CREATE TABLE list.ou (
  id				TEXT NOT NULL,
  rest_content			TEXT NOT NULL,
  soap_content			TEXT NOT NULL,
  primary key (id)
);

CREATE TABLE list.filter (
  role_id			TEXT,
  type				TEXT NOT NULL,
  scope_rule			TEXT NOT NULL,
  policy_rule			TEXT NOT NULL,
  CONSTRAINT FK_FILTER_ROLE_ID FOREIGN KEY (role_id) REFERENCES aa.escidoc_role(id)
);

--CREATE TABLE list.property (
--  resource_id                   TEXT NOT NULL,
--  local_path                    TEXT NOT NULL,
--  value                         VARCHAR(${escidoc.database.index.prefix.length}) NOT NULL,
--  position                    INTEGER NOT NULL
--);

-- FIXME the prefix length is configurable
CREATE TABLE list.property (
  resource_id                   TEXT NOT NULL,
  local_path                    TEXT NOT NULL,
  value                         VARCHAR(2000) NOT NULL,
  position                      INTEGER NOT NULL
);

-- create indexes

CREATE INDEX id_path_contentmodelid_index
  ON list.property
  USING btree
  (resource_id, local_path, value)
-- FIXME TABLESPACE ${escidoc.database.tablespace.list}
  TABLESPACE pg_default
  WHERE local_path = '/properties/content-model/id'::text;

CREATE INDEX id_path_position
  ON list.property
  USING btree
  (resource_id, local_path, "position")
-- FIXME TABLESPACE ${escidoc.database.tablespace.list};
  TABLESPACE pg_default;

CREATE INDEX local_path_public_status_value_index
  ON list.property
  USING btree
  (local_path, value)
-- FIXME TABLESPACE ${escidoc.database.tablespace.list}
  TABLESPACE pg_default
  WHERE local_path = '/properties/public-status'::text;

CREATE INDEX local_path_version_status_value_index
  ON list.property
  USING btree
  (local_path, value)
-- FIXME TABLESPACE ${escidoc.database.tablespace.list}
  TABLESPACE pg_default
  WHERE local_path = '/properties/version/status'::text;

CREATE INDEX path_contentmodeltitle_value_index
  ON list.property
  USING btree
  (local_path, value)
-- FIXME TABLESPACE ${escidoc.database.tablespace.list}
  TABLESPACE pg_default
  WHERE local_path = '/properties/content-model/title'::text;

CREATE INDEX path_contextid_value_index
  ON list.property
  USING btree
  (local_path, value)
-- FIXME TABLESPACE ${escidoc.database.tablespace.list}
  TABLESPACE pg_default
  WHERE local_path = '/properties/context/id'::text;

CREATE INDEX path_createdby_value_index
  ON list.property
  USING btree
  (local_path, value)
-- FIXME TABLESPACE ${escidoc.database.tablespace.list}
  TABLESPACE pg_default
  WHERE local_path = '/properties/created-by/id'::text;

CREATE INDEX path_id_position
  ON list.property
  USING btree
  (local_path, resource_id, "position")
-- FIXME TABLESPACE ${escidoc.database.tablespace.list};
  TABLESPACE pg_default;

CREATE INDEX path_parents_value_index
  ON list.property
  USING btree
  (local_path, value, resource_id)
-- FIXME TABLESPACE ${escidoc.database.tablespace.list}
  TABLESPACE pg_default
  WHERE local_path = '/parents/parent/id'::text;

CREATE INDEX path_structmap_container_index
  ON list.property
  USING btree
  (local_path, resource_id)
-- FIXME TABLESPACE ${escidoc.database.tablespace.list}
  TABLESPACE pg_default
  WHERE local_path = '/struct-map/container/id'::text;

CREATE INDEX path_structmap_index
  ON list.property
  USING btree
  (local_path, resource_id)
-- FIXME TABLESPACE ${escidoc.database.tablespace.list}
  TABLESPACE pg_default
  WHERE local_path = '/struct-map/item/id'::text;

-- create stored procedures

CREATE TYPE resource AS (resource_id TEXT);

CREATE OR REPLACE FUNCTION getChildContainers(param_resource_id TEXT) RETURNS SETOF resource AS '
  DECLARE\
    var_resource_id TEXT;\
  BEGIN\
    IF param_resource_id IS NOT NULL THEN\
      FOR var_resource_id IN SELECT value FROM list.property WHERE local_path=''/struct-map/container/id'' AND resource_id=param_resource_id LOOP\
        RETURN QUERY SELECT DISTINCT CAST(value AS TEXT) FROM list.property WHERE value=var_resource_id\
                     UNION ALL\
                     SELECT * FROM getChildContainers(var_resource_id);\
      END LOOP;\
    END IF;\
  END' LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION getAllChildContainers(param_expression TEXT) RETURNS SETOF resource AS '
  DECLARE\
    var_resource_id TEXT;\
  BEGIN\
    IF param_expression IS NOT NULL THEN\
      FOR var_resource_id IN EXECUTE param_expression LOOP\
        RETURN QUERY SELECT CAST(var_resource_id AS TEXT) FROM list.container\
                     UNION ALL\
                     SELECT * FROM getChildContainers(var_resource_id);\
      END LOOP;\
    END IF;\
  END' LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION getChildItems(param_container_id TEXT) RETURNS SETOF resource AS '
  DECLARE\
    var_container_id TEXT;\
  BEGIN\
    IF param_container_id IS NOT NULL THEN\
      FOR var_container_id IN SELECT value FROM list.property WHERE local_path=''/struct-map/container/id'' AND resource_id=param_container_id LOOP\
        RETURN QUERY SELECT * FROM getChildItems(var_container_id);\
      END LOOP;\
      RETURN QUERY SELECT DISTINCT CAST(value AS TEXT) FROM list.property WHERE local_path=''/struct-map/item/id'' AND resource_id=param_container_id;\
    END IF;\
  END' LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION getAllChildItems(param_expression TEXT) RETURNS SETOF resource AS '
  DECLARE\
    var_container_id TEXT;\
  BEGIN\
    IF param_expression IS NOT NULL THEN\
      FOR var_container_id IN EXECUTE param_expression LOOP\
        RETURN QUERY SELECT * FROM getChildItems(var_container_id);\
      END LOOP;\
    END IF;\
  END' LANGUAGE 'plpgsql';

 CREATE OR REPLACE FUNCTION getChildOUs(param_resource_id TEXT) RETURNS SETOF resource AS '
   DECLARE\
     var_resource_id TEXT;\
   BEGIN\
     IF param_resource_id IS NOT NULL THEN\
       FOR var_resource_id IN SELECT resource_id FROM list.property WHERE local_path=''/parents/parent/id'' AND value=param_resource_id LOOP\
         RETURN QUERY SELECT DISTINCT CAST(value AS TEXT) FROM list.property WHERE value=var_resource_id\
                      UNION ALL\
                      SELECT * FROM getChildOUs(var_resource_id);\
       END LOOP;\
     END IF;\
   END' LANGUAGE 'plpgsql';

 CREATE OR REPLACE FUNCTION getAllChildOUs(param_expression TEXT) RETURNS SETOF resource AS '
   DECLARE\
     var_resource_id TEXT;\
   BEGIN\
     IF param_expression IS NOT NULL THEN\
       FOR var_resource_id IN EXECUTE param_expression LOOP\
         RETURN QUERY SELECT DISTINCT CAST(var_resource_id AS TEXT) FROM list.property\
                      UNION ALL\
                      SELECT * FROM getChildOUs(var_resource_id);\
       END LOOP;\
     END IF;\
   END' LANGUAGE 'plpgsql';
