package ro.pasimedicina.servicii;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ro.pasimedicina.model.Utilizator;
import ro.pasimedicina.bd.UtilizatorRepository;

import java.util.Collections;
import java.util.List;

/**
 * Serviciu care implementeaza interfata standard Spring Security pentru incarcarea datelor de autentificare
 */
@Service
public class ServiciuDetaliiUtilizator implements UserDetailsService {

    @Autowired
    private UtilizatorRepository utilizatorRepository;

    /**
     * Incarca datele utilizatorului din baza de date pe baza adresei de email
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        
        Utilizator utilizator = utilizatorRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Nu am gasit email-ul: " + email));

        /**
         * Creeaza lista de autoritati bazata pe rolul definit in entitatea Utilizator
         */
        List<SimpleGrantedAuthority> autoritati = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + utilizator.getRol()) 
        );

        /**
         * Returneaza obiectul User standard compatibil cu mecanismele de securitate Spring
         */
        return new User(
                utilizator.getEmail(),
                utilizator.getParola(),
                autoritati
        );
    }
}