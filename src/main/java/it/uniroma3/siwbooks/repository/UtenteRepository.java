package it.uniroma3.siwbooks.repository;

import it.uniroma3.siwbooks.models.Utente;
import org.springframework.data.repository.CrudRepository;

interface UtenteRepository extends CrudRepository<Utente, Long> {
}
