-- DROP FUNCTION IF EXISTS trigger_set_timestamp;
-- CREATE OR REPLACE FUNCTION trigger_set_timestamp()
--     RETURNS TRIGGER LANGUAGE plpgsql AS
-- $$BEGIN
--     NEW.last_modified = NOW();
--     RETURN NEW;
-- END$$;

DROP TABLE IF EXISTS contact;
CREATE TABLE IF NOT EXISTS contact
(
    id             SERIAL PRIMARY KEY,
    version        INTEGER,
    first_name     VARCHAR(255) NOT NULL,
    last_name      VARCHAR(255) NOT NULL,
    email_address  VARCHAR(255) UNIQUE,
    phone_number   VARCHAR(255),
    personal_notes TEXT,
    last_modified  TIMESTAMP DEFAULT NOW(),
    group_name     VARCHAR(20) NOT NULL
);

DROP TRIGGER IF EXISTS set_timestamp ON contact;
CREATE TRIGGER set_timestamp
    BEFORE UPDATE
    ON contact
    FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();
