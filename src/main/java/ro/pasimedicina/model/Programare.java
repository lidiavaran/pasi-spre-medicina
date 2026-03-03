package ro.pasimedicina.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Entitate pentru gestionarea intalnirilor dintre mentori si elevi pentru un anumit curs
 */
@Entity
@Table(name = "programari")
public class Programare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Elevul care participa la sedinta programata
     */
    @ManyToOne
    @JoinColumn(name = "elev_id")
    private Utilizator elev;

    /**
     * Mentorul care sustine sedinta de pregatire
     */
    @ManyToOne
    @JoinColumn(name = "mentor_id")
    private Utilizator mentor;

    /**
     * Cursul specific pentru care se realizeaza programarea
     */
    @ManyToOne
    @JoinColumn(name = "curs_id")
    private Curs curs;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name = "data_ora")
    private LocalDateTime dataOra;
    
    private String subiect; 
    private String linkMeeting;

    public Programare() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Utilizator getElev() { return elev; }
    public void setElev(Utilizator elev) { this.elev = elev; }
    
    public Utilizator getMentor() { return mentor; }
    public void setMentor(Utilizator mentor) { this.mentor = mentor; }

    public Curs getCurs() { return curs; }
    public void setCurs(Curs curs) { this.curs = curs; }

    public LocalDateTime getDataOra() { return dataOra; }
    public void setDataOra(LocalDateTime dataOra) { this.dataOra = dataOra; }
    
    public String getSubiect() { return subiect; }
    public void setSubiect(String subiect) { this.subiect = subiect; }
    
    public String getLinkMeeting() { return linkMeeting; }
    public void setLinkMeeting(String linkMeeting) { this.linkMeeting = linkMeeting; }
}