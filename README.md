# Raízes do Nordeste — API REST

**Aluno:** Miguel de Matos Cavalcante Leite  
**RU:** 4756857  
**Disciplina:** Projeto Back-End  

---

## Sobre o Projeto

API REST desenvolvida em **Java 17 + Spring Boot** para gerenciamento de pedidos da rede de restaurantes **Raízes do Nordeste**. O sistema suporta múltiplos canais de venda (App, Totem, Balcão, Web) e implementa controle de acesso baseado em perfis (RBAC).

## Tecnologias

- Java 17
- Spring Boot 3.5.0
- Spring Data JPA + Hibernate
- Spring Security (HTTP Basic + BCrypt)
- PostgreSQL
- Swagger/OpenAPI (springdoc)
- Maven

## Pré-requisitos

- Java 17+
- PostgreSQL instalado e rodando
- Maven 3.8+

## Configuração

1. Crie o banco de dados:
```sql
CREATE DATABASE raizes_nordeste;
```

2. Configure as variáveis de ambiente (ou edite `application.properties`):
```
DB_URL=jdbc:postgresql://localhost:5432/raizes_nordeste
DB_USER=postgres
DB_PASSWORD=sua_senha
```

3. Execute o projeto:
```bash
mvn spring-boot:run
```

4. Acesse o Swagger:
```
http://localhost:8080/swagger-ui/index.html
```

## Autenticação

A API utiliza **HTTP Basic Authentication**. Na primeira execução, é criado automaticamente o usuário admin:

| Email | Senha | Perfil |
|---|---|---|
| admin@raizesnordeste.com | admin123 | ADMIN |

## Perfis de Acesso

| Perfil | Permissões |
|---|---|
| **ADMIN** | Acesso total |
| **GERENTE** | Gestão de usuários, produtos e pedidos |
| **ATENDENTE** | Consulta e atualização de pedidos e produtos |
| **CLIENTE** | Visualiza cardápio, faz pedidos e pagamentos |

## Endpoints

### Usuário (`/usuario`)
| Método | Rota | Descrição |
|---|---|---|
| POST | `/usuario/registrar` | Registra novo usuário |
| GET | `/usuario/{id}` | Busca usuário por ID |
| DELETE | `/usuario/{id}` | Remove usuário |

### Produto (`/produto`)
| Método | Rota | Descrição |
|---|---|---|
| POST | `/produto/criar` | Cadastra produto |
| GET | `/produto/listar` | Lista todos os produtos |
| GET | `/produto/{id}` | Busca produto por ID |

### Pedido (`/pedido`)
| Método | Rota | Descrição |
|---|---|---|
| POST | `/pedido/criar` | Cria um pedido |
| GET | `/pedido/listar` | Lista pedidos (filtro: `?canalPedido=TOTEM`) |
| GET | `/pedido/{id}` | Consulta pedido |
| PATCH | `/pedido/{id}/preparar` | Status → EM_PREPARO |
| PATCH | `/pedido/{id}/pronto` | Status → AGUARDANDO_ENTREGADOR |
| PATCH | `/pedido/{id}/entregar` | Status → SAIU_PARA_ENTREGA |
| PATCH | `/pedido/{id}/finalizar` | Status → ENTREGUE |
| PATCH | `/pedido/{id}/cancelar` | Cancela pedido e devolve estoque |

### Pagamento (`/pagamento`)
| Método | Rota | Descrição |
|---|---|---|
| POST | `/pagamento/{pedidoId}/pagar` | Efetua pagamento do pedido |

## Fluxo do Pedido

```
AGUARDANDO_PAGAMENTO → PAGO → EM_PREPARO → AGUARDANDO_ENTREGADOR → SAIU_PARA_ENTREGA → ENTREGUE
                ↓         ↓        ↓                ↓                      ↓
             CANCELADO  CANCELADO CANCELADO      CANCELADO             CANCELADO
```

## Canais de Venda

- `APP` — Aplicativo mobile
- `TOTEM` — Totem de autoatendimento
- `BALCAO` — Atendimento presencial
- `WEB` — Plataforma web

## Regras de Negócio

- O estoque é **decrementado automaticamente** ao criar um pedido
- Ao **cancelar** um pedido, o estoque é **devolvido**
- Pedidos **já entregues** não podem ser cancelados
- A transição de status segue ordem sequencial obrigatória
- Email de usuário é **único** — tentativa de duplicação retorna 409
- A senha do usuário **nunca é retornada** nas respostas da API (LGPD)

## Formas de Pagamento

- `PIX`
- `CARTAO_CREDITO`
- `CARTAO_DEBITO`
- `DINHEIRO`

## Estrutura do Projeto

```
com.miguel.raizesnordeste
├── config/          → Configurações (Security, Swagger, Admin seed)
├── controller/      → Endpoints REST
├── dto/             → Objetos de requisição
├── model/           → Entidades JPA e Enums
├── repository/      → Acesso ao banco de dados
├── security/        → Autenticação customizada
└── service/         → Regras de negócio
```
