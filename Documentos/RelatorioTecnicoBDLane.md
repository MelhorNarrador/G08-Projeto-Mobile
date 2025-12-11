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

```mermaid
erDiagram
    USER_DETAILS {
        int account_id
        string account_name
        string account_username
        string account_email
        string account_password_hash
        text account_bio
        text account_photo_url
        boolean account_verified
        date account_dob
        string account_gender
    }
