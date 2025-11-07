# Relatório da Base de Dados: Projeto "Lane"

-----

## 1\. Introdução

Este documento detalha o desenho, a implementação e as justificações técnicas da base de dados PostgreSQL que serve como fundação para a aplicação móvel "Lane".

O "Lane" é um projeto de aplicação social focado na descoberta, criação e participação em eventos locais. A arquitetura de dados foi desenhada para suportar um sistema social complexo (seguidores e amigos), uma plataforma de gestão de eventos robusta (públicos e privados) e para ser escalável e performante.

As ferramentas utilizadas para o desenvolvimento e gestão desta base de dados foram o **PostgreSQL** e o gestor de base de dados **pgAdmin**.

## 2\. Modelo Conceptual e Lógico (Justificações)

A passagem do modelo conceptual (ideias e mockups) para o modelo físico (tabelas SQL) foi guiada por um conjunto de regras de negócio e requisitos funcionais identificados nos mockups e discussões do projeto.

### 2.1. Lógica Social: A Decisão "Friends" vs. "Followers"

O requisito de negócio principal era: **"Um utilizador só é amigo de outro se se seguirem mutuamente."**

  * **Implementação Inicial:** A BD original continha duas tabelas separadas: `friends` e `followers`.
  * **Problema Identificado:** Manter ambas as tabelas seria redundante. A tabela `friends` armazenaria dados que podiam ser *calculados* a partir da tabela `followers`, levando a um risco elevado de inconsistência de dados (ex: um "unfollow" que não apaga a "amizade").
  * **Solução Adotada:** A tabela `friends` foi **removida** (`DROP TABLE`). A tabela `followers` foi mantida como a **única "fonte da verdade"** para todas as relações sociais. O estado de "amizade" é agora um dado calculado pelo *backend* (API), que corre uma query para encontrar seguidores mútuos (ver Query 3).

### 2.2. Lógica de Eventos: Participantes e Limites

Os mockups da aplicação mostravam claramente a necessidade de gerir participantes e limites de eventos (ex: "127/200 participantes").

  * **Tabela `event_participants`:** Para suportar isto, foi criada uma tabela de junção N-para-N (muitos-para-muitos), `event_participants`. Esta tabela liga `user_details` a `events` e é populada quando um utilizador clica no botão "Participar".
  * **Coluna `max_participants`:** A tabela `events` foi alterada para incluir a coluna `max_participants`, que armazena o limite definido pelo criador.

### 2.3. Integridade de Dados: O Uso de `CONSTRAINTS`

Uma prioridade foi garantir a integridade dos dados ao nível da base de dados, prevenindo a entrada de dados inválidos.

  * **Validação Dupla:** A app (frontend) irá usar *dropdown menus* para campos como `genero` e `event_visibility`. A base de dados implementa uma validação "espelho" através de `CHECK constraints`.
  * **Prevenção de Duplicados:** Foram implementadas `UNIQUE constraints` em todas as tabelas de junção (`followers`, `invitations`, `event_participants`) para impedir relações duplicadas (ex: um utilizador não se pode juntar ao mesmo evento duas vezes).
  * **Lógica:** Foram usadas `CHECK constraints` para regras de negócio, como `no_self_follow` e `no_self_invite`.

## 3\. Modelo Físico (Dicionário de Dados)

Abaixo está a descrição detalhada de cada tabela na sua forma final.

### 3.1. Tabela: `user_details`

**Propósito:** Tabela central que armazena a informação de cada conta de utilizador.

| Coluna | Tipo | PK/FK/Constraint | Descrição |
|---|---|---|---|
| `account_id` | `SERIAL` | `PK` | Identificador único e auto-incrementado. |
| `account_name` | `VARCHAR(100)` | `NOT NULL` | Nome real do utilizador. |
| `account_username` | `VARCHAR(50)` | `NOT NULL`, `UNIQUE` | Nome de utilizador público. `UNIQUE` impede registos duplicados. |
| `account_email` | `VARCHAR(120)` | `NOT NULL`, `UNIQUE` | Email de login. `UNIQUE` impede registos duplicados. |
| `account_password_hash` | `VARCHAR(255)` | `NOT NULL` | A password *hashada* (ex: bcrypt). A password original nunca é guardada. |
| `account_bio` | `TEXT` | | Biografia opcional do perfil. |
| `account_photo_url` | `TEXT` | | URL para a imagem de perfil. |
| `account_verified` | `BOOLEAN` | `DEFAULT false` | Estado de verificação da conta. |
| `account_dob` | `DATE` | | **Adicionada.** Data de Nascimento. Usada para calcular a idade dinamicamente (evita dados estáticos). |
| `account_gender` | `VARCHAR(30)` | `CHECK` | **Adicionada.** Género do utilizador. A `CHECK` constraint garante a integridade dos dados do *dropdown menu*. |

### 3.2. Tabela: `filters`

**Propósito:** Armazena as categorias (Tipos de Evento) para o *dropdown menu* de criação de eventos.

