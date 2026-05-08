# MAP – Meu Avatar Preventivo

Care Plus Challenge – SOA e WebServices Sprint 3

---

## Integrantes

* **Gilson Dias Ramos Junior - RM552345**
* **Jeferson Gabriel de Mendonça - RM553149**
* **Larissa Estella Gonçalves dos Santos - RM552695**

---

## Descrição do Projeto

O **MAP (Meu Avatar Preventivo)** é uma API REST construída em **Spring Boot** que implementa um módulo de gamificação para o aplicativo da operadora de saúde **Care Plus**.

### Conceito Central

Cada usuário possui um **avatar digital** que evolui conforme ele completa **missões de prevenção**  ações de autocuidado como registrar horas de sono, beber água, agendar check-ups e meditar. O avatar reflete **hábitos e comportamentos preventivos** do usuário.


---

## Arquitetura em Camadas

```
┌─────────────────────────────────────────────┐
│              CLIENTE (HTTP)                  │
│       Postman / Swagger UI / App             │
└──────────────────┬──────────────────────────┘
                   │ REST JSON
┌──────────────────▼──────────────────────────┐
│             CONTROLLER LAYER                 │
│  UsuarioController  MissaoController         │
│  AvatarController                            │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│              SERVICE LAYER                   │
│  UsuarioService  MissaoService               │
│  AvatarService                               │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│            REPOSITORY LAYER                  │
│  UsuarioRepository  AvatarRepository         │
│  MissaoRepository   MissaoCompletadaRepo     │
└──────────────────┬──────────────────────────┘
                   │ JPA / Hibernate
┌──────────────────▼──────────────────────────┐
│              DATABASE (H2 / PostgreSQL)      │
│  usuarios  avatares  missoes                 │
│  missoes_completadas                         │
└─────────────────────────────────────────────┘
```

---

## Modelo de Entidades (ER)

```
USUARIOS ──────────── AVATARES
   │  1                  1
   │
   │ 1
   ├──────── MISSOES_COMPLETADAS ──────── MISSOES
               (usuario_id, missao_id,        │
                completada_em, observacao)     │
                                         CATEGORIA (enum)
                                         SAUDE | HIDRATACAO |
                                         SONO  | EXERCICIO  |
                                         BEM_ESTAR
```

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

### Passos

```bash
# 1. Clone o repositório
git clone https://github.com/seu-usuario/map-api.git
cd map-api

# 2. Execute com Maven (usa H2 em memória por padrão)
mvn spring-boot:run

# 3. Acesse a documentação interativa
# Swagger UI:  http://localhost:8080/swagger-ui.html
# H2 Console:  http://localhost:8080/h2-console
#   JDBC URL:  jdbc:h2:mem:mapdb
#   User: sa   Password: (vazio)
```

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

### Cadastrar Usuário

**Request**
```http
POST /api/v1/usuarios
Content-Type: application/json

{
  "nome": "Ana Souza",
  "email": "ana.souza@email.com",
  "dataNascimento": "1990-05-15",
  "nomeAvatar": "AnaFit"
}
```

**Response 201 Created**
```json
{
  "id": 1,
  "nome": "Ana Souza",
  "email": "ana.souza@email.com",
  "dataNascimento": "1990-05-15",
  "criadoEm": "2025-06-01T10:00:00"
}
```

---

### Completar Missão

**Request**
```http
POST /api/v1/usuarios/1/missoes/4/completar
Content-Type: application/json

{
  "observacao": "Bebi exatamente 2,3 litros hoje!"
}
```

**Response 200 OK**
```json
{
  "id": 1,
  "usuarioId": 1,
  "missaoId": 4,
  "tituloMissao": "2L de Água Hoje",
  "observacao": "Bebi exatamente 2,3 litros hoje!",
  "completadaEm": "2025-06-01T14:30:00",
  "avatarAtualizado": {
    "id": 1,
    "usuarioId": 1,
    "nomeAvatar": "AnaFit",
    "nivel": 1,
    "pontosTotal": 10,
    "energia": 100,
    "hidratacao": 100,
    "focoMental": 100,
    "atualizadoEm": "2025-06-01T14:30:00"
  }
}
```

---

### Consultar Estatísticas

**Request**
```http
GET /api/v1/usuarios/1/estatisticas
```

**Response 200 OK**
```json
{
  "totalMissoesCompletadas": 5,
  "missoesPorCategoria": {
    "HIDRATACAO": 2,
    "EXERCICIO": 2,
    "BEM_ESTAR": 1
  },
  "pontosTotal": 55,
  "nivelAtual": 1,
  "pontosParaProximoNivel": 145
}
```

---

### Resposta de Erro (Validação)

```json
{
  "timestamp": "2025-06-01T10:05:00",
  "status": 400,
  "erro": "Bad Request",
  "mensagem": "Um ou mais campos são inválidos.",
  "campos": {
    "email": "Formato de e-mail inválido",
    "nome": "O nome é obrigatório"
  }
}
```

---

## 🗂️ Estrutura do Projeto

```
map-api/
├── src/main/java/com/careplus/map/
│   ├── MapApplication.java
│   ├── config/
│   │   └── OpenApiConfig.java
│   ├── controller/
│   │   ├── UsuarioController.java
│   │   ├── MissaoController.java
│   │   └── AvatarController.java
│   ├── service/
│   │   ├── UsuarioService.java
│   │   ├── MissaoService.java
│   │   └── AvatarService.java
│   ├── repository/
│   │   ├── UsuarioRepository.java
│   │   ├── AvatarRepository.java
│   │   ├── MissaoRepository.java
│   │   └── MissaoCompletadaRepository.java
│   ├── model/
│   │   ├── entity/
│   │   │   ├── Usuario.java
│   │   │   ├── Avatar.java
│   │   │   ├── Missao.java
│   │   │   └── MissaoCompletada.java
│   │   ├── dto/
│   │   │   ├── UsuarioCadastroDTO.java
│   │   │   ├── UsuarioAtualizacaoDTO.java
│   │   │   ├── UsuarioResponseDTO.java
│   │   │   ├── AvatarResponseDTO.java
│   │   │   ├── MissaoCadastroDTO.java
│   │   │   ├── MissaoResponseDTO.java
│   │   │   ├── CompletarMissaoDTO.java
│   │   │   └── MissaoCompletadaResponseDTO.java
│   │   ├── vo/
│   │   │   ├── RankingVO.java
│   │   │   └── EstatisticasVO.java
│   │   └── enums/
│   │       └── CategoriaMissao.java
│   └── exception/
│       ├── RecursoNaoEncontradoException.java
│       ├── RegraNegocioException.java
│       └── GlobalExceptionHandler.java
├── src/main/resources/
│   ├── application.properties
│   └── db/migration/
│       ├── V1__create_tables.sql
│       └── V2__seed_missoes.sql
│   └── static/
│       ├── index.html
│       ├── script.js
└── pom.xml
```

