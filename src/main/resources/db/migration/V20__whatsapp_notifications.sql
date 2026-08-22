ALTER TABLE appointments ADD COLUMN confirmation_sent_at TIMESTAMP;
ALTER TABLE appointments ADD COLUMN reminder_sent_at TIMESTAMP;

COMMENT ON COLUMN appointments.confirmation_sent_at IS 'Timestamp da confirmação enviada por WhatsApp ao criar o agendamento';
COMMENT ON COLUMN appointments.reminder_sent_at IS 'Timestamp do lembrete enviado por WhatsApp antes do agendamento';

CREATE TABLE notification_settings (
    id BIGINT PRIMARY KEY DEFAULT 1,
    confirmation_enabled BOOLEAN NOT NULL DEFAULT true,
    reminder_enabled BOOLEAN NOT NULL DEFAULT true,
    reminder_minutes_before INTEGER NOT NULL DEFAULT 30,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_notification_settings_single_row CHECK (id = 1)
);

INSERT INTO notification_settings (id, confirmation_enabled, reminder_enabled, reminder_minutes_before)
VALUES (1, true, true, 30);
