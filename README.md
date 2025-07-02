# 📚 SiwBooks

Gestione di libri, autori e recensioni con ruoli differenziati per gli utenti.

---

## 🧩 Funzionalità Principali

### 👥 Tipi di Utenti

| Ruolo               | Permessi                                                                 |
|---------------------|--------------------------------------------------------------------------|
| **Utente Occasionale** | Consultazione di libri, autori e recensioni. Nessuna modifica.             |
| **Utente Registrato**  | Può scrivere, modificare ed eliminare **solo le proprie recensioni**.      |
| **Amministratore**     | Accesso completo: può gestire libri, autori e recensioni.                  |

---

## 🗃️ Struttura dei Dati

### 📚 Book (Libro)
- Titolo  
- Data di pubblicazione  
- Copertina  
- Immagini aggiuntive  
- Autori associati  
- Recensioni collegate  

### 👤 Author (Autore)
- Nome e cognome  
- Data di nascita  
- Data di morte (opzionale)  
- Nazionalità  
- Fotografia  
- Libri scritti  

### 🧑‍💻 User (Utente)
- Nome e cognome  
- Email  

### 📝 Review (Recensione)
- Titolo  
- Testo  
- Valutazione numerica  
- Libro recensito  
- Utente autore

---

## 🚀 Casi d’Uso

### 📘 Inserimento di un nuovo libro
**Attore:** Amministratore  
1. Accesso alla sezione di inserimento libri  
2. Compilazione modulo  
3. Conferma e salvataggio

---

### ✏️ Modifica di un libro
**Attore:** Amministratore  
1. Selezione del libro  
2. Modifica dei dati  
3. Conferma e aggiornamento

---

### ❌ Eliminazione di un libro
**Attore:** Amministratore  
1. Selezione del libro  
2. Conferma  
3. Eliminazione

---

### 🔍 Visualizzazione elenco libri
**Attore:** Tutti gli utenti  
- Elenco completo  
- Ordinamento per titolo/autore  
- Filtri per titolo, autore, anno  
- Visualizzazione per singolo autore

---

### 👤 Inserimento di un autore
**Attore:** Amministratore  
1. Accesso alla sezione  
2. Compilazione modulo  
3. Conferma e salvataggio

---

### 🖊️ Modifica di un autore
**Attore:** Amministratore  
1. Selezione autore  
2. Modifica  
3. Salvataggio

---

### 🗑️ Eliminazione di un autore
**Attore:** Amministratore  
1. Selezione autore  
2. Conferma  
3. Rimozione

---

### 👁️ Visualizzazione elenco autori
**Attore:** Tutti gli utenti  
- Elenco completo  
- Filtro per nome/cognome (prefisso)

---

### 📝 Inserimento di una recensione
**Attore:** Utente autenticato  
1. Accesso alla pagina libro  
2. Compilazione modulo  
3. Conferma e salvataggio

---

### ✏️ Modifica di una recensione
**Attore:** Utente autenticato  
1. Accesso alla recensione  
2. Modifica dei dati  
3. Conferma e aggiornamento

---

## 📌 Note Finali

- Ogni ruolo ha accesso solo alle funzionalità consentite.
- Dati strettamente collegati tra loro (autori → libri → recensioni).
- L’interfaccia guida l’utente nella gestione e consultazione del catalogo.

---

## 📂 Tecnologie Utilizzate

- Java / Spring Boot  
- Thymeleaf  
- PostgreSQL  
- HTML / CSS / JS  
- Bootstrap

---
