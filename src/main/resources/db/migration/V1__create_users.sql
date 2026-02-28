CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    cpf VARCHAR(14) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_email UNIQUE (email),
    CONSTRAINT uk_user_cpf UNIQUE (cpf),
    CONSTRAINT uk_user_phone UNIQUE (phone)
);

CREATE INDEX idx_user_email ON users (email);
CREATE INDEX idx_user_cpf ON users (cpf);
CREATE INDEX idx_user_phone ON users (phone);
CREATE INDEX idx_user_role ON users (role);
