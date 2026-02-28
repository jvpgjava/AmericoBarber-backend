-- Senha de todos: 123456 (hash BCrypt)
-- Conecte no banco americobarber e execute (psql ou DBeaver, etc.)

INSERT INTO users (name, email, cpf, phone, password, role, active, created_at) VALUES
(
  'Admin Américo',
  'admin@americobarber.com',
  '11111111111',
  '11999990001',
  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
  'ROLE_ADMIN',
  true,
  CURRENT_TIMESTAMP
),
(
  'João Cliente',
  'cliente@email.com',
  '22222222222',
  '11999990002',
  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
  'ROLE_CLIENT',
  true,
  CURRENT_TIMESTAMP
),
(
  'Carlos Barbeiro',
  'barbeiro@americobarber.com',
  '33333333333',
  '11999990003',
  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
  'ROLE_BARBER',
  true,
  CURRENT_TIMESTAMP
);
