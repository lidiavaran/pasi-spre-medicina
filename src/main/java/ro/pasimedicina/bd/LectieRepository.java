package ro.pasimedicina.bd;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.pasimedicina.model.Lectie;

/**
 * Interfata pentru gestionarea operatiilor de persistenta ale lectiilor
 */
@Repository
public interface LectieRepository extends JpaRepository<Lectie, Long> {
}