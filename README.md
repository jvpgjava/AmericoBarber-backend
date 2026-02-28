# Américo Barber - Backend

API REST em Java 21 + Kotlin (controllers), Spring Boot 3, PostgreSQL, JWT, Flyway.

## Stack

- Java 21, Kotlin (apenas controllers)
- Spring Boot 3, Web, Data JPA, Security
- PostgreSQL, Flyway
- JWT (jjwt), BCrypt, Swagger/OpenAPI
- Lombok, MapStruct, Maven

## Estrutura

- `config`: Security, OpenAPI, Web (CORS)
- `controller` (Kotlin): Auth, Admin, Client, Barber
- `service` / `service.impl` (Java): regras de negócio
- `repository`, `entity`, `dto`, `mapper`, `security`, `exception`, `enums`, `util`

## Roles

- **ROLE_ADMIN**: painel admin (criar barbeiros, serviços, gerenciar usuários e agendamentos). Usuários com `isBarber=true` são barbeiros: cada um acessa o painel do barbeiro e vê apenas seus clientes, agendamentos, serviços e disponibilidade.
- **ROLE_CLIENT**: cadastro público; perfil, agendar, meus agendamentos, histórico.

Não existe mais ROLE_BARBER; barbeiros são admins com a flag `is_barber` no banco. Não há dados iniciais (seed); usuários são criados pelo registro (cliente) ou pelo painel admin / inserção manual (admin/barbeiro).

## Executar

```bash
# PostgreSQL em 5432, DB: americobarber, user/pass: postgres
mvn spring-boot:run
```

Ou com Docker:

```bash
docker-compose up -d
```

API: http://localhost:6060  
Swagger: http://localhost:6060/swagger-ui.html  
**Postman:** importe o arquivo `AmericoBarber-Postman.json` na raiz do projeto para testar todos os endpoints (variável `baseUrl` = http://localhost:6060; após Login, defina a variável `token` com o JWT retornado).

## Variáveis de ambiente

- DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD
- JWT_SECRET (mín. 32 caracteres)
- app.cors.allowed-origins (opcional, default http://localhost:5173)
