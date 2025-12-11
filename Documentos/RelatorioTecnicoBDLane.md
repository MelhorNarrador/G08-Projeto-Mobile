# Relatório Técnico da Base de Dados  
**Projeto:** Lane – Plataforma de Descoberta e Participação em Eventos  
**Curso:** Engenharia Informática  
**Unidade Curricular:** Base De Dados
**Ano Letivo:** 2024 / 2025

---

A aplicação **Lane** é uma plataforma social focada na descoberta, criação e participação em eventos. O utilizador pode:

- Criar eventos;
- Juntar-se como participante a eventos existentes.
- Visualizar eventos no mundo todo

A base de dados foi implementada em **PostgreSQL**, com o objetivo de garantir:

- **Integridade** dos dados
- **Escalabilidade e performance** (através de índices nas colunas mais usadas em `JOIN`/`WHERE`);
- **Coerência com o backend** da API

O ficheiro `Create.sql` contém o script de criação completo da base de dados (tabelas, constraints e índices), servindo de referência principal para este relatório.

---

## 2. Diagrama Entidade-Relação (Descrição)

A base de dados é organizada em torno de 6 entidades principais:

- `user_details` – Utilizadores da plataforma;
- `filters` – Categorias/tipos de evento;
- `events` – Eventos criados pelos utilizadores;
- `followers` – Relações “seguir” entre utilizadores;
- `invitations` – Convites enviados para eventos;
- `event_participants` – Participações dos utilizadores em eventos.

### 2.1. Relações principais
**Um utilizador** (`user_details`) pode:
  - Criar **muitos eventos** → relação 1-N entre `user_details (account_id)` e `events (event_creator_id)`;
  - Seguir **muitos outros utilizadores** → relação N-N auto-referenciada via `followers`;
  - Participar em **muitos eventos** → relação N-N via `event_participants`;
  - Enviar/receber **muitos convites** → relações 1-N entre `user_details` e `invitations`.

- **Um evento** (`events`) pertence a **uma categoria** (`filters`), através da FK `event_category_id`.

- **Um evento** pode:
  - Ter **muitos participantes** (`event_participants`);
  - Ter **muitos convites** (`invitations`).

---

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
| `event_image` | `TEXT` | `–` | Imagem associada ao evento (Base64) |


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



