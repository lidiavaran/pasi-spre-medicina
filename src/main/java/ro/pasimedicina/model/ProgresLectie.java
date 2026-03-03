package ro.pasimedicina.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entitate care urmareste progresul individual al fiecarui elev pentru o anumita lectie
 */
@Entity
@Table(name = "progres_lectii")
public class ProgresLectie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Utilizator elev;

    @ManyToOne
    private Lectie lectie;

    private int procentCompletat = 0; 
    private boolean asignat = false;  
    private LocalDateTime dataFinalizarii;

    /**
     * Constructor fara parametri necesar pentru mecanismul JPA
     */
    public ProgresLectie() {}

    /**
     * Constructor pentru initializarea progresului atunci cand o lectie este asignata unui elev
     */
    public ProgresLectie(Utilizator elev, Lectie lectie) {
        this.elev = elev;
        this.lectie = lectie;
        this.procentCompletat = 0;
        this.asignat = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Utilizator getElev() { return elev; }
    public void setElev(Utilizator elev) { this.elev = elev; }
    
    public Lectie getLectie() { return lectie; }
    public void setLectie(Lectie lectie) { this.lectie = lectie; }
    
    public int getProcentCompletat() { return procentCompletat; }
    public void setProcentCompletat(int procentCompletat) { this.procentCompletat = procentCompletat; }
    
    public boolean isAsignat() { return asignat; }
    public void setAsignat(boolean asignat) { this.asignat = asignat; }
    
    public LocalDateTime getDataFinalizarii() { return dataFinalizarii; }
    public void setDataFinalizarii(LocalDateTime dataFinalizarii) { this.dataFinalizarii = dataFinalizarii; }
}