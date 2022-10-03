CREATE TABLE apifort_client_profile (
  uuid varchar(36) NOT NULL,
  api_key varchar(36) DEFAULT NULL,
  auth_claim_key varchar(60) NOT NULL,
  created_date datetime(6) DEFAULT NULL,
  jwt_public_certificate varchar(3000) DEFAULT NULL,
  realm varchar(50) DEFAULT NULL,
  updated_date datetime(6) DEFAULT NULL,
  PRIMARY KEY (uuid),
  UNIQUE KEY UK_7v7fnh47s06s91kh6umw7f40d (api_key),
  UNIQUE KEY UK_h1ye0hd81cy8al3pikx9ijenw (realm),
  KEY apifort_client_profile_index_realm (realm),
  KEY apifort_client_profile_index_apikey (api_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE apifort_client_services (
  uuid varchar(36) NOT NULL,
  client_profile_uuid_fk varchar(36) NOT NULL,
  service_path varchar(150) DEFAULT NULL,
  service_context varchar(100) DEFAULT NULL,
  service_title varchar(150) DEFAULT NULL,
  description varchar(200) DEFAULT NULL,
  is_activate bit(1) DEFAULT NULL,
  created_date datetime(6) DEFAULT NULL,
  updated_date datetime(6) DEFAULT NULL,
  PRIMARY KEY (uuid),
  UNIQUE KEY apifort_client_services_constraint (client_profile_uuid_fk,service_path,service_context),
  KEY apifort_client_services_constraint_index (client_profile_uuid_fk)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE apifort_client_endpoints (
  uuid varchar(36) NOT NULL,
  is_activate bit(1) DEFAULT NULL,
  auth_claim_value varchar(250) NOT NULL,
  client_uuid_fk varchar(255) DEFAULT NULL,
  created_date datetime(6) DEFAULT NULL,
  description varchar(200) DEFAULT NULL,
  endpoint_path varchar(250) DEFAULT NULL,
  endpoint_regex varchar(250) DEFAULT NULL,
  method_type varchar(6) DEFAULT NULL,
  offline_authentication bit(1) DEFAULT NULL,
  is_public_service bit(1) DEFAULT NULL,
  service_uuid_fk varchar(255) DEFAULT NULL,
  title varchar(150) DEFAULT NULL,
  updated_date datetime(6) DEFAULT NULL,
  version_number int DEFAULT NULL,
  PRIMARY KEY (uuid),
  UNIQUE KEY apifort_client_endpoints_constraint (client_uuid_fk,service_uuid_fk,endpoint_regex,method_type),
  KEY client_endpoints_client_profile_fk_index (client_uuid_fk)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
