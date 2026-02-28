CREATE TABLE appointments (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    barber_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_appointment_client FOREIGN KEY (client_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_appointment_barber FOREIGN KEY (barber_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_appointment_service FOREIGN KEY (service_id) REFERENCES services (id) ON DELETE CASCADE,
    CONSTRAINT chk_appointment_times CHECK (end_time > start_time)
);

CREATE INDEX idx_appointment_client ON appointments (client_id);
CREATE INDEX idx_appointment_barber ON appointments (barber_id);
CREATE INDEX idx_appointment_date ON appointments (date);
CREATE INDEX idx_appointment_status ON appointments (status);
CREATE INDEX idx_appointment_barber_date ON appointments (barber_id, date, start_time);
