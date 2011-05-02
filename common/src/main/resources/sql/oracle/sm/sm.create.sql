CREATE TABLE sm.scopes ( 
  id VARCHAR2(255) not null,
  name VARCHAR2(255 CHAR) not null,
  scope_type VARCHAR2(20) not null,
  creator_id VARCHAR2(255),
  creation_date TIMESTAMP,
  modified_by_id VARCHAR2(255),
  last_modification_date TIMESTAMP,
  CONSTRAINT PK_SCOPES PRIMARY KEY(id)
);

CREATE TABLE sm.statistic_data ( 
  id VARCHAR2(255) not null,
  xml_data CLOB NOT NULL,
  scope_id VARCHAR2(255) NOT NULL,
  timemarker TIMESTAMP NOT NULL,
   CONSTRAINT FK_SD_SCOPE_ID FOREIGN KEY (scope_id) REFERENCES sm.scopes
   ON DELETE CASCADE,
  CONSTRAINT PK_STATISTIC_DATA PRIMARY KEY(id)
);

CREATE TABLE sm.aggregation_definitions ( 
  id VARCHAR2(255) not null,
  scope_id VARCHAR2(255) NOT NULL,
  name VARCHAR2(255 CHAR) NOT NULL,
  creator_id VARCHAR2(255),
  creation_date TIMESTAMP,
   CONSTRAINT FK_AD_SCOPE_ID FOREIGN KEY (scope_id) REFERENCES sm.scopes
   ON DELETE CASCADE,
  CONSTRAINT PK_AGG_DEFS PRIMARY KEY(id)
);

CREATE TABLE sm.aggregation_tables ( 
  id VARCHAR2(255) not null,
  aggregation_definition_id VARCHAR2(255) NOT NULL,
  name VARCHAR2(255 CHAR) NOT NULL,
  list_index NUMERIC(2,0) NOT NULL,
   CONSTRAINT FK_AT_AGG_DEF FOREIGN KEY (aggregation_definition_id) REFERENCES sm.aggregation_definitions
   ON DELETE CASCADE,
  CONSTRAINT PK_AGG_TABLES PRIMARY KEY(id)
);

CREATE TABLE sm.aggregation_table_indexes ( 
  id VARCHAR2(255) not null,
  aggregation_table_id VARCHAR2(255) NOT NULL,
  name VARCHAR2(255 CHAR) NOT NULL,
  list_index NUMERIC(2,0) NOT NULL,
   CONSTRAINT FK_ATI_AGG_TABLE FOREIGN KEY (aggregation_table_id) REFERENCES sm.aggregation_tables
   ON DELETE CASCADE,
  CONSTRAINT PK_AGG_TABLES_IDX PRIMARY KEY(id)
);

CREATE TABLE sm.aggregation_table_index_fields ( 
  id VARCHAR2(255) not null,
  aggregation_table_index_id VARCHAR2(255) NOT NULL,
  field VARCHAR2(255) NOT NULL,
  list_index NUMERIC(2,0) NOT NULL,
   CONSTRAINT FK_ATIF_AGG_TABLE_INDEX FOREIGN KEY (aggregation_table_index_id) REFERENCES sm.aggregation_table_indexes
   ON DELETE CASCADE,
  CONSTRAINT PK_AGG_TABLES_IDX_FLDS PRIMARY KEY(id)
);

CREATE TABLE sm.aggregation_table_fields ( 
  id VARCHAR2(255) not null,
  aggregation_table_id VARCHAR2(255) NOT NULL,
  field_type_id NUMERIC(2,0) NOT NULL,
  name VARCHAR2(255 CHAR) NOT NULL,
  feed VARCHAR2(255),
  xpath CLOB,
  data_type VARCHAR2(10),
  reduce_to VARCHAR2(255),
  list_index NUMERIC(2,0) NOT NULL,
   CONSTRAINT FK_ATF_AGG_TABLE FOREIGN KEY (aggregation_table_id) REFERENCES sm.aggregation_tables
   ON DELETE CASCADE,
  CONSTRAINT PK_AGG_TABLES_FIELDS PRIMARY KEY(id)
);

