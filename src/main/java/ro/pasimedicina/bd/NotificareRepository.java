package ro.pasimedicina.bd;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.pasimedicina.model.Notificare;

/**
 * Interfata pentru gestionarea notificarilor in sistem
 */
public interface NotificareRepository extends JpaRepository<Notificare, Long> {
}