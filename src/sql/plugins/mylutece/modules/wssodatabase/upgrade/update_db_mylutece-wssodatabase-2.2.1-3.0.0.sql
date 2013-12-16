--
-- UPDATE of email size to 256 for WSSO constraints
--
ALTER TABLE mylutece_wsso_user MODIFY email  varchar(256) default '' NOT NULL;
ALTER TABLE mylutece_wsso_user ADD COLUMN date_last_login date NULL;