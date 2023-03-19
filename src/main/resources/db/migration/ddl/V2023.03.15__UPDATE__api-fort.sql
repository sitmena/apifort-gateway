ALTER TABLE `apifort_client_profile` ADD COLUMN `title` VARCHAR(100) NULL AFTER `uuid`;
ALTER TABLE `apifort_client_profile` ADD COLUMN `description` VARCHAR(150) NULL AFTER `title`;