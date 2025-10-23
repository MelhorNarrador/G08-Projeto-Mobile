--
-- PostgreSQL database dump
--

\restrict n2scseUSIzUvXmDEoW1pZHbobFsEu4ccYlQyzrUwBYNMFeyKo0HDna3HT89Rgql

-- Dumped from database version 17.6
-- Dumped by pg_dump version 17.6

-- Started on 2025-10-23 17:17:51

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 222 (class 1259 OID 16446)
-- Name: events; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.events (
    event_id integer NOT NULL,
    event_title character varying(150) NOT NULL,
    event_description text,
    event_visibility character varying(20) NOT NULL,
    event_category_id integer,
    event_creator_id integer,
    location character varying(255),
    event_latitude numeric(9,6),
    event_longitude numeric(9,6),
    event_date timestamp without time zone NOT NULL,
    event_price numeric(10,2) DEFAULT 0,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT events_event_visibility_check CHECK (((event_visibility)::text = ANY ((ARRAY['public'::character varying, 'private'::character varying, 'invite'::character varying])::text[])))
);


ALTER TABLE public.events OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 16445)
-- Name: events_event_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.events_event_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.events_event_id_seq OWNER TO postgres;

--
-- TOC entry 4877 (class 0 OID 0)
-- Dependencies: 221
-- Name: events_event_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.events_event_id_seq OWNED BY public.events.event_id;


--
-- TOC entry 220 (class 1259 OID 16430)
-- Name: filters; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.filters (
    filters_id integer NOT NULL,
    filters_name character varying(50) NOT NULL
);


ALTER TABLE public.filters OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 16429)
-- Name: filters_filters_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.filters_filters_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.filters_filters_id_seq OWNER TO postgres;

--
-- TOC entry 4878 (class 0 OID 0)
-- Dependencies: 219
-- Name: filters_filters_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.filters_filters_id_seq OWNED BY public.filters.filters_id;


--
-- TOC entry 228 (class 1259 OID 16526)
-- Name: followers; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.followers (
    follow_id integer NOT NULL,
    follower_id integer NOT NULL,
    following_id integer NOT NULL,
    followed_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT no_self_follow CHECK ((follower_id <> following_id))
);


ALTER TABLE public.followers OWNER TO postgres;

--
-- TOC entry 227 (class 1259 OID 16525)
-- Name: followers_follow_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.followers_follow_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.followers_follow_id_seq OWNER TO postgres;

--
-- TOC entry 4879 (class 0 OID 0)
-- Dependencies: 227
-- Name: followers_follow_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.followers_follow_id_seq OWNED BY public.followers.follow_id;


--
-- TOC entry 224 (class 1259 OID 16468)
-- Name: friends; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.friends (
    friends_id integer NOT NULL,
    user_id integer NOT NULL,
    friends_user_id integer NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT no_self_friend CHECK ((user_id <> friends_user_id))
);


ALTER TABLE public.friends OWNER TO postgres;

--
-- TOC entry 223 (class 1259 OID 16467)
-- Name: friends_friends_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.friends_friends_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.friends_friends_id_seq OWNER TO postgres;

--
-- TOC entry 4880 (class 0 OID 0)
-- Dependencies: 223
-- Name: friends_friends_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.friends_friends_id_seq OWNED BY public.friends.friends_id;


--
-- TOC entry 226 (class 1259 OID 16500)
-- Name: invitations; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.invitations (
    invitations_id integer NOT NULL,
    event_id integer NOT NULL,
    sender_id integer NOT NULL,
    receiver_id integer NOT NULL,
    status character varying(20) DEFAULT 'pending'::character varying NOT NULL,
    sent_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT invitations_status_check CHECK (((status)::text = ANY ((ARRAY['pending'::character varying, 'accepted'::character varying, 'rejected'::character varying])::text[]))),
    CONSTRAINT no_self_invite CHECK ((sender_id <> receiver_id))
);


ALTER TABLE public.invitations OWNER TO postgres;

--
-- TOC entry 225 (class 1259 OID 16499)
-- Name: invitations_invitations_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.invitations_invitations_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.invitations_invitations_id_seq OWNER TO postgres;

--
-- TOC entry 4881 (class 0 OID 0)
-- Dependencies: 225
-- Name: invitations_invitations_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.invitations_invitations_id_seq OWNED BY public.invitations.invitations_id;


