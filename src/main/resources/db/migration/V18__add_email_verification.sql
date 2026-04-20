ALTER TABLE users
ADD COLUMN email_verified BOOLEAN DEFAULT FALSE;

-- Se houver usuários existentes, marcamos eles como verificados para eles não perderem o acesso
UPDATE users SET email_verified = TRUE;

CREATE TABLE confirmation_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
