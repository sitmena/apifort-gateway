
INSERT INTO apifort_client_profile (uuid,realm,api_key,auth_claim_key,jwt_public_certificate,created_date,updated_date)
VALUES ('b8b8b9c9-fa7b-4891-ad23-597771d1ee67','sitech','AZ87-6563-XUJH-00001','realm_access','MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAurkNWYq4VwV4L3D9QwTeGKHG2ohP5XB4HM85ciDJKHQ8t9D5X6W8tA2UpmoeYjkN5kaH9qa1sBZVCaiEuTZ19mwuvbmE28wODb5pzDa6jbxTTHoQvnn/eJtm9itBFZSbgabvskkkhKx3gncVlnf4NLnBeOVTBFDDodXXbx2FDi5hvHr/Cyk9ZTGKFf/VdhrgQ4w7nlooTxy55rVp1M+aAneC3xP9uYn5t1/GVuU17PS0TvWtE0VCkr5M0/3T6oBhXZsQOqGHA1jluG76wjsi5dWH3rTMgq935OyVCoUnYj52N//wNSrPWjnhnDuHQ43b7KQuCSM1rF+qgU5OxkD6PwIDAQAB','2022-09-20 09:15:55.813780','2022-09-20 09:15:55.813780');


INSERT INTO apifort_client_services (uuid,client_profile_uuid_fk,service_title,description,service_path,service_context,is_activate,created_date,updated_date)
VALUES ('48a3ae7b-dcf0-44d3-99cb-1ef2e49afb4c','b8b8b9c9-fa7b-4891-ad23-597771d1ee67','Customer Dashboard','customer dashboard details','omni-ops-customer-dashboard-service.omni-ops.svc.cluster.local:8000','omni-ops-customer-dashboard',1,'2022-09-20 09:17:24.941000','2022-09-20 09:17:24.941000');

INSERT INTO apifort_client_endpoints
(uuid,client_uuid_fk,service_uuid_fk,title,description,method_type,endpoint_path,endpoint_regex,auth_claim_value,version_number,offline_authentication,is_public_service,is_activate,created_date,updated_date)
VALUES
('1610fbbc-a486-48df-97c5-e57f6e4cc130','b8b8b9c9-fa7b-4891-ad23-597771d1ee67','48a3ae7b-dcf0-44d3-99cb-1ef2e49afb4c','Cards List','Get customer dashboard cards list','GET','/cards','^/apis/omni-ops-customer-dashboard/cards$','uma_authorization',1,1,0,1,'2022-09-20 09:19:10.837000','2022-09-20 09:19:10.837000'),
('1ef884f1-2a5b-4c52-8293-422d5e360dfa','b8b8b9c9-fa7b-4891-ad23-597771d1ee67','48a3ae7b-dcf0-44d3-99cb-1ef2e49afb4c','Cards stats','Get customer dashboard cards stats','GET','/cards/stats','^/apis/omni-ops-customer-dashboard/cards/stats$','uma_authorization',1,1,0,1,'2022-09-20 09:19:10.837000','2022-09-20 09:19:10.837000'),
('39c9cfd8-8029-4e16-86a1-31b1ee8a19a1','b8b8b9c9-fa7b-4891-ad23-597771d1ee67','48a3ae7b-dcf0-44d3-99cb-1ef2e49afb4c','Cards lookup','Get customer dashboard cards lookup list','GET','/cards/lookups','^/apis/omni-ops-customer-dashboard/cards/lookups$','uma_authorization',1,1,0,1,'2022-09-20 09:19:10.837000','2022-09-20 09:19:10.837000'),
('67afc6fe-d0e3-438f-b393-bd5ad36a4f9a','b8b8b9c9-fa7b-4891-ad23-597771d1ee67','48a3ae7b-dcf0-44d3-99cb-1ef2e49afb4c','Cards analytics','Get customer dashboard cards analytics list','GET','/cards/analytics','^/apis/omni-ops-customer-dashboard/cards/analytics$','uma_authorization',1,1,0,1,'2022-09-20 09:19:10.837000','2022-09-20 09:19:10.837000');
