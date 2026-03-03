package ro.pasimedicina.servicii;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.pasimedicina.model.Curs;
import ro.pasimedicina.bd.CursRepository;
import java.util.List;
import ro.pasimedicina.model.Lectie;
import ro.pasimedicina.bd.LectieRepository;

/**
 * Serviciu care gestioneaza operatiile de business pentru cursuri si lectiile aferente
 */
@Service
public class CursServiciu {

    @Autowired
    private CursRepository cursRepository;
    
    @Autowired
    private LectieRepository lectieRepository;

    /**
     * Returneaza lista completa a cursurilor disponibile in platforma
     */
    public List<Curs> toateCursurile() {
        return cursRepository.findAll();
    }
    
    /**
     * Salveaza sau actualizeaza un curs in baza de date
     */
    public void salveaza(Curs curs) {
        cursRepository.save(curs);
    }

    /**
     * Adauga sau actualizeaza o lectie in cadrul unui curs
     */
    public void salveazaLectie(Lectie lectie) {
        lectieRepository.save(lectie);
    }

    /**
     * Cauta un curs specific folosind identificatorul unic
     */
    public Curs gasesteDupaId(Long id) {
        return cursRepository.findById(id).orElse(null);
    }
    
    /**
     * Elimina o lectie din baza de date folosind id-ul
     */
    public void stergeLectie(Long id) {
        lectieRepository.deleteById(id);
    }

    /**
     * Sterge un curs intreg impreuna cu dependintele sale
     */
    public void stergeCurs(Long id) {
        cursRepository.deleteById(id);
    }
    
    /**
     * Cauta o lectie specifica dupa id pentru editare sau vizualizare
     */
    public Lectie gasesteLectieDupaId(Long id) {
        return lectieRepository.findById(id).orElse(null);
    }
}