CREATE TABLE sm.agg_stat_data_selectors ( 
  id VARCHAR2(255) not null,
  aggregation_definition_id VARCHAR2(255) NOT NULL,
  selector_type VARCHAR2(50) NOT NULL,
  xpath VARCHAR2(255) NOT NULL,
  list_index NUMERIC(2,0) NOT NULL,
   CONSTRAINT FK_ASDS_AGG_DEF FOREIGN KEY (aggregation_definition_id) REFERENCES sm.aggregation_definitions
   ON DELETE CASCADE,
  CONSTRAINT PK_AGG_STAT_DATA_SELECTORS PRIMARY KEY(id)
);

CREATE INDEX agg_def_scope_id_idx
ON sm.aggregation_definitions (scope_id);

CREATE TABLE sm.preprocessing_logs ( 
  id VARCHAR2(255) not null,
  aggregation_definition_id VARCHAR2(255) not null,
  processing_date DATE not null,
  log_entry CLOB,
  has_error NUMBER(1,0),
   CONSTRAINT FK_AGGREGATION_DEFINITION_ID FOREIGN KEY (aggregation_definition_id) 
   REFERENCES sm.aggregation_definitions
   ON DELETE CASCADE,
  CONSTRAINT PK_PREPROC_LOGS PRIMARY KEY(id)
);

CREATE INDEX preproc_logs_agg_def_id_idx
ON sm.preprocessing_logs (aggregation_definition_id);

CREATE INDEX preproc_logs_date_idx
ON sm.preprocessing_logs (processing_date);

CREATE INDEX preproc_logs_agg_def_date_idx
ON sm.preprocessing_logs (aggregation_definition_id, processing_date);

CREATE INDEX preproc_logs_error_agg_def_idx
ON sm.preprocessing_logs (aggregation_definition_id, has_error);

CREATE INDEX preproc_logs_error_date_idx
ON sm.preprocessing_logs (processing_date, has_error);

CREATE TABLE sm.report_definitions ( 
  id VARCHAR2(255) not null,
  scope_id VARCHAR2(255) NOT NULL,
  name VARCHAR2(255 CHAR) NOT NULL,
  sql CLOB NOT NULL,
  creator_id VARCHAR2(255),
  creation_date TIMESTAMP,
  modified_by_id VARCHAR2(255),
  last_modification_date TIMESTAMP,
   CONSTRAINT FK_RD_SCOPE_ID FOREIGN KEY (scope_id) REFERENCES sm.scopes
   ON DELETE CASCADE,
  CONSTRAINT PK_REP_DEFS PRIMARY KEY(id)
);

CREATE TABLE sm.report_definition_roles ( 
  id VARCHAR2(255) not null,
  report_definition_id VARCHAR2(255) NOT NULL,
  role_id VARCHAR2(255) NOT NULL,
  list_index NUMERIC(2,0) NOT NULL,
   CONSTRAINT FK_REPDEF_ID FOREIGN KEY (report_definition_id) REFERENCES sm.report_definitions
   ON DELETE CASCADE,
  CONSTRAINT PK_REP_DEF_ROLES PRIMARY KEY(id)
);

CREATE INDEX rep_def_scope_id_idx
ON sm.report_definitions (scope_id);

CREATE INDEX timestamp_scope_id_idx
ON sm.statistic_data (trunc( timemarker, 'DAY'),scope_id);

INSERT INTO sm.scopes (id, name, scope_type, creator_id, creation_date, modified_by_id, last_modification_date) 
VALUES ('escidoc:scope1','Scope for Framework', 'normal', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}', CURRENT_TIMESTAMP);

INSERT INTO sm.scopes (id, name, scope_type, creator_id, creation_date, modified_by_id, last_modification_date) 
VALUES ('escidoc:scope2','Admin Scope', 'admin', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}', CURRENT_TIMESTAMP);

