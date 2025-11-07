--- Query 1:
-- Atualiza a biografia e o género de um utilizador específico.
UPDATE public.user_details
SET 
    account_bio = 'Esta é a minha nova biografia!',
    account_gender = 'Outro'
WHERE 
    account_username = 'pedro';


--- Query 2: 
-- Procura todos os eventos públicos de "Música" que vão acontecer no futuro
-- e mostra o nome da categoria e do criador.
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


--- Query 3:
-- Encontra todos os "Amigos" (seguidores mútuos) do utilizador com ID 1.
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


--- Query 4:
-- Mostra a contagem de participantes de um evento (ID 1) e 
-- se ele está "Cheio" ou "Com Vagas", com base no limite.
SELECT 
    e.event_title AS "Evento",
    COUNT(p.participant_id) AS "Total de Participantes",
    e.max_participants AS "Limite",
    CASE 
        WHEN COUNT(p.participant_id) >= e.max_participants THEN 'Esgotado'
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


--- Query 5: 
-- Apaga todos os convites pendentes (pending) que foram enviados há mais de 30 dias.
DELETE FROM public.invitations
WHERE 
    status = 'pending'
    AND sent_at < (CURRENT_TIMESTAMP - INTERVAL '30 days');


--- Query 6:
-- Cria um "ranking" dos utilizadores mais populares (com mais seguidores).
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
    "Nº de Seguidores" DESC; -- Ordena do mais popular para o menos