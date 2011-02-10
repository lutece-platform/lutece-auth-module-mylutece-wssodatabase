--
-- Dumping data for table core_admin_right
--
INSERT INTO core_admin_right(id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url) VALUES
('WSSODATABASE_MANAGEMENT_USERS','module.mylutece.wssodatabase.adminFeature.wssodatabase_management_user.name',3,'jsp/admin/plugins/mylutece/modules/wssodatabase/ManageUsers.jsp','module.mylutece.wssodatabase.adminFeature.wssodatabase_management_user.description',0,'mylutece-wssodatabase','USERS',NULL,NULL);

--
-- Dumping data for table core_user_right
--
INSERT INTO core_user_right(id_right,id_user) VALUES('WSSODATABASE_MANAGEMENT_USERS',1);
