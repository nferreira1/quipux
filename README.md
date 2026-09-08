# Person API — Quipux

API REST para cadastro de pessoas, com previsão de nacionalidade a partir do nome
usando a API pública [nationalize.io](https://nationalize.io).

## Stack

- Java 21 + Quarkus 3
- PostgreSQL 16
- Hibernate ORM com Panache
- Frontend em HTML, CSS e JavaScript, servido por nginx
- Docker Compose

## Como executar

Requisito único: Docker com Docker Compose.

```bash
cp .env.example .env
docker compose up --build
```

Na primeira execução o Maven baixa as dependências dentro do container, o que
leva alguns minutos. As execuções seguintes reaproveitam o cache.

| Serviço | Endereço |
|---|---|
| Interface web | http://localhost:8080 |
| API | http://localhost:8081 |
| Swagger UI | http://localhost:8081/q/swagger-ui |
| PostgreSQL | localhost:5432 |

Para parar:

```bash
docker compose down
```

Para parar e apagar os dados do banco:

```bash
docker compose down -v
```

## Execução em modo de desenvolvimento

Sobe apenas o banco em container e roda o backend na máquina, com live reload:

```bash
docker compose up -d postgres
cd backend
./mvnw quarkus:dev        # Linux e macOS
mvnw.cmd quarkus:dev      # Windows
```

O frontend pode ser aberto direto pelo arquivo `frontend/index.html`.

## Endpoints

| Método | Rota | Autenticação | Descrição |
|---|---|---|---|
| POST | `/registrarName` | Sim | Cadastra uma pessoa |
| GET | `/list` | Não | Lista as pessoas, com paginação |
| GET | `/list/{cpf}` | Não | Busca uma pessoa pelo CPF |
| DELETE | `/list/{cpf}` | Sim | Exclui uma pessoa |
| GET | `/findNacionalityByPerson/{cpf}` | Não | Retorna o país mais provável |

### Autenticação

As operações de escrita exigem o cabeçalho:

```
X-API-Key: quipux-secret-key
```

O valor é configurável pela variável `API_KEY` no arquivo `.env`. No Swagger UI,
use o botão **Authorize** no topo da página.

### Paginação

`GET /list` aceita `page` (começando em 1) e `size` (entre 1 e 100):

```
GET /list?page=1&size=20
```

A resposta traz os registros em `data` e os metadados de paginação ao lado:

```json
{
  "data": [ ... ],
  "page": 1,
  "size": 20,
  "totalElements": 137,
  "totalPages": 7,
  "hasNext": true
}
```

### Formato de erro

Toda resposta de erro usa a mesma estrutura. O campo `errors` detalha falhas por
campo e vem vazio quando o erro não é de validação.

```json
{
  "message": "Erro de validação nos dados enviados.",
  "errors": [
    { "field": "email", "message": "O e-mail está em um formato inválido." }
  ]
}
```

| Status | Situação |
|---|---|
| 400 | Dados ou parâmetros inválidos |
| 401 | Chave de API ausente ou inválida |
| 404 | Pessoa não encontrada |
| 409 | CPF ou e-mail já cadastrado |
| 503 | API externa de nacionalidade indisponível |

## Validações

O CPF é validado em formato e dígitos verificadores, portanto sequências como
`12345678901` são rejeitadas. Para testes, use um CPF válido como `11144477735`.

E-mails são normalizados para minúsculas antes de gravar, de modo que o mesmo
endereço não pode ser cadastrado duas vezes com caixas diferentes.

## Estrutura

```
.
├── docker-compose.yml
├── .env.example
├── backend/
│   ├── Dockerfile
│   └── src/main/java/org/quipux/
│       ├── clients/        consumo da API de nacionalidade
│       ├── config/         configuração do OpenAPI
│       ├── dtos/           objetos de entrada e saída
│       ├── entities/       mapeamento JPA
│       ├── exceptions/     tratamento padronizado de erros
│       ├── filters/        autenticação por chave de API
│       ├── repositories/   acesso a dados
│       ├── resources/      endpoints REST
│       └── services/       regras de negócio
└── frontend/
    ├── Dockerfile
    ├── nginx.conf
    ├── index.html
    ├── css/
    └── js/
```

## Decisões de projeto

**Camadas separadas.** Os endpoints recebem `PersonRequest` e devolvem
`PersonResponse`, nunca a entidade JPA. Isso evita expor o identificador interno
como campo de entrada e desacopla o contrato da API do modelo de persistência.

**Erros centralizados.** O tratamento fica em `ExceptionMapper`, não espalhado
pelos endpoints, garantindo que qualquer falha saia no mesmo formato.

**Schema gerado pelo Hibernate.** Em um cenário de produção o schema seria
versionado com Flyway; aqui a geração automática foi mantida pela simplicidade.

**Rotas conforme o enunciado.** Os caminhos `/registrarName` e
`/findNacionalityByPerson` seguem exatamente a especificação recebida, incluindo
a grafia original.
