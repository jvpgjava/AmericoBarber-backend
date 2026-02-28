-- Barbeiros passam a ser usuários com role ADMIN e flag is_barber.
-- Cada admin barbeiro vê apenas seus clientes, agendamentos, serviços e disponibilidade.

ALTER TABLE users ADD COLUMN IF NOT EXISTS is_barber BOOLEAN NOT NULL DEFAULT false;
COMMENT ON COLUMN users.is_barber IS 'Quando true, o usuário ADMIN atua como barbeiro (vê só seus clientes/serviços/agendamentos).';

UPDATE users SET role = 'ROLE_ADMIN', is_barber = true WHERE role = 'ROLE_BARBER';

CREATE INDEX idx_user_is_barber ON users (is_barber) WHERE is_barber = true;
