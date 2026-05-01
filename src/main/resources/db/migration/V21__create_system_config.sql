-- System config key-value table
CREATE TABLE IF NOT EXISTS system_config (
    config_key VARCHAR(100) PRIMARY KEY,
    config_value TEXT
);

-- Insert default PIX key
INSERT INTO system_config (config_key, config_value)
VALUES ('PIX_KEY', '89c0800d-0bf4-426b-a9f1-39bea0acdcea')
ON CONFLICT (config_key) DO NOTHING;