INSERT INTO sm.aggregation_definitions (id, scope_id, name, creator_id, creation_date) 
    VALUES 
    ('escidoc:aggdef1', 'escidoc:scope1', 'Page Statistics for Framework', '${escidoc.creator.user}', CURRENT_TIMESTAMP);

INSERT INTO sm.aggregation_tables (id, aggregation_definition_id, name, list_index) 
    VALUES 
    ('escidoc:aggdef1table1', 'escidoc:aggdef1', 'escidocaggdef1_request_stats', 1);

INSERT INTO sm.aggregation_tables (id, aggregation_definition_id, name, list_index) 
    VALUES 
    ('escidoc:aggdef1table2', 'escidoc:aggdef1', 'escidocaggdef1_object_stats', 2);

INSERT INTO sm.aggregation_table_indexes (id, aggregation_table_id, name, list_index) 
    VALUES 
    ('escidoc:aggdef1table1idx1', 'escidoc:aggdef1table1', 'escidocaggdef1_time1_idx', 1);

INSERT INTO sm.aggregation_table_index_fields (id, aggregation_table_index_id, field, list_index) 
    VALUES 
    ('escidoc:aggdef1table1idx1field1', 'escidoc:aggdef1table1idx1', 'day', 1);

INSERT INTO sm.aggregation_table_index_fields (id, aggregation_table_index_id, field, list_index) 
    VALUES 
    ('escidoc:aggdef1table1idx1field2', 'escidoc:aggdef1table1idx1', 'month', 2);

INSERT INTO sm.aggregation_table_index_fields (id, aggregation_table_index_id, field, list_index) 
    VALUES 
    ('escidoc:aggdef1table1idx1field3', 'escidoc:aggdef1table1idx1', 'year', 3);

INSERT INTO sm.aggregation_table_indexes (id, aggregation_table_id, name, list_index) 
    VALUES 
    ('escidoc:aggdef1table1idx2', 'escidoc:aggdef1table1', 'escidocaggdef1_time2_idx', 2);

INSERT INTO sm.aggregation_table_index_fields (id, aggregation_table_index_id, field, list_index) 
    VALUES 
    ('escidoc:aggdef1table1idx2field1', 'escidoc:aggdef1table1idx2', 'month', 1);

INSERT INTO sm.aggregation_table_index_fields (id, aggregation_table_index_id, field, list_index) 
    VALUES 
    ('escidoc:aggdef1table1idx2field2', 'escidoc:aggdef1table1idx2', 'year', 2);

INSERT INTO sm.aggregation_table_indexes (id, aggregation_table_id, name, list_index) 
    VALUES 
    ('escidoc:aggdef1table2idx1', 'escidoc:aggdef1table2', 'escidocaggdef1_time3_idx', 1);

INSERT INTO sm.aggregation_table_index_fields (id, aggregation_table_index_id, field, list_index) 
    VALUES 
    ('escidoc:aggdef1table2idx1field1', 'escidoc:aggdef1table2idx1', 'month', 1);

INSERT INTO sm.aggregation_table_index_fields (id, aggregation_table_index_id, field, list_index) 
    VALUES 
    ('escidoc:aggdef1table2idx1field2', 'escidoc:aggdef1table2idx1', 'year', 2);

INSERT INTO sm.aggregation_table_fields (id, aggregation_table_id, field_type_id, name, feed, xpath, data_type, reduce_to, list_index) 
    VALUES 
    ('escidoc:aggdef1table1field1', 
    'escidoc:aggdef1table1', 
    1, 
    'handler', 
    'statistics-data', 
    '//parameter[@name="handler"]/stringvalue', 
    'text', null, 1);

