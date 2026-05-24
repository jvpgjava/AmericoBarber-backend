CREATE TABLE business_settings (
    id BIGINT PRIMARY KEY DEFAULT 1,
    pix_key VARCHAR(255) NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_business_settings_single_row CHECK (id = 1)
);

INSERT INTO business_settings (id, pix_key) VALUES (1, '89c0800d-0bf4-426b-a9f1-39bea0acdcea');
