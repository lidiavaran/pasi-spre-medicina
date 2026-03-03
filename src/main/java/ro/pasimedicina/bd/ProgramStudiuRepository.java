package ro.pasimedicina.bd;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.pasimedicina.model.ProgramStudiu;
import ro.pasimedicina.model.Utilizator;
import java.util.List;

/**
 * Interfata pentru gestionarea planurilor si programelor de studiu individuale
 */
@Repository
public interface ProgramStudiuRepository extends JpaRepository<ProgramStudiu, Long> {
    
    /**
     * Gaseste planurile de studiu create de un mentor ordonate dupa data limita
     */
    List<ProgramStudiu> findByMentorOrderByDataLimitaAsc(Utilizator mentor);
    
    /**
     * Gaseste planurile de studiu asignate unui elev ordonate dupa data limita
     */
    List<ProgramStudiu> findByElevOrderByDataLimitaAsc(Utilizator elev);
}