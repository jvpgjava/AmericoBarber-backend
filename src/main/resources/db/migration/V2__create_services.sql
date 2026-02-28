CREATE TABLE services (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price NUMERIC(10, 2) NOT NULL,
    duration_minutes INTEGER,
    description VARCHAR(1000),
    active BOOLEAN NOT NULL DEFAULT true,
    barber_id BIGINT NOT NULL,
    CONSTRAINT fk_service_barber FOREIGN KEY (barber_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT chk_service_price_positive CHECK (price > 0)
);

CREATE INDEX idx_service_barber ON services (barber_id);
CREATE INDEX idx_service_active ON services (active);
