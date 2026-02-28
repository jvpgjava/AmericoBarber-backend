CREATE TABLE availability (
    id BIGSERIAL PRIMARY KEY,
    barber_id BIGINT NOT NULL,
    day_of_week INTEGER NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    CONSTRAINT fk_availability_barber FOREIGN KEY (barber_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT chk_day_of_week CHECK (day_of_week >= 1 AND day_of_week <= 7),
    CONSTRAINT chk_availability_times CHECK (end_time > start_time)
);

CREATE INDEX idx_availability_barber ON availability (barber_id);
CREATE INDEX idx_availability_barber_day ON availability (barber_id, day_of_week);