INSERT INTO sm.aggregation_table_fields (id, aggregation_table_id, field_type_id, name, feed, xpath, data_type, reduce_to, list_index) 
    VALUES 
    ('escidoc:aggdef1table1field2', 
    'escidoc:aggdef1table1', 
    1, 
    'request', 
    'statistics-data', 
    '//parameter[@name="request"]/stringvalue', 
    'text', null, 2);

INSERT INTO sm.aggregation_table_fields (id, aggregation_table_id, field_type_id, name, feed, xpath, data_type, reduce_to, list_index) 
    VALUES 
    ('escidoc:aggdef1table1field3', 
    'escidoc:aggdef1table1', 
    1, 
    'interface', 
    'statistics-data', 
    '//parameter[@name="interface"]/stringvalue', 
    'text', null, 3);

INSERT INTO sm.aggregation_table_fields (id, aggregation_table_id, field_type_id, name, feed, xpath, data_type, reduce_to, list_index) 
    VALUES 
    ('escidoc:aggdef1table1field4', 
    'escidoc:aggdef1table1', 
    2, 
    'day', 
    'statistics-data', 
    null, 
    null, 'day', 4);

INSERT INTO sm.aggregation_table_fields (id, aggregation_table_id, field_type_id, name, feed, xpath, data_type, reduce_to, list_index) 
    VALUES 
    ('escidoc:aggdef1table1field5', 
    'escidoc:aggdef1table1', 
    2, 
    'month', 
    'statistics-data', 
    null, 
    null, 'month', 5);

INSERT INTO sm.aggregation_table_fields (id, aggregation_table_id, field_type_id, name, feed, xpath, data_type, reduce_to, list_index) 
    VALUES 
    ('escidoc:aggdef1table1field6', 
    'escidoc:aggdef1table1', 
    2, 
    'year', 
    'statistics-data', 
    null, 
    null, 'year', 6);

INSERT INTO sm.aggregation_table_fields (id, aggregation_table_id, field_type_id, name, feed, xpath, data_type, reduce_to, list_index) 
    VALUES 
    ('escidoc:aggdef1table1field7', 
    'escidoc:aggdef1table1', 
    3, 
    'requests', 
    null, 
    null, 
    null, null, 7);

INSERT INTO sm.aggregation_table_fields (id, aggregation_table_id, field_type_id, name, feed, xpath, data_type, reduce_to, list_index) 
    VALUES 
    ('escidoc:aggdef1table2field1', 
    'escidoc:aggdef1table2', 
    1, 
    'handler', 
    'statistics-data', 
    '//parameter[@name="handler"]/stringvalue', 
    'text', null, 1);

INSERT INTO sm.aggregation_table_fields (id, aggregation_table_id, field_type_id, name, feed, xpath, data_type, reduce_to, list_index) 
    VALUES 
    ('escidoc:aggdef1table2field2', 
    'escidoc:aggdef1table2', 
    1, 
    'request', 
    'statistics-data', 
    '//parameter[@name="request"]/stringvalue', 
    'text', null, 2);

INSERT INTO sm.aggregation_table_fields (id, aggregation_table_id, field_type_id, name, feed, xpath, data_type, reduce_to, list_index) 
    VALUES 
    ('escidoc:aggdef1table2field3', 
    'escidoc:aggdef1table2', 
    1, 
    'object_id', 
    'statistics-data', 
    '//parameter[@name="object_id"]/stringvalue', 
    'text', null, 3);

INSERT INTO sm.aggregation_table_fields (id, aggregation_table_id, field_type_id, name, feed, xpath, data_type, reduce_to, list_index) 
    VALUES 
    ('escidoc:aggdef1table2field4', 
    'escidoc:aggdef1table2', 
    1, 
    'parent_object_id', 
    'statistics-data', 
    '//parameter[@name="parent_object_id1"]/stringvalue', 
    'text', null, 4);

INSERT INTO sm.aggregation_table_fields (id, aggregation_table_id, field_type_id, name, feed, xpath, data_type, reduce_to, list_index) 
    VALUES 
    ('escidoc:aggdef1table2field5', 
    'escidoc:aggdef1table2', 
    1, 
    'user_id', 
    'statistics-data', 
    '//parameter[@name="user_id"]/stringvalue', 
    'text', null, 5);

