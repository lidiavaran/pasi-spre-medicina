package ro.pasimedicina.model;

import jakarta.persistence.*;

/**
 * Entitate care defineste structura unei lectii si suportul pentru fisiere atasate
 */
@Entity
@Table(name = "lectii")
public class Lectie {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String titlu;
    
    @Column(columnDefinition = "TEXT")
    private String continut; 
    
    @Column(length = 1000)   
    private String linkMaterial;

    private String numeFisier;
    private String tipFisier;

    /**
     * Camp de tip LONGBLOB pentru stocarea binara a fisierelor PDF sau a imaginilor
     */
    @Lob
    @Column(columnDefinition = "LONGBLOB") 
    private byte[] dateFisier;

    /**
     * Asociere cu entitatea curs pentru ierarhizarea materialelor didactice
     */
    @ManyToOne
    @JoinColumn(name = "curs_id")
    private Curs curs;

    public Lectie() {}

    public Lectie(String titlu, String continut, String linkMaterial, Curs curs) {
        this.titlu = titlu;
        this.continut = continut;
        this.linkMaterial = linkMaterial;
        this.curs = curs;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitlu() { return titlu; }
    public void setTitlu(String titlu) { this.titlu = titlu; }
    
    public String getContinut() { return continut; }
    public void setContinut(String continut) { this.continut = continut; }
    
    public String getLinkMaterial() { return linkMaterial; }
    public void setLinkMaterial(String linkMaterial) { this.linkMaterial = linkMaterial; }
    
    public String getNumeFisier() { return numeFisier; }
    public void setNumeFisier(String numeFisier) { this.numeFisier = numeFisier; }
    
    public String getTipFisier() { return tipFisier; }
    public void setTipFisier(String tipFisier) { this.tipFisier = tipFisier; }
    
    public byte[] getDateFisier() { return dateFisier; }
    public void setDateFisier(byte[] dateFisier) { this.dateFisier = dateFisier; }
    
    public Curs getCurs() { return curs; }
    public void setCurs(Curs curs) { this.curs = curs; }
}