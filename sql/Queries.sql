-- Query 1: Ver todos os utilizadores
SELECT * FROM public.user_details;

-- Query 2: Ver todos os eventos públicos
SELECT * FROM public.events WHERE event_visibility = 'public';

-- Query 3: Encontrar todos os "Amigos" (seguidores mútuos) do Pedro (ID = 1)
SELECT * FROM public.user_details
WHERE account_id IN (
    -- 1. Encontra todos os que o Pedro (1) segue...
    SELECT f1.following_id
    FROM public.followers AS f1
    WHERE f1.follower_id = 1
    
    INTERSECT -- (e que também...)
    
    -- 2. ...Seguem o Pedro (1) de volta
    SELECT f2.follower_id
    FROM public.followers AS f2
    WHERE f2.following_id = 1
);

-- Query 4: Contar quantos participantes estão no "Concerto de Verã" (ID = 1)
SELECT 
    COUNT(p.participant_id) AS "Total de Participantes",
    e.max_participants
FROM public.event_participants AS p
JOIN public.events AS e ON p.event_id = e.event_id
WHERE p.event_id = 1
GROUP BY e.max_participants;