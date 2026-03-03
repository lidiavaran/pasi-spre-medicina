package ro.pasimedicina.bd;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.pasimedicina.model.Sedinta;
import java.util.List;

/**
 * Interfata pentru gestionarea sedintelor de pregatire inregistrate in sistem
 */
public interface SedintaRepository extends JpaRepository<Sedinta, Long> {

    /**
     * Gaseste toate sedintele organizate de un anumit mentor
     */
    List<Sedinta> findByMentorId(Long mentorId);
}