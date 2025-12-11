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

