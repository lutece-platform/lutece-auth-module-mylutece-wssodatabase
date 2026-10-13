-- liquibase formatted sql
-- changeset mylutece-wssodatabase:update_db_mylutece-wssodatabase-2.2.1-3.0.0.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = database() AND table_name = 'mylutece_wsso_user' AND column_name = 'date_last_login'
--
-- UPDATE of email size to 256 for WSSO constraints
--
ALTER TABLE mylutece_wsso_user MODIFY email  varchar(256) default '' NOT NULL;
ALTER TABLE mylutece_wsso_user ADD COLUMN date_last_login date NULL;