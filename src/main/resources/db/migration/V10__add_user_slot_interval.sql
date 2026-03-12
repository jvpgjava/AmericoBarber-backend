-- Migration to add slot_interval_minutes to users table
ALTER TABLE users ADD COLUMN slot_interval_minutes INTEGER DEFAULT 30;
COMMENT ON COLUMN users.slot_interval_minutes IS 'Intervalo de tempo entre agendamentos em minutos para o barbeiro.';
