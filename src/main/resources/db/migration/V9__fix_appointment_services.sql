-- Migration to fix the relationship between appointments and services
CREATE TABLE appointment_services (
    appointment_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    PRIMARY KEY (appointment_id, service_id),
    CONSTRAINT fk_apt_serv_appointment FOREIGN KEY (appointment_id) REFERENCES appointments (id) ON DELETE CASCADE,
    CONSTRAINT fk_apt_serv_service FOREIGN KEY (service_id) REFERENCES services (id) ON DELETE CASCADE
);

-- Copy existing service_id to the new join table
INSERT INTO appointment_services (appointment_id, service_id)
SELECT id, service_id FROM appointments;

-- Add missing columns to appointments
ALTER TABLE appointments ADD COLUMN total_price NUMERIC(10, 2);

-- Remove old service_id column (now many-to-many)
ALTER TABLE appointments DROP COLUMN service_id;
