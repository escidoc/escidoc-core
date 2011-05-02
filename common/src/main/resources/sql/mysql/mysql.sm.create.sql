CREATE SCHEMA sm;

CREATE TABLE sm.scopes ( 
  id VARCHAR(255) unique not null primary key,
  xml_data TEXT NOT NULL
);

CREATE TABLE sm.statistic_data ( 
  id VARCHAR(255) unique not null primary key,
  xml_data TEXT NOT NULL,
  scope_id VARCHAR(255) NOT NULL,
  timemarker TIMESTAMP NOT NULL,
   CONSTRAINT FK_SCOPE_ID_SD FOREIGN KEY (scope_id) REFERENCES sm.scopes (id)
   ON DELETE CASCADE
);

CREATE TABLE sm.aggregation_definitions ( 
  id VARCHAR(255) unique not null primary key,
  xml_data TEXT NOT NULL,
  scope_id VARCHAR(255) NOT NULL,
   CONSTRAINT FK_SCOPE_ID_AD FOREIGN KEY (scope_id) REFERENCES sm.scopes (id)
   ON DELETE CASCADE
);

CREATE INDEX agg_def_scope_id_idx
ON sm.aggregation_definitions (scope_id);

CREATE TABLE sm.preprocessing_logs ( 
  id VARCHAR(255) unique not null primary key,
  aggregation_definition_id VARCHAR(255) not null,
  processing_date DATE not null,
  log_entry TEXT,
  has_error BOOLEAN,
   CONSTRAINT FK_AGGREGATION_DEFINITION_ID FOREIGN KEY (aggregation_definition_id) 
   REFERENCES sm.aggregation_definitions
   ON DELETE CASCADE
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
  id VARCHAR(255) unique not null primary key,
  xml_data TEXT NOT NULL,
  scope_id VARCHAR(255) NOT NULL,
  name VARCHAR(255) NOT NULL,
   CONSTRAINT FK_SCOPE_ID_RD FOREIGN KEY (scope_id) REFERENCES sm.scopes (id)
   ON DELETE CASCADE
);

CREATE INDEX rep_def_scope_id_idx
ON sm.report_definitions (scope_id);

# Disabled because mysql does not understand date_trunc( 'day',timemarker)
#CREATE INDEX timestamp_scope_id_idx
#ON sm.statistic_data (date_trunc( 'day',timemarker),scope_id);



