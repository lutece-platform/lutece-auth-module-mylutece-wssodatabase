--
-- UPDATE of email size to 256 for WSSO constraints
--
ALTER TABLE mylutece_wsso_user MODIFY email  varchar(256) default '' NOT NULL; 