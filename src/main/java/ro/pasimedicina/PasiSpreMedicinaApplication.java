package ro.pasimedicina;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import ro.pasimedicina.model.Utilizator;
import ro.pasimedicina.bd.UtilizatorRepository;

/**
 * Clasa principala de pornire a aplicatiei Spring Boot
 */
@SpringBootApplication
public class PasiSpreMedicinaApplication {

    /**
     * Metoda main care lanseaza executia intregului sistem
     */
    public static void main(String[] args) {
        SpringApplication.run(PasiSpreMedicinaApplication.class, args);
    }

    /**
     * Initializeaza datele de baza la pornirea aplicatiei cum ar fi contul de administrator
     */
    @Bean
    CommandLineRunner init(UtilizatorRepository repo, PasswordEncoder encoder) {
        return args -> {
            String emailAdmin = "lidiavaran450@gmail.com"; 
            if (repo.findByEmail(emailAdmin).isEmpty()) {
                Utilizator admin = new Utilizator();
                admin.setNume("Ionica Admin");
                admin.setEmail(emailAdmin);
                admin.setRol("ADMIN");
                admin.setParola(encoder.encode("admin123"));
                repo.save(admin);
                
                System.out.println("Cont admin creat");
            }
        };
    }
}