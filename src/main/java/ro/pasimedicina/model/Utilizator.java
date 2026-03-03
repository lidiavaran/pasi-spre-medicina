package ro.pasimedicina.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Entitate principala pentru gestionarea utilizatorilor mentorilor si elevilor
 */
@Entity
@Table(name="utilizatori")
public class Utilizator {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nume;
    
    @Column(unique = true)
    private String email;
    
    private String parola;
    
    private String rol;

    /**
     * Relatie de auto-asociere pentru a lega un elev de mentorul sau coordonator
     */
    @ManyToOne
    @JoinColumn(name = "mentor_id")
    private Utilizator mentor;

    /**
     * Lista cursurilor la care este inscris utilizatorul ca elev
     */
    @ManyToMany
    @JoinTable(
        name = "inscrieri", 
        joinColumns = @JoinColumn(name = "utilizator_id"),
        inverseJoinColumns = @JoinColumn(name = "curs_id")
    )
    private List<Curs> cursuri;

    /**
     * Lista cursurilor pe care utilizatorul le gestioneaza in calitate de mentor
     */
    @ManyToMany(mappedBy = "mentori")
    private List<Curs> cursuriMentor = new ArrayList<>();

    public Utilizator() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNume() { return nume; }
    public void setNume(String nume) { this.nume = nume; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getParola() { return parola; }
    public void setParola(String parola) { this.parola = parola; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public Utilizator getMentor() { return mentor; }
    public void setMentor(Utilizator mentor) { this.mentor = mentor; }

    public List<Curs> getCursuri() { return cursuri; }
    public void setCursuri(List<Curs> cursuri) { this.cursuri = cursuri; }

    public List<Curs> getCursuriMentor() { return cursuriMentor; }
    public void setCursuriMentor(List<Curs> cursuriMentor) { this.cursuriMentor = cursuriMentor; }
}