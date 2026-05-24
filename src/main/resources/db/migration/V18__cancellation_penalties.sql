CREATE TABLE cancellation_penalties (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL REFERENCES users(id),
    appointment_id BIGINT NOT NULL REFERENCES appointments(id),
    amount NUMERIC(10, 2) NOT NULL,
    status VARCHAR(30) NOT NULL,
    proof_image TEXT,
    admin_notes TEXT,
    reviewed_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    proof_submitted_at TIMESTAMP,
    reviewed_at TIMESTAMP
);

CREATE INDEX idx_cancellation_penalties_client ON cancellation_penalties (client_id);
CREATE INDEX idx_cancellation_penalties_status ON cancellation_penalties (status);
CREATE INDEX idx_cancellation_penalties_appointment ON cancellation_penalties (appointment_id);

COMMENT ON TABLE cancellation_penalties IS 'Penalidades por cancelamento tardio (menos de 12h antes do horario).';
COMMENT ON COLUMN cancellation_penalties.status IS 'PENDING_PAYMENT, PROOF_SUBMITTED, APPROVED, REJECTED';
