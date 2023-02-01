INSERT INTO apifort_client_profile (uuid,realm,api_key,auth_claim_key,jwt_public_certificate,created_date,updated_date)
VALUES ('b8b8b9c9-fa7b-4891-ad23-597771d1ee67','sitech','AZ87-6563-XUJH-00001','realm_access','MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhEyH6WOL5VHr2hKsYKdD1yEMBw8kovV5kr/yth+P2OanuQ03p4TwFMYxYTdHrLbPOlsJPf5YxIzd7PWCgWYNsTWunPOIE/f5wOkbdoPkbds/buwMYPAeEpTEqAn515X1aFUj2NR50UeLphawXjHVbOI4fpeByPbhhFMpBVys+07tKGscPmc4BWMlFMDYl08VPg9Y12rVDBod6rD9f4Cg92zzLzu1Bjao/UbRsqtfU2ijtukB0kigyeI+EJTrxDxYCllFiKJcOhOSlzQM/W9G11DmN1LPoOEDCVtKHNcAx/rvI/8TFKTmsnKILEsJVXxfKs0SR4UtqsVKgfiWgTeWrwIDAQAB','2022-09-20 09:15:55.813780','2022-09-20 09:15:55.813780');

INSERT INTO apifort_client_services (uuid,client_profile_uuid_fk,service_title,description,service_path,service_context,is_activate,created_date,updated_date)
VALUES ('48a3ae7b-dcf0-44d3-99cb-1ef2e49afb4c','b8b8b9c9-fa7b-4891-ad23-597771d1ee67','Customer Dashboard','customer dashboard details','omni-ops-customer-dashboard-service.omni-ops.svc.cluster.local:8000','omni-ops-customer-dashboard',1,'2022-09-20 09:17:24.941000','2022-09-20 09:17:24.941000');

