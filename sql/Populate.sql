-- 1. Inserir Utilizadores
INSERT INTO public.user_details (account_name, account_username, account_email, account_password_hash, account_dob, account_gender)
VALUES
('Pedro Silva', 'pedro', 'pedro@email.com', 'hash_secreta_123', '1995-05-10', 'Masculino'),
('Ana Santos', 'ana', 'ana@email.com', 'hash_secreta_456', '2000-10-20', 'Feminino'),
('Maria Costa', 'maria', 'maria@email.com', 'hash_secreta_789', '1998-02-15', 'Feminino');

-- 2. Inserir Categorias (Filtros)
INSERT INTO public.filters (filters_name)
VALUES
('Música'), ('Arte'), ('Comida'), ('Desporto');

-- 3. Inserir Eventos
-- (Assume que 'pedro' tem account_id = 1, 'ana' tem id = 2, 'Música' tem id = 1)
INSERT INTO public.events (event_title, event_description, event_visibility, event_category_id, event_creator_id, location, event_latitude, event_longitude, event_date, max_participants)
VALUES
('Concerto de Verão', 'Grande concerto na praia.', 'public', 1, 1, 'Praia de Carcavelos', 38.679820, -9.336420, '2025-07-15 21:00:00', 500),
('Festa Privada da Ana', 'Festa de aniversário.', 'private', 3, 2, 'Casa da Ana', 38.711048, -9.141219, '2025-08-01 22:00:00', 50);

-- 4. Inserir Relações (Seguidores)
INSERT INTO public.followers (follower_id, following_id)
VALUES
(1, 2), -- Pedro segue Ana
(3, 1), -- Maria segue Pedro
(2, 1); -- Ana segue Pedro (agora Pedro e Ana são "amigos")

-- 5. Inserir Participantes de Eventos
-- (Ana(2) e Maria(3) participam no Concerto de Verão (evento 1))
INSERT INTO public.event_participants (event_id, user_id)
VALUES
(1, 2),
(1, 3);