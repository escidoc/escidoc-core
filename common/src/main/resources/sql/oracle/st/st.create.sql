CREATE TABLE st.staging_file (
  token VARCHAR2(255) NOT NULL,
  expiry_ts NUMBER(19) NOT NULL,
  reference CLOB,
  mime_type VARCHAR2(255),
  upload NUMBER(1,0) NOT NULL,
  CONSTRAINT files_pkey PRIMARY KEY(token)
);