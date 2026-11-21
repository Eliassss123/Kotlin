CREATE DATABASE IF NOT EXISTS induztek_roles CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS induztek_catalog CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS induztek_operations CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
GRANT ALL PRIVILEGES ON induztek_roles.* TO 'induztek_app'@'%';
GRANT ALL PRIVILEGES ON induztek_catalog.* TO 'induztek_app'@'%';
GRANT ALL PRIVILEGES ON induztek_operations.* TO 'induztek_app'@'%';