| Coluna | Tipo | PK/FK/Constraint | Descrição |
| --- | --- | --- | --- |
| `filters_id` | `SERIAL` | `PK` | Identificador único da categoria. |
| `filters_name` | `VARCHAR(50)` | `NOT NULL`, `UNIQUE` | O nome da categoria (ex: "Música", "Cultural", "Político"). |

### 3.3. Tabela: `events`

**Propósito:** Armazena a informação de todos os eventos criados na plataforma.

| Coluna | Tipo | PK/FK/Constraint | Descrição |
| --- | --- | --- | --- |
| `event_id` | `SERIAL` | `PK` | Identificador único do evento. |
| `event_title` | `VARCHAR(150)` | `NOT NULL` | Título do evento. |
| `event_description` | `TEXT` | | Descrição longa do evento. |
| `event_visibility` | `VARCHAR(20)` | `NOT NULL`, `CHECK` | Visibilidade ('public', 'private'). |
| `event_category_id` | `INT` | `FK` (filters) | Liga ao `filters_id`. É o "Tipo de Evento". |
| `event_creator_id` | `INT` | `FK` (user\_details) | O utilizador que criou o evento. `ON DELETE CASCADE`. |
| `location` | `VARCHAR(255)` | | O endereço textual. |
| `event_latitude` | `NUMERIC(9,6)` | | Coordenada para o Google Maps. |
| `event_longitude` | `NUMERIC(9,6)` | | Coordenada para o Google Maps. |
| `event_date` | `TIMESTAMP` | `NOT NULL` | A data e hora exata do evento. |
| `event_price` | `NUMERIC(10,2)` | `DEFAULT 0` | O preço do evento. |
| `max_participants` | `INT` | `NOT NULL` | **Adicionada.** Limite de participantes. Verificado pelo *backend* para evitar *race conditions*. |
| `created_at` | `TIMESTAMP` | `DEFAULT NOW()` | Data de criação do registo. |

### 3.4. Tabela: `followers`

**Propósito:** Tabela de junção que armazena a relação "seguir" (mão única). É a fonte da verdade para a lógica social.

| Coluna | Tipo | PK/FK/Constraint | Descrição |
| --- | --- | --- | --- |
| `follow_id` | `SERIAL` | `PK` | ID da relação. |
| `follower_id` | `INT` | `NOT NULL`, `FK` (user) | O utilizador que *segue*. |
| `following_id` | `INT` | `NOT NULL`, `FK` (user) | O utilizador que *é seguido*. |
| `(follower_id, following_id)` | | `UNIQUE` | Garante que A só pode seguir B uma vez. |

### 3.5. Tabela: `event_participants`

**Propósito:** Tabela de junção que armazena quem "aderiu" a um evento (o botão "Participar").

| Coluna | Tipo | PK/FK/Constraint | Descrição |
| --- | --- | --- | --- |
| `participant_id` | `SERIAL` | `PK` | ID da participação. |
| `event_id` | `INT` | `NOT NULL`, `FK` (events) | O evento ao qual aderiu. |
| `user_id` | `INT` | `NOT NULL`, `FK` (user) | O utilizador que aderiu. |
| `(event_id, user_id)` | | `UNIQUE` | Garante que um utilizador não pode aderir duas vezes. |

### 3.6. Tabela: `invitations`

**Propósito:** Tabela de junção que gere convites para eventos *privados* ou por *convite*.

| Coluna | Tipo | PK/FK/Constraint | Descrição |
| --- | --- | --- | --- |
| `invitations_id` | `SERIAL` | `PK` | ID do convite. |
| `event_id` | `INT` | `NOT NULL`, `FK` (events) | O evento para o qual se convida. |
| `sender_id` | `INT` | `NOT NULL`, `FK` (user) | O utilizador que *envia* o convite. |
| `receiver_id` | `INT` | `NOT NULL`, `FK` (user) | O utilizador que *recebe* o convite. |
| `status` | `VARCHAR(20)` | `CHECK` | Estado: 'pending', 'accepted', 'rejected'. |
| `(event_id, sender_id, receiver_id)` | | `UNIQUE` | Impede o envio de convites duplicados. |

## 4\. Otimização e Performance (Índices)

A performance é crítica para uma aplicação móvel. Uma query lenta no *backend* resulta numa UI "congelada". Para mitigar isto, foram criados **índices* em todas as colunas que servem como Chave Estrangeira (FK).

  * **Justificação:** Operações como carregar um evento e o seu criador, ou encontrar todos os seguidores de um utilizador, exigem `JOIN`s ou `WHERE`s nessas colunas.
  * **Impacto:** Um `INDEX` transforma uma operação de "Table Scan" (ler a tabela inteira, muito lento) numa operação "Index Scan" (ir direto ao dado, muito rápido), garantindo que a app se mantém rápida mesmo com milhões de linhas.

**Índices Criados:**

  * `idx_events_creator_id` e `idx_events_category_id`
  * `idx_followers_follower_id` e `idx_followers_following_id`
  * `idx_invitations_event_id`, `idx_invitations_sender_id`, `idx_invitations_receiver_id`
  * `idx_event_participants_event_id` e `idx_event_participants_user_id`

