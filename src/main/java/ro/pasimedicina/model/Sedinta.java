package ro.pasimedicina.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entitate care reprezinta o sedinta de pregatire programata intre un mentor si un elev
 */
@Entity
@Table(name = "sedinte")
public class Sedinta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titluCurs; 
    private LocalDateTime dataOra; 
    
    /**
     * Elevul care participa la sedinta respectiva
     */
    @ManyToOne
    @JoinColumn(name = "elev_id")
    private Utilizator elev;

    /**
     * Mentorul care sustine sesiunea de pregatire
     */
    @ManyToOne
    @JoinColumn(name = "mentor_id")
    private Utilizator mentor;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitluCurs() { return titluCurs; }
    public void setTitluCurs(String titluCurs) { this.titluCurs = titluCurs; }
    
    public LocalDateTime getDataOra() { return dataOra; }
    public void setDataOra(LocalDateTime dataOra) { this.dataOra = dataOra; }
    
    public Utilizator getElev() { return elev; }
    public void setElev(Utilizator elev) { this.elev = elev; }
    
    public Utilizator getMentor() { return mentor; }
    public void setMentor(Utilizator mentor) { this.mentor = mentor; }
}