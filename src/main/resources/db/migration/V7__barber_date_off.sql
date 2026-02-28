-- Dias específicos em que o barbeiro não atende (fora do calendário de disponibilidade semanal)
CREATE TABLE barber_date_off (
    id BIGSERIAL PRIMARY KEY,
    barber_id BIGINT NOT NULL,
    date_off DATE NOT NULL,
    CONSTRAINT fk_barber_date_off_barber FOREIGN KEY (barber_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT uk_barber_date_off UNIQUE (barber_id, date_off)
);

CREATE INDEX idx_barber_date_off_barber ON barber_date_off (barber_id);
CREATE INDEX idx_barber_date_off_date ON barber_date_off (date_off);