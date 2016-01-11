DROP SCHEMA IF EXISTS liquibase_demo;
CREATE SCHEMA liquibase_demo;

#SET search_path = liquibase_demo;
use liquibase_demo;

CREATE TABLE box
(
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE,
  label text NOT NULL,
  deleted TINYINT UNSIGNED DEFAULT 0,
  CONSTRAINT box_pkey PRIMARY KEY (id)
);

INSERT INTO box VALUES (1, 'init', FALSE );
INSERT INTO box VALUES (2, 'pink', FALSE );
INSERT INTO box VALUES (3, 'purple', FALSE );
INSERT INTO box VALUES (4, 'white', FALSE );
INSERT INTO box VALUES (5, 'black', FALSE );
