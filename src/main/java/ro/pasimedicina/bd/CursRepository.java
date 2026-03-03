package ro.pasimedicina.bd;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.pasimedicina.model.Curs;
import java.util.List;

/**
 * Interfata pentru operatiile de baza asupra tabelului de cursuri in baza de date
 */
public interface CursRepository extends JpaRepository<Curs, Long> {

    /**
     * Gaseste lista de cursuri care contin un anumit mentor identificat prin id
     */
    List<Curs> findByMentori_Id(Long mentorId);
}