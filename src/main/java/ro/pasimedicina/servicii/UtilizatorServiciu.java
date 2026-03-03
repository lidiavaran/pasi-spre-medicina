package ro.pasimedicina.servicii;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ro.pasimedicina.model.Utilizator;
import ro.pasimedicina.bd.UtilizatorRepository;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviciu pentru gestionarea logicii de business legate de utilizatori si securitatea parolelor
 */
@Service
public class UtilizatorServiciu {

    @Autowired
    private UtilizatorRepository utilizatorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Salveaza utilizatorul si cripteaza parola folosind BCrypt daca este in format text clar
     */
    public void salveaza(Utilizator u) {
        if (u.getParola() != null && !u.getParola().startsWith("$2a$")) {
            u.setParola(passwordEncoder.encode(u.getParola()));
        }
        utilizatorRepository.save(u);
    }

    /**
     * Returneaza lista completa a persoanelor inregistrate in sistem
     */
    public List<Utilizator> totiUtilizatorii() {
        return utilizatorRepository.findAll();
    }
    
    /**
     * Filtreaza utilizatorii pentru a extrage doar pe cei cu privilegii de administrare sau mentorat
     */
    public List<Utilizator> gasesteMentoriSiAdmini() {
        return utilizatorRepository.findAll().stream()
                   .filter(u -> "MENTOR".equals(u.getRol()) || "ADMIN".equals(u.getRol()))
                   .collect(Collectors.toList());
    }
    
    /**
     * Returneaza o lista de utilizatori filtrata strict dupa un anumit rol
     */
    public List<Utilizator> gasesteDupaRol(String rol) {
        return utilizatorRepository.findAll().stream()
                   .filter(u -> rol.equals(u.getRol()))
                   .collect(Collectors.toList());
    }
    
    /**
     * Cauta un utilizator in baza de date folosind id-ul unic
     */
    public Utilizator gasesteDupaId(Long id) {
        return utilizatorRepository.findById(id).orElse(null);
    }
    
    /**
     * Elimina definitiv un utilizator din sistem pe baza id-ului
     */
    public void stergeDupaId(Long id) {
        utilizatorRepository.deleteById(id);
    }
}