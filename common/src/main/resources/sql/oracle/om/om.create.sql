CREATE TABLE om.lockstatus
(
  objid VARCHAR2(255) NOT NULL,
  owner VARCHAR2(255),
  ownertitle VARCHAR2(255 CHAR) NOT NULL,
  locked NUMBER(1,0) NOT NULL,
  lock_timestamp TIMESTAMP DEFAULT SYSDATE NOT NULL,
  CONSTRAINT lockstatus_pkey PRIMARY KEY (objid)
);
