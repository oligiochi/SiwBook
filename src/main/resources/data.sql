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

INSERT INTO books (id, title, release_date)
VALUES (nextval('books_seq'), 'Il nome della rosa', '1980-10-01'),
       (nextval('books_seq'), 'Se questo è un uomo', '1947-01-01'),
       (nextval('books_seq'), 'Il Gattopardo', '1958-11-01'),
       (nextval('books_seq'), 'Cristo si è fermato a Eboli', '1945-01-01'),
       (nextval('books_seq'), 'La coscienza di Zeno', '1923-01-01'),
       (nextval('books_seq'), 'I promessi sposi', '1827-01-01'),
       (nextval('books_seq'), 'Canne al vento', '1913-01-01'),
       (nextval('books_seq'), 'Uno, nessuno e centomila', '1926-01-01'),
       (nextval('books_seq'), 'Il barone rampante', '1957-01-01'),
       (nextval('books_seq'), 'Le città invisibili', '1972-01-01');

INSERT INTO books_generi (libri_id, generi_id)
VALUES (1, 1), -- Il nome della rosa -> Umberto Eco
       (51, 51),
       (251, 51);-- Se questo è un uomo -> Primo Levi
INSERT INTO utente (id, name,surname,email)
VALUES
    (nextval('utente_seq'), 'Mario','Rossi','marRos'),
    (nextval('utente_seq'), 'Giuseppe','Verdi','giusVerdi'),
    (nextval('utente_seq'), 'Alfonso','Neri','alfNeri'),
    (nextval('utente_seq'), 'Utente','Test','utenteTest@mail.com'),
    (nextval('utente_seq'), 'Admin','Admin','admin@mail.com');

insert into recensione (stelle, author_id, book_id, id, commento,titolo)
values
    (5,1,1,nextval('recensione_seq'),'Review Positiva','Commento positivo'),
    (5,1,51,nextval('recensione_seq'),'Review Positiva','Commento positivo'),
    (4,51,51,nextval('recensione_seq'),'Review Media','Commento Medio');

-- Se il join table si chiama books_generi, puoi popolarlo così:
-- INSERT INTO books_generi (books_id, generi_id) VALUES (1, 1);
-- INSERT INTO books_generi (books_id, generi_id) VALUES (1, 4);
-- INSERT INTO books_generi (books_id, generi_id) VALUES (2, 2);
-- INSERT INTO books_generi (books_id, generi_id) VALUES (3, 3);
