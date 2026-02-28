-- Observação (cliente) e proposta de reagendamento (barbeiro)
ALTER TABLE appointments ADD COLUMN observation TEXT NULL;
ALTER TABLE appointments ADD COLUMN barber_message TEXT NULL;
ALTER TABLE appointments ADD COLUMN proposed_date DATE NULL;
ALTER TABLE appointments ADD COLUMN proposed_start_time TIME NULL;
ALTER TABLE appointments ADD COLUMN proposed_end_time TIME NULL;

COMMENT ON COLUMN appointments.observation IS 'Observação opcional do cliente ao cancelar ou reagendar.';
COMMENT ON COLUMN appointments.barber_message IS 'Mensagem do barbeiro ao propor novo horário (ex.: imprevisto).';
COMMENT ON COLUMN appointments.proposed_date IS 'Data proposta pelo barbeiro para reagendamento.';
COMMENT ON COLUMN appointments.proposed_start_time IS 'Hora início proposta pelo barbeiro.';
COMMENT ON COLUMN appointments.proposed_end_time IS 'Hora fim proposta pelo barbeiro.';

-- Novos status: CANCELADO_POR_BARBEIRO, CANCELADO_POR_CLIENTE, PROPOSTA_REAGENDAMENTO
ALTER TABLE appointments ALTER COLUMN status TYPE VARCHAR(30);