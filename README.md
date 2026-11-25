# Pokédex — React + Spring Boot + MongoDB

Atividade 04 de Desenvolvimento e Integração de Aplicações Web (PUC Minas): a Pokédex da Atividade 03 agora consome uma **API REST em Spring Boot** que persiste os dados no **MongoDB**.

```
Script_Pokedex.sql → MongoDB → Spring Boot REST API → React + Vite
```

## Estrutura

```
pokedex/
├── src/, package.json, ...   # Frontend React + Vite (Atividade 03 integrada à API)
├── backend/                  # API Spring Boot (Web + Data MongoDB)
│   └── src/main/resources/seed/   # CSVs gerados a partir do Script_Pokedex.sql
├── scripts/gerar_seed.py     # Converte o Script_Pokedex.sql nos CSVs de seed
├── Script_Pokedex.sql        # Fonte dos dados
└── README.md
```

## Pré-requisitos

- Java 25 (conforme `backend/pom.xml`; o Maven vem pelo `mvnw`)
- Node.js 20+
- Um banco MongoDB: **MongoDB Atlas** (recomendado) ou MongoDB local

## 1. Configurar o MongoDB

### Opção A — MongoDB Atlas

1. Crie um cluster gratuito em <https://www.mongodb.com/atlas>.
2. Em **Database Access**, crie um usuário e senha.
3. Em **Network Access**, libere o seu IP (ou `0.0.0.0/0` para testes).
4. Em **Connect → Drivers**, copie a string de conexão e **inclua o nome do banco** antes do `?`:

   ```
   mongodb+srv://USUARIO:SENHA@cluster0.xxxxx.mongodb.net/pokedex?retryWrites=true&w=majority
   ```

5. Informe a URI ao backend **sem commitar a senha**, de uma das formas:

   - **Variável de ambiente** `MONGODB_URI`
     - Linux/macOS: `export MONGODB_URI="mongodb+srv://..."`
     - Windows (PowerShell): `$env:MONGODB_URI="mongodb+srv://..."`
   - **Perfil local**: copie `backend/src/main/resources/application-local.properties.example` para `application-local.properties` (já está no `.gitignore`), preencha a URI e rode com `-Dspring-boot.run.profiles=local`.

### Opção B — MongoDB local

Com o MongoDB rodando em `localhost:27017`, não é preciso configurar nada: o padrão é `mongodb://localhost:27017/pokedex`.

### Carga dos dados

Na primeira execução, o backend popula as coleções `pokemons` (151), `types` (18) e `trainers` (12) automaticamente, **só se estiverem vazias**. Os dados vêm de `backend/src/main/resources/seed/*.csv`, gerados a partir do `Script_Pokedex.sql`. Para regerar os CSVs:

```bash
python scripts/gerar_seed.py
```

## 2. Rodar o backend

```bash
cd backend
./mvnw spring-boot:run          # Windows: mvnw.cmd spring-boot:run
# com perfil local:  ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

A API sobe em <http://localhost:8080>. O CORS libera `http://localhost:5173`; para outra origem, use a variável `CORS_ORIGINS`.

## 3. Rodar o frontend

```bash
npm install
npm run dev
```

Abra <http://localhost:5173>. Se o backend estiver em outro endereço, copie `.env.example` para `.env.local` e ajuste `VITE_API_URL`.

## API REST

| Método   | Endpoint             | Descrição                                           |
| -------- | -------------------- | --------------------------------------------------- |
| `GET`    | `/api/pokemons`      | Lista todos (filtros opcionais `?name=char&type=Fire`) |
| `GET`    | `/api/pokemons/{id}` | Busca um Pokémon pelo ID                            |
| `POST`   | `/api/pokemons`      | Cadastra um Pokémon (o `id` é gerado se omitido)    |
| `PUT`    | `/api/pokemons/{id}` | Atualiza um Pokémon                                 |
| `DELETE` | `/api/pokemons/{id}` | Remove um Pokémon                                   |
| `GET`    | `/api/types`         | Lista os tipos                                      |
| `GET`    | `/api/trainers`      | Lista os treinadores (com os `pokemonIds`)          |
| `GET`    | `/api/trainers/{id}` | Busca um treinador                                  |

Exemplo de documento (`pokemons`):

```json
{
  "id": 25,
  "name": "Pikachu",
  "hp": 35,
  "attack": 55,
  "defense": 30,
  "specialAttack": 50,
  "specialDefense": 40,
  "speed": 90,
  "types": ["Electric"]
}
```

Exemplo de cadastro:

```bash
curl -X POST http://localhost:8080/api/pokemons \
  -H "Content-Type: application/json" \
  -d '{"name":"Teste","hp":50,"attack":50,"defense":50,"specialAttack":50,"specialDefense":50,"speed":50,"types":["Fire"]}'
```

Validações: nome obrigatório, stats entre 0 e 255 e no máximo 2 tipos (resposta `400`); `404` para id inexistente; `409` se o `id` informado no POST já existir.

## Funcionalidades do frontend

- Listagem dos Pokémon vindos da API
- Pesquisa por nome e filtro por tipo
- Detalhes do Pokémon (modal com barras de atributos)
- Cadastro, edição e exclusão (persistidos no MongoDB)

## Desafios opcionais implementados

- Consulta por tipo e busca por nome pela API (`?type=` e `?name=`)
- Treinadores persistidos no MongoDB, com relação aos Pokémon (`pokemonIds`)

## Realizadores

- Miguel Xavier
