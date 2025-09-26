
# Lane – Aplicação de Localização de Eventos

**Universidade Europeia / IADE – Engenharia Informática**  
**Projeto Mobile – 3º semestre (2025/2026)**  
**Grupo XX**  
**Elementos do Grupo**: Duarte Barbosa (XXXXX), Nome2 (XXXXX), Nome3 (XXXXX)  

**GitHub Repo**: <link>  
**Figma**: [Protótipo no Figma](https://www.figma.com/make/c35ZsdYoegR7YtuFe3PDYD/Aplicação-de-Localização-de-Eventos?node-id=0-1&t=coD0KPh89QaUTP2I-1)  
**ClickUp**: [Espaço ClickUp](https://app.clickup.com/90151662103/v/s/90156861389)  
**Discord**: [Servidor Lane](https://discord.gg/N9EJm2re)

---

![Logo da Lane](imgs/logo_lane.png)

---

## Palavras-chave
aplicações móveis, geolocalização, descoberta de eventos, convites digitais, perfis verificados, redes sociais, mapas interativos

---

## 1. Descrição & Problema
A descoberta de eventos locais encontra-se atualmente dispersa entre diferentes plataformas, dificultando a centralização da informação.  
A **Lane** propõe-se a resolver este problema, oferecendo uma aplicação mobile onde os utilizadores podem **criar, partilhar e descobrir eventos**.  
Além disso, a aplicação diferencia-se pela possibilidade de eventos **públicos, privados e por convite**, pela implementação de **perfis verificados** e pela integração com **mapas interativos** que guiam os utilizadores até ao evento.

---

## 2. Objetivos & Motivação
### Objetivos
- Desenvolver uma aplicação mobile para Android.  
- Criar eventos públicos, privados e por convite.  
- Filtrar eventos por tipo e proximidade.  
- Integrar rotas de navegação (a pé, carro, transportes públicos).  
- Implementar sistema de perfis verificados.  
- Oferecer interface intuitiva baseada em **mockups Figma**.  

### Motivação
Com a proliferação de plataformas dispersas, os utilizadores sentem dificuldade em encontrar rapidamente eventos relevantes.  
A **Lane** pretende tornar este processo mais **simples, seguro e centralizado**.

---

## 3. Público-alvo
- **Jovens universitários** → interessados em festas e eventos culturais.  
- **Organizações políticas e sociais** → divulgação de eventos oficiais com credibilidade.  
- **Artistas e promotores** → promoção de concertos e eventos noturnos.  
- **Cidadãos ativos** → procura de atividades próximas para lazer e convívio.  

---

## 4. Pesquisa de Mercado
| App           | Pontos fortes                 | Pontos fracos                  | Oportunidade para Lane       |
|---------------|-------------------------------|--------------------------------|-------------------------------|
| Facebook Events | Grande base de utilizadores | Conteúdo disperso e ruído       | App dedicada só a eventos     |
| Eventbrite      | Gestão profissional, bilhética | Pouco usada em eventos informais | Apostar em simplicidade       |
| Meetup          | Comunidades sólidas         | Menos foco em festas/noite       | Tornar-se mais abrangente e jovem |

---

## 5. Guiões de Teste
### Guion 1 – Descobrir evento próximo (Core)
1. O utilizador abre a app e dá permissão de localização.  
2. A app apresenta lista/mapa de eventos próximos.  
3. O utilizador escolhe um evento e visualiza os detalhes.  
4. Seleciona “Obter direções”.  
**Resultado esperado**: O utilizador consegue chegar ao evento.  

### Guion 2 – Criar evento privado
1. O utilizador clica em “Criar evento”.  
2. Introduz título, descrição, tipo e localização.  
3. Marca como **Privado/Convite**.  
4. Seleciona convidados.  
**Resultado esperado**: Apenas os convidados recebem acesso.  

### Guion 3 – Seguir perfil verificado
1. O utilizador pesquisa “Partido X”.  
2. Surge perfil verificado com selo.  
3. Clica em “Seguir”.  
**Resultado esperado**: Passa a receber notificações oficiais.  

---

## 6. Casos de Utilização
### UC-Core: Descobrir eventos próximos
- **Ator**: Utilizador  
- **Pré-condições**: Localização ativa  
- **Fluxo principal**: Mostrar eventos → Selecionar → Ver rota  
- **Pós-condição**: Utilizador chega ao evento  

### UC-2: Criar evento privado  
### UC-3: Seguir perfil verificado  

---

## 7. Descrição da Solução (provisória)
### 7.1 Enquadramento nas UCs
- **PDM**: Kotlin + Jetpack Compose  
- **POO**: Backend Spring Boot (REST APIs)  
- **BD**: PostgreSQL (eventos, utilizadores, convites, categorias)  
- **CC/MD**: Google Maps API para rotas  

### 7.2 Requisitos
**Funcionais**:  
- Criar eventos (públicos/privados).  
- Filtrar por tipo e proximidade.  
- Mostrar rotas.  
- Gestão de convites.  
- Perfis verificados.  

**Não-funcionais**:  
- Resposta rápida (<2s).  
- Conformidade RGPD.  
- Escalabilidade (500+ utilizadores ativos).  

### 7.3 Arquitetura
App Android (Kotlin/Compose) ↔ API REST (Spring Boot) ↔ PostgreSQL  

---

## 8. Modelo de Domínio
![Modelo de Domínio – Lane](imgs/modelo_dominio_lane.png)

---

## 9. Planeamento
### 9.1 WBS
![WBS – Lane](imgs/wbs_lane.png)

### 9.2 Gantt (resumo)
| Semana | Atividade |
|--------|------------|
| 1 | Ideação, requisitos, mockups |
| 2 | Modelo de domínio, casos de uso |
| 3 | Backend esqueleto |
| 4 | Mobile esqueleto |
| 5 | Integração mapas + filtros |
| 6 | Testes + relatório v1 |
| 7 | Poster + vídeo |
| 8 | Revisões e submissão |

---

## 10. Mockups (Figma)
Aqui inserem-se capturas dos principais ecrãs exportados do Figma:

- **Ecrã inicial**  
![Mockup ecrã inicial](imgs/mockup_inicial.png)  

- **Criação de evento**  
![Mockup criar evento](imgs/mockup_criar_evento.png)  

- **Mapa com eventos**  
![Mockup mapa](imgs/mockup_mapa.png)  

---

## 11. Conclusão
A **Lane** será uma aplicação que centraliza a descoberta de eventos locais, tornando o processo intuitivo, rápido e confiável.  
Na **2ª entrega** será desenvolvido um protótipo funcional mínimo com:  
- Criação de eventos  
- Pesquisa por proximidade  
- Perfis verificados (protótipo inicial)  

---

## Bibliografia
[1] Eventbrite – https://www.eventbrite.com  
[2] Facebook Events – https://www.facebook.com/events  
[3] Meetup – https://www.meetup.com  
[4] Google Maps API – https://developers.google.com/maps  
