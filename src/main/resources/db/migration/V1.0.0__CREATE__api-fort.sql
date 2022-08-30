
    create table client_profile (
        id bigint AUTO_INCREMENT NOT NULL,
		uuid varchar(36) NOT NULL,
        api_key varchar(36) NOT NULL,
		jwt_public_certificate varchar(3000) NOT NULL,
		realm varchar(50) NOT NULL,
        auth_claim_key varchar(60) NOT NULL,
        primary key (id)
    ) engine=InnoDB;
	

    create table client_endpoints (
        id bigint AUTO_INCREMENT NOT NULL,
		uuid varchar(36) NOT NULL,
		clients_profile_uuid varchar(36) NOT NULL,
		service_name varchar(150) NOT NULL,
		context_path varchar(150) NOT NULL,
		endpoint_path varchar(250) NOT NULL,
		endpoint_regex varchar(250) NOT NULL,
		method_type varchar(6),
		auth_claim_value varchar(250) NOT NULL,
		offline_authentication bit(1) DEFAULT NULL,
		version_number integer NOT NULL,
        is_activate bit(1) DEFAULT NULL,
        is_terminate bit(1) DEFAULT NULL,
        primary key (id)
    ) engine=InnoDB;

    create table hibernate_sequence (
       next_val bigint
    ) engine=InnoDB;

    insert into hibernate_sequence values ( 1 );

    alter table client_endpoints 
       add constraint client_endpoints_uuid_constraint unique (uuid);

    alter table client_profile 
       add constraint client_profile_api_key_constraint unique (api_key);

    alter table client_profile 
       add constraint client_profile_realm_constraint unique (realm);

    alter table client_profile 
       add constraint client_profile_uuid_constraint unique (uuid);
