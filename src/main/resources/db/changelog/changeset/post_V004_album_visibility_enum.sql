ALTER TABLE album
    DROP COLUMN visibility;

ALTER TABLE album
    ADD COLUMN visibility SMALLINT NOT NULL DEFAULT 0;