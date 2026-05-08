# MAP – Meu Avatar Preventivo

Care Plus Challenge – SOA e WebServices Sprint 3

---

## Integrantes

* **Gilson Dias Ramos Junior - RM552345**
* **Jeferson Gabriel de Mendonça - RM553149**
* **Larissa Estella Gonçalves dos Santos - RM552695**

---

## Descrição do Projeto

O MAP é uma API REST construída em Spring Boot que implementa um módulo de gamificação para o aplicativo da operadora de saúde Care Plus.

Cada usuário possui um **avatar digital** que evolui conforme ele completa **missões de prevenção**, ações de autocuidado como registrar horas de sono, beber água, agendar check-ups e meditar. O avatar reflete **hábitos e comportamentos preventivos** do usuário.


---

## Tecnologias Utilizadas

| Tecnologia | Versão | Papel |
|---|---|---|
| Java | 17 | Linguagem principal |
| Spring Boot | 3.2.5 | Framework web/REST |
| Spring Data JPA | 3.2.5 | Persistência ORM |
| Hibernate | 6.x | Implementação JPA |
| Flyway | 9.x | Migrações de banco |
| H2 Database | 2.x | Banco em memória (dev) |
| PostgreSQL | 16 | Banco de produção |
| Lombok | 1.18.x | Redução de boilerplate |
| SpringDoc OpenAPI | 2.5.0 | Documentação Swagger UI |
| Maven | 3.9.x | Build e dependências |

---

## Configuração e Execução

### Pré-requisitos

- Java 17+
- Maven 3.8+


1. Clone o repositório

```bash

git clone https://github.com/larissaestella/SOA_e_WebServices-Sprint_3.git
cd SOA_e_WebServices-Sprint_3

```

2. Execute com Maven (usa H2 em memória por padrão)

```bash
mvn spring-boot:run

```

3. Acesse a documentação

-  Swagger UI:  http://localhost:8080/swagger-ui.html
- H2 Console:  http://localhost:8080/h2-console
  - JDBC URL:  jdbc:h2:mem:mapdb
  - User: sa   Password: (vazio)


---

## Endpoints

### Usuários — `/api/v1/usuarios`

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/v1/usuarios` | Cadastrar usuário + avatar |
| `GET` | `/api/v1/usuarios` | Listar todos |
| `GET` | `/api/v1/usuarios/{id}` | Buscar por ID |
| `PUT` | `/api/v1/usuarios/{id}` | Atualizar dados |
| `DELETE` | `/api/v1/usuarios/{id}` | Remover usuário |

### Missões — `/api/v1/missoes`

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/v1/missoes` | Criar missão |
| `GET` | `/api/v1/missoes` | Listar ativas (filtro: `?categoria=SAUDE`) |
| `GET` | `/api/v1/missoes/{id}` | Buscar por ID |
| `PUT` | `/api/v1/missoes/{id}` | Atualizar missão |
| `DELETE` | `/api/v1/missoes/{id}` | Desativar missão (soft delete) |

### Avatar & Gamificação

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/api/v1/usuarios/{id}/avatar` | Consultar estado do avatar |
| `POST` | `/api/v1/usuarios/{uId}/missoes/{mId}/completar` | Completar missão |
| `GET` | `/api/v1/usuarios/{id}/estatisticas` | Estatísticas de progresso |
| `GET` | `/api/v1/ranking` | Ranking global |

---

## Exemplos de Requisições e Respostas

### 1. Gestão de Usuários (`UsuarioController`)

**Criar Usuário (POST)**

```http
POST /api/v1/usuarios
Content-Type: application/json

{
  "nome": "Carlos Almeida",
  "email": "carlos@email.com",
  "dataNascimento": "1995-08-22",
  "nomeAvatar": "CarlosHero"
}

```

**Listar Todos os Usuários (GET)**

```http
GET /api/v1/usuarios

```

**Buscar Usuário por ID (GET)**

```http
GET /api/v1/usuarios/1

```

**Atualizar Usuário (PUT)**
*(Nota: O nome do avatar não muda aqui, apenas os dados pessoais)*

```http
PUT /api/v1/usuarios/1
Content-Type: application/json

{
  "nome": "Carlos A. Editado",
  "email": "carlos.novo@email.com",
  "dataNascimento": "1995-08-22"
}

```

**Excluir Usuário (DELETE)**
*(Nota: Irá excluir em cascata o Avatar e o histórico de missões do usuário)*

```http
DELETE /api/v1/usuarios/1

```

---

### 2. Gestão de Missões (`MissaoController`)

**Criar Missão (POST)**

```http
POST /api/v1/missoes
Content-Type: application/json

{
  "titulo": "Yoga Matinal",
  "descricao": "Realize 15 minutos de Yoga ao acordar.",
  "categoria": "BEM_ESTAR",
  "pontosRecompensa": 15,
  "bonusSaude": 0,
  "bonusHidratacao": 0,
  "bonusSono": 0,
  "bonusExercicio": 5,
  "bonusBemEstar": 25
}

```

**Listar Todas as Missões (GET)**

```http
GET /api/v1/missoes

```

**Listar Missões por Categoria (GET com Query Param)**

```http
GET /api/v1/missoes?categoria=SAUDE

```

**Buscar Missão por ID (GET)**

```http
GET /api/v1/missoes/1

```

**Atualizar Missão (PUT)**

```http
PUT /api/v1/missoes/1
Content-Type: application/json

{
  "titulo": "Yoga Matinal Avançada",
  "descricao": "Realize 30 minutos de Yoga ao acordar.",
  "categoria": "BEM_ESTAR",
  "pontosRecompensa": 30,
  "bonusSaude": 0,
  "bonusHidratacao": 0,
  "bonusSono": 0,
  "bonusExercicio": 10,
  "bonusBemEstar": 40
}

```

**Desativar Missão - Soft Delete (DELETE)**

```http
DELETE /api/v1/missoes/1

```

---

### 3. Avatar e Gamificação (`AvatarController`)

**Consultar Estado do Avatar (GET)**

```http
GET /api/v1/usuarios/1/avatar

```

**Completar uma Missão (POST)**
*(Nota: O ID do usuário e o ID da missão vão na URL)*

```http
POST /api/v1/usuarios/1/missoes/4/completar
Content-Type: application/json

{
  "observacao": "Sensação maravilhosa após finalizar a missão!"
}

```

**Consultar Estatísticas do Usuário (GET)**

```http
GET /api/v1/usuarios/1/estatisticas

```

**Consultar Ranking Global (GET)**

```http
GET /api/v1/ranking

```