## 5\. Demonstração de Queries

Esta secção apresentanda um conjunto de queries em **código SQL** que demonstram a **complexidade e diversidade** da lógica implementada.

-----

**Query 1: Diversidade (UPDATE)**

  * **Objetivo:** Demonstrar uma operação de `UPDATE` para modificar dados de um perfil de utilizador.

<!-- end list -->

```sql
UPDATE public.user_details
SET 
    account_bio = 'Esta é a minha nova biografia!',
    account_gender = 'Outro'
WHERE 
    account_username = 'pedro';
```

-----

**Query 2: Complexidade (Multi-JOIN e WHERE)**

  * **Objetivo:** Demonstrar uma query de pesquisa complexa, combinando 3 tabelas (`events`, `filters`, `user_details`) para encontrar eventos futuros que cumprem múltiplos critérios.

<!-- end list -->

```sql
SELECT 
    e.event_title AS "Evento",
    f.filters_name AS "Categoria",
    u.account_name AS "Criador",
    e.event_date AS "Data"
FROM 
    public.events AS e
JOIN 
    public.filters AS f ON e.event_category_id = f.filters_id
JOIN 
    public.user_details AS u ON e.event_creator_id = u.account_id
WHERE 
    f.filters_name = 'Música'
    AND e.event_visibility = 'public'
    AND e.event_date > CURRENT_TIMESTAMP;
```

-----

**Query 3: Complexidade (Lógica "Amigos" com Sub-query e INTERSECT)**

  * **Objetivo:** Demonstrar a query central da lógica de negócio social. Implementa a regra de "amizade" (seguimento mútuo) usando apenas a tabela `followers`.

<!-- end list -->

```sql
SELECT * FROM public.user_details
WHERE account_id IN (
    -- 1. Encontra todos os que o Utilizador 1 segue...
    SELECT f1.following_id
    FROM public.followers AS f1
    WHERE f1.follower_id = 1
    
    INTERSECT -- (e que também...)
    
    -- 2. ...Seguem o Utilizador 1 de volta
    SELECT f2.follower_id
    FROM public.followers AS f2
    WHERE f2.following_id = 1
);
```

-----

**Query 4: Complexidade (Agregação, Lógica de Negócio e `CASE`)**

  * **Objetivo:** Implementar a lógica de "vagas" (ex: "127/200"). Utiliza `COUNT`, `LEFT JOIN` (para gerir eventos sem participantes) e uma instrução `CASE` para devolver um estado.

<!-- end list -->

```sql
SELECT 
    e.event_title AS "Evento",
    COUNT(p.participant_id) AS "Total de Participantes",
    e.max_participants AS "Limite",
    CASE 
        WHEN COUNT(p.participant_id) >= e.max_participants THEN 'Cheio'
        ELSE 'Com Vagas'
    END AS "Estado"
FROM 
    public.events AS e
LEFT JOIN 
    public.event_participants AS p ON e.event_id = p.event_id
WHERE 
    e.event_id = 1
GROUP BY 
    e.event_title, e.max_participants;
```

-----

**Query 5: Diversidade (DELETE com Lógica Temporal)**

  * **Objetivo:** Demonstrar uma operação `DELETE` usada para manutenção da BD. Remove convites "pendentes" que expiraram (ex: mais de 30 dias).

<!-- end list -->

```sql
DELETE FROM public.invitations
WHERE 
    status = 'pending'
    AND sent_at < (CURRENT_TIMESTAMP - INTERVAL '30 days');
```

-----

**Query 6: Complexidade (Ranking com `GROUP BY` e `ORDER BY`)**

  * **Objetivo:** Demonstrar uma query de "ranking". Cria uma lista dos utilizadores mais populares (com mais seguidores), ordenando-a de forma descendente.

<!-- end list -->

```sql
SELECT 
    u.account_username AS "Utilizador",
    COUNT(f.follow_id) AS "Nº de Seguidores"
FROM 
    public.user_details AS u
LEFT JOIN 
    public.followers AS f ON u.account_id = f.following_id
GROUP BY 
    u.account_username
ORDER BY 
    "Nº de Seguidores" DESC;
```

-----

## 6\. Conclusão

A estrutura da base de dados desenhada cumpre todos os requisitos funcionais identificados nos mockups e V1 do relatório da aplicação
A implementação de `CONSTRAINTS` robustas (como `CHECK`, `UNIQUE`, `FOREIGN KEY`) garante a integridade e a consistência dos dados, enquanto a adição de `INDEXES` assegura a performance e a escalabilidade da aplicação. A base de dados está pronta para ser ligada ao *backend* (API REST) e suportar todas as operações da aplicação "Lane".

## 7\. Anexos (Ficheiros SQL)

  * **`Create.sql`**: Contém todo o código `SQL DDL` para criar a estrutura completa e vazia da base de dados.
  * **`Populate.sql`**: Contém código `SQL DML` (`INSERT`) para popular a base de dados com dados de teste.
  * **`Queries.sql`**: Contém as queries de demonstração listadas na Secção 5, prontas para execução e teste.
