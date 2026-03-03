package ro.pasimedicina.bd;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.pasimedicina.model.Programare;
import java.util.List;

/**
 * Interfata pentru gestionarea programarilor si a sedintelor in baza de date
 */
@Repository
public interface ProgramareRepository extends JpaRepository<Programare, Long> {

    /**
     * Gaseste toate programarile asociate unui anumit elev
     */
    List<Programare> findByElevId(Long elevId);

    /**
     * Gaseste toate programarile asociate unui anumit mentor
     */
    List<Programare> findByMentorId(Long mentorId);
}