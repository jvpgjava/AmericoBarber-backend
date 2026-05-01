-- Cancellation penalties table
CREATE TABLE IF NOT EXISTS cancellation_penalties (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL REFERENCES users(id),
    appointment_id BIGINT NOT NULL REFERENCES appointments(id),
    amount NUMERIC(10, 2) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    proof_image_data TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    reviewed_at TIMESTAMPTZ,
    reviewed_by BIGINT REFERENCES users(id)
);

CREATE INDEX IF NOT EXISTS idx_penalty_client ON cancellation_penalties(client_id);
CREATE INDEX IF NOT EXISTS idx_penalty_status ON cancellation_penalties(status);