--
-- TOC entry 218 (class 1259 OID 16396)
-- Name: user_details; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.user_details (
    account_id integer NOT NULL,
    account_name character varying(100) NOT NULL,
    account_username character varying(50) NOT NULL,
    account_email character varying(120) NOT NULL,
    account_password_hash character varying(255) NOT NULL,
    account_bio text,
    account_photo_url text,
    account_verified boolean DEFAULT false NOT NULL
);


ALTER TABLE public.user_details OWNER TO postgres;

--
-- TOC entry 217 (class 1259 OID 16395)
-- Name: user_details_account_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.user_details_account_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.user_details_account_id_seq OWNER TO postgres;

--
-- TOC entry 4882 (class 0 OID 0)
-- Dependencies: 217
-- Name: user_details_account_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.user_details_account_id_seq OWNED BY public.user_details.account_id;


--
-- TOC entry 4669 (class 2604 OID 16449)
-- Name: events event_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.events ALTER COLUMN event_id SET DEFAULT nextval('public.events_event_id_seq'::regclass);


--
-- TOC entry 4668 (class 2604 OID 16433)
-- Name: filters filters_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.filters ALTER COLUMN filters_id SET DEFAULT nextval('public.filters_filters_id_seq'::regclass);


--
-- TOC entry 4677 (class 2604 OID 16529)
-- Name: followers follow_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.followers ALTER COLUMN follow_id SET DEFAULT nextval('public.followers_follow_id_seq'::regclass);


--
-- TOC entry 4672 (class 2604 OID 16471)
-- Name: friends friends_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.friends ALTER COLUMN friends_id SET DEFAULT nextval('public.friends_friends_id_seq'::regclass);


--
-- TOC entry 4674 (class 2604 OID 16503)
-- Name: invitations invitations_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.invitations ALTER COLUMN invitations_id SET DEFAULT nextval('public.invitations_invitations_id_seq'::regclass);


--
-- TOC entry 4666 (class 2604 OID 16399)
-- Name: user_details account_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_details ALTER COLUMN account_id SET DEFAULT nextval('public.user_details_account_id_seq'::regclass);


--
-- TOC entry 4865 (class 0 OID 16446)
-- Dependencies: 222
-- Data for Name: events; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.events (event_id, event_title, event_description, event_visibility, event_category_id, event_creator_id, location, event_latitude, event_longitude, event_date, event_price, created_at) FROM stdin;
\.


--
-- TOC entry 4863 (class 0 OID 16430)
-- Dependencies: 220
-- Data for Name: filters; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.filters (filters_id, filters_name) FROM stdin;
\.


--
-- TOC entry 4871 (class 0 OID 16526)
-- Dependencies: 228
-- Data for Name: followers; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.followers (follow_id, follower_id, following_id, followed_at) FROM stdin;
\.


--
-- TOC entry 4867 (class 0 OID 16468)
-- Dependencies: 224
-- Data for Name: friends; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.friends (friends_id, user_id, friends_user_id, created_at) FROM stdin;
\.


--
-- TOC entry 4869 (class 0 OID 16500)
-- Dependencies: 226
-- Data for Name: invitations; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.invitations (invitations_id, event_id, sender_id, receiver_id, status, sent_at) FROM stdin;
\.


--
-- TOC entry 4861 (class 0 OID 16396)
-- Dependencies: 218
-- Data for Name: user_details; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.user_details (account_id, account_name, account_username, account_email, account_password_hash, account_bio, account_photo_url, account_verified) FROM stdin;
\.


--
-- TOC entry 4883 (class 0 OID 0)
-- Dependencies: 221
-- Name: events_event_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.events_event_id_seq', 1, false);


--
-- TOC entry 4884 (class 0 OID 0)
-- Dependencies: 219
-- Name: filters_filters_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.filters_filters_id_seq', 1, false);


--
-- TOC entry 4885 (class 0 OID 0)
-- Dependencies: 227
-- Name: followers_follow_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.followers_follow_id_seq', 1, false);


--
-- TOC entry 4886 (class 0 OID 0)
-- Dependencies: 223
-- Name: friends_friends_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.friends_friends_id_seq', 1, false);


--
-- TOC entry 4887 (class 0 OID 0)
-- Dependencies: 225
-- Name: invitations_invitations_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.invitations_invitations_id_seq', 1, false);


--
-- TOC entry 4888 (class 0 OID 0)
-- Dependencies: 217
-- Name: user_details_account_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.user_details_account_id_seq', 1, false);


--
-- TOC entry 4695 (class 2606 OID 16456)
-- Name: events events_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.events
    ADD CONSTRAINT events_pkey PRIMARY KEY (event_id);


--
-- TOC entry 4691 (class 2606 OID 16437)
-- Name: filters filters_filters_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.filters
    ADD CONSTRAINT filters_filters_name_key UNIQUE (filters_name);


