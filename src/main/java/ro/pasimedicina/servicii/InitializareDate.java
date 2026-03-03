package ro.pasimedicina.servicii;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ro.pasimedicina.model.*;
import ro.pasimedicina.bd.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

/**
 * Clasa de configurare pentru popularea bazei de date cu date de test la pornirea aplicatiei
 */
@Configuration
public class InitializareDate {

    /**
     * Bean care ruleaza automat la startup pentru a verifica si crea datele initiale
     */
    @Bean
    CommandLineRunner initDatabase(UtilizatorRepository userRepo, 
                                   CursRepository cursRepo, 
                                   LectieRepository lectieRepo) {
        return args -> {
            // 1. Verificare existenta elev pentru a preveni erori de duplicare
            Utilizator elev;
            Optional<Utilizator> elevExistent = userRepo.findByEmail("elev@test.com");
            
            if (elevExistent.isEmpty()) {
                elev = new Utilizator();
                elev.setNume("Andrei Elevul");
                elev.setEmail("elev@test.com");
                elev.setParola("1234");
                elev.setRol("ELEV");
                elev = userRepo.save(elev);
                System.out.println("Elev nou creat");
            } else {
                elev = elevExistent.get();
                System.out.println("Elevul exista deja");
            }

            // 2. Creare curs initial daca tabela este goala
            if (cursRepo.count() == 0) {
                Curs curs = new Curs();
                curs.setTitlu("Anatomia Inimii");
                curs.setMaterie("Anatomie");
                curs.setDescriere("Curs detaliat despre sistemul cardiovascular");
                
                // Inscriere automata a elevului de test
                curs.setElevi(new ArrayList<>(Arrays.asList(elev)));
                curs = cursRepo.save(curs);

                // 3. Adaugare lectii asociate cursului de anatomie
                Lectie l1 = new Lectie();
                l1.setTitlu("Introducere in Cardiologie");
                l1.setContinut("Inima este un organ muscular cavitar");
                l1.setCurs(curs);
                lectieRepo.save(l1);

                Lectie l2 = new Lectie();
                l2.setTitlu("Vene si Arterele");
                l2.setContinut("Sangele circula prin corp prin vase de sange");
                l2.setCurs(curs);
                lectieRepo.save(l2);

                System.out.println("Datele de test au fost initializate cu succes");
            }
        };
    }
}