
    create table client_endpoints (
       id bigint not null,
        is_activate bit,
        auth_claim_value varchar(255) not null,
        clients_profile_uuid varchar(255) not null,
        endpoint_path varchar(255),
        endpoint_regex varchar(255),
        method_type varchar(10),
        offline_authentication bit,
        service_name varchar(150),
        is_terminate bit,
        uuid varchar(36) not null,
        version_number integer,
        primary key (id)
    ) engine=InnoDB;

    create table client_profile (
       id bigint not null,
        api_key varchar(36),
        auth_claim_key varchar(60) not null,
        jwt_public_certificate varchar(2500),
        realm varchar(50),
        uuid varchar(36) not null,
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