--
-- TOC entry 4693 (class 2606 OID 16435)
-- Name: filters filters_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.filters
    ADD CONSTRAINT filters_pkey PRIMARY KEY (filters_id);


--
-- TOC entry 4703 (class 2606 OID 16533)
-- Name: followers followers_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.followers
    ADD CONSTRAINT followers_pkey PRIMARY KEY (follow_id);


--
-- TOC entry 4697 (class 2606 OID 16475)
-- Name: friends friends_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.friends
    ADD CONSTRAINT friends_pkey PRIMARY KEY (friends_id);


--
-- TOC entry 4701 (class 2606 OID 16509)
-- Name: invitations invitations_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.invitations
    ADD CONSTRAINT invitations_pkey PRIMARY KEY (invitations_id);


--
-- TOC entry 4705 (class 2606 OID 16535)
-- Name: followers unique_follow; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.followers
    ADD CONSTRAINT unique_follow UNIQUE (follower_id, following_id);


--
-- TOC entry 4699 (class 2606 OID 16477)
-- Name: friends unique_friendship; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.friends
    ADD CONSTRAINT unique_friendship UNIQUE (user_id, friends_user_id);


--
-- TOC entry 4685 (class 2606 OID 16408)
-- Name: user_details user_details_account_email_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_details
    ADD CONSTRAINT user_details_account_email_key UNIQUE (account_email);


--
-- TOC entry 4687 (class 2606 OID 16406)
-- Name: user_details user_details_account_username_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_details
    ADD CONSTRAINT user_details_account_username_key UNIQUE (account_username);


--
-- TOC entry 4689 (class 2606 OID 16404)
-- Name: user_details user_details_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_details
    ADD CONSTRAINT user_details_pkey PRIMARY KEY (account_id);


--
-- TOC entry 4706 (class 2606 OID 16457)
-- Name: events events_event_category_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.events
    ADD CONSTRAINT events_event_category_id_fkey FOREIGN KEY (event_category_id) REFERENCES public.filters(filters_id) ON DELETE SET NULL;


--
-- TOC entry 4707 (class 2606 OID 16462)
-- Name: events events_event_creator_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.events
    ADD CONSTRAINT events_event_creator_id_fkey FOREIGN KEY (event_creator_id) REFERENCES public.user_details(account_id) ON DELETE CASCADE;


--
-- TOC entry 4713 (class 2606 OID 16536)
-- Name: followers followers_follower_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.followers
    ADD CONSTRAINT followers_follower_id_fkey FOREIGN KEY (follower_id) REFERENCES public.user_details(account_id) ON DELETE CASCADE;


--
-- TOC entry 4714 (class 2606 OID 16541)
-- Name: followers followers_following_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.followers
    ADD CONSTRAINT followers_following_id_fkey FOREIGN KEY (following_id) REFERENCES public.user_details(account_id) ON DELETE CASCADE;


--
-- TOC entry 4708 (class 2606 OID 16483)
-- Name: friends friends_friends_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.friends
    ADD CONSTRAINT friends_friends_user_id_fkey FOREIGN KEY (friends_user_id) REFERENCES public.user_details(account_id) ON DELETE CASCADE;


--
-- TOC entry 4709 (class 2606 OID 16478)
-- Name: friends friends_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.friends
    ADD CONSTRAINT friends_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user_details(account_id) ON DELETE CASCADE;


--
-- TOC entry 4710 (class 2606 OID 16510)
-- Name: invitations invitations_event_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.invitations
    ADD CONSTRAINT invitations_event_id_fkey FOREIGN KEY (event_id) REFERENCES public.events(event_id) ON DELETE CASCADE;


--
-- TOC entry 4711 (class 2606 OID 16520)
-- Name: invitations invitations_receiver_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.invitations
    ADD CONSTRAINT invitations_receiver_id_fkey FOREIGN KEY (receiver_id) REFERENCES public.user_details(account_id) ON DELETE CASCADE;


--
-- TOC entry 4712 (class 2606 OID 16515)
-- Name: invitations invitations_sender_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.invitations
    ADD CONSTRAINT invitations_sender_id_fkey FOREIGN KEY (sender_id) REFERENCES public.user_details(account_id) ON DELETE CASCADE;


-- Completed on 2025-10-23 17:17:52

--
-- PostgreSQL database dump complete
--

\unrestrict n2scseUSIzUvXmDEoW1pZHbobFsEu4ccYlQyzrUwBYNMFeyKo0HDna3HT89Rgql

