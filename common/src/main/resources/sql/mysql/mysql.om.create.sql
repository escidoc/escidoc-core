CREATE SCHEMA om;

CREATE TABLE om.lockstatus
(
  objid VARCHAR(255) NOT NULL,
  owner VARCHAR(255) NULL,
  ownertitle VARCHAR(255) NOT NULL,
  locked BOOLEAN NOT NULL,
  lock_timestamp TIMESTAMP NOT NULL DEFAULT now(),
  CONSTRAINT lockstatus_pkey PRIMARY KEY (objid)
);

