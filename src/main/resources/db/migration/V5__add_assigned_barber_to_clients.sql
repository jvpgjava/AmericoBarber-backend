-- Cliente vinculado a um barbeiro (cada barbeiro tem seus clientes)
ALTER TABLE users ADD COLUMN assigned_barber_id BIGINT NULL;
ALTER TABLE users ADD CONSTRAINT fk_user_assigned_barber
    FOREIGN KEY (assigned_barber_id) REFERENCES users (id) ON DELETE SET NULL;
CREATE INDEX idx_user_assigned_barber ON users (assigned_barber_id);

COMMENT ON COLUMN users.assigned_barber_id IS 'Para role CLIENT: barbeiro ao qual o cliente está vinculado. Apenas esse barbeiro atende o cliente.';