INSERT INTO sm.aggregation_table_fields (id, aggregation_table_id, field_type_id, name, feed, xpath, data_type, reduce_to, list_index) 
    VALUES 
    ('escidoc:aggdef1table2field6', 
    'escidoc:aggdef1table2', 
    2, 
    'month', 
    'statistics-data', 
    null, 
    null, 'month', 6);

INSERT INTO sm.aggregation_table_fields (id, aggregation_table_id, field_type_id, name, feed, xpath, data_type, reduce_to, list_index) 
    VALUES 
    ('escidoc:aggdef1table2field7', 
    'escidoc:aggdef1table2', 
    2, 
    'year', 
    'statistics-data', 
    null, 
    null, 'year', 7);

INSERT INTO sm.aggregation_table_fields (id, aggregation_table_id, field_type_id, name, feed, xpath, data_type, reduce_to, list_index) 
    VALUES 
    ('escidoc:aggdef1table2field8', 
    'escidoc:aggdef1table2', 
    3, 
    'requests', 
    null, 
    null, 
    null, null, 8);

INSERT INTO sm.agg_stat_data_selectors ( 
  id, aggregation_definition_id, selector_type, xpath, list_index)
  VALUES
  ('escidoc:aggdef1selector', 'escidoc:aggdef1', 'statistic-table', '//parameter[@name="successful"]/*=''1'' 
            AND //parameter[@name="internal"]/*=''0''', 1);

CREATE TABLE sm.escidocaggdef1_request_stats ( 
  handler VARCHAR2(4000),
  request VARCHAR2(4000),
  interface VARCHAR2(4000),
  day NUMERIC,
  month NUMERIC,
  year NUMERIC,
  requests NUMERIC
);

CREATE TABLE sm.escidocaggdef1_object_stats ( 
  handler VARCHAR2(4000),
  request VARCHAR2(4000),
  object_id VARCHAR2(4000),
  parent_object_id VARCHAR2(4000),
  user_id VARCHAR2(4000),
  month NUMERIC,
  year NUMERIC,
  requests NUMERIC
);

CREATE INDEX escidocaggdef1_time1_idx
ON sm.escidocaggdef1_request_stats (day, month, year);

CREATE INDEX escidocaggdef1_time2_idx
ON sm.escidocaggdef1_request_stats (month, year);

CREATE INDEX escidocaggdef1_time3_idx
ON sm.escidocaggdef1_object_stats (month, year);

INSERT INTO sm.aggregation_definitions (id, scope_id, name, creator_id, creation_date) 
    VALUES 
    ('escidoc:aggdef2', 'escidoc:scope1', 'Error Statistics for Framework', '${escidoc.creator.user}', CURRENT_TIMESTAMP);

INSERT INTO sm.aggregation_tables (id, aggregation_definition_id, name, list_index) 
    VALUES 
    ('escidoc:aggdef2table1', 'escidoc:aggdef2', 'escidocaggdef2_error_stats', 1);

INSERT INTO sm.aggregation_table_indexes (id, aggregation_table_id, name, list_index) 
    VALUES 
    ('escidoc:aggdef2table1idx1', 'escidoc:aggdef2table1', 'escidocaggdef2_time1_idx', 1);

INSERT INTO sm.aggregation_table_index_fields (id, aggregation_table_index_id, field, list_index) 
    VALUES 
    ('escidoc:aggdef2table1idx1field1', 'escidoc:aggdef2table1idx1', 'day', 1);

INSERT INTO sm.aggregation_table_index_fields (id, aggregation_table_index_id, field, list_index) 
    VALUES 
    ('escidoc:aggdef2table1idx1field2', 'escidoc:aggdef2table1idx1', 'month', 2);

INSERT INTO sm.aggregation_table_index_fields (id, aggregation_table_index_id, field, list_index) 
    VALUES 
    ('escidoc:aggdef2table1idx1field3', 'escidoc:aggdef2table1idx1', 'year', 3);

INSERT INTO sm.aggregation_table_indexes (id, aggregation_table_id, name, list_index) 
    VALUES 
    ('escidoc:aggdef2table1idx2', 'escidoc:aggdef2table1', 'escidocaggdef2_time2_idx', 2);

INSERT INTO sm.aggregation_table_index_fields (id, aggregation_table_index_id, field, list_index) 
    VALUES 
    ('escidoc:aggdef2table1idx2field1', 'escidoc:aggdef2table1idx2', 'month', 1);

INSERT INTO sm.aggregation_table_index_fields (id, aggregation_table_index_id, field, list_index) 
    VALUES 
    ('escidoc:aggdef2table1idx2field2', 'escidoc:aggdef2table1idx2', 'year', 2);


INSERT INTO sm.aggregation_table_fields (id, aggregation_table_id, field_type_id, name, feed, xpath, data_type, reduce_to, list_index) 
    VALUES 
    ('escidoc:aggdef2table1field1', 
    'escidoc:aggdef2table1', 
    1, 
    'handler', 
    'statistics-data', 
    '//parameter[@name="handler"]/stringvalue', 
    'text', null, 1);

INSERT INTO sm.aggregation_table_fields (id, aggregation_table_id, field_type_id, name, feed, xpath, data_type, reduce_to, list_index) 
    VALUES 
    ('escidoc:aggdef2table1field2', 
    'escidoc:aggdef2table1', 
    1, 
    'request', 
    'statistics-data', 
    '//parameter[@name="request"]/stringvalue', 
    'text', null, 2);

INSERT INTO sm.aggregation_table_fields (id, aggregation_table_id, field_type_id, name, feed, xpath, data_type, reduce_to, list_index) 
    VALUES 
    ('escidoc:aggdef2table1field3', 
    'escidoc:aggdef2table1', 
    1, 
    'interface', 
    'statistics-data', 
    '//parameter[@name="interface"]/stringvalue', 
    'text', null, 3);

INSERT INTO sm.aggregation_table_fields (id, aggregation_table_id, field_type_id, name, feed, xpath, data_type, reduce_to, list_index) 
    VALUES 
    ('escidoc:aggdef2table1field4', 
    'escidoc:aggdef2table1', 
    1, 
    'exception_name', 
    'statistics-data', 
    '//parameter[@name="exception_name"]/stringvalue', 
    'text', null, 4);

INSERT INTO sm.aggregation_table_fields (id, aggregation_table_id, field_type_id, name, feed, xpath, data_type, reduce_to, list_index) 
    VALUES 
    ('escidoc:aggdef2table1field5', 
    'escidoc:aggdef2table1', 
    2, 
    'day', 
    'statistics-data', 
    null, 
    null, 'day', 5);

INSERT INTO sm.aggregation_table_fields (id, aggregation_table_id, field_type_id, name, feed, xpath, data_type, reduce_to, list_index) 
    VALUES 
    ('escidoc:aggdef2table1field6', 
    'escidoc:aggdef2table1', 
    2, 
    'month', 
    'statistics-data', 
    null, 
    null, 'month', 6);

INSERT INTO sm.aggregation_table_fields (id, aggregation_table_id, field_type_id, name, feed, xpath, data_type, reduce_to, list_index) 
    VALUES 
    ('escidoc:aggdef2table1field7', 
    'escidoc:aggdef2table1', 
    2, 
    'year', 
    'statistics-data', 
    null, 
    null, 'year', 7);

INSERT INTO sm.aggregation_table_fields (id, aggregation_table_id, field_type_id, name, feed, xpath, data_type, reduce_to, list_index) 
    VALUES 
    ('escidoc:aggdef2table1field8', 
    'escidoc:aggdef2table1', 
    3, 
    'requests', 
    null, 
    null, 
    null, null, 8);

INSERT INTO sm.agg_stat_data_selectors ( 
  id, aggregation_definition_id, selector_type, xpath, list_index)
  VALUES
  ('escidoc:aggdef2selector', 'escidoc:aggdef2', 'statistic-table', '//parameter[@name="successful"]/*=''0''', 1);

CREATE TABLE sm.escidocaggdef2_error_stats ( 
  handler VARCHAR2(4000),
  request VARCHAR2(4000),
  interface VARCHAR2(4000),
  exception_name VARCHAR2(4000),
  day NUMERIC,
  month NUMERIC,
  year NUMERIC,
  requests NUMERIC
);

CREATE INDEX escidocaggdef2_time1_idx
ON sm.escidocaggdef2_error_stats (day, month, year);

CREATE INDEX escidocaggdef2_time2_idx
ON sm.escidocaggdef2_error_stats (month, year);

INSERT INTO sm.report_definitions ( 
  id, scope_id, name, sql, creator_id, creation_date, modified_by_id, last_modification_date)
  VALUES
  ('escidoc:repdef1', 'escidoc:scope1', 'Successful Framework Requests', 
  'select handler, request, day, month, year, sum(requests) 
    from escidocaggdef1_request_stats 
    group by handler, request, day, month, year;', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}', CURRENT_TIMESTAMP);

INSERT INTO sm.report_definition_roles ( 
  id,
  report_definition_id,
  role_id, list_index)
  VALUES
  ('escidoc:repdef1role1', 'escidoc:repdef1', 'escidoc:role-default-user', 1);

INSERT INTO sm.report_definitions ( 
  id, scope_id, name, sql, creator_id, creation_date, modified_by_id, last_modification_date)
  VALUES
  ('escidoc:repdef2', 'escidoc:scope1', 'Unsuccessful Framework Requests', 
  'select * from escidocaggdef2_error_stats;', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}', CURRENT_TIMESTAMP);

INSERT INTO sm.report_definition_roles ( 
  id,
  report_definition_id,
  role_id, list_index)
  VALUES
  ('escidoc:repdef2role1', 'escidoc:repdef2', 'escidoc:role-default-user', 1);

INSERT INTO sm.report_definitions ( 
  id, scope_id, name, sql, creator_id, creation_date, modified_by_id, last_modification_date)
  VALUES
  ('escidoc:repdef3', 'escidoc:scope1', 'Successful Framework Requests by Month and Year', 
  'select * from escidocaggdef1_request_stats where month = {month} and year = {year};', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}', CURRENT_TIMESTAMP);

INSERT INTO sm.report_definition_roles ( 
  id,
  report_definition_id,
  role_id, list_index)
  VALUES
  ('escidoc:repdef3role1', 'escidoc:repdef3', 'escidoc:role-default-user', 1);

INSERT INTO sm.report_definitions ( 
  id, scope_id, name, sql, creator_id, creation_date, modified_by_id, last_modification_date)
  VALUES
  ('escidoc:repdef4', 'escidoc:scope2', 'Item retrievals, all users', 
  'select object_id as itemid, sum(requests) as itemrequests 
    from escidocaggdef1_object_stats 
    where object_id = {object_id} 
    and handler=''de.escidoc.core.om.service.ItemHandler'' 
    and request=''retrieve'' group by object_id;', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}', CURRENT_TIMESTAMP);

INSERT INTO sm.report_definition_roles ( 
  id,
  report_definition_id,
  role_id, list_index)
  VALUES
  ('escidoc:repdef4role1', 'escidoc:repdef4', 'escidoc:role-default-user', 1);

INSERT INTO sm.report_definitions ( 
  id, scope_id, name, sql, creator_id, creation_date, modified_by_id, last_modification_date)
  VALUES
  ('escidoc:repdef5', 'escidoc:scope2', 'File downloads per Item, all users', 
  'select parent_object_id as itemid, sum(requests) as filerequests 
    from escidocaggdef1_object_stats 
    where parent_object_id = {object_id} 
    and handler=''de.escidoc.core.om.service.ItemHandler'' 
    and request=''retrieveContent'' 
    group by parent_object_id;', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}', CURRENT_TIMESTAMP);

INSERT INTO sm.report_definition_roles ( 
  id,
  report_definition_id,
  role_id, list_index)
  VALUES
  ('escidoc:repdef5role1', 'escidoc:repdef5', 'escidoc:role-default-user', 1);

INSERT INTO sm.report_definitions ( 
  id, scope_id, name, sql, creator_id, creation_date, modified_by_id, last_modification_date)
  VALUES
  ('escidoc:repdef6', 'escidoc:scope2', 'File downloads, all users', 
  'select object_id as fileid, sum(requests) as filerequests 
    from escidocaggdef1_object_stats 
    where object_id = {object_id} 
    and handler=''de.escidoc.core.om.service.ItemHandler'' 
    and request=''retrieveContent'' 
    group by object_id;', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}', CURRENT_TIMESTAMP);

INSERT INTO sm.report_definition_roles ( 
  id,
  report_definition_id,
  role_id, list_index)
  VALUES
  ('escidoc:repdef6role1', 'escidoc:repdef6', 'escidoc:role-default-user', 1);

INSERT INTO sm.report_definitions ( 
  id, scope_id, name, sql, creator_id, creation_date, modified_by_id, last_modification_date)
  VALUES
  ('escidoc:repdef7', 'escidoc:scope2', 'Item retrievals, anonymous users', 
  'select object_id as itemid, sum(requests) as itemrequests 
    from escidocaggdef1_object_stats 
    where object_id = {object_id} 
    and handler=''de.escidoc.core.om.service.ItemHandler'' 
    and request=''retrieve'' 
    and user_id='''' 
    group by object_id;', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}', CURRENT_TIMESTAMP);

INSERT INTO sm.report_definition_roles ( 
  id,
  report_definition_id,
  role_id, list_index)
  VALUES
  ('escidoc:repdef7role1', 'escidoc:repdef7', 'escidoc:role-default-user', 1);

INSERT INTO sm.report_definitions ( 
  id, scope_id, name, sql, creator_id, creation_date, modified_by_id, last_modification_date)
  VALUES
  ('escidoc:repdef8', 'escidoc:scope2', 'File downloads per Item, anonymous users', 
  'select parent_object_id as itemid, sum(requests) as filerequests 
    from escidocaggdef1_object_stats 
    where parent_object_id = {object_id} 
    and handler=''de.escidoc.core.om.service.ItemHandler'' 
    and request=''retrieveContent'' 
    and user_id='''' 
    group by parent_object_id;', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}', CURRENT_TIMESTAMP);

INSERT INTO sm.report_definition_roles ( 
  id,
  report_definition_id,
  role_id, list_index)
  VALUES
  ('escidoc:repdef8role1', 'escidoc:repdef8', 'escidoc:role-default-user', 1);

INSERT INTO sm.report_definitions ( 
  id, scope_id, name, sql, creator_id, creation_date, modified_by_id, last_modification_date)
  VALUES
  ('escidoc:repdef9', 'escidoc:scope2', 'File downloads, anonymous users', 
  'select object_id as fileid, sum(requests) as filerequests 
    from escidocaggdef1_object_stats 
    where object_id = {object_id} 
    and handler=''de.escidoc.core.om.service.ItemHandler'' 
    and request=''retrieveContent'' 
    and user_id='''' 
    group by object_id;', '${escidoc.creator.user}', CURRENT_TIMESTAMP, '${escidoc.creator.user}', CURRENT_TIMESTAMP);

INSERT INTO sm.report_definition_roles ( 
  id,
  report_definition_id,
  role_id, list_index)
  VALUES
  ('escidoc:repdef9role1', 'escidoc:repdef9', 'escidoc:role-default-user', 1);

