![Logo IADE](imgs/logo_iade.png)

# Relatório da 2.ª Entrega
### Projeto de Desenvolvimento Móvel – **Lane**

---

**Universidade Europeia / IADE – Engenharia Informática**  
**Unidade Curricular:** Desenvolvimento Móvel (3.º semestre – 2025/2026)  
**Grupo 08**  
**Elementos:** Francisco Abecasis (20240120), Pedro António (20241273), Filomeno Sabino (20241963), Savio Casimira (20240896), Gianni Lopes (20240593)

**Figma:** [Protótipo Figma](https://www.figma.com/make/vswsO7IQaQb8flOO56HPa4/Lane?node-id=0-1&p=f&t=6yDjtiKhbPivRkoO-0&fullscreen=1)  
**ClickUp:** [Espaço ClickUp](https://app.clickup.com/90151662103/v/s/90156861389)  
**GitHub:** [Repositório do Projeto](https://github.com/MelhorNarrador/G08-Projeto-Mobile.git)  

---

## Palavras-chave
aplicações móveis, geolocalização, eventos, convites digitais, perfis verificados, mapas interativos

---

## 1. Introdução

A aplicação **Lane** visa facilitar a descoberta e partilha de eventos próximos, permitindo que os utilizadores possam **criar, explorar e participar** em eventos de forma simples e intuitiva.  
Na **primeira entrega**, foram definidos o conceito, público-alvo, arquitetura e mockups base.  
Nesta **segunda entrega**, o foco foi o **desenvolvimento do protótipo funcional mínimo**, incluindo **criação e visualização de eventos**, **login**, e **integração inicial com APIs externas**.

---

## 2. Enquadramento e Problema

A informação sobre eventos encontra-se dispersa entre múltiplas plataformas, dificultando o acesso a eventos locais e relevantes.  
A **Lane** centraliza este processo, promovendo a **proximidade social** e a **autenticidade dos eventos**, com um sistema de **perfis verificados**, **filtros por localização**, e **convites personalizados**.

---

## 3. Público-alvo

| Segmento | Características | Necessidades |
|-----------|-----------------|---------------|
| Jovens universitários | Frequentam festas e eventos culturais | Encontrar eventos próximos e seguros |
| Promotores e artistas | Criam eventos e desejam promover | Ferramentas simples de gestão e partilha |
| Cidadãos ativos | Procuram lazer e convívio | Ver eventos verificados e próximos |

---

## 4. Casos de Utilização do Objeto Core

### Caso 1 – Criar um evento privado

1. O utilizador faz login.  
2. Seleciona “Criar evento”.  
3. Introduz nome, descrição, categoria, data e localização.  
4. Escolhe o tipo **Privado** e adiciona amigos convidados.  
5. Confirma e o evento é registado na base de dados.

**Resultado esperado:** Apenas os convidados podem visualizar e participar.

---

### Caso 2 – Explorar eventos próximos

1. O utilizador concede acesso à localização.  
2. A app apresenta um mapa com eventos próximos.  
3. O utilizador filtra por tipo e distância.  
4. Ao selecionar um evento, pode ver detalhes e navegar até lá.

**Resultado esperado:** O utilizador encontra eventos relevantes de forma rápida.

---

## 5. Arquitetura e Tecnologias

### 5.1 Arquitetura
- **Camada Mobile:** Android (Kotlin + Jetpack Compose)  
- **Camada Backend:** Spring Boot (REST API)  
- **Base de Dados:** PostgreSQL  
- **Integrações Externas:** Google Maps API  

**Descrição:**  
A arquitetura segue o modelo **Cliente–Servidor**, com comunicação via **API RESTful**.  
O backend gere autenticação, eventos e utilizadores, enquanto o frontend apresenta os dados em tempo real através de endpoints.

---

### 5.2 Tecnologias e Ferramentas

| Categoria | Tecnologia | Função |
|------------|-------------|--------|
| Mobile | Kotlin, Jetpack Compose | Interface e lógica de apresentação |
| Backend | Java/Spring Boot | Gestão de rotas e API REST |
| Base de Dados | PostgreSQL | Armazenamento de utilizadores e eventos |
| APIs | Google Maps SDK | Localização e rotas |
| Gestão | GitHub, ClickUp, Discord | Controlo de versões e comunicação |
| Design | Figma | Mockups e UI/UX |

---

## 6. Requisitos Funcionais e Não Funcionais

### Requisitos Funcionais
- Login e registo de utilizadores  
- Criação e edição de eventos  
- Pesquisa e filtragem de eventos por tipo e distância  
- Sistema de convites privados  
- Perfis verificados com selo  

### Requisitos Não Funcionais
- Interface intuitiva e responsiva  
- Segurança via JWT Tokens  
- Conformidade com o RGPD
- Chat privado

---

## 7. Mockups e Interfaces

Incluem-se abaixo os principais ecrãs do protótipo:

![Login](imgs/Ecrã_Log_In.png)  
![Mapa de eventos](imgs/Mapa.png)  
![Criação de evento](imgs/Criação_de_Eventos_1.png)  

[Ver protótipo completo no Figma](https://www.figma.com/make/vswsO7IQaQb8flOO56HPa4/Lane?node-id=0-1&p=f&t=6yDjtiKhbPivRkoO-0&fullscreen=1)

---

## 8. Planeamento e Execução

O projeto foi organizado no ClickUp e Discord.  
Nesta fase, foram concluídas as seguintes tarefas:

- Integração entre frontend e backend  
- Ligação da base de dados PostgreSQL  
- Criação de eventos  
- Implementação do login e registo de utilizadores
- Planeamento da verificação de perfis

**Planeado para a 3ª entrega:**
- Implementação de tokens JWT
- Finalização de todos os ecrãs da app 
- Integração completa do mapa

[Gráfico de Gantt]

---

## 9. Diagrama de Classes

![Diagrama de classes](imgs/diagrama_classes.png)

Descrição: O diagrama de classes reflete as principais entidades (Utilizador, Evento, Convite, seguidor) e as suas relações, permitindo uma visão clara da estrutura da aplicação.

---

## 10. Dicionário de Dados

| Tabela | Campos principais | Descrição |
|--------|------------------|------------|
| `users` | id, name, email, verified | Dados de utilizadores |
| `events` | id, title, type, location_id | Registo de eventos |
| `invites` | id, event_id, user_id | Convites privados |
| `locations` | id, latitude, longitude | Dados geográficos |
| `verification` | id, user_id, status | Processo de verificação |

---

## 11. Documentação REST

---

12. Conclusão
O desenvolvimento da Lane encontra-se numa fase sólida, com o MVP funcional e integração entre os principais módulos.
A aplicação demonstra potencial para evoluir numa plataforma social de eventos centralizada e segura.
Os próximos passos passam por otimizar a experiência do utilizador, melhorar a segurança dos dados e expandir funcionalidades sociais.

13. Bibliografia
Eventbrite

Facebook Events

Meetup

Google Maps API

Jetpack Compose Docs


---
