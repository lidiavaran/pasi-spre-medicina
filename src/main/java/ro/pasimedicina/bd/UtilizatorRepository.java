package ro.pasimedicina.bd;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.pasimedicina.model.Utilizator;
import java.util.List;
import java.util.Optional;

/**
 * Interfata pentru gestionarea datelor utilizatorilor in baza de date
 */
public interface UtilizatorRepository extends JpaRepository<Utilizator, Long> {

    /**
     * Cauta un utilizator in baza de date folosind adresa de email
     */
    Optional<Utilizator> findByEmail(String email);

    /**
     * Extrage o lista de utilizatori care au acelasi rol in sistem
     */
    List<Utilizator> findByRol(String rol);

    /**
     * Gaseste toti elevii care sunt asignati unui anumit mentor prin id
     */
    List<Utilizator> findByMentorId(Long mentorId);

    /**
     * Metoda pentru verificarea credentialelor de autentificare
     */
    Utilizator findByEmailAndParola(String email, String parola);

    /**
     * Gaseste utilizatorii cu un rol specific care apartin de un anumit mentor
     */
    List<Utilizator> findByRolAndMentor(String rol, Utilizator mentor);
}