package ro.pasimedicina.bd;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.pasimedicina.model.ProgresLectie;
import ro.pasimedicina.model.Utilizator;
import ro.pasimedicina.model.Lectie;
import java.util.List;
import java.util.Optional;

/**
 * Interfata pentru monitorizarea stadiului de parcurgere a lectiilor de catre elevi
 */
public interface ProgresLectieRepository extends JpaRepository<ProgresLectie, Long> {
    
    /**
     * Gaseste toate inregistrarile de progres asociate unui anumit elev
     */
    List<ProgresLectie> findByElev(Utilizator elev);
    
    /**
     * Extrage progresul specific al unui elev pentru o anumita lectie
     */
    Optional<ProgresLectie> findByElevAndLectie(Utilizator elev, Lectie lectie);
}
