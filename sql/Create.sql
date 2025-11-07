DROP TABLE IF EXISTS public.event_participants;
DROP TABLE IF EXISTS public.invitations;
DROP TABLE IF EXISTS public.followers;
DROP TABLE IF EXISTS public.events;
DROP TABLE IF EXISTS public.filters;
DROP TABLE IF EXISTS public.user_details;

-- 1. TABELA DE UTILIZADORES
CREATE TABLE public.user_details (
    account_id SERIAL PRIMARY KEY,
    account_name VARCHAR(100) NOT NULL,
    account_username VARCHAR(50) NOT NULL UNIQUE,
    account_email VARCHAR(120) NOT NULL UNIQUE,
    account_password_hash VARCHAR(255) NOT NULL,
    account_bio TEXT,
    account_photo_url TEXT,
    account_verified BOOLEAN DEFAULT false NOT NULL,
    account_dob DATE,
    account_gender VARCHAR(30),
    CONSTRAINT check_gender CHECK (account_gender IN ('Masculino', 'Feminino', 'Outro', 'Prefiro_nao_dizer'))
);

-- 2. TABELA DE CATEGORIAS/FILTROS
CREATE TABLE public.filters (
    filters_id SERIAL PRIMARY KEY,
    filters_name VARCHAR(50) NOT NULL UNIQUE
);

-- 3. TABELA DE EVENTOS
CREATE TABLE public.events (
    event_id SERIAL PRIMARY KEY,
    event_title VARCHAR(150) NOT NULL,
    event_description TEXT,
    event_visibility VARCHAR(20) NOT NULL,
    event_category_id INT,
    event_creator_id INT,
    location VARCHAR(255),
    event_latitude NUMERIC(9,6),
    event_longitude NUMERIC(9,6),
    event_date TIMESTAMP NOT NULL,
    event_price NUMERIC(10,2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    max_participants INT NOT NULL,
    
    CONSTRAINT events_event_visibility_check CHECK (event_visibility IN ('public', 'private', 'invite')),
    CONSTRAINT fk_events_category FOREIGN KEY (event_category_id) REFERENCES public.filters(filters_id) ON DELETE SET NULL,
    CONSTRAINT fk_events_creator FOREIGN KEY (event_creator_id) REFERENCES public.user_details(account_id) ON DELETE CASCADE
);

-- 4. TABELA DE SEGUIDORES (tambem serve para as amizades)
CREATE TABLE public.followers (
    follow_id SERIAL PRIMARY KEY,
    follower_id INT NOT NULL,
    following_id INT NOT NULL,
    followed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    
    CONSTRAINT no_self_follow CHECK (follower_id <> following_id),
    CONSTRAINT unique_follow UNIQUE (follower_id, following_id),
    CONSTRAINT fk_followers_follower FOREIGN KEY (follower_id) REFERENCES public.user_details(account_id) ON DELETE CASCADE,
    CONSTRAINT fk_followers_following FOREIGN KEY (following_id) REFERENCES public.user_details(account_id) ON DELETE CASCADE
);

-- 5. TABELA DE CONVITES
CREATE TABLE public.invitations (
    invitations_id SERIAL PRIMARY KEY,
    event_id INT NOT NULL,
    sender_id INT NOT NULL,
    receiver_id INT NOT NULL,
    status VARCHAR(20) DEFAULT 'pending' NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    
    CONSTRAINT invitations_status_check CHECK (status IN ('pending', 'accepted', 'rejected')),
    CONSTRAINT no_self_invite CHECK (sender_id <> receiver_id),
    CONSTRAINT unique_invitation UNIQUE (event_id, sender_id, receiver_id),
    CONSTRAINT fk_invitations_event FOREIGN KEY (event_id) REFERENCES public.events(event_id) ON DELETE CASCADE,
    CONSTRAINT fk_invitations_sender FOREIGN KEY (sender_id) REFERENCES public.user_details(account_id) ON DELETE CASCADE,
    CONSTRAINT fk_invitations_receiver FOREIGN KEY (receiver_id) REFERENCES public.user_details(account_id) ON DELETE CASCADE
);

-- 6. TABELA DE PARTICIPANTES (Quem aderiu ao evento)
CREATE TABLE public.event_participants (
    participant_id SERIAL PRIMARY KEY,
    event_id INT NOT NULL,
    user_id INT NOT NULL,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    
    CONSTRAINT unique_participation UNIQUE (event_id, user_id),
    CONSTRAINT fk_participants_event FOREIGN KEY (event_id) REFERENCES public.events(event_id) ON DELETE CASCADE,
    CONSTRAINT fk_participants_user FOREIGN KEY (user_id) REFERENCES public.user_details(account_id) ON DELETE CASCADE
);

-- 7. ÍNDICES PARA PERFORMANCE
CREATE INDEX idx_events_creator_id ON public.events(event_creator_id);
CREATE INDEX idx_events_category_id ON public.events(event_category_id);
CREATE INDEX idx_events_location ON public.events(event_latitude, event_longitude);
CREATE INDEX idx_followers_follower_id ON public.followers(follower_id);
CREATE INDEX idx_followers_following_id ON public.followers(following_id);
CREATE INDEX idx_invitations_event_id ON public.invitations(event_id);
CREATE INDEX idx_invitations_sender_id ON public.invitations(sender_id);
CREATE INDEX idx_invitations_receiver_id ON public.invitations(receiver_id);
CREATE INDEX idx_event_participants_event_id ON public.event_participants(event_id);
CREATE INDEX idx_event_participants_user_id ON public.event_participants(user_id);