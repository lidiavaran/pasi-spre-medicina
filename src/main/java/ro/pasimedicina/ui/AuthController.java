package ro.pasimedicina.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import ro.pasimedicina.bd.UtilizatorRepository; 
import ro.pasimedicina.model.Utilizator;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Controller pentru gestionarea proceselor de autentificare si inregistrare
 */
@Controller
public class AuthController {

    @Autowired
    private UtilizatorRepository utilizatorRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Redirectioneaza utilizatorul catre pagina principala dupa accesarea radacinii
     */
    @GetMapping("/")
    public String homePage() {
        return "redirect:/elev/dashboard/1";
    }

    /**
     * Afiseaza pagina de login pentru utilizatori
     */
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    /**
     * Afiseaza pagina de creare cont nou
     */
    @GetMapping("/cont-nou")
    public String registerPage() {
        return "register";
    }

    /**
     * Proceseaza datele din formularul de inregistrare si cripteaza parola
     */
    @PostMapping("/register")
    public String processRegister(@ModelAttribute Utilizator utilizator) {
        if (utilizatorRepo.findByEmail(utilizator.getEmail()).isPresent()) {
            return "redirect:/cont-nou?error=email_exists";
        }
        
        utilizator.setParola(passwordEncoder.encode(utilizator.getParola()));
        utilizator.setRol("ELEV");
        
        utilizatorRepo.save(utilizator);
        return "redirect:/login?success";
    }
    
    /**
     * Redirectioneaza utilizatorul logat catre dashboard in functie de rolul detinut
     */
    @GetMapping("/home")
    public String redirectDupaLogin(java.security.Principal principal) {
        String email = principal.getName();
        
        Utilizator u = utilizatorRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilizator inexistent"));

        if ("MENTOR".equals(u.getRol())) {
            return "redirect:/mentor/dashboard";
        }
        
        return "redirect:/elev/dashboard/" + u.getId();
    }
}