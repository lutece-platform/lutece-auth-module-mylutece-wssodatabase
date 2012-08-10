--
-- Table struture for mylutece_wsso_user
--
DROP TABLE IF EXISTS mylutece_wsso_user;
CREATE TABLE mylutece_wsso_user (
	mylutece_wsso_user_id int NOT NULL,
	guid varchar(40) default '0' NOT NULL,
	last_name varchar(100) default '' NOT NULL,
	first_name varchar(100) default '' NOT NULL,
	email varchar(256) default '' NOT NULL,
	date_last_login date NULL,
	PRIMARY KEY (mylutece_wsso_user_id)
);

--
-- Table struture for mylutece_wsso_user_role
--
DROP TABLE IF EXISTS mylutece_wsso_user_role;
CREATE TABLE mylutece_wsso_user_role (
	mylutece_wsso_user_id int NOT NULL,
	role varchar(50) NOT NULL,
	PRIMARY KEY (mylutece_wsso_user_id,role)
);
