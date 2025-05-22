package it.uniroma3.siwbooks.service;

import it.uniroma3.siwbooks.models.Utente;
import it.uniroma3.siwbooks.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The UtenteService handles logic for Utentes.
 */
@Service
public class UtenteService {

    @Autowired
    protected UtenteRepository userRepository;

    /**
     * This method retrieves a Utente from the DB based on its ID.
     * @param id the id of the Utente to retrieve from the DB
     * @return the retrieved Utente, or null if no Utente with the passed ID could be found in the DB
     */
    @Transactional
    public Utente getUtente(Long id) {
        Optional<Utente> result = this.userRepository.findById(id);
        return result.orElse(null);
    }

    /**
     * This method saves a Utente in the DB.
     * @param user the Utente to save into the DB
     * @return the saved Utente
     * @throws DataIntegrityViolationException if a Utente with the same username
     *                              as the passed Utente already exists in the DB
     */
    @Transactional
    public Utente saveUtente(Utente user) {
        return this.userRepository.save(user);
    }

    /**
     * This method retrieves all Utentes from the DB.
     * @return a List with all the retrieved Utentes
     */
    @Transactional
    public List<Utente> getAllUtentes() {
        List<Utente> result = new ArrayList<>();
        Iterable<Utente> iterable = this.userRepository.findAll();
        for(Utente user : iterable)
            result.add(user);
        return result;
    }
}
