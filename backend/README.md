# Dom Pagamentos — Backend

Infraestrutura de pagamentos com **arquitetura hexagonal**, **Spring Boot 3** e **Apache Camel**.

## Arquitetura

```
src/main/java/br/com/dompagamentos/
├── domain/                  ← Núcleo: entidades, regras, exceções (zero dependências)
│   ├── model/               ← Payment, Merchant + enums
│   ├── service/             ← PspRoutingService (regra de roteamento Asaas/iugu)
│   └── exception/           ← DomainException, PaymentNotFoundException...
│
├── application/             ← Orquestração: ports + use cases
│   ├── ports/input/         ← Primary ports (interfaces chamadas pelos adapters de entrada)
│   ├── ports/output/        ← Secondary ports (interfaces para infra: DB, PSP)
│   └── usecase/             ← ProcessPaymentUseCase, CreateMerchantUseCase...
│
└── infrastructure/          ← Adapters: Spring, JPA, REST, Camel, Asaas
    ├── adapters/input/
    │   ├── rest/             ← Controllers + DTOs + GlobalExceptionHandler
    │   └── camel/            ← Rotas Camel (webhook processing + retry/DLQ)
    ├── adapters/output/
    │   ├── gateway/          ← AsaasGatewayAdapter (HTTP → Asaas API)
    │   └── persistence/      ← JPA adapters + entities + repositories
    └── config/               ← SecurityConfig, OpenApiConfig, InfrastructureConfig
```

## Tecnologias

| Tecnologia | Versão | Uso |
|---|---|---|
| Spring Boot | 3.2.3 | Framework base |
| Apache Camel | 4.4.0 | Rotas de integração, retry, DLQ |
| Spring Security | 6.x | Autenticação JWT stateless |
| Spring Data JPA | 3.x | Persistência |
| Flyway | 10.x | Migrations de banco |
| H2 | — | Banco em memória (dev) |
| PostgreSQL | 16+ | Banco em produção |
| SpringDoc OpenAPI | 2.3 | Swagger UI |
| JUnit 5 + Mockito | — | Testes |

## Como rodar

```bash
# Dev com H2 em memória
./mvnw spring-boot:run

# Acessar Swagger
http://localhost:8080/swagger-ui.html

# H2 Console
http://localhost:8080/h2-console
```

## Variáveis de ambiente

```env
ASAAS_API_KEY=sua_chave_asaas_sandbox
ASAAS_WEBHOOK_TOKEN=token_para_validar_webhooks
DB_URL=jdbc:postgresql://localhost:5432/dompagamentos
DB_USERNAME=postgres
DB_PASSWORD=senha
JWT_SECRET=sua-chave-secreta-minimo-256-bits
```

## Endpoints principais

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/v1/merchants` | Cadastrar merchant |
| `POST` | `/api/v1/payments` | Criar cobrança (Pix, boleto, cartão) |
| `GET` | `/api/v1/payments/{id}` | Buscar cobrança |
| `GET` | `/api/v1/payments/merchant/{id}` | Listar cobranças do merchant |
| `POST` | `/api/v1/webhooks/asaas` | Receptor de webhooks Asaas |

## Regra de roteamento PSP

```
Volume mensal do merchant < R$ 500k  →  ASAAS  (zero mensalidade)
Volume mensal do merchant ≥ R$ 500k  →  iugu   (MDR negociado)
```

Configurável via `psp.routing.iugu-threshold-cents` no `application.yml`.
