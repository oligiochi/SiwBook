-- Inserimento dati nella tabella Genere
-- id e genere
INSERT INTO genere (id, genere)
values (nextval('genere_seq'), 'Fantasy'),
       (nextval('genere_seq'), 'Thriller'),
       (nextval('genere_seq'), 'Romanzo Storico'),
       (nextval('genere_seq'), 'Fantascienza'),
       (nextval('genere_seq'), 'Giallo'),
       (nextval('genere_seq'), 'Narrativa Contemporanea'),
       (nextval('genere_seq'), 'Biografia'),
       (nextval('genere_seq'), 'Saggio'),
       (nextval('genere_seq'), 'Poesia');


-- Se il join table si chiama books_generi, puoi popolarlo così:
-- INSERT INTO books_generi (books_id, generi_id) VALUES (1, 1);
-- INSERT INTO books_generi (books_id, generi_id) VALUES (1, 4);
-- INSERT INTO books_generi (books_id, generi_id) VALUES (2, 2);
-- INSERT INTO books_generi (books_id, generi_id) VALUES (3, 3);
