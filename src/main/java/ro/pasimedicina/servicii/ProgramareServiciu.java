package ro.pasimedicina.servicii;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.pasimedicina.model.Programare;
import ro.pasimedicina.bd.ProgramareRepository;
import java.util.List;

/**
 * Serviciu pentru gestionarea programarilor si a intalnirilor dintre mentori si elevi
 */
@Service
public class ProgramareServiciu {

    @Autowired
    private ProgramareRepository programareRepository;

    /**
     * Returneaza lista tuturor programarilor existente in sistem
     */
    public List<Programare> toateProgramarile() {
        return programareRepository.findAll();
    }

    /**
     * Salveaza o noua programare sau actualizeaza una existenta
     */
    public void salveaza(Programare p) {
        programareRepository.save(p);
    }
    
    /**
     * Sterge o programare din baza de date folosind identificatorul unic
     */
    public void stergeDupaId(Long id) {
        programareRepository.deleteById(id);
    }
}