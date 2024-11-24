CREATE TABLE user_short_info (
    user_id BIGINT PRIMARY KEY,
    username VARCHAR(64) NOT NULL,
    file_id VARCHAR(255),
    small_file_id VARCHAR(255),
    saved_date_time TIMESTAMP DEFAULT current_timestamp NOT NULL
);

CREATE OR REPLACE FUNCTION update_saved_date_time()
RETURNS TRIGGER AS $$
BEGIN
    NEW.saved_date_time = current_timestamp;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_saved_date_time
BEFORE UPDATE ON user_short_info
FOR EACH ROW
EXECUTE FUNCTION update_saved_date_time();

CREATE INDEX idx_saved_datetime ON user_short_info (saved_date_time);