INSERT INTO sm.scopes (id, xml_data) VALUES ('escidoc:scope1','<?xml version="1.0" encoding="UTF-8"?>
<scope xmlns="http://www.escidoc.de/schemas/scope/0.3" objid="escidoc:scope1">
	<name>Scope for Framework</name>
	<type>normal</type>
</scope>');

INSERT INTO sm.scopes (id, xml_data) VALUES ('escidoc:scope2','<?xml version="1.0" encoding="UTF-8"?>
<scope xmlns="http://www.escidoc.de/schemas/scope/0.3" objid="escidoc:scope2">
	<name>Admin Scope</name>
	<type>admin</type>
</scope>');

INSERT INTO sm.aggregation_definitions (id, xml_data, scope_id) VALUES ('escidoc:aggdef1', '<?xml version="1.0" encoding="UTF-8"?>
<aggregation-definition xmlns="http://www.escidoc.de/schemas/aggregationdefinition/0.3" objid="escidoc:aggdef1">
	<name>Page Statistics for Framework</name> 
	<scope objid="escidoc:scope1" />
	<aggregation-table>
		<name>_escidocaggdef1_Request_Statistics</name>
		<field>
			<info-field feed="statistics-data">
				<name>handler</name>
				<type>text</type>
				<xpath>//parameter[@name="handler"]/stringvalue</xpath>
			</info-field>
		</field>
		<field>
			<info-field feed="statistics-data">
				<name>request</name>
				<type>text</type>
				<xpath>//parameter[@name="request"]/stringvalue</xpath>
			</info-field>
		</field>
		<field>
			<info-field feed="statistics-data">
				<name>interface</name>
				<type>text</type>
				<xpath>//parameter[@name="interface"]/stringvalue</xpath>
			</info-field>
		</field>
		<field>
			<time-reduction-field feed="statistics-data">
				<name>day</name>
				<reduce-to>day</reduce-to>
			</time-reduction-field>
		</field>
		<field>
			<time-reduction-field feed="statistics-data">
				<name>month</name>
				<reduce-to>month</reduce-to>
			</time-reduction-field>
		</field>
		<field>
			<time-reduction-field feed="statistics-data">
				<name>year</name>
				<reduce-to>year</reduce-to>
			</time-reduction-field>
		</field>
		<field>
			<count-cumulation-field>
				<name>requests</name>
			</count-cumulation-field>
		</field>
		<index>
			<name>_escidocaggdef1_time1_idx</name>
			<field>day</field>
			<field>month</field>
			<field>year</field>
		</index>
		<index>
			<name>_escidocaggdef1_time2_idx</name>
			<field>month</field>
			<field>year</field>
		</index>
	</aggregation-table>
	<aggregation-table>
		<name>_escidocaggdef1_Object_Statistics</name>
		<field>
			<info-field feed="statistics-data">
				<name>handler</name>
				<type>text</type>
				<xpath>//parameter[@name="handler"]/stringvalue</xpath>
			</info-field>
		</field>
		<field>
			<info-field feed="statistics-data">
				<name>request</name>
				<type>text</type>
				<xpath>//parameter[@name="request"]/stringvalue</xpath>
			</info-field>
		</field>
		<field>
			<info-field feed="statistics-data">
				<name>object_id</name>
				<type>text</type>
				<xpath>//parameter[@name="object_id"]/stringvalue</xpath>
			</info-field>
		</field>
		<field>
			<info-field feed="statistics-data">
				<name>parent_object_id</name>
				<type>text</type>
				<xpath>//parameter[@name="parent_object_id1"]/stringvalue</xpath>
			</info-field>
		</field>
		<field>
			<info-field feed="statistics-data">
				<name>user_id</name>
				<type>text</type>
				<xpath>//parameter[@name="user_id"]/stringvalue</xpath>
			</info-field>
		</field>
		<field>
			<time-reduction-field feed="statistics-data">
				<name>month</name>
				<reduce-to>month</reduce-to>
			</time-reduction-field>
		</field>
		<field>
			<time-reduction-field feed="statistics-data">
				<name>year</name>
				<reduce-to>year</reduce-to>
			</time-reduction-field>
		</field>
		<field>
			<count-cumulation-field>
				<name>requests</name>
			</count-cumulation-field>
		</field>
		<index>
			<name>_escidocaggdef1_time3_idx</name>
			<field>month</field>
			<field>year</field>
		</index>
	</aggregation-table>
	<statistic-data>
		<statistic-table>
			<xpath>//parameter[@name="successful"]/*=''1'' 
			AND //parameter[@name="internal"]/*=''0''</xpath>
		</statistic-table>
	</statistic-data>
</aggregation-definition>
', 'escidoc:scope1');

CREATE TABLE sm._escidocaggdef1_request_statistics ( 
  handler TEXT NOT NULL,
  request TEXT NOT NULL,
  interface TEXT NOT NULL,
  day NUMERIC NOT NULL,
  month NUMERIC NOT NULL,
  year NUMERIC NOT NULL,
  requests NUMERIC NOT NULL
);

CREATE TABLE sm._escidocaggdef1_object_statistics ( 
  handler TEXT NOT NULL,
  request TEXT NOT NULL,
  object_id TEXT NOT NULL,
  parent_object_id TEXT NOT NULL,
  user_id TEXT NOT NULL,
  month NUMERIC NOT NULL,
  year NUMERIC NOT NULL,
  requests NUMERIC NOT NULL
);

CREATE INDEX _escidocaggdef1_time1_idx
ON sm._escidocaggdef1_request_statistics (day, month, year);

CREATE INDEX _escidocaggdef1_time2_idx
ON sm._escidocaggdef1_request_statistics (month, year);

CREATE INDEX _escidocaggdef1_time3_idx
ON sm._escidocaggdef1_object_statistics (month, year);

INSERT INTO sm.aggregation_definitions (id, xml_data, scope_id) VALUES ('escidoc:aggdef2', '<?xml version="1.0" encoding="UTF-8"?>
<aggregation-definition xmlns="http://www.escidoc.de/schemas/aggregationdefinition/0.3" objid="escidoc:aggdef2">
	<name>Error Statistics for Framework</name>
	<scope objid="escidoc:scope1" />
	<aggregation-table>
		<name>_escidocaggdef2_Error_Statistics</name>
		<field>
			<info-field feed="statistics-data">
				<name>handler</name>
				<type>text</type>
				<xpath>//parameter[@name="handler"]/stringvalue</xpath>
			</info-field>
		</field>
		<field>
			<info-field feed="statistics-data">
				<name>request</name>
				<type>text</type>
				<xpath>//parameter[@name="request"]/stringvalue</xpath>
			</info-field>
		</field>
		<field>
			<info-field feed="statistics-data">
				<name>interface</name>
				<type>text</type>
				<xpath>//parameter[@name="interface"]/stringvalue</xpath>
			</info-field>
		</field>
		<field>
			<info-field feed="statistics-data">
				<name>exception_name</name>
				<type>text</type>
				<xpath>//parameter[@name="exception_name"]/stringvalue</xpath>
			</info-field>
		</field>
		<field>
			<time-reduction-field feed="statistics-data">
				<name>day</name>
				<reduce-to>day</reduce-to>
			</time-reduction-field>
		</field>
		<field>
			<time-reduction-field feed="statistics-data">
				<name>month</name>
				<reduce-to>month</reduce-to>
			</time-reduction-field>
		</field>
		<field>
			<time-reduction-field feed="statistics-data">
				<name>year</name>
				<reduce-to>year</reduce-to>
			</time-reduction-field>
		</field>
		<field>
			<count-cumulation-field>
				<name>requests</name>
			</count-cumulation-field>
		</field>
		<index>
			<name>_escidocaggdef2_time1_idx</name>
			<field>day</field>
			<field>month</field>
			<field>year</field>
		</index>
		<index>
			<name>_escidocaggdef2_time2_idx</name>
			<field>month</field>
			<field>year</field>
		</index>
	</aggregation-table>
	<statistic-data>
		<statistic-table>
			<xpath>//parameter[@name="successful"]/*=''0''</xpath>
		</statistic-table>
	</statistic-data>
</aggregation-definition>
', 'escidoc:scope1');

CREATE TABLE sm._escidocaggdef2_error_statistics ( 
  handler TEXT NOT NULL,
  request TEXT NOT NULL,
  interface TEXT NOT NULL,
  exception_name TEXT NOT NULL,
  day NUMERIC NOT NULL,
  month NUMERIC NOT NULL,
  year NUMERIC NOT NULL,
  requests NUMERIC NOT NULL
);

CREATE INDEX _escidocaggdef2_time1_idx
ON sm._escidocaggdef2_error_statistics (day, month, year);

CREATE INDEX _escidocaggdef2_time2_idx
ON sm._escidocaggdef2_error_statistics (month, year);

INSERT INTO sm.report_definitions (id, xml_data, scope_id, name) VALUES ('escidoc:repdef1', '<?xml version="1.0" encoding="UTF-8"?><report-definition xmlns="http://www.escidoc.de/schemas/reportdefinition/0.3" objid="escidoc:repdef1">	<name>Successful Framework Requests</name>   <scope objid="escidoc:scope1" />	<sql>		select 		handler, request, day, month, year, sum(requests) 		from _escidocaggdef1_request_statistics		group by handler, request, day, month, year;	</sql></report-definition>', 'escidoc:scope1', 'Successful Framework Requests');

INSERT INTO sm.report_definitions (id, xml_data, scope_id, name) VALUES ('escidoc:repdef2', '<?xml version="1.0" encoding="UTF-8"?><report-definition xmlns="http://www.escidoc.de/schemas/reportdefinition/0.3" objid="escidoc:repdef2">	<name>Unsuccessful Framework Requests</name>  <scope objid="escidoc:scope1" />	<sql>		select * 		from _2_error_statistics;	</sql> </report-definition>', '1', 'Unsuccessful Framework Requests');

INSERT INTO sm.report_definitions (id, xml_data, scope_id, name) VALUES ('escidoc:repdef3', '<?xml version="1.0" encoding="UTF-8"?><report-definition xmlns="http://www.escidoc.de/schemas/reportdefinition/0.3" objid="escidoc:repdef3">	<name>Successful Framework Requests by Month and Year</name>  <scope objid="escidoc:scope1" />	<sql>		select *		from _escidocaggdef1_request_statistics		where month = {month} and year = {year};	</sql> </report-definition>', 'escidoc:scope1', 'Successful Framework Requests by Month and Year');

INSERT INTO sm.report_definitions (id, xml_data, scope_id, name) VALUES ('escidoc:repdef4', '<?xml version="1.0" encoding="UTF-8"?><report-definition xmlns="http://www.escidoc.de/schemas/reportdefinition/0.3" objid="escidoc:repdef4">	<name>Item retrievals, all users</name>  <scope objid="escidoc:scope2" />	<sql>		select object_id as itemId, sum(requests) as itemRequests		from _escidocaggdef1_object_statistics		where object_id = {object_id} and handler=''ItemHandler'' and request=''retrieve'' group by object_id;	</sql> </report-definition>', 'escidoc:scope2', 'Item retrievals, all users');

INSERT INTO sm.report_definitions (id, xml_data, scope_id, name) VALUES ('escidoc:repdef5', '<?xml version="1.0" encoding="UTF-8"?><report-definition xmlns="http://www.escidoc.de/schemas/reportdefinition/0.3" objid="escidoc:repdef5">	<name>File downloads per Item, all users</name>  <scope objid="escidoc:scope2" />	<sql>		select parent_object_id as itemId, sum(requests)	as fileRequests	from _escidocaggdef1_object_statistics		where parent_object_id = {object_id} and handler=''ItemHandler'' and request=''retrieveContent'' group by parent_object_id;	</sql> </report-definition>', 'escidoc:scope2', 'File downloads per Item, all users');

INSERT INTO sm.report_definitions (id, xml_data, scope_id, name) VALUES ('escidoc:repdef6', '<?xml version="1.0" encoding="UTF-8"?><report-definition xmlns="http://www.escidoc.de/schemas/reportdefinition/0.3" objid="escidoc:repdef6">	<name>File downloads, all users</name>  <scope objid="escidoc:scope2" />	<sql>		select object_id as fileId, sum(requests) as fileRequests		from _escidocaggdef1_object_statistics		where object_id = {object_id} and handler=''ItemHandler'' and request=''retrieveContent'' group by object_id;	</sql> </report-definition>', 'escidoc:scope2', 'File downloads, all users');

INSERT INTO sm.report_definitions (id, xml_data, scope_id, name) VALUES ('escidoc:repdef7', '<?xml version="1.0" encoding="UTF-8"?><report-definition xmlns="http://www.escidoc.de/schemas/reportdefinition/0.3" objid="escidoc:repdef7">	<name>Item retrievals, anonymous users</name>  <scope objid="escidoc:scope2" />	<sql>		select object_id as itemId, sum(requests) as itemRequests		from _escidocaggdef1_object_statistics		where object_id = {object_id} and handler=''ItemHandler'' and request=''retrieve'' and user_id='''' group by object_id;	</sql> </report-definition>', 'escidoc:scope2', 'Item retrievals, anonymous users');

INSERT INTO sm.report_definitions (id, xml_data, scope_id, name) VALUES ('escidoc:repdef8', '<?xml version="1.0" encoding="UTF-8"?><report-definition xmlns="http://www.escidoc.de/schemas/reportdefinition/0.3" objid="escidoc:repdef8">	<name>File downloads per Item, anonymous users</name>  <scope objid="escidoc:scope2" />	<sql>		select parent_object_id as itemId, sum(requests)	as fileRequests	from _escidocaggdef1_object_statistics		where parent_object_id = {object_id} and handler=''ItemHandler'' and request=''retrieveContent'' and user_id='''' group by parent_object_id;	</sql> </report-definition>', 'escidoc:scope2', 'File downloads per Item, anonymous users');

INSERT INTO sm.report_definitions (id, xml_data, scope_id, name) VALUES ('escidoc:repdef9', '<?xml version="1.0" encoding="UTF-8"?><report-definition xmlns="http://www.escidoc.de/schemas/reportdefinition/0.3" objid="escidoc:repdef9">	<name>File downloads, anonymous users</name>  <scope objid="escidoc:scope2" />	<sql>		select object_id as fileId, sum(requests) as fileRequests		from _escidocaggdef1_object_statistics		where object_id = {object_id} and handler=''ItemHandler'' and request=''retrieveContent'' and user_id='''' group by object_id;	</sql> </report-definition>', 'escidoc:scope2', 'File downloads, anonymous users');
