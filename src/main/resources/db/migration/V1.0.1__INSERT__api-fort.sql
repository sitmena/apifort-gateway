--
-- Dumping data for table `client_profile`
--
 INSERT INTO `client_profile`(uuid, api_key, jwt_public_certificate, realm, auth_claim_key)
 VALUES ('af3ff92f-427b-43e8-a4ac-530a34c4620a','AZ87-6563-XUJH-00001','MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhEyH6WOL5VHr2hKsYKdD1yEMBw8kovV5kr/yth+P2OanuQ03p4TwFMYxYTdHrLbPOlsJPf5YxIzd7PWCgWYNsTWunPOIE/f5wOkbdoPkbds/buwMYPAeEpTEqAn515X1aFUj2NR50UeLphawXjHVbOI4fpeByPbhhFMpBVys+07tKGscPmc4BWMlFMDYl08VPg9Y12rVDBod6rD9f4Cg92zzLzu1Bjao/UbRsqtfU2ijtukB0kigyeI+EJTrxDxYCllFiKJcOhOSlzQM/W9G11DmN1LPoOEDCVtKHNcAx/rvI/8TFKTmsnKILEsJVXxfKs0SR4UtqsVKgfiWgTeWrwIDAQAB','sitech','realm_access');
--
-- Dumping data for table `client_endpoints`
--
INSERT INTO client_endpoints(uuid,clients_profile_uuid,service_name,context_path,endpoint_path,endpoint_regex,method_type,auth_claim_value,offline_authentication,is_public_service,version_number,is_activate,is_terminate)
VALUES ('5161d0d3-cbd1-41cf-9080-1c55a81cac7c','af3ff92f-427b-43e8-a4ac-530a34c4620a','omni-ops-customer-dashboard:8000','omni-ops-customer-dashboard','/cards','^/apis/omni-ops-customer-dashboard/cards$','GET','uma_authorization',0,1,1,0,1);
INSERT INTO client_endpoints(uuid,clients_profile_uuid,service_name,context_path,endpoint_path,endpoint_regex,method_type,auth_claim_value,offline_authentication,is_public_service,version_number,is_activate,is_terminate)
VALUES ('96d2a845-8008-4c17-949a-08e5bbbe65a8','af3ff92f-427b-43e8-a4ac-530a34c4620a','omni-ops-customer-dashboard:8000','omni-ops-customer-dashboard','/cards/lookups','^/apis/omni-ops-customer-dashboard/cards/lookups$','GET','uma_authorization',0,1,1,0,1);
INSERT INTO client_endpoints(uuid,clients_profile_uuid,service_name,context_path,endpoint_path,endpoint_regex,method_type,auth_claim_value,offline_authentication,is_public_service,version_number,is_activate,is_terminate)
VALUES ('59d0b79d-bd75-4a51-8ff0-ad12a8eef3df','af3ff92f-427b-43e8-a4ac-530a34c4620a','omni-ops-customer-dashboard:8000','omni-ops-customer-dashboard','/cards/analytics','^/apis/omni-ops-customer-dashboard/cards/analytics$','GET','uma_authorization',0,1,1,0,1);
INSERT INTO client_endpoints(uuid,clients_profile_uuid,service_name,context_path,endpoint_path,endpoint_regex,method_type,auth_claim_value,offline_authentication,is_public_service,version_number,is_activate,is_terminate)
VALUES ('66f9bba9-ad2d-48af-aaaf-70cc2fe2d76c','af3ff92f-427b-43e8-a4ac-530a34c4620a','omni-ops-customer-dashboard:8000','omni-ops-customer-dashboard','/cards/stats','^/apis/omni-ops-customer-dashboard/cards/stats$','GET','uma_authorization',0,1,1,0,1);
