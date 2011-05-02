CREATE SCHEMA st;

CREATE TABLE st.staging_file (
  token VARCHAR(255) NOT NULL,
  expiry_ts int8 NOT NULL,
  reference TEXT,
  mime_type VARCHAR(255),
  upload BOOLEAN NOT NULL,
  CONSTRAINT files_pkey PRIMARY KEY(token)
);