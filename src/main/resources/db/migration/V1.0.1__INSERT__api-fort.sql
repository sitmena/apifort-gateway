--
-- Dumping data for table `client_profile`
--

INSERT INTO `client_profile`(api_key, auth_claim_key, jwt_public_certificate, realm, uuid)
VALUES ('AZ87-6563-XUJH-00001','realm_access','MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhEyH6WOL5VHr2hKsYKdD1yEMBw8kovV5kr/yth+P2OanuQ03p4TwFMYxYTdHrLbPOlsJPf5YxIzd7PWCgWYNsTWunPOIE/f5wOkbdoPkbds/buwMYPAeEpTEqAn515X1aFUj2NR50UeLphawXjHVbOI4fpeByPbhhFMpBVys+07tKGscPmc4BWMlFMDYl08VPg9Y12rVDBod6rD9f4Cg92zzLzu1Bjao/UbRsqtfU2ijtukB0kigyeI+EJTrxDxYCllFiKJcOhOSlzQM/W9G11DmN1LPoOEDCVtKHNcAx/rvI/8TFKTmsnKILEsJVXxfKs0SR4UtqsVKgfiWgTeWrwIDAQAB','sitech','24ac7b3b-9a2a-4f96-8887-87a11dc7f878');

--
-- Dumping data for table `client_endpoints`
--

INSERT INTO client_endpoints(is_activate, clients_profile_uuid, auth_claim_value, endpoint_path, endpoint_regex, method_type, offline_authentication, service_name, is_terminate, uuid, version_number)
VALUES (0, 'AZ87-6563-XUJH-00001','uma_authorization','/cards','/cards','GET',1,'omni-ops-customer-dashboard:8000',0,'fa5f27c1-64dd-4a3b-b8a0-fcd70c815270',1);
INSERT INTO client_endpoints(is_activate, clients_profile_uuid, auth_claim_value, endpoint_path, endpoint_regex, method_type, offline_authentication, service_name, is_terminate, uuid, version_number)
VALUES (0, 'AZ87-6563-XUJH-00001','uma_authorization','/cards/stats','/cards/stats','GET',1,'omni-ops-customer-dashboard:8000',0,'a0ca1311-9afa-4727-8bef-aa121ce79fbc',1);
INSERT INTO client_endpoints(is_activate, clients_profile_uuid, auth_claim_value, endpoint_path, endpoint_regex, method_type, offline_authentication, service_name, is_terminate, uuid, version_number)
VALUES (0, 'AZ87-6563-XUJH-00001','uma_authorization','/cards/analytics','/cards/analytics','GET',1,'omni-ops-customer-dashboard:8000',0,'78ffbb17-868d-4a82-9a00-62496754ae64',1);
INSERT INTO client_endpoints(is_activate, clients_profile_uuid, auth_claim_value, endpoint_path, endpoint_regex, method_type, offline_authentication, service_name, is_terminate, uuid, version_number)
VALUES (0, 'AZ87-6563-XUJH-00001','uma_authorization','/cards/lookups','/cards/lookups','GET',1,'omni-ops-customer-dashboard:8000',0,'5edc719b-42b6-40cc-8587-b3fd42633fdb',1);
