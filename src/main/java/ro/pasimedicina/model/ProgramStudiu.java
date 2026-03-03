package ro.pasimedicina.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Entitate pentru gestionarea planului de studiu si a schimburilor de fisiere intre mentor si elev
 */
@Entity
@Table(name = "programe_studiu")
public class ProgramStudiu {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titlu;
    
    @Column(length = 1000)
    private String descriere;

    private LocalDate dataLimita;

    private String numeFisier;
    private String tipFisier;
    
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] dateFisier;

    private String numeFisierRaspuns;
    private String tipFisierRaspuns;
    
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] dateFisierRaspuns;

    /**
     * Elevul caruia ii este alocat acest plan de studiu
     */
    @ManyToOne
    @JoinColumn(name = "elev_id")
    private Utilizator elev;

    /**
     * Mentorul care a creat si supravegheaza acest plan
     */
    @ManyToOne
    @JoinColumn(name = "mentor_id")
    private Utilizator mentor;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitlu() { return titlu; }
    public void setTitlu(String titlu) { this.titlu = titlu; }
    
    public String getDescriere() { return descriere; }
    public void setDescriere(String descriere) { this.descriere = descriere; }
    
    public LocalDate getDataLimita() { return dataLimita; }
    public void setDataLimita(LocalDate dataLimita) { this.dataLimita = dataLimita; }

    public String getNumeFisier() { return numeFisier; }
    public void setNumeFisier(String numeFisier) { this.numeFisier = numeFisier; }
    
    public String getTipFisier() { return tipFisier; }
    public void setTipFisier(String tipFisier) { this.tipFisier = tipFisier; }
    
    public byte[] getDateFisier() { return dateFisier; }
    public void setDateFisier(byte[] dateFisier) { this.dateFisier = dateFisier; }

    public String getNumeFisierRaspuns() { return numeFisierRaspuns; }
    public void setNumeFisierRaspuns(String numeFisierRaspuns) { this.numeFisierRaspuns = numeFisierRaspuns; }
    
    public String getTipFisierRaspuns() { return tipFisierRaspuns; }
    public void setTipFisierRaspuns(String tipFisierRaspuns) { this.tipFisierRaspuns = tipFisierRaspuns; }
    
    public byte[] getDateFisierRaspuns() { return dateFisierRaspuns; }
    public void setDateFisierRaspuns(byte[] dateFisierRaspuns) { this.dateFisierRaspuns = dateFisierRaspuns; }

    public Utilizator getElev() { return elev; }
    public void setElev(Utilizator elev) { this.elev = elev; }
    
    public Utilizator getMentor() { return mentor; }
    public void setMentor(Utilizator mentor) { this.mentor = mentor; }
}