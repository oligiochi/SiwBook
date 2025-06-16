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
       (251, 51),
       (51, 151);-- Se questo è un uomo -> Primo Levi
INSERT INTO utente (id, name,surname,email)
VALUES
    (nextval('utente_seq'), 'Mario','Rossi','marRos'),
    (nextval('utente_seq'), 'Giuseppe','Verdi','giusVerdi'),
    (nextval('utente_seq'), 'Alfonso','Neri','alfNeri'),
    (nextval('utente_seq'), 'Utente','Test','utenteTest@mail.com'),
    (nextval('utente_seq'), 'Admin','Admin','admin@mail.com');

insert into recensione (stelle, author_id, book_id, id, commento,titolo,data)
values
    (5,1,1,nextval('recensione_seq'),'Review Positiva','Commento positivo','2016-02-19'),
    (5,1,51,nextval('recensione_seq'),'Review Positiva','Commento positivo','2024-04-19'),
    (4,51,51,nextval('recensione_seq'),'Review Media','Commento Medio','2025-06-14'),
    (3,151,1,nextval('recensione_seq'),'Test','Test','2025-06-14');

INSERT INTO autore (id, nome, cognome, date_of_birth, date_of_death, nationality)
VALUES (nextval('autore_seq'), 'Umberto', 'Eco', '1932-01-05', '2016-02-19', 'Italiana'),
       (nextval('autore_seq'), 'Primo', 'Levi', '1919-07-31', '1987-04-11', 'Italiana'),
       (nextval('autore_seq'), 'Giuseppe', 'Tomasi di Lampedusa', '1896-12-23', '1957-07-23', 'Italiana'),
       (nextval('autore_seq'), 'Carlo', 'Levi', '1900-11-29', '1975-01-04', 'Italiana'),
       (nextval('autore_seq'), 'Italo', 'Svevo', '1861-12-19', '1928-09-13', 'Italiana'),
       (nextval('autore_seq'), 'Alessandro', 'Manzoni', '1785-03-07', '1873-05-22', 'Italiana'),
       (nextval('autore_seq'), 'Grazia', 'Deledda', '1871-09-28', '1936-08-15', 'Italiana'),
       (nextval('autore_seq'), 'Luigi', 'Pirandello', '1867-06-28', '1936-12-10', 'Italiana'),
       (nextval('autore_seq'), 'Italo', 'Calvino', '1923-10-15', '1985-09-19', 'Italiana');

INSERT INTO books_author (author_id, libri_id)
VALUES (1, 1), -- Il nome della rosa -> Umberto Eco
       (51, 51),-- Se questo è un uomo -> Primo Levi
       (301, 51);

insert into credentials (id, user_id, password, role, username,oauth_user)
values  (nextval('credentials_seq'),201,'$2a$10$KRpc45VH8yQWOGdhlOaWzOm.9MEO3H8.v/ZGNh5.V5za6mlrQUO5y','ADMIN','ADMIN',false),
        (nextval('credentials_seq'),151,'$2a$10$IeRJPuUQOtTFRQ1Be8pn7esXBuiMuCBUnnqTk.CVwBOD53Kj6iXnW','DEFAULT','Test',false);
-- Se il join table si chiama books_generi, puoi popolarlo così:
-- INSERT INTO books_generi (books_id, generi_id) VALUES (1, 1);
-- INSERT INTO books_generi (books_id, generi_id) VALUES (1, 4);
-- INSERT INTO books_generi (books_id, generi_id) VALUES (2, 2);
-- INSERT INTO books_generi (books_id, generi_id) VALUES (3, 3);