INSERT INTO apifort_client_endpoints
(uuid,client_uuid_fk,service_uuid_fk,title,description,method_type,endpoint_path,endpoint_regex,auth_claim_value,version_number,offline_authentication,is_public_service,is_activate,created_date,updated_date)
VALUES
('1610fbbc-a486-48df-97c5-e57f6e4cc130','b8b8b9c9-fa7b-4891-ad23-597771d1ee67','48a3ae7b-dcf0-44d3-99cb-1ef2e49afb4c','Cards List','Get customer dashboard cards list','GET','/cards','^/apis/omni-ops-customer-dashboard/cards$','uma_authorization',1,1,0,1,'2022-09-20 09:19:10.837000','2022-09-20 09:19:10.837000');
INSERT INTO apifort_client_endpoints
(uuid,client_uuid_fk,service_uuid_fk,title,description,method_type,endpoint_path,endpoint_regex,auth_claim_value,version_number,offline_authentication,is_public_service,is_activate,created_date,updated_date)
VALUES
('1ef884f1-2a5b-4c52-8293-422d5e360dfa','b8b8b9c9-fa7b-4891-ad23-597771d1ee67','48a3ae7b-dcf0-44d3-99cb-1ef2e49afb4c','Cards stats','Get customer dashboard cards stats','GET','/cards/stats','^/apis/omni-ops-customer-dashboard/cards/stats$','uma_authorization',1,1,0,1,'2022-09-20 09:19:10.837000','2022-09-20 09:19:10.837000');
INSERT INTO apifort_client_endpoints
(uuid,client_uuid_fk,service_uuid_fk,title,description,method_type,endpoint_path,endpoint_regex,auth_claim_value,version_number,offline_authentication,is_public_service,is_activate,created_date,updated_date)
VALUES
('39c9cfd8-8029-4e16-86a1-31b1ee8a19a1','b8b8b9c9-fa7b-4891-ad23-597771d1ee67','48a3ae7b-dcf0-44d3-99cb-1ef2e49afb4c','Cards lookup','Get customer dashboard cards lookup list','GET','/cards/lookups','^/apis/omni-ops-customer-dashboard/cards/lookups$','uma_authorization',1,1,0,1,'2022-09-20 09:19:10.837000','2022-09-20 09:19:10.837000');
INSERT INTO apifort_client_endpoints
(uuid,client_uuid_fk,service_uuid_fk,title,description,method_type,endpoint_path,endpoint_regex,auth_claim_value,version_number,offline_authentication,is_public_service,is_activate,created_date,updated_date)
VALUES
('67afc6fe-d0e3-438f-b393-bd5ad36a4f9a','b8b8b9c9-fa7b-4891-ad23-597771d1ee67','48a3ae7b-dcf0-44d3-99cb-1ef2e49afb4c','Cards analytics','Get customer dashboard cards analytics list','GET','/cards/analytics','^/apis/omni-ops-customer-dashboard/cards/analytics$','uma_authorization',1,1,0,1,'2022-09-20 09:19:10.837000','2022-09-20 09:19:10.837000');
INSERT INTO apifort_client_endpoints
(uuid,client_uuid_fk,service_uuid_fk,title,description,method_type,endpoint_path,endpoint_regex,auth_claim_value,version_number,offline_authentication,is_public_service,is_activate,created_date,updated_date)
VALUES
('f7bc4b97-f6b6-4ebd-8f50-69fff23a9a8e','b8b8b9c9-fa7b-4891-ad23-597771d1ee67','48a3ae7b-dcf0-44d3-99cb-1ef2e49afb4c','Coast Chart','Get Coast Chart','GET','/costs/chart','^/apis/omni-ops-customer-dashboard/costs/chart$','uma_authorization',1,1,0,1,'2022-09-20 09:19:10.837000','2022-09-20 09:19:10.837000');
INSERT INTO apifort_client_endpoints
(uuid,client_uuid_fk,service_uuid_fk,title,description,method_type,endpoint_path,endpoint_regex,auth_claim_value,version_number,offline_authentication,is_public_service,is_activate,created_date,updated_date)
VALUES
('4d9aa40c-79c8-4e7c-93c9-cefd56e8a9c3','b8b8b9c9-fa7b-4891-ad23-597771d1ee67','48a3ae7b-dcf0-44d3-99cb-1ef2e49afb4c','Coast Stats','Get Coast Stats','GET','/costs/stats','^/apis/omni-ops-customer-dashboard/costs/stats$','uma_authorization',1,1,0,1,'2022-09-20 09:19:10.837000','2022-09-20 09:19:10.837000');
INSERT INTO apifort_client_endpoints
(uuid,client_uuid_fk,service_uuid_fk,title,description,method_type,endpoint_path,endpoint_regex,auth_claim_value,version_number,offline_authentication,is_public_service,is_activate,created_date,updated_date)
VALUES
('e9e9b209-d41a-4781-8dd1-029a92d23025','b8b8b9c9-fa7b-4891-ad23-597771d1ee67','48a3ae7b-dcf0-44d3-99cb-1ef2e49afb4c','Coast Details','Get Coast Details','GET','/costs/details','^/apis/omni-ops-customer-dashboard/costs/details$','uma_authorization',1,1,0,1,'2022-09-20 09:19:10.837000','2022-09-20 09:19:10.837000');
INSERT INTO apifort_client_endpoints
(uuid,client_uuid_fk,service_uuid_fk,title,description,method_type,endpoint_path,endpoint_regex,auth_claim_value,version_number,offline_authentication,is_public_service,is_activate,created_date,updated_date)
VALUES
('f8a19d1e-988b-43a5-bdf6-df4b3e34031b','b8b8b9c9-fa7b-4891-ad23-597771d1ee67','48a3ae7b-dcf0-44d3-99cb-1ef2e49afb4c','Cloud Connect','Get Cloud Connect','GET','/cloud-connect','^/apis/omni-ops-customer-dashboard/cloud-connect$','uma_authorization',1,1,0,1,'2022-09-20 09:19:10.837000','2022-09-20 09:19:10.837000');
INSERT INTO apifort_client_endpoints
(uuid,client_uuid_fk,service_uuid_fk,title,description,method_type,endpoint_path,endpoint_regex,auth_claim_value,version_number,offline_authentication,is_public_service,is_activate,created_date,updated_date)
VALUES
('587620b8-48dc-49ec-ae82-dbb71c99f153','b8b8b9c9-fa7b-4891-ad23-597771d1ee67','48a3ae7b-dcf0-44d3-99cb-1ef2e49afb4c','Cloud Connect','POST Account Cloud Connect','POST','/cloud-connect/{account_type}','^/apis/omni-ops-customer-dashboard/cloud-connect/[^/]+$','uma_authorization',1,1,0,1,'2022-09-20 09:19:10.837000','2022-09-20 09:19:10.837000');
INSERT INTO apifort_client_endpoints
(uuid,client_uuid_fk,service_uuid_fk,title,description,method_type,endpoint_path,endpoint_regex,auth_claim_value,version_number,offline_authentication,is_public_service,is_activate,created_date,updated_date)
VALUES
('038511ca-9eed-4135-9024-fcce36402888','b8b8b9c9-fa7b-4891-ad23-597771d1ee67','48a3ae7b-dcf0-44d3-99cb-1ef2e49afb4c','Cloud Connect','Update Account Cloud Connect','PUT','/cloud-connect/{id}','^/apis/omni-ops-customer-dashboard/cloud-connect/[^/]+$','uma_authorization',1,1,0,1,'2022-09-20 09:19:10.837000','2022-09-20 09:19:10.837000');
INSERT INTO apifort_client_endpoints
(uuid,client_uuid_fk,service_uuid_fk,title,description,method_type,endpoint_path,endpoint_regex,auth_claim_value,version_number,offline_authentication,is_public_service,is_activate,created_date,updated_date)
VALUES
('54100dc4-71d4-422f-995f-f7380039b792','b8b8b9c9-fa7b-4891-ad23-597771d1ee67','48a3ae7b-dcf0-44d3-99cb-1ef2e49afb4c','Cloud Connect','Delete Account Cloud Connect','DELETE','/cloud-connect/{id}','^/apis/omni-ops-customer-dashboard/cloud-connect/[^/]+$','uma_authorization',1,1,0,1,'2022-09-20 09:19:10.837000','2022-09-20 09:19:10.837000');