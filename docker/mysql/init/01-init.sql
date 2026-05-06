-- ================================================
-- StallMart Docker MySQL bootstrap
-- ================================================
-- Business schema and seed data are managed by Flyway migrations under:
-- server/src/main/resources/db/migration/
--
-- Keep this file intentionally lightweight. Creating business tables here
-- makes the schema non-empty before Flyway starts and prevents migration.
-- ================================================

SELECT 'StallMart database is ready; Flyway owns schema migrations.' AS message;
